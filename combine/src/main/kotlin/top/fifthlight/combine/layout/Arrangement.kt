package top.fifthlight.combine.layout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.math.min

@Immutable
object Arrangement {
    @Stable
    interface Horizontal {
        val spacing: Int
            get() = 0

        fun arrangeHorizontally(
            totalSize: Int,
            sizes: IntArray,
            outPositions: IntArray
        )
    }

    @Stable
    interface Vertical {
        val spacing
            get() = 0

        fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray)
    }

    @Stable
    interface HorizontalOrVertical : Horizontal, Vertical {
        override val spacing: Int
            get() = 0
    }

    @Stable
    val Left =
        object : Horizontal {
            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeLeftOrTop(sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#Left"
        }

    @Stable
    val Right =
        object : Horizontal {
            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeRightOrBottom(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#Right"
        }

    @Stable
    val Top =
        object : Vertical {
            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeLeftOrTop(sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#Top"
        }

    @Stable
    val Bottom =
        object : Vertical {
            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeRightOrBottom(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#Bottom"
        }

    @Stable
    val Center =
        object : HorizontalOrVertical {
            override val spacing = 0

            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeCenter(totalSize, sizes, outPositions, reverseInput = false)

            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeCenter(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#Center"
        }

    @Stable
    val SpaceEvenly =
        object : HorizontalOrVertical {
            override val spacing = 0

            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeSpaceEvenly(totalSize, sizes, outPositions, reverseInput = false)

            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeSpaceEvenly(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#SpaceEvenly"
        }

    @Stable
    val SpaceBetween =
        object : HorizontalOrVertical {
            override val spacing = 0

            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeSpaceBetween(totalSize, sizes, outPositions, reverseInput = false)

            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeSpaceBetween(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#SpaceBetween"
        }

    @Stable
    val SpaceAround =
        object : HorizontalOrVertical {
            override val spacing = 0

            override fun arrangeHorizontally(
                totalSize: Int,
                sizes: IntArray,
                outPositions: IntArray
            ) = placeSpaceAround(totalSize, sizes, outPositions, reverseInput = false)

            override fun arrangeVertically(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
                placeSpaceAround(totalSize, sizes, outPositions, reverseInput = false)

            override fun toString() = "Arrangement#SpaceAround"
        }

    @Stable
    fun spacedBy(space: Int): HorizontalOrVertical =
        SpacedAligned(space) { size ->
            Alignment.Left.align(0, size)
        }

    @Stable
    fun spacedBy(space: Int, alignment: Alignment.Horizontal): Horizontal =
        SpacedAligned(space) { size ->
            alignment.align(0, size)
        }

    @Stable
    fun spacedBy(space: Int, alignment: Alignment.Vertical): Vertical =
        SpacedAligned(space) { size -> alignment.align(0, size) }

    @Stable
    fun aligned(alignment: Alignment.Horizontal): Horizontal =
        SpacedAligned(0) { size ->
            alignment.align(0, size)
        }

    @Stable
    fun aligned(alignment: Alignment.Vertical): Vertical =
        SpacedAligned(0) { size -> alignment.align(0, size) }

    @Immutable
    object Absolute {
        @Stable
        val Left =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeLeftOrTop(sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#Left"
            }

        @Stable
        val Center =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeCenter(totalSize, sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#Center"
            }

        @Stable
        val Right =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeRightOrBottom(totalSize, sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#Right"
            }

        @Stable
        val SpaceBetween =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeSpaceBetween(totalSize, sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#SpaceBetween"
            }

        @Stable
        val SpaceEvenly =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeSpaceEvenly(totalSize, sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#SpaceEvenly"
            }

        @Stable
        val SpaceAround =
            object : Horizontal {
                override fun arrangeHorizontally(
                    totalSize: Int,
                    sizes: IntArray,
                    outPositions: IntArray
                ) = placeSpaceAround(totalSize, sizes, outPositions, reverseInput = false)

                override fun toString() = "AbsoluteArrangement#SpaceAround"
            }

        @Stable fun spacedBy(space: Int): HorizontalOrVertical = SpacedAligned(space, null)

        @Stable
        fun spacedBy(space: Int, alignment: Alignment.Horizontal): Horizontal =
            SpacedAligned(space) { size ->
                alignment.align(0, size)
            }

        @Stable
        fun spacedBy(space: Int, alignment: Alignment.Vertical): Vertical =
            SpacedAligned(space) { size -> alignment.align(0, size) }

        @Stable
        fun aligned(alignment: Alignment.Horizontal): Horizontal =
            SpacedAligned(0) { size ->
                alignment.align(0, size)
            }
    }

    @Immutable
    internal data class SpacedAligned(
        val space: Int,
        val alignment: ((Int) -> Int)?
    ) : HorizontalOrVertical {

        override val spacing = space

        override fun arrangeVertically(
            totalSize: Int,
            sizes: IntArray,
            outPositions: IntArray
        ) {
            if (sizes.isEmpty()) return

            var occupied = 0
            var lastSpace = 0
            sizes.forEachIndexed { index, it ->
                outPositions[index] = min(occupied, totalSize - it)
                lastSpace = min(space, totalSize - outPositions[index] - it)
                occupied = outPositions[index] + it + lastSpace
            }
            occupied -= lastSpace

            if (alignment != null && occupied < totalSize) {
                val groupPosition = alignment.invoke(totalSize - occupied)
                for (index in outPositions.indices) {
                    outPositions[index] += groupPosition
                }
            }
        }

        override fun arrangeHorizontally(totalSize: Int, sizes: IntArray, outPositions: IntArray) =
            arrangeVertically(totalSize, sizes, outPositions)

        override fun toString() =
            "Arrangement#spacedAligned($space, $alignment)"
    }

    internal fun placeRightOrBottom(
        totalSize: Int,
        size: IntArray,
        outPosition: IntArray,
        reverseInput: Boolean
    ) {
        val consumedSize = size.fold(0) { a, b -> a + b }
        var current = totalSize - consumedSize
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current
            current += it
        }
    }

    internal fun placeLeftOrTop(size: IntArray, outPosition: IntArray, reverseInput: Boolean) {
        var current = 0
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current
            current += it
        }
    }

    internal fun placeCenter(
        totalSize: Int,
        size: IntArray,
        outPosition: IntArray,
        reverseInput: Boolean
    ) {
        val consumedSize = size.fold(0) { a, b -> a + b }
        var current = (totalSize - consumedSize).toFloat() / 2
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current.toInt()
            current += it.toFloat()
        }
    }

    internal fun placeSpaceEvenly(
        totalSize: Int,
        size: IntArray,
        outPosition: IntArray,
        reverseInput: Boolean
    ) {
        val consumedSize = size.fold(0) { a, b -> a + b }
        val gapSize = (totalSize - consumedSize).toFloat() / (size.size + 1)
        var current = gapSize
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current.toInt()
            current += it.toFloat() + gapSize
        }
    }

    internal fun placeSpaceBetween(
        totalSize: Int,
        size: IntArray,
        outPosition: IntArray,
        reverseInput: Boolean
    ) {
        if (size.isEmpty()) return

        val consumedSize = size.fold(0) { a, b -> a + b }
        val noOfGaps = maxOf(size.lastIndex, 1)
        val gapSize = (totalSize - consumedSize).toFloat() / noOfGaps

        var current = 0f
        if (reverseInput && size.size == 1) {
            current = gapSize
        }
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current.toInt()
            current += it.toFloat() + gapSize
        }
    }

    internal fun placeSpaceAround(
        totalSize: Int,
        size: IntArray,
        outPosition: IntArray,
        reverseInput: Boolean
    ) {
        val consumedSize = size.fold(0) { a, b -> a + b }
        val gapSize =
            if (size.isNotEmpty()) {
                (totalSize - consumedSize).toFloat() / size.size
            } else {
                0f
            }
        var current = gapSize / 2
        size.forEachIndexed(reverseInput) { index, it ->
            outPosition[index] = current.toInt()
            current += it.toFloat() + gapSize
        }
    }

    private inline fun IntArray.forEachIndexed(reversed: Boolean, action: (Int, Int) -> Unit) {
        if (!reversed) {
            forEachIndexed(action)
        } else {
            for (i in (size - 1) downTo 0) {
                action(i, get(i))
            }
        }
    }
}
