package top.fifthlight.touchcontroller.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.model.LicenseScreenViewModel

@Composable
fun LicenseScreen(viewModel: LicenseScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .height(24)
                .fillMaxWidth()
                .border(bottom = 1, color = Colors.WHITE),
            alignment = Alignment.Center,
        ) {
            Text(Text.translatable(Texts.SCREEN_LICENSE_TITLE))
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(4)
                .verticalScroll(),
        ) {
            Text(uiState.licenseText)
        }

        Box(
            modifier = Modifier
                .height(32)
                .fillMaxWidth()
                .border(top = 1, color = Colors.WHITE),
            alignment = Alignment.Center,
        ) {
            val closeHandler = LocalCloseHandler.current
            Button(
                modifier = Modifier.fillMaxWidth(.25f),
                onClick = { viewModel.close(closeHandler) }
            ) {
                Text(Text.translatable(Texts.SCREEN_LICENSE_CLOSE_TITLE), shadow = true)
            }
        }
    }
}