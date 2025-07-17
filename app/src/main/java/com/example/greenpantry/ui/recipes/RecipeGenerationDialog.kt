package com.example.greenpantry.ui.recipes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.model.GeneratedRecipe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeGenerationDialog : DialogFragment() {
    
    private val viewModel: RecipeGenerationViewModel by viewModels()
    
    private lateinit var progressBar: ProgressBar
    private lateinit var rvGeneratedRecipes: RecyclerView
    private lateinit var tvError: TextView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var btnRegenerate: Button
    private lateinit var btnClose: Button
    
    private lateinit var recipesAdapter: GeneratedRecipesAdapter
    
    companion object {
        fun newInstance(): RecipeGenerationDialog {
            return RecipeGenerationDialog()
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_recipe_generation, null)
        
        setupViews(view)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
    }
    
    private fun setupViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        rvGeneratedRecipes = view.findViewById(R.id.rvGeneratedRecipes)
        tvError = view.findViewById(R.id.tvError)
        llEmptyState = view.findViewById(R.id.llEmptyState)
        btnRegenerate = view.findViewById(R.id.btnRegenerate)
        btnClose = view.findViewById(R.id.btnClose)
    }
    
    private fun setupRecyclerView() {
        recipesAdapter = GeneratedRecipesAdapter(
            onDetailsClick = { recipe ->
                showRecipeDetails(recipe)
            },
            onSaveClick = { recipe ->
                viewModel.saveRecipeToDatabase(recipe)
            }
        )
        
        rvGeneratedRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipesAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnRegenerate.setOnClickListener {
            viewModel.regenerateRecipes()
        }
        
        btnClose.setOnClickListener {
            dismiss()
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        
        viewModel.generatedRecipes.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                showRecipes(recipes)
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
        
        viewModel.saveSuccess.observe(this) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveSuccess()
            }
        }
    }
    
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rvGeneratedRecipes.visibility = View.GONE
        tvError.visibility = View.GONE
        llEmptyState.visibility = View.GONE
        btnRegenerate.isEnabled = false
    }
    
    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }
    
    private fun showRecipes(recipes: List<GeneratedRecipe>) {
        rvGeneratedRecipes.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        llEmptyState.visibility = View.GONE
        btnRegenerate.isEnabled = true
        recipesAdapter.submitList(recipes)
    }
    
    private fun showError(error: String) {
        tvError.text = error
        tvError.visibility = View.VISIBLE
        
        if (error.contains("at least")) {
            llEmptyState.visibility = View.VISIBLE
            btnRegenerate.isEnabled = false
        } else {
            llEmptyState.visibility = View.GONE
            btnRegenerate.isEnabled = true
        }
        
        rvGeneratedRecipes.visibility = View.GONE
    }
    
    private fun showRecipeDetails(recipe: GeneratedRecipe) {
        val detailsDialog = RecipeDetailsDialog.newInstance(recipe)
        detailsDialog.show(parentFragmentManager, "recipe_details")
    }
}