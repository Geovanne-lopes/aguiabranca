package br.com.fiap.challengeaguiabranca.ui.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.UserRole
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateCard
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLogoBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLogoForeground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateScreenBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateShapes
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateSuccess
import br.com.fiap.challengeaguiabranca.ui.theme.currentRoleAccent
import br.com.fiap.challengeaguiabranca.ui.theme.innovateBorderColor
import br.com.fiap.challengeaguiabranca.ui.theme.innovateLinkColor
import br.com.fiap.challengeaguiabranca.ui.theme.innovateSurface2Color
import br.com.fiap.challengeaguiabranca.ui.theme.innovateSurfaceColor
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateError
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: (String) -> Unit,
    onToggleTheme: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome(event.route)
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onOpenAbout = viewModel::openAbout,
        onOpenRegister = viewModel::openRegister,
        onOpenReset = viewModel::openReset,
        onBackToLogin = viewModel::backToLogin,
        onRegisterNameChange = viewModel::onRegisterNameChange,
        onRegisterEmailChange = viewModel::onRegisterEmailChange,
        onRegisterPasswordChange = viewModel::onRegisterPasswordChange,
        onRegisterRoleChange = viewModel::onRegisterRoleChange,
        onRegisterSubmit = viewModel::registerLocalAccount,
        onResetEmailChange = viewModel::onResetEmailChange,
        onResetPasswordChange = viewModel::onResetPasswordChange,
        onResetSubmit = viewModel::resetLocalPassword,
        onApplyDemo = viewModel::applyDemoAccount,
        onToggleTheme = onToggleTheme
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenRegister: () -> Unit,
    onOpenReset: () -> Unit,
    onBackToLogin: () -> Unit,
    onRegisterNameChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onRegisterRoleChange: (UserRole) -> Unit,
    onRegisterSubmit: () -> Unit,
    onResetEmailChange: (String) -> Unit,
    onResetPasswordChange: (String) -> Unit,
    onResetSubmit: () -> Unit,
    onApplyDemo: (UserRole) -> Unit,
    onToggleTheme: () -> Unit
) {
    when (uiState.mode) {
        LoginMode.ABOUT -> AboutInnovateScreen(onBack = onBackToLogin, onRegister = onOpenRegister)
        LoginMode.REGISTER -> RegisterScreen(
            uiState = uiState,
            onBack = onOpenAbout,
            onNameChange = onRegisterNameChange,
            onEmailChange = onRegisterEmailChange,
            onPasswordChange = onRegisterPasswordChange,
            onRoleChange = onRegisterRoleChange,
            onSubmit = onRegisterSubmit
        )
        LoginMode.RESET -> ResetPasswordScreen(
            uiState = uiState,
            onBack = onBackToLogin,
            onEmailChange = onResetEmailChange,
            onPasswordChange = onResetPasswordChange,
            onSubmit = onResetSubmit
        )
        LoginMode.LOGIN -> Unit
    }
    if (uiState.mode != LoginMode.LOGIN) return

    val accent = currentRoleAccent()

    InnovateScreenBackground(
        modifier = Modifier.imePadding()
    ) {
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 20.dp)
                .size(36.dp)
                .border(1.dp, innovateBorderColor(), InnovateShapes.Small)
                .background(innovateSurfaceColor(), InnovateShapes.Small)
        ) {
            Text("◐", fontSize = 17.sp, color = InnovateTextPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(InnovateShapes.Medium)
                        .background(InnovateLogoBackground)
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = InnovateLogoForeground
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    color = InnovateTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = stringResource(R.string.login_tagline),
                    color = InnovateTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            InnovateCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.login_demo_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = InnovateTextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(innovateSurface2Color(), InnovateShapes.Small)
                            .border(1.dp, InnovatePrimary.copy(alpha = 0.1f), InnovateShapes.Small)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DemoChip("Operador") { onApplyDemo(UserRole.OPERATOR) }
                        DemoChip("Gestor") { onApplyDemo(UserRole.MANAGER) }
                        DemoChip("Líder") { onApplyDemo(UserRole.LEADER) }
                    }

                    LoginField(
                        label = stringResource(R.string.login_email_label),
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.login_email_hint),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardType = KeyboardType.Email
                    )
                    LoginField(
                        label = stringResource(R.string.login_password_label),
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    TextButton(
                        onClick = onOpenReset,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            color = innovateLinkColor(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = InnovateError,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF2F2), InnovateShapes.Small)
                                .border(1.dp, InnovateError.copy(alpha = 0.18f), InnovateShapes.Small)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }

                    uiState.successMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color(0xFF047857),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFECFDF5), InnovateShapes.Small)
                                .border(1.dp, InnovateSuccess.copy(alpha = 0.2f), InnovateShapes.Small)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading,
                        shape = InnovateShapes.Small,
                        colors = ButtonDefaults.buttonColors(containerColor = accent.accent)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = InnovateOnPrimary,
                                modifier = Modifier.height(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.login_button),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.login_first_time),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = InnovateTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(
                        onClick = onOpenAbout,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = stringResource(R.string.login_know_brand),
                            color = innovateLinkColor(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.login_copyright),
                color = InnovateTextSecondary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun DemoChip(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .clip(InnovateShapes.Pill)
            .border(1.dp, innovateBorderColor(), InnovateShapes.Pill)
            .background(innovateSurfaceColor(), InnovateShapes.Pill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = InnovateTextPrimary
    )
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = InnovateTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder) }
            } else {
                null
            },
            leadingIcon = leadingIcon,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                androidx.compose.ui.text.input.VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = InnovateShapes.Small,
            colors = outlinedFieldColors()
        )
    }
}

@Composable
private fun AboutInnovateScreen(onBack: () -> Unit, onRegister: () -> Unit) {
    val sections = listOf(
        Triple("🛣️ Nossa trajetória", "A InnovateCorp nasceu para transformar ideias do cotidiano em melhorias reais. Cada sugestão entra em uma trilha clara: registro, curadoria, projeto e aprendizado.", R.drawable.about_team_whiteboard),
        Triple("🧩 O que fazemos", "Conectamos operadores, gestores e liderança em um workspace de ideias, orientações, projetos, ranking, colaboradores e chat.", R.drawable.about_team_workshop),
        Triple("💬 Feedbacks", "Nada se perde no caminho. O operador acompanha a ideia, o gestor orienta e a liderança cria diretrizes para manter todos alinhados.", R.drawable.about_team_feedback)
    )
    InnovateScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        TextButton(onClick = onBack) {
            Text("← Voltar", color = innovateLinkColor(), fontWeight = FontWeight.Bold)
        }
        Text("⚡ InnovateCorp", color = InnovateTextSecondary, fontWeight = FontWeight.Bold)
        Text(
            "Um lugar para ideias saírem do corredor e virarem projeto.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Conheça o fluxo por trás do app: pessoas, contexto e feedback no mesmo workspace.",
            color = InnovateTextSecondary
        )
        sections.forEach { section ->
            AboutTopicCard(section.first, section.second, section.third)
        }
        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
        ) {
            Text("Criar minha conta", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutTopicCard(title: String, text: String, imageRes: Int) {
    InnovateCard {
        Column {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(text, color = InnovateTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun RegisterScreen(
    uiState: LoginUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onSubmit: () -> Unit
) {
    AuthSimpleScaffold(title = "🪪 Criar cadastro", onBack = onBack) {
        Text("Cadastro local, sem SMTP. Depois entre com este e-mail e senha.", color = InnovateTextSecondary)
        OutlinedTextField(uiState.registerName, onNameChange, Modifier.fillMaxWidth(), label = { Text("Nome completo") }, leadingIcon = { Icon(Icons.Default.Person, null) })
        OutlinedTextField(uiState.registerEmail, onEmailChange, Modifier.fillMaxWidth(), label = { Text("E-mail") }, leadingIcon = { Icon(Icons.Default.Email, null) })
        OutlinedTextField(uiState.registerPassword, onPasswordChange, Modifier.fillMaxWidth(), label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, null) })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(UserRole.OPERATOR to "Operador", UserRole.MANAGER to "Gestor", UserRole.LEADER to "Líder").forEach { (role, label) ->
                Text(
                    text = label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (uiState.registerRole == role) InnovatePrimary else Color(0xFFF3F4F6))
                        .clickable { onRoleChange(role) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (uiState.registerRole == role) InnovateOnPrimary else Color.Black
                )
            }
        }
        uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)) {
            Text("Cadastrar e voltar ao login")
        }
    }
}

@Composable
private fun ResetPasswordScreen(
    uiState: LoginUiState,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AuthSimpleScaffold(title = "🔐 Alterar senha", onBack = onBack) {
        Text("Sem envio de e-mail: informe o e-mail cadastrado e escolha uma nova senha localmente.", color = InnovateTextSecondary)
        Text("E-mail para recuperação: ${uiState.resetEmail.ifBlank { "preencha abaixo" }}", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(uiState.resetEmail, onEmailChange, Modifier.fillMaxWidth(), label = { Text("E-mail cadastrado") }, leadingIcon = { Icon(Icons.Default.Email, null) })
        OutlinedTextField(uiState.resetPassword, onPasswordChange, Modifier.fillMaxWidth(), label = { Text("Nova senha") }, visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, null) })
        uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)) {
            Text("Salvar nova senha")
        }
    }
}

@Composable
private fun AuthSimpleScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable Column.() -> Unit
) {
    InnovateScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Voltar", color = innovateLinkColor(), fontWeight = FontWeight.Bold)
            }
            InnovateCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    content()
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = currentRoleAccent().accent,
    focusedLabelColor = currentRoleAccent().accent,
    unfocusedBorderColor = innovateBorderColor()
)
