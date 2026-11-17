// Transliterated and parceled from rust-kotlin output for crate cfg-if.
package io.github.kotlinmania.cfgif

// <div class="warning">
//
// This crate has been "replaced" by the `cfg_select!` macro in Rust, which is stable
// since Rust 1.95.0 with a slightly different syntax.
//
// </div>
//
// A utility for defining configuration if-else cascades.
//
// Structured like an `if` / `else if` / `else` chain, only the first matching
// branch is evaluated.

private const val CFG_IF_TEMPLATE: String =
    "cfgIf {@ items(); ((\$(\$iMeta)+)(\$(\$iTokens)*)), \$(((\$(\$eiMeta)+)(\$(\$eiTokens)*)), )*\$((()(\$(\$eTokens)*)), )?}"

private const val CFG_IF_TEMPLATE_2: String =
    "cfgIf {@ tempGroup\$(\$tokens)*} cfgIf {@ items(\$((\$(\$no)+), )*\$((\$(\$yes)+), )?); \$(\$rest, )*}"

private const val CFG_IF_TEMPLATE_3: String = "\$(\$tokens)*"

/**
 * Callback executed when a matching configuration branch is selected.
 */
public fun interface CfgAction {
    /**
     * Executes this branch body.
     */
    public fun run()
}

/**
 * Callback supplying a [Boolean] result for a matching configuration branch.
 */
public fun interface CfgBoolSupplier {
    /**
     * Produces the branch's [Boolean] value.
     */
    public fun get(): Boolean
}

/**
 * Callback supplying an [Int] result for a matching configuration branch.
 */
public fun interface CfgIntSupplier {
    /**
     * Produces the branch's [Int] value.
     */
    public fun get(): Int
}

/**
 * Callback supplying a [String] result for a matching configuration branch.
 */
public fun interface CfgStringSupplier {
    /**
     * Produces the branch's [String] value.
     */
    public fun get(): String
}

/**
 * Conditional action branch in a `cfg_if` cascade.
 */
public class CfgIfActionBranch(
    public val enabled: Boolean,
    public val action: CfgAction,
)

/**
 * Conditional [Boolean] branch in a `cfg_if` cascade.
 */
public class CfgIfBoolBranch(
    public val enabled: Boolean,
    public val supplier: CfgBoolSupplier,
)

/**
 * Conditional [Int] branch in a `cfg_if` cascade.
 */
public class CfgIfIntBranch(
    public val enabled: Boolean,
    public val supplier: CfgIntSupplier,
)

/**
 * Conditional [String] branch in a `cfg_if` cascade.
 */
public class CfgIfStringBranch(
    public val enabled: Boolean,
    public val supplier: CfgStringSupplier,
)

/**
 * Evaluates a list of boolean predicates and returns the 0-based index of the first
 * `true` predicate, or `-1` if none match.
 */
public fun cfgIfFirstEnabledIndex(conditions: List<Boolean>): Int {
    for (index in conditions.indices) {
        if (conditions[index]) {
            return index
        }
    }
    return -1
}

/**
 * Evaluates a `cfg_if` cascade of action branches, executing only the first enabled
 * branch, or [fallback] if no branch matches.
 *
 * Returns `true` if a branch or fallback ran, `false` otherwise.
 */
public fun cfgIfRun(
    branches: List<CfgIfActionBranch>,
    fallback: CfgAction? = null,
): Boolean {
    for (branch in branches) {
        if (branch.enabled) {
            branch.action.run()
            return true
        }
    }
    if (fallback != null) {
        fallback.run()
        return true
    }
    return false
}

/**
 * Evaluates a `cfg_if` cascade returning the [Boolean] produced by the first enabled
 * branch, or by [fallback] if no branch matches.
 */
public fun cfgIfSelectBool(
    branches: List<CfgIfBoolBranch>,
    fallback: CfgBoolSupplier,
): Boolean {
    for (branch in branches) {
        if (branch.enabled) {
            return branch.supplier.get()
        }
    }
    return fallback.get()
}

/**
 * Evaluates a `cfg_if` cascade returning the [Int] produced by the first enabled
 * branch, or by [fallback] if no branch matches.
 */
public fun cfgIfSelectInt(
    branches: List<CfgIfIntBranch>,
    fallback: CfgIntSupplier,
): Int {
    for (branch in branches) {
        if (branch.enabled) {
            return branch.supplier.get()
        }
    }
    return fallback.get()
}

/**
 * Evaluates a `cfg_if` cascade returning the [String] produced by the first enabled
 * branch, or by [fallback] if no branch matches.
 */
public fun cfgIfSelectString(
    branches: List<CfgIfStringBranch>,
    fallback: CfgStringSupplier,
): String {
    for (branch in branches) {
        if (branch.enabled) {
            return branch.supplier.get()
        }
    }
    return fallback.get()
}

/**
 * Evaluates a single-branch `cfg_if` cascade with fallback in Kotlin callers.
 */
public inline fun <T> cfgIfSelect(
    c0: Boolean,
    b0: () -> T,
    fallback: () -> T,
): T = if (c0) b0() else fallback()

/**
 * Evaluates a two-branch `cfg_if` cascade with fallback in Kotlin callers.
 */
public inline fun <T> cfgIfSelect(
    c0: Boolean,
    b0: () -> T,
    c1: Boolean,
    b1: () -> T,
    fallback: () -> T,
): T =
    when {
        c0 -> b0()
        c1 -> b1()
        else -> fallback()
    }

/**
 * Evaluates a three-branch `cfg_if` cascade with fallback in Kotlin callers.
 */
public inline fun <T> cfgIfSelect(
    c0: Boolean,
    b0: () -> T,
    c1: Boolean,
    b1: () -> T,
    c2: Boolean,
    b2: () -> T,
    fallback: () -> T,
): T =
    when {
        c0 -> b0()
        c1 -> b1()
        c2 -> b2()
        else -> fallback()
    }

/**
 * Evaluates a single-branch `cfg_if` cascade without an `else` clause, returning `null` if unmatched.
 */
public inline fun <T> cfgIfSelectOrNull(
    c0: Boolean,
    b0: () -> T,
): T? = if (c0) b0() else null

/**
 * Evaluates a two-branch `cfg_if` cascade without an `else` clause, returning `null` if unmatched.
 */
public inline fun <T> cfgIfSelectOrNull(
    c0: Boolean,
    b0: () -> T,
    c1: Boolean,
    b1: () -> T,
): T? =
    when {
        c0 -> b0()
        c1 -> b1()
        else -> null
    }

/**
 * The macro expansion template provided by the upstream `cfg_if!` macro.
 */
public fun cfgIf(
    iMeta: String = "",
    iTokens: String = "",
    eiMeta: String = "",
    eiTokens: String = "",
    eTokens: String = "",
    unused: String = "",
    no: String = "",
    yes: String = "",
    tokens: String = "",
    rest: String = "",
    clause: String = "",
): String =
    when {
        clause == "ifBranch # cfg elseBranch ifBranch # cfg elseBranch" &&
            iMeta.isNotEmpty() &&
            iTokens.isNotEmpty() &&
            eiMeta.isNotEmpty() &&
            eiTokens.isNotEmpty() &&
            eTokens.isNotEmpty() -> {
            CFG_IF_TEMPLATE
                .replace("\$iMeta", iMeta)
                .replace("\$iTokens", iTokens)
                .replace("\$eiMeta", eiMeta)
                .replace("\$eiTokens", eiTokens)
                .replace("\$eTokens", eTokens)
        }
        clause == "@items" &&
            no.isNotEmpty() &&
            yes.isNotEmpty() &&
            tokens.isNotEmpty() &&
            rest.isNotEmpty() -> {
            CFG_IF_TEMPLATE_2
                .replace("\$no", no)
                .replace("\$yes", yes)
                .replace("\$tokens", tokens)
                .replace("\$rest", rest)
        }
        clause == "@items" && unused.isNotEmpty() -> ""
        clause == "@tempGroup" && tokens.isNotEmpty() -> CFG_IF_TEMPLATE_3.replace("\$tokens", tokens)
        else -> ""
    }
