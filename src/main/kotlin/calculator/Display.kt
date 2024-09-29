package calculator

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Display(value: String, lastValue: String, operation: String) {
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
            maxLines = 1,
            modifier = Modifier.padding(start = 1.dp)
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
