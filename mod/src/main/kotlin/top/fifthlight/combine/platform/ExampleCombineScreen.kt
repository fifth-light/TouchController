package top.fifthlight.combine.platform

import net.minecraft.client.gui.screen.Screen
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.Tab
import top.fifthlight.combine.widget.ui.TabItem

class ExampleCombineScreen(parent: Screen?) : CombineScreen(net.minecraft.text.Text.empty(), parent) {
    override fun init() {
        setContent {
            Column {
                Tab(modifier = Modifier.fillMaxWidth()) {
                    TabItem("Global", selected = true)
                    TabItem("Items")
                    TabItem("Custom")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(4)
                                .weight(3f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(4)
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text("Option 1", shadow = true)
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text("Option 2", shadow = true)
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {}
                            ) {
                                Text("Option 3", shadow = true)
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(4)
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Colors.BLACK),
                            verticalArrangement = Arrangement.spacedBy(4)
                        ) {
                            Text("Description")
                            Text("Hello, world!")
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4)
                            ) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                ) {
                                    Text("Save", shadow = true)
                                }
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {}
                                ) {
                                    Text("Close", shadow = true)
                                }
                            }
                        }
                    }
                }
            }
        }
        super.init()
    }
}