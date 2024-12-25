package top.fifthlight.touchcontroller.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.ui.state.ConfigScreenState
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory
import top.fifthlight.touchcontroller.ui.view.config.category.GlobalCategory

class ConfigScreenViewModel(scope: CoroutineScope) : ViewModel(scope), KoinComponent {
    private val configHolder: TouchControllerConfigHolder by inject()
    private val client: MinecraftClient by inject()

    private val _uiState = MutableStateFlow(ConfigScreenState(
        config = configHolder.config.value,
        selectedCategory = GlobalCategory
    ))
    val uiState = _uiState.asStateFlow()

    fun selectCategory(category: ConfigCategory) {
        _uiState.getAndUpdate {
            it.copy(
                selectedCategory = category
            )
        }
    }

    fun updateConfig(update: TouchControllerConfig.() -> TouchControllerConfig) {
        _uiState.getAndUpdate {
            it.copy(
                config = update(it.config)
            )
        }
    }

    fun reset() {
        _uiState.getAndUpdate {
            it.copy(
                config = configHolder.config.value
            )
        }
    }

    fun exit(closeHandler: CloseHandler) {
        closeHandler.close()
    }

    fun saveAndExit(closeHandler: CloseHandler) {
        configHolder.saveConfig(uiState.value.config)
        closeHandler.close()
    }
}