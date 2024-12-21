package top.fifthlight.combine.modifier

/**
 * An ordered, immutable collection of [modifier elements][Modifier.Node] that decorate or add
 * behavior to Compose UI elements. For example, backgrounds, padding and click event listeners
 * decorate or add behavior to rows, text or buttons.
 *
 * This class is taken from the androidx Jetpack Compose UI library so as to avoid extra dependencies.
 */
interface Modifier {

    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value
     * and each element from outside in.
     *
     * Elements wrap one another in a chain from left to right; an [Node] that appears to the
     * left of another in a `+` expression or in [operation]'s parameter order affects all
     * of the elements that appear after it. [foldIn] may be used to accumulate a value starting
     * from the parent or head of the modifier chain to the final wrapped child.
     */
    fun <R> foldIn(initial: R, operation: (R, Node<*>) -> R): R

    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value
     * and each element from inside out.
     *
     * Elements wrap one another in a chain from left to right; an [Node] that appears to the
     * left of another in a `+` expression or in [operation]'s parameter order affects all
     * of the elements that appear after it. [foldOut] may be used to accumulate a value starting
     * from the child or tail of the modifier chain up to the parent or head of the chain.
     */
    fun <R> foldOut(initial: R, operation: (Node<*>, R) -> R): R

    /**
     * Returns `true` if [predicate] returns true for any [Node] in this [Modifier].
     */
    fun any(predicate: (Node<*>) -> Boolean): Boolean

    /**
     * Returns `true` if [predicate] returns true for all [Node]s in this [Modifier] or if
     * this [Modifier] contains no [Node]s.
     */
    fun all(predicate: (Node<*>) -> Boolean): Boolean

    /**
     * Concatenates this modifier with another.
     *
     * Returns a [Modifier] representing this modifier followed by [other] in sequence.
     */
    infix fun then(other: Modifier): Modifier =
        if (other === Modifier) this else CombinedModifier(this, other)

    /**
     * A single element contained within a [Modifier] chain.
     */
    interface Node<Self : Node<Self>> : Modifier {
        override fun <R> foldIn(initial: R, operation: (R, Node<*>) -> R): R =
            operation(initial, this)

        override fun <R> foldOut(initial: R, operation: (Node<*>, R) -> R): R =
            operation(this, initial)

        override fun any(predicate: (Node<*>) -> Boolean): Boolean = predicate(this)

        override fun all(predicate: (Node<*>) -> Boolean): Boolean = predicate(this)
    }

    /**
     * The companion object `Modifier` is the empty, default, or starter [Modifier]
     * that contains no [elements][Node]. Use it to create a new [Modifier] using
     * modifier extension factory functions.
     */
    // The companion object implements `Modifier` so that it may be used  as the start of a
    // modifier extension factory expression.
    companion object : Modifier {
        override fun <R> foldIn(initial: R, operation: (R, Node<*>) -> R): R = initial
        override fun <R> foldOut(initial: R, operation: (Node<*>, R) -> R): R = initial
        override fun any(predicate: (Node<*>) -> Boolean): Boolean = false
        override fun all(predicate: (Node<*>) -> Boolean): Boolean = true
        override infix fun then(other: Modifier): Modifier = other
        override fun toString() = "Modifier"
    }
}

/**
 * A node in a [Modifier] chain. A CombinedModifier always contains at least two elements;
 * a Modifier [outer] that wraps around the Modifier [inner].
 */
class CombinedModifier(
    private val outer: Modifier,
    private val inner: Modifier
) : Modifier {
    override fun <R> foldIn(initial: R, operation: (R, Modifier.Node<*>) -> R): R =
        inner.foldIn(outer.foldIn(initial, operation), operation)

    override fun <R> foldOut(initial: R, operation: (Modifier.Node<*>, R) -> R): R =
        outer.foldOut(inner.foldOut(initial, operation), operation)

    override fun any(predicate: (Modifier.Node<*>) -> Boolean): Boolean =
        outer.any(predicate) || inner.any(predicate)

    override fun all(predicate: (Modifier.Node<*>) -> Boolean): Boolean =
        outer.all(predicate) && inner.all(predicate)

    override fun equals(other: Any?): Boolean =
        other is CombinedModifier && outer == other.outer && inner == other.inner

    override fun hashCode(): Int = outer.hashCode() + 31 * inner.hashCode()

    override fun toString() = "[" + foldIn("") { acc, element ->
        if (acc.isEmpty()) element.toString() else "$acc, $element"
    } + "]"
}
