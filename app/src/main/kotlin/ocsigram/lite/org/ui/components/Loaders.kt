package ocsigram.lite.org.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonLoader(modifier: Modifier = Modifier) {
    CircularWavyProgressIndicator(
        modifier = modifier.size(28.dp),
        color = androidx.compose.material3.LocalContentColor.current,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScreenLoader(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularWavyProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FullScreenLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ScreenLoader()
    }
}

@PreviewLightDark
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadersPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) {
        ScreenLoader(modifier = Modifier.padding(32.dp).size(64.dp))
    }
}
