package ocsigram.lite.org.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ocsigram.lite.org.R
import ocsigram.lite.org.ui.components.ButtonLoader
import ocsigram.lite.org.ui.components.PalettePreview
import ocsigram.lite.org.ui.components.PrimaryButton
import ocsigram.lite.org.ui.components.ScreenLoader
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ComponentsGallery(dynamic: Boolean) {
    OcsiGramTheme(
        darkTheme = remember { mutableStateOf(false) },
        dynamicColor = remember { mutableStateOf(dynamic) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PrimaryButton(text = stringResource(R.string.continue_action), onClick = {})
            PrimaryButton(text = stringResource(R.string.disabled_preview), onClick = {}, enabled = false)
            PrimaryButton(text = stringResource(R.string.loading_preview), onClick = {}, isLoading = true)
            PrimaryButton(
                text = stringResource(R.string.with_icon_preview),
                onClick = {},
                leadingIcon = painterResource(R.drawable.ic_phone),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {}) { Text(stringResource(R.string.cancel)) }
                IconButton(onClick = {}) {
                    Icon(painterResource(R.drawable.ic_settings), contentDescription = stringResource(R.string.open_settings))
                }
            }
            ButtonLoader()
            ScreenLoader(modifier = Modifier.padding(8.dp))
            OutlinedTextField(value = stringResource(R.string.phone_preview_value), onValueChange = {}, label = { Text(stringResource(R.string.phone)) })
            OutlinedTextField(
                value = stringResource(R.string.invalid_code_preview),
                onValueChange = {},
                isError = true,
                label = { Text(stringResource(R.string.code)) },
                supportingText = { Text(stringResource(R.string.invalid_code_preview)) },
            )
            Snackbar(modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.signed_in)) }
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionContentColor = MaterialTheme.colorScheme.onErrorContainer,
                dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
                action = { TextButton(onClick = {}) { Text(stringResource(R.string.copy_log)) } },
                dismissAction = {
                    TextButton(onClick = {}) { Text(stringResource(R.string.cancel)) }
                },
            ) { Text(stringResource(R.string.error_default)) }
            PalettePreview()
        }
    }
}

@PreviewLightDark
@Composable
private fun ComponentsGalleryStaticPreview() {
    ComponentsGallery(dynamic = false)
}

@PreviewDynamicColors(
)
@Composable
private fun ComponentsGalleryDynamicPreview() {
    ComponentsGallery(dynamic = true)
}
