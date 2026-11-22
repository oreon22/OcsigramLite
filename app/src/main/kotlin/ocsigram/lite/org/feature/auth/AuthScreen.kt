package ocsigram.lite.org.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ocsigram.lite.org.R
import ocsigram.lite.org.data.auth.AuthState
import ocsigram.lite.org.ui.components.PrimaryButton
import ocsigram.lite.org.ui.theme.OcsiGramTheme

@Composable
fun AuthRoute(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AuthScreen(
        state = state,
        onPhoneChanged = viewModel::phoneChanged,
        onCodeChanged = viewModel::codeChanged,
        onPasswordChanged = viewModel::passwordChanged,
        onFirstNameChanged = viewModel::firstNameChanged,
        onLastNameChanged = viewModel::lastNameChanged,
        onSubmit = viewModel::submit,
    )
}

@Composable
fun AuthScreen(
    state: AuthUiState,
    onPhoneChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val stepKey = when (state.authState) {
                    AuthState.WaitPhone -> "phone"
                    AuthState.WaitCode -> "code"
                    is AuthState.WaitPassword -> "password"
                    AuthState.WaitRegistration -> "registration"
                    is AuthState.Unsupported -> "unsupported"
                    else -> "loading"
                }
                AnimatedContent(
                    targetState = stepKey,
                    transitionSpec = {
                        (slideInHorizontally { it / 3 } + fadeIn()).togetherWith(
                            slideOutHorizontally { -it / 3 } + fadeOut(),
                        )
                    },
                    label = "authStep",
                ) { step ->
                    when (step) {
                        "phone" -> PhoneStep(state, onPhoneChanged, onSubmit)
                        "code" -> CodeStep(state, onCodeChanged, onSubmit)
                        "password" -> PasswordStep(
                            state,
                            (state.authState as AuthState.WaitPassword).hint,
                            onPasswordChanged,
                            onSubmit,
                        )
                        "registration" -> RegistrationStep(
                            state,
                            onFirstNameChanged,
                            onLastNameChanged,
                            onSubmit,
                        )
                        "unsupported" -> UnsupportedStep(onSubmit)
                        else -> Box(Modifier.fillMaxWidth().padding(vertical = 48.dp)) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthHeader(title: String, body: String) {
    Card(
        modifier = Modifier.size(72.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_ocsi_ring),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
        }
    }
    Text(
        text = title,
        modifier = Modifier.padding(top = 16.dp),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
    )
    Text(
        text = body,
        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun PhoneStep(state: AuthUiState, onChange: (String) -> Unit, onSubmit: () -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    AuthHeader(stringResource(R.string.auth_phone_title), stringResource(R.string.auth_phone_body))
    OutlinedTextField(
        value = state.phone,
        onValueChange = onChange,
        label = { Text(stringResource(R.string.phone)) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_phone), contentDescription = null) },
        supportingText = { Text(stringResource(R.string.auth_phone_support)) },
        isError = state.error != null,
        enabled = !state.busy,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); onSubmit() }),
        modifier = Modifier.fillMaxWidth(),
    )
    PrimaryButton(stringResource(R.string.continue_action), onSubmit, Modifier.padding(top = 24.dp), state.busy)
}

@Composable
private fun CodeStep(state: AuthUiState, onChange: (String) -> Unit, onSubmit: () -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    AuthHeader(stringResource(R.string.auth_code_title), stringResource(R.string.auth_code_body))
    OutlinedTextField(
        value = state.code,
        onValueChange = onChange,
        label = { Text(stringResource(R.string.code)) },
        supportingText = { Text(stringResource(R.string.auth_code_support)) },
        enabled = !state.busy,
        isError = state.error != null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); onSubmit() }),
        modifier = Modifier.fillMaxWidth(),
    )
    PrimaryButton(stringResource(R.string.continue_action), onSubmit, Modifier.padding(top = 24.dp), state.busy)
}

@Composable
private fun PasswordStep(state: AuthUiState, hint: String, onChange: (String) -> Unit, onSubmit: () -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    var visible by rememberSaveable { mutableStateOf(false) }
    AuthHeader(stringResource(R.string.auth_password_title), stringResource(R.string.auth_password_body))
    OutlinedTextField(
        value = state.password,
        onValueChange = onChange,
        label = { Text(stringResource(R.string.password)) },
        supportingText = { Text(hint.ifBlank { stringResource(R.string.auth_password_support) }) },
        enabled = !state.busy,
        isError = state.error != null,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    painter = painterResource(if (visible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                    contentDescription = stringResource(if (visible) R.string.hide_password else R.string.show_password),
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); onSubmit() }),
        modifier = Modifier.fillMaxWidth(),
    )
    PrimaryButton(stringResource(R.string.continue_action), onSubmit, Modifier.padding(top = 24.dp), state.busy)
}

@Composable
private fun RegistrationStep(
    state: AuthUiState,
    onFirstName: (String) -> Unit,
    onLastName: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    AuthHeader(stringResource(R.string.auth_registration_title), stringResource(R.string.auth_registration_body))
    OutlinedTextField(
        value = state.firstName,
        onValueChange = onFirstName,
        label = { Text(stringResource(R.string.first_name)) },
        enabled = !state.busy,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = state.lastName,
        onValueChange = onLastName,
        label = { Text(stringResource(R.string.last_name)) },
        enabled = !state.busy,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); onSubmit() }),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
    )
    PrimaryButton(stringResource(R.string.continue_action), onSubmit, Modifier.padding(top = 24.dp), state.busy)
}

@Composable
private fun UnsupportedStep(onRetry: () -> Unit) {
    AuthHeader(stringResource(R.string.unsupported_auth), "")
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.unsupported_auth),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    PrimaryButton(stringResource(R.string.retry), onRetry, Modifier.padding(top = 24.dp))
}

@PreviewLightDark
@Composable
private fun AuthPhonePreview() {
    OcsiGramTheme(darkTheme = remember { mutableStateOf(false) }, dynamicColor = remember { mutableStateOf(false) }) {
        AuthScreen(AuthUiState(authState = AuthState.WaitPhone), {}, {}, {}, {}, {}, {})
    }
}
