package ocsigram.lite.org.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.R
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: Painter? = null,
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(56.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.alpha(if (isLoading) 0f else 1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    Icon(painter = leadingIcon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = text, textAlign = TextAlign.Center)
            }
            if (isLoading) {
                ButtonLoader()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PrimaryButtonPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PrimaryButton("Обычная", {})
                PrimaryButton("Отключена", {}, enabled = false)
                PrimaryButton("Загрузка", {}, isLoading = true)
                PrimaryButton(
                    "С иконкой",
                    {},
                    leadingIcon = painterResource(R.drawable.ic_phone),
                )
            }
        }
    }
}
