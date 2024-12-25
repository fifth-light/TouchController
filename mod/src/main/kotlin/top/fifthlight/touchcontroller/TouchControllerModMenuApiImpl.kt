package top.fifthlight.touchcontroller

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen
import org.koin.core.component.KoinComponent
import top.fifthlight.touchcontroller.ui.screen.config.openConfigScreen

class TouchControllerModMenuApiImpl : ModMenuApi, KoinComponent {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> =
        ConfigScreenFactory<Screen> { parent -> openConfigScreen(parent) }
}