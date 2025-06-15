package com.example.railid

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import java.net.URLEncoder
import java.net.URLDecoder

@Composable
fun AuthMethodScreen(navController: NavHostController, trainJson: String) {
    var selectedMethod by remember { mutableStateOf<AuthMethod?>(null) }

    Column(Modifier.padding(16.dp)) {
        Text("Select Verification Method", style = MaterialTheme.typography.titleLarge)

        RadioButtonGroup(
            options = AuthMethod.values().toList(),
            selectedOption = selectedMethod,
            onOptionSelected = { selectedMethod = it },
            optionText = { it.name }
        )

        Button(
            onClick = {
                selectedMethod?.let {
                    navController.navigate("biometricAuth/${URLEncoder.encode(trainJson, "UTF-8")}?method=${it.name}")
                }
            },
            enabled = selectedMethod != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify")
        }
    }
}

@Composable
fun <T> RadioButtonGroup(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionText: (T) -> String
) {
    Column {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = optionText(option),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}