package com.example.greenpantry.ui.sharedcomponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DatabaseLoadingScreen(
    isLoadingAuth: Boolean = false,
    isLoadingDatabase: Boolean = false,
    loadingProgress: Float = 0f,
    loadingMessage: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Main loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                strokeWidth = 4.dp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main status message
            Text(
                text = when {
                    isLoadingAuth -> "Checking login..."
                    isLoadingDatabase -> loadingMessage
                    else -> "Initializing..."
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            // Show progress for database loading
            if (isLoadingDatabase) {
                Spacer(modifier = Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = loadingProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${(loadingProgress * 100).toInt()}% complete",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress details
                Text(
                    text = when (loadingProgress) {
                        in 0f..0.1f -> "Preparing database initialization..."
                        in 0.1f..0.5f -> "Loading food items database..."
                        in 0.5f..0.9f -> "Loading recipes database..."
                        in 0.9f..1f -> "Finalizing setup..."
                        else -> "Processing data..."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            // Additional info for first-time users
            if (isLoadingDatabase && loadingProgress < 0.8f) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "First Launch Setup",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We're loading our food and recipe databases. This only happens once and makes the app much faster afterward!",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}
