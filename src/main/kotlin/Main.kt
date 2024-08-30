import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import calculator.CalculatorApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        title = "Calculadora de horas",
        state = WindowState(width = 300.dp, height = 475.dp),
        icon = painterResource("icon.png")
    ) {
        CalculatorApp()
    }
}
