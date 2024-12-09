package com.loeth.wealthway

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InputScreen(navController: NavController, viewModel: MainViewModel) {
    var principal by rememberSaveable { mutableStateOf("") }
    var dailyRate by rememberSaveable { mutableStateOf("6.6") }
    var withdrawalPercentage by rememberSaveable { mutableStateOf("50") }
    var withdrawalCycle by rememberSaveable { mutableStateOf("20") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
//        TextField(
//            value = principal,
//            onValueChange = { },
//            label = { Text("Enter Principal Amount") },
//            keyboardType = KeyboardType.Number
//        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val principalAmount = principal.toDoubleOrNull() ?: 0.0
                val rate = dailyRate.toDoubleOrNull() ?: 6.6
                val withdrawal = withdrawalPercentage.toDoubleOrNull() ?: 50.0
                val cycle = withdrawalCycle.toIntOrNull() ?: 20

                viewModel.calculateEarnings(
                    principal = principalAmount,
                    dailyRate = rate / 100,
                    withdrawalPercentage = withdrawal / 100,
                    withdrawalCycle = cycle
                )
                navController.navigate("results")
            }
        ) {
            Text("Calculate")
        }
    }
}
