package top.fifthlight.touchcontroller.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import top.fifthlight.combine.data.ItemFactory
import top.fifthlight.touchcontroller.ext.TouchControllerLayoutSerializer
import java.io.IOException
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class TouchControllerConfigHolder : KoinComponent {
    private val logger = LoggerFactory.getLogger(TouchControllerConfig::class.java)
    private val gameConfigEditor: GameConfigEditor by inject()
    private val itemFactory: ItemFactory = get()
    private val configDirectoryProvider: ConfigDirectoryProvider = get()
    private val configDir = configDirectoryProvider.getConfigDirectory()
    private val configFile = configDir.resolve("config.json")
    private val layoutFile = configDir.resolve("layout.json")

    private val json: Json by inject()
    private val _config = MutableStateFlow(TouchControllerConfig.default(itemFactory))
    val config = _config.asStateFlow()
    private val _layout = MutableStateFlow(defaultTouchControllerLayout)
    val layout = _layout.asStateFlow()

    fun load() {
        try {
            createConfigDirectory()
            logger.info("Reading TouchController config file")
            _config.value = json.decodeFromString(configFile.readText())
            logger.info("Reading TouchController layout file")
            _layout.value = json.decodeFromString(TouchControllerLayoutSerializer(), layoutFile.readText())
        } catch (ex: Exception) {
            logger.warn("Failed to read config: ", ex)
        }
    }

    private fun createConfigDirectory() {
        if (!configDir.exists()) {
            // Change Minecraft options
            logger.info("First startup of TouchController, turn on auto jumping")
            gameConfigEditor.enableAutoJump()
        }
        try {
            configDir.createDirectory()
        } catch (_: IOException) {
        }
    }

    fun saveConfig(config: TouchControllerConfig) {
        _config.value = config
        createConfigDirectory()
        logger.info("Saving TouchController config file")
        configFile.writeText(json.encodeToString(config))
    }

    fun saveLayout(layout: TouchControllerLayout) {
        _layout.value = layout
        createConfigDirectory()
        val serializer = TouchControllerLayoutSerializer()
        logger.info("Saving TouchController layout file")
        layoutFile.writeText(json.encodeToString(serializer, layout))
    }
}