package ocsigram.lite.org.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.R
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
fun PalettePreview(modifier: Modifier = Modifier) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )
    val description = stringResource(R.string.palette_preview)
    Column(modifier = modifier.semantics { contentDescription = description }) {
        Text(
            text = description,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            colors.forEach { color ->
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = color,
                ) {}
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PalettePreviewPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) {
        PalettePreview(modifier = Modifier.padding(16.dp))
    }
}
