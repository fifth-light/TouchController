package top.fifthlight.combine.input.input

import androidx.compose.runtime.Immutable

@Immutable
data class TextInputState(
    val text: String = "",
    val composition: TextRange = TextRange.EMPTY,
    val selection: TextRange = TextRange(text.length),
    val selectionLeft: Boolean = true,
) {
    init {
        require(composition.end <= text.length) { "composition region end ${composition.end} should not exceed text length ${text.length}" }
        require(selection.end <= text.length) { "selection region end ${selection.end} should not exceed text length ${text.length}" }
    }

    val compositionText = text.substring(composition)
    val selectionText = text.substring(selection)

    fun dropComposition() = copy(
        composition = TextRange.EMPTY,
    )

    fun dropSelection() = copy(
        selection = TextRange.EMPTY,
    )

    fun dropRange() = copy(
        composition = TextRange.EMPTY,
        selection = TextRange.EMPTY,
    )

    fun replaceText(text: String, resetComposition: Boolean = false): TextInputState {
        val newSelectionEnd = selection.end.coerceAtMost(text.length)
        val newSelectionStart = selection.start.coerceAtMost(newSelectionEnd)
        val newSelection = TextRange(newSelectionStart, newSelectionEnd - newSelectionStart)
        return if (resetComposition) {
            copy(
                text = text.removeRange(composition),
                composition = TextRange.EMPTY,
                selection = newSelection,
            )
        } else {
            copy(
                text = text.removeRange(composition) + compositionText,
                composition = TextRange.EMPTY,
                selection = newSelection,
            )
        }
    }

    fun commitText(commitText: String): TextInputState = if (composition.length == 0) {
        // Insert at selection end
        TextInputState(
            text = text.substring(0, selection.start) + commitText + text.substring(selection.end),
            selection = TextRange(selection.start + commitText.length),
            composition = TextRange.EMPTY,
        )
    } else {
        // Insert at composition start
        TextInputState(
            text = text.substring(0, composition.start) + commitText + text.substring(composition.end),
            selection = TextRange(composition.start + commitText.length),
            composition = TextRange.EMPTY,
        )
    }

    fun removeSelection(): TextInputState = copy(
        text = text.removeRange(selection),
        selection = TextRange(selection.start),
        composition = TextRange.EMPTY,
    )

    fun doBackspace(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length != 0) {
        copy(
            text = text.removeRange(selection),
            selection = TextRange(selection.start),
            composition = TextRange.EMPTY,
        )
    } else if (selection.start > 0) {
        copy(
            text = text.removeRange(selection.start - 1, selection.start),
            selection = TextRange(selection.start - 1),
            composition = TextRange.EMPTY,
        )
    } else {
        this
    }

    fun doDelete(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length != 0) {
        copy(
            text = text.removeRange(selection),
            selection = TextRange(selection.start),
            composition = TextRange.EMPTY,
        )
    } else if (selection.end < text.length) {
        copy(
            text = text.removeRange(selection.start, selection.start + 1),
            selection = TextRange(selection.start),
            composition = TextRange.EMPTY,
        )
    } else {
        this
    }

    fun doHome(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else {
        copy(
            selection = TextRange(0),
            composition = TextRange.EMPTY,
        )
    }

    fun doEnd(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else {
        copy(
            selection = TextRange(text.length),
            composition = TextRange.EMPTY,
        )
    }

    fun doArrowLeft(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length > 0) {
        copy(
            selection = TextRange(selection.start),
            composition = TextRange.EMPTY,
        )
    } else if (selection.start > 0) {
        copy(
            selection = TextRange(selection.start - 1),
            composition = TextRange.EMPTY,
        )
    } else {
        this
    }

    fun doArrowRight(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length > 0) {
        copy(
            selection = TextRange(selection.end),
            composition = TextRange.EMPTY,
        )
    } else if (selection.end < text.length) {
        copy(
            selection = TextRange(selection.end + 1),
            composition = TextRange.EMPTY,
        )
    } else {
        this
    }

    fun doShiftLeft(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length == 0) {
        if (selection.start > 0) {
            copy(
                selection = TextRange(selection.start - 1, 1),
                composition = TextRange.EMPTY,
                selectionLeft = true,
            )
        } else {
            this
        }
    } else if (selectionLeft) {
        if (selection.start > 0) {
            copy(
                selection = TextRange(selection.start - 1, selection.length + 1),
                composition = TextRange.EMPTY,
                selectionLeft = true,
            )
        } else {
            this
        }
    } else {
        if (selection.end > 0) {
            copy(
                selection = TextRange(selection.start, selection.length - 1),
                composition = TextRange.EMPTY,
                selectionLeft = false,
            )
        } else {
            this
        }
    }

    fun doShiftRight(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selection.length == 0) {
        if (selection.end < text.length) {
            copy(
                selection = TextRange(selection.start, 1),
                composition = TextRange.EMPTY,
                selectionLeft = false,
            )
        } else {
            this
        }
    } else if (selectionLeft) {
        if (selection.start < text.length) {
            copy(
                selection = TextRange(selection.start + 1, selection.length - 1),
                composition = TextRange.EMPTY,
                selectionLeft = true,
            )
        } else {
            this
        }
    } else {
        if (selection.end < text.length) {
            copy(
                selection = TextRange(selection.start, selection.length + 1),
                composition = TextRange.EMPTY,
                selectionLeft = false,
            )
        } else {
            this
        }
    }

    fun doShiftHome(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selectionLeft) {
        copy(
            selection = TextRange(0, selection.end),
            composition = TextRange.EMPTY,
            selectionLeft = true,
        )
    } else {
        copy(
            selection = TextRange(0, selection.start),
            composition = TextRange.EMPTY,
            selectionLeft = true,
        )
    }

    fun doShiftEnd(): TextInputState = if (composition.length != 0) {
        // IME should handle this, not by us
        this
    } else if (selectionLeft) {
        copy(
            selection = TextRange(selection.end, text.length - selection.end),
            composition = TextRange.EMPTY,
            selectionLeft = false,
        )
    } else {
        copy(
            selection = TextRange(selection.start, text.length - selection.start),
            composition = TextRange.EMPTY,
            selectionLeft = false,
        )
    }
}

