package ocsigram.lite.org.feature.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import ocsigram.lite.org.R
import ocsigram.lite.org.core.debug.FileLoggingTree

@Composable
fun BetaLogsScreen() {
    val context = LocalContext.current
    Column(Modifier.padding(16.dp)) {
        Text(context.getString(R.string.logs_warning))
        Button(onClick = {
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
        }) { Text(context.getString(R.string.share_logs)) }
        Button(onClick = { FileLoggingTree.clear(context) }) {
            Text(context.getString(R.string.clear_logs))
        }
    }
}
