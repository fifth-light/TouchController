package top.fifthlight.touchcontroller.di

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.model.TouchStateModel

val appModule = module {
    single {
        @OptIn(ExperimentalSerializationApi::class)
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
            allowComments = true
            allowTrailingComma = true
        }
    }
    single { TouchControllerConfigHolder() }
    single { ControllerHudModel() }
    single { TouchStateModel() }
}