package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import top.fifthlight.combine.data.LocalTextFactory
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.screen.LocalScreenFactory
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.BuildInfo
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.screen.openLicenseScreen

data object AboutCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_TITLE,
    content = { modifier, viewModel ->
        val screenFactory = LocalScreenFactory.current
        val textFactory = LocalTextFactory.current
        fun showLicense(licenseText: String) {
            openLicenseScreen(
                screenFactory = screenFactory,
                textFactory = textFactory,
                licenseText = licenseText,
            )
        }

        Column(
            modifier = modifier
                .padding(8)
                .verticalScroll(),
            verticalArrangement = Arrangement.spacedBy(4)
        ) {
            val uiState by viewModel.uiState.collectAsState()
            Row(horizontalArrangement = Arrangement.spacedBy(4)) {
                Text(Text.literal(BuildInfo.MOD_NAME).bold())
                Text(BuildInfo.MOD_VERSION)
            }
            Text(BuildInfo.MOD_DESCRIPTION)

            Spacer()

            Row {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_AUTHORS_TITLE))
                Text(BuildInfo.MOD_AUTHORS)
            }
            Row {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_CONTRIBUTORS_TITLE))
                Text(BuildInfo.MOD_CONTRIBUTORS)
            }

            Spacer()

            val aboutInfo = uiState.aboutInfo
            if (aboutInfo != null) {
                val modLicense = aboutInfo.modLicense
                if (modLicense != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_LICENSE_TITLE))
                        Text(BuildInfo.MOD_LICENSE)
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { showLicense(modLicense) }) {
                            Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_SHOW_TITLE), shadow = true)
                        }
                    }
                }

                Spacer()

                val libraries = aboutInfo.libraries
                if (libraries != null) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ABOUT_LIBRARIES_TITLE))
                    for (library in libraries.libraries) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4)
                                .border(size = 1, color = Colors.WHITE),
                            verticalArrangement = Arrangement.spacedBy(4),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(library.name)
                                library.artifactVersion?.let { version ->
                                    Text(version, color = Colors.ALTERNATE_WHITE)
                                } ?: run {
                                    Text("Unknown version", color = Colors.ALTERNATE_WHITE)
                                }
                            }
                            Text(library.uniqueId, color = Colors.ALTERNATE_WHITE)
                            Row(horizontalArrangement = Arrangement.spacedBy(4)) {
                                for (developer in library.developers) {
                                    developer.name?.let { name ->
                                        Text(
                                            text = name,
                                            color = Colors.ALTERNATE_WHITE
                                        )
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4, Alignment.Right)) {
                                for (license in library.licenses) {
                                    license.licenseContent?.let { content ->
                                        Text(
                                            modifier = Modifier.clickable { showLicense(content) },
                                            text = license.name,
                                            color = Colors.BLUE,
                                        )
                                    } ?: run {
                                        Text(license.name)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Loading")
            }
        }
    }
)