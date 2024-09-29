import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import calculator.*

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        title = "Calculadora de horas",
        state = WindowState(width = 300.dp, height = 575.dp),
        icon = painterResource("icon.png"),
    ) {
        CalculatorApp()
    }
}

@Composable
@Preview
fun CalculatorApp() {
    val focusRequester = remember { FocusRequester() }

    var displayValue by remember { mutableStateOf("") }
    var displayLastValue by remember { mutableStateOf("") }
    var lastValue by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Pair<totalHours, minutes>
    var lastOperation by remember { mutableStateOf<String?>(null) }
    var operationHistory by remember {
        mutableStateOf(
            listOf<String>()
        )
    }

    LaunchedEffect(displayValue) {
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
        var inputValue = ""
        when (button) {
            "=", "+", "-", "*", "/" -> {
                lastValue?.let { (hours, minutes) ->
                    if (displayValue == "") return
                    when (lastOperation) {
                        "+" -> {
                            val (currentHours, currentMinutes) = parseTime(displayValue)
                            inputValue = "$currentHours:$currentMinutes"
                            val totalMinutes = minutes + currentMinutes
                            val extraHours = totalMinutes / 60
                            val finalMinutes = totalMinutes % 60
                            val finalHours = hours + currentHours + extraHours
                            displayValue = formatTime(finalHours, finalMinutes)
                        }

                        "-" -> {
                            val (currentHours, currentMinutes) = parseTime(displayValue)
                            inputValue = "$currentHours:$currentMinutes"
                            val totalMinutes = minutes - currentMinutes
                            val adjustedMinutes = if (totalMinutes < 0) totalMinutes + 60 else totalMinutes
                            val adjustedHours = hours - currentHours - if (totalMinutes < 0) 1 else 0
                            displayValue = formatTime(adjustedHours, adjustedMinutes)
                        }

                        "*" -> {
                            val factor = displayValue.toIntOrNull() ?: 1
                            inputValue = "$factor"
                            val totalMinutes = (hours * 60 + minutes) * factor
                            val finalHours = totalMinutes / 60
                            val finalMinutes = totalMinutes % 60
                            displayValue = formatTime(finalHours, finalMinutes)
                        }

                        "/" -> {
                            val factor = displayValue.toIntOrNull() ?: 1
                            inputValue = "$factor"
                            val totalMinutes = (hours * 60 + minutes) / factor
                            val finalHours = totalMinutes / 60
                            val finalMinutes = totalMinutes % 60
                            displayValue = formatTime(finalHours, finalMinutes)
                        }
                    }

                }

                if (displayLastValue.isNotEmpty()
                    && lastOperation?.isNotEmpty() == true
                    && inputValue.isNotEmpty()
                ) {
                    operationHistory += "$displayLastValue $lastOperation $inputValue = $displayValue"
                }

                if (button != "=" && displayValue != "") {
                    displayLastValue = displayValue
                    lastValue = parseTime(displayValue)
                    lastOperation = button
                    displayValue = ""
                } else {
                    displayLastValue = ""
                    lastOperation = ""
                }

            }

            "C" -> {
                displayValue = ""
                lastValue = null
                lastOperation = null
                displayLastValue = ""
                operationHistory = listOf()
            }

            "Back" -> {
                if (displayValue.isNotEmpty()) {
                    displayValue = displayValue.dropLast(1)
                }
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
                        val keyChar = getKeyChar(keyEvent)
                        keyChar?.let {
                            onButtonClick(it)
                            true
                        } ?: false
                    } else {
                        false
                    }
                }
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFD3D3D3))
                ) {
                    if (operationHistory.isNotEmpty()) OperationHistory(messages = operationHistory)
                }

                Display(
                    value = displayValue,
                    lastValue = displayLastValue,
                    operation = lastOperation ?: ""
                )
                Keyboard(onButtonClick = ::onButtonClick)
            }

        }
    }
}
