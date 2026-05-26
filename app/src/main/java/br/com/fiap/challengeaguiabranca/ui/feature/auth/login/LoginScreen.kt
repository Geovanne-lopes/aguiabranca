package br.com.fiap.challengeaguiabranca.ui.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Brush
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
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLink
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimaryDark
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimaryLight
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: (String) -> Unit,
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
        onResetSubmit = viewModel::resetLocalPassword
    )
}

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
    onResetSubmit: () -> Unit
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InnovateBackground)
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(InnovatePrimary, InnovatePrimaryDark)
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(InnovatePrimaryLight.copy(alpha = 0.35f))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = InnovateOnPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        color = InnovateOnPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.login_welcome),
                        color = InnovateOnPrimary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-36).dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_accounts_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = InnovateTextSecondary
                    )
                    Text(
                        text = stringResource(R.string.login_password_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = InnovateTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.login_email_label)) },
                        placeholder = { Text(stringResource(R.string.login_email_hint)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = outlinedFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.login_password_label)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = outlinedFieldColors()
                    )

                    TextButton(
                        onClick = onOpenReset,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            color = InnovateLink
                        )
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    uiState.successMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color(0xFF047857),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
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

                    Spacer(modifier = Modifier.height(8.dp))
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
                            color = InnovateLink,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.login_copyright),
            color = InnovateTextSecondary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InnovateBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Voltar") }
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

@Composable
private fun AboutTopicCard(title: String, text: String, imageRes: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InnovateBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Voltar") }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                content()
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = InnovatePrimary,
    focusedLabelColor = InnovatePrimary
)
