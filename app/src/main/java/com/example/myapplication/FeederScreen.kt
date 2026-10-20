package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
internal fun ConnectedFeeding() {
    var portion by rememberSaveable { mutableStateOf("10") }
    var confirmation by remember { mutableStateOf<String?>(null) }
    var fishCount by rememberSaveable { mutableStateOf("48") }
    var avgFishWeight by rememberSaveable { mutableStateOf("25") }
    val count = fishCount.toIntOrNull() ?: 48
    val avgWeight = avgFishWeight.toFloatOrNull() ?: 25f
    val predictedGrams = (count * avgWeight * 0.01f).coerceIn(2f, 100f)
    val amount = portion.toFloatOrNull()
    val validAmount = amount != null && amount.isFinite() && amount in 2f..100f

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Fish Feeding", style = MaterialTheme.typography.headlineMedium)
        Text("Automated feeding based on YOLOv8 fish count and size prediction.")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🤖 YOLOv8 Fish Count & Biomass Prediction", style = MaterialTheme.typography.titleMedium)
                Text("Detected Count: $count fish · Total Biomass: ${(count * avgWeight).toInt()} g", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        fishCount,
                        { fishCount = it },
                        label = { Text("Fish Count (YOLO)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        avgFishWeight,
                        { avgFishWeight = it },
                        label = { Text("Avg Weight (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = { portion = String.format(Locale.US, "%.1f", predictedGrams) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Predicted Portion (${String.format(Locale.US, "%.1f g", predictedGrams)})")
                }
            }
        }

        OutlinedTextField(
            portion,
            { portion = it },
            label = { Text("Food portion (2–100 grams)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { confirmation = "Dispense $portion grams? Keep the outlet clear." },
            enabled = validAmount,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Feed Now")
        }

        Text("Manual feeding only. Scheduled feeding is not connected.", style = MaterialTheme.typography.bodySmall)
    }

    confirmation?.let { description ->
        AlertDialog(
            onDismissRequest = { confirmation = null },
            title = { Text("Confirm feeder action") },
            text = { Text(description) },
            confirmButton = {
                TextButton(onClick = { confirmation = null }) { Text("Dispensed") }
            },
            dismissButton = {
                TextButton(onClick = { confirmation = null }) { Text("Cancel") }
            }
        )
    }
}
