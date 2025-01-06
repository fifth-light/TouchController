package top.fifthlight.touchcontroller.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.ext.ControllerLayoutSerializer
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider
import java.io.IOException
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class GlobalConfigHolder : KoinComponent {
    private val logger = LoggerFactory.getLogger(GlobalConfig::class.java)
    private val gameConfigEditor: GameConfigEditor by inject()
    private val defaultItemListProvider: DefaultItemListProvider = get()
    private val configDirectoryProvider: ConfigDirectoryProvider = get()
    private val configDir = configDirectoryProvider.getConfigDirectory()
    private val configFile = configDir.resolve("config.json")
    private val layoutFile = configDir.resolve("layout.json")
    private val presetFile = configDir.resolve("preset.json")

    private val json: Json by inject()
    private val _config = MutableStateFlow(GlobalConfig.default(defaultItemListProvider))
    val config = _config.asStateFlow()
    private val _layout = MutableStateFlow(defaultControllerLayout)
    val layout = _layout.asStateFlow()
    private val _presets = MutableStateFlow(LayoutPresets())
    val presets = _presets.asStateFlow()

    fun load() {
        try {
            createConfigDirectory()
            logger.info("Reading TouchController config file")
            _config.value = json.decodeFromString(configFile.readText())
            logger.info("Reading TouchController layout file")
            _layout.value = json.decodeFromString(ControllerLayoutSerializer(), layoutFile.readText())
            logger.info("Reading TouchController preset file")
            _presets.value = json.decodeFromString(presetFile.readText())
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

    fun saveConfig(config: GlobalConfig) {
        _config.value = config
        createConfigDirectory()
        logger.info("Saving TouchController config file")
        configFile.writeText(json.encodeToString(config))
    }

    fun saveLayout(layout: ControllerLayout) {
        _layout.value = layout
        createConfigDirectory()
        val serializer = ControllerLayoutSerializer()
        logger.info("Saving TouchController layout file")
        layoutFile.writeText(json.encodeToString(serializer, layout))
    }

    fun savePreset(config: LayoutPresets) {
        _presets.value = config
        createConfigDirectory()
        logger.info("Saving TouchController preset file")
        presetFile.writeText(json.encodeToString(config))
    }
}