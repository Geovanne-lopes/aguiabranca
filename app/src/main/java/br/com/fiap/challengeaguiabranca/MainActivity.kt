package br.com.fiap.challengeaguiabranca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import br.com.fiap.challengeaguiabranca.ui.InnovationApp
import br.com.fiap.challengeaguiabranca.ui.theme.ChallengeAguiaBrancaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChallengeAguiaBrancaTheme {
                InnovationApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
