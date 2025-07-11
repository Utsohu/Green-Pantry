package com.example.greenpantry.ui.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel :ViewModel() {
    val vege = MutableLiveData(false)
    val fruit = MutableLiveData(false)
    val prot = MutableLiveData(false)
    val grain = MutableLiveData(false)
    val dairy = MutableLiveData(false)
    val oth = MutableLiveData(false)
}