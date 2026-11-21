package ocsigram.lite.org.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ocsigram.lite.org.R
import ocsigram.lite.org.data.auth.AuthState

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            when (val auth = state.authState) {
                AuthState.Loading -> CircularProgressIndicator()
                AuthState.WaitPhone -> OutlinedTextField(
                    value = state.phone,
                    onValueChange = viewModel::phoneChanged,
                    label = { Text(stringResource(R.string.phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.busy,
                )
                AuthState.WaitCode -> OutlinedTextField(
                    value = state.code,
                    onValueChange = viewModel::codeChanged,
                    label = { Text(stringResource(R.string.code)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.busy,
                )
                is AuthState.WaitPassword -> OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::passwordChanged,
                    label = { Text(auth.hint.ifEmpty { stringResource(R.string.password) }) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.busy,
                )
                AuthState.WaitRegistration -> {
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = viewModel::firstNameChanged,
                        label = { Text(stringResource(R.string.first_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                    )
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = viewModel::lastNameChanged,
                        label = { Text(stringResource(R.string.last_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                    )
                }
                is AuthState.Unsupported -> Text(
                    text = stringResource(R.string.unsupported_auth),
                    color = MaterialTheme.colorScheme.error,
                )
                else -> Unit
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            if (state.authState is AuthState.WaitPhone ||
                state.authState is AuthState.WaitCode ||
                state.authState is AuthState.WaitPassword ||
                state.authState is AuthState.WaitRegistration
            ) {
                Button(
                    onClick = viewModel::submit,
                    enabled = !state.busy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.busy) CircularProgressIndicator() else Text(stringResource(R.string.continue_action))
                }
            }
        }
    }
}
