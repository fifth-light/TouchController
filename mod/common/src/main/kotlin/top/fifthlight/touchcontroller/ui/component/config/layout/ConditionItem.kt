package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.DropdownMenuBox
import top.fifthlight.combine.widget.ui.DropdownMenuIcon
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayerConditionKey
import top.fifthlight.touchcontroller.config.LayerConditionValue

private fun keyToText(key: LayerConditionKey) = when (key) {
    LayerConditionKey.SWIMMING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SWIMMING_TITLE
    LayerConditionKey.FLYING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_FLYING_TITLE
    LayerConditionKey.SNEAKING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SNEAKING_TITLE
    LayerConditionKey.SPRINTING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SPRINTING_TITLE
    LayerConditionKey.ON_GROUND -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_GROUND_TITLE
    LayerConditionKey.NOT_ON_GROUND -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_NOT_ON_GROUND_TITLE
    LayerConditionKey.USING_ITEM -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_USING_ITEM_TITLE
    LayerConditionKey.ON_MINECART -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_MINECART_TITLE
    LayerConditionKey.ON_BOAT -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_BOAT_TITLE
    LayerConditionKey.ON_PIG -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_PIG_TITLE
    LayerConditionKey.ON_HORSE -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_HORSE_TITLE
    LayerConditionKey.ON_CAMEL -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_CAMEL_TITLE
    LayerConditionKey.ON_LLAMA -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_LLAMA_TITLE
    LayerConditionKey.ON_STRIDER -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_STRIDER_TITLE
    LayerConditionKey.RIDING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_RIDING_TITLE
}

private fun valueToText(value: LayerConditionValue?) = when (value) {
    LayerConditionValue.NEVER -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_NEVER_TITLE
    LayerConditionValue.WANT -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_WANT_TITLE
    LayerConditionValue.REQUIRE -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_REQUIRE_TITLE
    null -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_IGNORE_TITLE
}

@Composable
fun ConditionItem(
    modifier: Modifier = Modifier,
    key: LayerConditionKey,
    value: LayerConditionValue? = null,
    onValueChanged: (LayerConditionValue?) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(Text.translatable(keyToText(key)))

        val valueText = valueToText(value)
        var expanded by remember { mutableStateOf(false) }
        DropdownMenuBox(
            modifier = Modifier.width(96),
            expanded = expanded,
            onExpandedChanged = { expanded = it },
            dropDownContent = { rect ->
                val values = listOf(
                    LayerConditionValue.NEVER,
                    LayerConditionValue.WANT,
                    LayerConditionValue.REQUIRE,
                    null,
                )
                Column {
                    for (showValue in values) {
                        Text(
                            modifier = Modifier
                                .padding(4)
                                .width(rect.size.width - 2)
                                .clickable {
                                    onValueChanged(showValue)
                                    expanded = false
                                },
                            text = Text.translatable(valueToText(showValue)),
                        )
                    }
                }
            }
        ) {
            Text(Text.translatable(valueText))
            Spacer(modifier = Modifier.weight(1f))
            DropdownMenuIcon(expanded)
        }
    }
}