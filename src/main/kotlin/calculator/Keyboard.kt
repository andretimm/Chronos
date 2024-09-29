package calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp

@Composable
fun ButtonSquare(text: String, onClick: () -> Unit) {
    val isNumber = text.toIntOrNull() != null
    val buttonColor = if (isNumber) {
        Color.LightGray
    } else {
        Color(0xFFff9900)
    }

    Button(
        onClick = {
            onClick()
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
fun ButtonLarge(text: String, onClick: () -> Unit) {
    val isNumber = text.toIntOrNull() != null
    val buttonColor = if (isNumber) {
        Color.LightGray
    } else {
        Color(0xFFff9900)
    }

    Button(
        onClick = {
            onClick()
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
fun Keyboard(onButtonClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val buttons = listOf(
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+")
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonLarge(text = "Apagar") {
                onButtonClick("Back")
            }
            ButtonSquare(text = "C") {
                onButtonClick("C")
            }
            ButtonSquare(text = "/") {
                onButtonClick("/")
            }
        }

        for (row in buttons) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (button in row) {
                    ButtonSquare(text = button) {
                        onButtonClick(button)
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonLarge(text = "0") {
                onButtonClick("0")
            }
            ButtonSquare(text = ":") {
                onButtonClick(":")
            }
            ButtonSquare(text = "=") {
                onButtonClick("=")
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