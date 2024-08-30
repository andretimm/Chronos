package calculator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.*

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)

    ) {
        Text(text = text)
    }
}

@Composable
fun CalculatorDisplay(value: String) {
    Text(
        text = value,
        style = MaterialTheme.typography.h4,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp),
        maxLines = 2
    )
}

@Composable
fun CalculatorKeypad(onButtonClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier,
    ) {
        val buttons = listOf(
            listOf("7", "8", "9", "C"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ":", "=", "+")
        )

        for (row in buttons) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (button in row) {
                    CalculatorButton(text = button) {
                        onButtonClick(button)
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onButtonClick("Back") },
                modifier = Modifier

                    .height(64.dp)
                    .width(128.dp)
                    .padding(4.dp)
            ) {
                Text(text = "Apagar")
            }
        }

    }
}

@Composable
@Preview
fun CalculatorApp() {
    val focusRequester = remember { FocusRequester() }

    var displayValue by remember { mutableStateOf("") }
    var lastValue by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Pair<totalHours, minutes>
    var lastOperation by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun parseTime(input: String): Pair<Int, Int> {
        val parts = input.split(":")
        val hours = parts[0].toInt()
        val minutes = if (parts.size > 1) parts[1].toInt() else 0
        return Pair(hours, minutes)
    }

    fun formatTime(hours: Int, minutes: Int): String {
        return "%d:%02d".format(hours, minutes)
    }

    fun onButtonClick(button: String) {
        when (button) {
            "=" ->
                lastValue?.let { (hours, minutes) ->
                    when (lastOperation) {
                        "+" -> {
                            val (currentHours, currentMinutes) = parseTime(displayValue)
                            val totalMinutes = minutes + currentMinutes
                            val extraHours = totalMinutes / 60
                            val finalMinutes = totalMinutes % 60
                            val finalHours = hours + currentHours + extraHours
                            displayValue = formatTime(finalHours, finalMinutes)
                        }

                        "-" -> {
                            val (currentHours, currentMinutes) = parseTime(displayValue)
                            val totalMinutes = minutes - currentMinutes
                            val adjustedMinutes = if (totalMinutes < 0) totalMinutes + 60 else totalMinutes
                            val adjustedHours = hours - currentHours - if (totalMinutes < 0) 1 else 0
                            displayValue = formatTime(adjustedHours, adjustedMinutes)
                        }

                        "*" -> {
                            val factor = displayValue.toIntOrNull() ?: 1
                            val totalMinutes = (hours * 60 + minutes) * factor
                            val finalHours = totalMinutes / 60
                            val finalMinutes = totalMinutes % 60
                            displayValue = formatTime(finalHours, finalMinutes)
                        }
                    }
                }
            }

            "C" -> {
                displayValue = ""
                lastValue = null
                lastOperation = null
            }

            "Back" -> {
                if (displayValue.isNotEmpty()) {
                    displayValue = displayValue.dropLast(1)
                }
            }

            "+", "-", "*" -> {
                lastValue = parseTime(displayValue)
                lastOperation = button
                displayValue = ""
            }

            else -> {
                displayValue += button
            }
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        val key = keyEvent.key
                        val keyChar = when (key) {
                            Key.NumPad0, Key.Zero -> "0"
                            Key.NumPad1, Key.One -> "1"
                            Key.NumPad2, Key.Two -> "2"
                            Key.NumPad3, Key.Three -> "3"
                            Key.NumPad4, Key.Four -> "4"
                            Key.NumPad5, Key.Five -> "5"
                            Key.NumPad6, Key.Six -> "6"
                            Key.NumPad7, Key.Seven -> "7"
                            Key.NumPad8, Key.Eight -> "8"
                            Key.NumPad9, Key.Nine -> "9"
                            Key.NumPadMultiply -> "*"
                            Key.NumPadSubtract -> "-"
                            Key.NumPadAdd -> "+"
                            Key.NumPadEnter -> "="
                            Key.Enter -> "="
                            Key.Backspace -> "Back"
                            else -> null
                        }

                        keyChar?.let {
                            onButtonClick(it)
                            true
                        } ?: false
                    } else {
                        false
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                CalculatorDisplay(value = displayValue)
                CalculatorKeypad(onButtonClick = ::onButtonClick)
            }
        }
    }
}