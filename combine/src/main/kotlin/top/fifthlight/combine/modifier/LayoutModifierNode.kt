package top.fifthlight.combine.modifier

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope

interface LayoutModifierNode {
    fun measure(measurable: Measurable, constraints: Constraints) =
        with(MeasureScope) { this.measure(measurable, constraints) }

    fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult
}
