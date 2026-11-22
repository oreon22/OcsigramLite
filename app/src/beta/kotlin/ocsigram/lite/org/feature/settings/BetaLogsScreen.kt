package ocsigram.lite.org.feature.settings

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import ocsigram.lite.org.R
import ocsigram.lite.org.core.debug.FileLoggingTree
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
fun BetaLogsRoute(onBack: () -> Unit) = BetaLogsScreen(onBack)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetaLogsScreen(onBack: () -> Unit, previewLines: List<String>? = null) {
    val context = LocalContext.current
    var shareDialog by remember { mutableStateOf(false) }
    var clearDialog by remember { mutableStateOf(false) }
    val lines = previewLines ?: remember {
        FileLoggingTree.logsDirectory(context).listFiles()?.sortedBy { it.name }
            ?.flatMap { it.readLines() }?.ifEmpty { listOf("") } ?: listOf("")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.logs)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_back), stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { shareDialog = true }) {
                        Icon(painterResource(R.drawable.ic_share), stringResource(R.string.share))
                    }
                    IconButton(onClick = { clearDialog = true }) {
                        Icon(painterResource(R.drawable.ic_delete), stringResource(R.string.clear))
                    }
                },
            )
        },
    ) { padding ->
        SelectionContainer {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
            ) {
                itemsIndexed(lines, key = { index, _ -> index }) { _, line ->
                    Text(line, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
    if (shareDialog) {
        AlertDialog(
            onDismissRequest = { shareDialog = false },
            title = { Text(stringResource(R.string.confirm_share)) },
            text = { Text(stringResource(R.string.logs_warning)) },
            confirmButton = {
                TextButton(onClick = {
                    shareDialog = false
            val directory = FileLoggingTree.logsDirectory(context)
            val file = directory.listFiles()?.firstOrNull()
            if (file != null) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
                context.startActivity(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    },
                )
            }
                }) { Text(stringResource(R.string.share)) }
            },
            dismissButton = {
                TextButton(onClick = { shareDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
    if (clearDialog) {
        AlertDialog(
            onDismissRequest = { clearDialog = false },
            title = { Text(stringResource(R.string.confirm_clear)) },
            confirmButton = {
                TextButton(onClick = {
                    FileLoggingTree.clear(context)
                    clearDialog = false
                }) { Text(stringResource(R.string.clear)) }
            },
            dismissButton = {
                TextButton(onClick = { clearDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@PreviewLightDark
@Composable
private fun BetaLogsPreview() {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(false) },
    ) { BetaLogsScreen(onBack = {}, previewLines = listOf("2026-11-22 Пример лога")) }
}
