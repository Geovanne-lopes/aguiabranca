package br.com.fiap.challengeaguiabranca.ui.feature.shared.collaborators

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.domain.catalog.MockOperatorsCatalog
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateBackground
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateOnPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary
import br.com.fiap.challengeaguiabranca.ui.util.premiumListEntrance

private data class Collaborator(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val ideas: Int
)

private data class ChatMessage(
    val fromMe: Boolean,
    val text: String
)

@Composable
fun CollaboratorsChatScreen(
    currentRole: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val collaborators = remember(currentRole) {
        buildCollaborators(currentRole)
    }
    var selected by remember { mutableStateOf<Collaborator?>(null) }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    Surface(modifier = modifier.fillMaxSize(), color = InnovateBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (selected == null) onBack() else selected = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
                Column {
                    Text(
                        text = selected?.name ?: "Colaboradores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selected?.let { "${it.role} · ${it.email}" }
                            ?: "Pessoas, ideias e chat 1:1",
                        color = InnovateTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (selected == null) {
                CollaboratorsList(
                    collaborators = collaborators,
                    onSelect = {
                        selected = it
                        messages.clear()
                        messages.add(ChatMessage(false, "Oi! Pode me chamar por aqui sobre ideias e orientações."))
                    }
                )
            } else {
                ChatDetail(
                    collaborator = selected!!,
                    messages = messages,
                    onSend = { text ->
                        messages.add(ChatMessage(true, text))
                        messages.add(ChatMessage(false, cannedReply(text)))
                    }
                )
            }
        }
    }
}

@Composable
private fun CollaboratorsList(
    collaborators: List<Collaborator>,
    onSelect: (Collaborator) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .premiumListEntrance(0),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, null, tint = InnovatePrimary)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Time conectado", fontWeight = FontWeight.Bold)
                        Text(
                            "Abra o perfil, veja ideias e converse sem sair do app.",
                            color = InnovateTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        itemsIndexed(collaborators, key = { _, item -> item.id }) { index, collaborator ->
            CollaboratorCard(
                collaborator = collaborator,
                onClick = { onSelect(collaborator) },
                modifier = Modifier.premiumListEntrance(index + 1)
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun CollaboratorCard(
    collaborator: Collaborator,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarBubble(name = collaborator.name)
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(collaborator.name, fontWeight = FontWeight.Bold)
                Text(collaborator.role, color = InnovatePrimary, style = MaterialTheme.typography.labelMedium)
                Text(collaborator.email, color = InnovateTextSecondary, style = MaterialTheme.typography.bodySmall)
                Text(
                    "${collaborator.ideas} ideia(s) registradas",
                    color = InnovateTextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(Icons.Default.Chat, null, tint = InnovatePrimary)
        }
    }
}

@Composable
private fun ChatDetail(
    collaborator: Collaborator,
    messages: List<ChatMessage>,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .premiumListEntrance(0),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AvatarBubble(name = collaborator.name)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(collaborator.name, fontWeight = FontWeight.Bold)
                    Text("${collaborator.ideas} ideias enviadas", color = InnovateTextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, null, tint = InnovatePrimary, modifier = Modifier.size(16.dp))
                        Text(" Perfil de inovação ativo", color = InnovateTextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(messages) { index, message ->
                MessageBubble(message = message, modifier = Modifier.premiumListEntrance(index))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escreva uma mensagem...") },
                singleLine = true
            )
            Button(
                onClick = {
                    val trimmed = text.trim()
                    if (trimmed.isNotEmpty()) {
                        onSend(trimmed)
                        text = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = InnovatePrimary)
            ) {
                Icon(Icons.Default.Send, null, tint = InnovateOnPrimary)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(if (message.fromMe) InnovatePrimary else Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                message.text,
                color = if (message.fromMe) InnovateOnPrimary else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AvatarBubble(name: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(InnovatePrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            color = InnovateOnPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun buildCollaborators(currentRole: String): List<Collaborator> {
    val activeProfiles = listOf(
        Collaborator("demo-operador", "Ana Silva", "operador@innovatecorp.com", "Operador", 2),
        Collaborator("demo-gestor", "Carlos Mendes", "gestor@innovatecorp.com", "Gestor", 4),
        Collaborator("demo-lider", "Mariana Costa", "lideranca@innovatecorp.com", "Líder", 6)
    ).filterNot { it.role.equals(currentRole, ignoreCase = true) }

    val operators = MockOperatorsCatalog.operators
        .filterNot { it.id == "demo-operador" }
        .mapIndexed { index, op ->
            Collaborator(op.id, op.name, op.email, "Operador", 2 + index)
        }
    return activeProfiles + operators
}

private fun cannedReply(text: String): String =
    when {
        text.contains("ideia", ignoreCase = true) -> "Boa! Vou detalhar essa ideia e te retorno ainda hoje."
        text.contains("projeto", ignoreCase = true) -> "Combinado, vou olhar o projeto e separar os pontos principais."
        else -> "Recebi sua mensagem. Vou alinhar com o time e retorno por aqui."
    }
