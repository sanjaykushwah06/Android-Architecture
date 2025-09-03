/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Details screen for displaying item information
 */

package com.iiwa.home.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iiwa.home.viewmodels.DetailsViewModel

@Composable
fun DetailsScreen(viewModel: DetailsViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Item ID: ${viewModel.itemId}",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
