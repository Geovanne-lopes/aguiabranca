package br.com.fiap.challengeaguiabranca.ui.feature.leader.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateLeaderPurple
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.parseAvatarColor
import br.com.fiap.challengeaguiabranca.ui.util.premiumListEntrance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

private val AVATAR_COLORS = listOf(
    "#7C3AED", "#4F1D96", "#3B82F6", "#EC4899",
    "#10B981", "#F59E0B", "#1A1A2E", "#6C5CE7"
)

@Composable
fun LeaderProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeaderProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarSelected(it.toString()) }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = InnovateLeaderPurple)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumListEntrance(0),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = InnovateLeaderPurple)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileAvatar(
                            avatarUrl = uiState.editAvatarUrl ?: uiState.avatarUrl,
                            name = uiState.editName.ifBlank { uiState.name },
                            onPickGallery = {
                                galleryLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            AVATAR_COLORS.forEach { hex ->
                                val color = parseAvatarColor("color:$hex")
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable {
                                            viewModel.onAvatarSelected("color:$hex")
                                        }
                                )
                            }
                        }

                        if (uiState.isEditing) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.editName,
                                    onValueChange = viewModel::onNameChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(R.string.profile_field_name)) },
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = uiState.editEmail,
                                    onValueChange = viewModel::onEmailChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(R.string.profile_field_email)) },
                                    singleLine = true
                                )
                            }
                        } else {
                            Text(
                                uiState.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = InnovateOnPrimary
                            )
                            Text(
                                uiState.email,
                                color = InnovateOnPrimary.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            stringResource(R.string.profile_leader_title),
                            color = InnovateOnPrimary.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumListEntrance(1),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.profile_stats_title), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatLine(
                            stringResource(R.string.leader_profile_total_ideas),
                            uiState.totalIdeas.toString()
                        )
                        StatLine(
                            stringResource(R.string.leader_profile_total_guidelines),
                            uiState.totalGuidelines.toString()
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumListEntrance(2),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Foco executivo", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Diretrizes curtas, mensuráveis e ligadas a ROI ajudam gestores e operadores a decidir melhor.",
                            color = InnovateTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                uiState.errorMessage?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error)
                }

                if (uiState.isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::cancelEditing,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isSaving
                        ) {
                            Text(stringResource(R.string.profile_cancel))
                        }
                        Button(
                            onClick = viewModel::saveProfile,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = InnovateLeaderPurple)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = InnovateOnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(stringResource(R.string.profile_save), color = InnovateOnPrimary)
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = viewModel::startEditing,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = InnovateLeaderPurple)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = InnovateOnPrimary)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(stringResource(R.string.profile_edit), color = InnovateOnPrimary)
                        }
                    }
                }

                Text(
                    stringResource(R.string.home_logout_hint),
                    color = InnovateTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(stringResource(R.string.home_logout_account), color = InnovateOnPrimary)
                }

                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = InnovateTextSecondary)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProfileAvatar(
    avatarUrl: String?,
    name: String,
    onPickGallery: () -> Unit
) {
    val context = LocalContext.current
    var galleryBitmap by remember(avatarUrl) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(avatarUrl) {
        galleryBitmap = null
        if (avatarUrl?.startsWith("content:") == true) {
            galleryBitmap = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(avatarUrl))?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    val bgColor = parseAvatarColor(avatarUrl)

    Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(onClick = onPickGallery),
            contentAlignment = Alignment.Center
        ) {
            when {
                galleryBitmap != null -> Image(
                    bitmap = galleryBitmap!!,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                else -> Text(
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = InnovateOnPrimary
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onPickGallery),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CameraAlt, null, tint = InnovateLeaderPurple, modifier = Modifier.size(18.dp))
        }
    }
}
