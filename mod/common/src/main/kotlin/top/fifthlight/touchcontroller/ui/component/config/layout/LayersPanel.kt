package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.ControllerLayout
import top.fifthlight.touchcontroller.config.LayerConditionKey
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.gal.GameFeatures

private object ConditionsList : KoinComponent {
    private val gameFeatures: GameFeatures by inject()

    val conditionsList by lazy {
        listOfNotNull(
            LayerConditionKey.SWIMMING,
            LayerConditionKey.UNDERWATER,
            LayerConditionKey.FLYING,
            LayerConditionKey.CAN_FLY,
            LayerConditionKey.SNEAKING,
            LayerConditionKey.SPRINTING,
            LayerConditionKey.ON_GROUND,
            LayerConditionKey.NOT_ON_GROUND,
            LayerConditionKey.USING_ITEM,
            LayerConditionKey.ON_MINECART,
            LayerConditionKey.ON_BOAT,
            LayerConditionKey.ON_PIG,
            LayerConditionKey.ON_HORSE,
            LayerConditionKey.ON_CAMEL.takeIf { gameFeatures.entity.haveCamel },
            LayerConditionKey.ON_LLAMA.takeIf { gameFeatures.entity.haveLlama },
            LayerConditionKey.ON_STRIDER.takeIf { gameFeatures.entity.haveStrider },
            LayerConditionKey.RIDING,
        )
    }
}

@Composable
fun LayersPanel(
    modifier: Modifier = Modifier,
    currentLayer: Pair<Int, LayoutLayer>? = null,
    layout: ControllerLayout = ControllerLayout(),
    onLayerSelected: (Int) -> Unit = {},
    onLayerChanged: (Int, LayoutLayer) -> Unit = { _, _ -> },
    onLayerRemoved: (Int, LayoutLayer) -> Unit = { _, _ -> },
    onLayerAdded: (LayoutLayer) -> Unit = {},
) {
    Row(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .width(128)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll()
                    .weight(1f),
            ) {
                for ((index, layer) in layout.layers.withIndex()) {
                    val soundManager = LocalSoundManager.current
                    if (currentLayer?.first == index) {
                        Text(
                            modifier = Modifier
                                .padding(8)
                                .fillMaxWidth()
                                .background(color = Colors.WHITE)
                                .border(bottom = 1, color = Colors.WHITE)
                                .clickable {
                                    soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                },
                            text = layer.name,
                            color = Colors.BLACK
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(8)
                                .fillMaxWidth()
                                .border(bottom = 1, color = Colors.WHITE)
                                .clickable {
                                    soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                    onLayerSelected(index)
                                },
                            text = layer.name,
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onLayerAdded(LayoutLayer())
                },
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_NEW_TITLE), shadow = true)
            }
            if (currentLayer != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLayerAdded(currentLayer.second)
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_COPY_TITLE), shadow = true)
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLayerRemoved(currentLayer.first, currentLayer.second)
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_REMOVE_TITLE), shadow = true)
                }
            }
        }

        Spacer(
            modifier = Modifier
                .width(1)
                .fillMaxHeight()
                .background(Colors.WHITE)
        )

        if (currentLayer == null) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                alignment = Alignment.Center
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_SELECT_A_LAYER_TITLE))
            }
        } else {
            val (index, layer) = currentLayer
            Column(
                modifier = Modifier
                    .padding(8)
                    .weight(1f)
                    .verticalScroll(),
                verticalArrangement = Arrangement.spacedBy(8),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_NAME_TITLE))
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = layer.name,
                        onValueChanged = {
                            onLayerChanged(index, layer.copy(name = it))
                        }
                    )
                }

                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TITLE))

                val texts = listOf(
                    Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TIP_IGNORE),
                    Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TIP_NEVER),
                    Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TIP_WANT),
                    Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TIP_REQUIRE),
                )
                Text(Text.build {
                    texts.forEachIndexed { index, text ->
                        appendWithoutStyle(text)
                        if (index != texts.size - 1) {
                            append("\n")
                        }
                    }
                })

                for (condition in ConditionsList.conditionsList) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8)
                    ) {
                        ConditionItem(
                            modifier = Modifier.fillMaxWidth(),
                            key = condition,
                            value = layer.condition[condition],
                            onValueChanged = { newValue ->
                                onLayerChanged(
                                    index, layer.copy(
                                        condition = layer.condition.set(condition, newValue)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}