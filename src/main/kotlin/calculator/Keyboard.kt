package calculator

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    val isNumber = text.toIntOrNull() != null
    val buttonColor = if (isNumber) {
        Color.LightGray
    } else {
        Color(0xFFff9900)
    }
    val focusManager = LocalFocusManager.current

    Button(
        onClick = {
            onClick()
            focusManager.clearFocus()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),

        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun CalculatorButtonLarge(text: String, onClick: () -> Unit) {
    val isNumber = text.toIntOrNull() != null
    val buttonColor = if (isNumber) {
        Color.LightGray
    } else {
        Color(0xFFff9900)
    }
    val focusManager = LocalFocusManager.current

    Button(
        onClick = {
            onClick()
            focusManager.clearFocus()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        modifier = Modifier
            .height(64.dp)
            .width(128.dp)
            .padding(4.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun CalculatorDisplay(value: String, lastValue: String, operation: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .height(24.dp)
    ) {
        Text(
            text = lastValue,
            style = MaterialTheme.typography.h6,
            maxLines = 1
        )
        Text(
            text = operation,
            style = MaterialTheme.typography.h6,
            maxLines = 1
        )
    }
    Text(
        text = value,
        style = MaterialTheme.typography.h4,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(80.dp),
        maxLines = 2
    )
}

@Composable
fun CalculatorKeypad(onButtonClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val buttons = listOf(
            listOf("7", "8", "9", "x"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+")
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CalculatorButtonLarge(text = "Apagar") {
                onButtonClick("Back")
            }
            CalculatorButton(text = "C") {
                onButtonClick("C")
            }
            CalculatorButton(text = "/") {
                onButtonClick("/")
            }
        }

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
            CalculatorButtonLarge(text = "0") {
                onButtonClick("0")
            }
            CalculatorButton(text = ":") {
                onButtonClick(":")
            }
            CalculatorButton(text = "=") {
                onButtonClick("=")
            }
        }

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
        mutableStateOf<List<String>>(
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

                if (button != "=") {
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
                operationHistory = listOf<String>()
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

            Column() {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFD3D3D3))
                ) {
                    if (operationHistory.isNotEmpty()) MessageList(messages = operationHistory)
                }

                CalculatorDisplay(
                    value = displayValue,
                    lastValue = displayLastValue,
                    operation = lastOperation ?: ""
                )
                CalculatorKeypad(onButtonClick = ::onButtonClick)
            }

        }
    }
}

fun getKeyChar(keyEvent: KeyEvent): String? {
    return when {
        keyEvent.isShiftPressed -> {
            when (keyEvent.key) {
                Key.Equals -> "+"
                Key.Semicolon -> ":"
                Key.Eight -> "*"
                else -> null
            }
        }

        else -> when (keyEvent.key) {
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
            Key.NumPadEnter, Key.Enter -> "="
            Key.NumPadDivide -> "/"
            Key.Minus -> "-"
            Key.Plus -> "+"
            Key.Slash -> "/"
            Key.Backspace -> "Back"
            Key.Escape -> "C"
            else -> null
        }
    }
}

@Composable
fun MessageList(messages: List<String>) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp,
                    top = 8.dp
                )
        ) {
            items(messages.size) { index ->
                SelectionContainer {
                    Text(
                        text = messages[index], fontSize = 14.sp, // Tamanho da fonte ajustado
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(8.dp)
                .background(Color.LightGray),
            adapter = rememberScrollbarAdapter(scrollState = listState),
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 4.dp,
                shape = MaterialTheme.shapes.small,
                hoverDurationMillis = 300,
                unhoverColor = Color.Gray,
                hoverColor = Color.DarkGray
            )
        )
    }
}