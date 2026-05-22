package br.com.fiap.challengeaguiabranca.ui.feature.manager.guidelines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.challengeaguiabranca.domain.model.StrategicGuideline
import br.com.fiap.challengeaguiabranca.domain.usecase.guideline.ObserveGuidelinesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagerGuidelinesViewModel(
    observeGuidelinesUseCase: ObserveGuidelinesUseCase
) : ViewModel() {

    private val _guidelines = MutableStateFlow<List<StrategicGuideline>>(emptyList())
    val guidelines = _guidelines.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            observeGuidelinesUseCase().collect { list ->
                _guidelines.value = list
                _isLoading.value = false
            }
        }
    }
}
