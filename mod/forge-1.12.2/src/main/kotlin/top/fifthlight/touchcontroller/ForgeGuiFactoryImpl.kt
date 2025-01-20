package top.fifthlight.touchcontroller

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement
import org.koin.core.component.KoinComponent
import top.fifthlight.touchcontroller.ui.screen.config.getConfigScreen

@Suppress("unused")
class ForgeGuiFactoryImpl : IModGuiFactory, KoinComponent {
    override fun initialize(minecraftInstance: Minecraft) {}

    override fun hasConfigGui(): Boolean = true

    override fun createConfigGui(parentScreen: GuiScreen): GuiScreen =
        getConfigScreen(parentScreen) as GuiScreen

    override fun runtimeGuiCategories() = setOf<RuntimeOptionCategoryElement>()
}