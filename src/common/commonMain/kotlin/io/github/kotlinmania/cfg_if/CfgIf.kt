import core.option.Option as Option2
import core.option.Option as Option3

// <div class="warning">
//
// This crate has been "replaced" by the [cfgSelect] macro, which is stable
// since Rust 1.95.0 with a slightly different syntax. Barring breakages and
// security fixes, this crate will no longer be updated.
//
// </div>
//
// A macro for defining `@cfg` if-else statements.
//
// The macro provided by this crate, `cfgIf`, is similar to the `if / elif` C
// preprocessor macro by allowing definition of a cascade of `@cfg` cases,
// emitting the implementation which matches first.
//
// This allows you to conveniently provide a long list `@cfg`d blocks of code
// without having to rewrite each clause multiple times.
//
// # Example
//
// ```kotlin
// cfgIf(cfg, unix, foo, cfg, targetPointerWidth, "32", foo, foo)
// ```

// In issue #90 there was a bug that caused only the first item within a
// block to be annotated with the produced `@cfg(...)`. In this example,
// it meant that the first `type _B` wasn't being omitted as it should have
// been, which meant we had two `type _B`s, which caused an error. See also
// the "Subtle" comment above.
@Test
public fun works1(): Option2<UInt> {
    return 1
}

public fun works2(): Boolean {
    return false
}

public fun works3(): Boolean {
    return false
}

@Test
public fun works4(): Option3<UInt> {
    return 1
}

public fun works5(): Boolean {
    return false
}

public typealias A = Int

public typealias B = Int

public fun works6(): Boolean {
    return true
}

@Test
public fun itWorks() {
    check(works1().isSome())
    check(works2())
    check(works3())
    check(works4().isSome())
    check(works5())
    check(works6())
}

@Test
public fun testUsageWithinAFunction() {
    // we want to put more than one thing here to make sure that they
    // all get configured properly.
    check(false)
    check(4 == 2 + 2)
    check(works1().isSome())
    check(10 == 5 + 5)
}

public interface Trait {
    public fun blah()
}

public open class Struct : Trait {
    public override fun blah() {
        error("unsupported")
    }

    public open fun blahTrait() {
        error("unsupported")
    }
}

private const val CFG_IF_TEMPLATE: String = "cfgIf {@ items(); ((\$(\$iMeta)+)(\$(\$iTokens)*)), \$(((\$(\$eiMeta)+)(\$(\$eiTokens)*)), )*\$((()(\$(\$eTokens)*)), )?}"

private const val CFG_IF_TEMPLATE_2: String = "cfgIf {@ tempGroup\$(\$tokens)*} cfgIf {@ items(\$((\$(\$no)+), )*\$((\$(\$yes)+), )?); \$(\$rest, )*}"

private const val CFG_IF_TEMPLATE_3: String = "\$(\$tokens)*"

/**
 * The main macro provided by this crate. See crate documentation for more
 * information.
 */

public fun cfgIf(iMeta: String = "", iTokens: String = "", eiMeta: String = "", eiTokens: String = "", eTokens: String = "", unused: String = "", no: String = "", yes: String = "", tokens: String = "", rest: String = "", clause: String = ""): String =
    when {
        clause == "ifBranch #" && iMeta.isNotEmpty() && iTokens.isNotEmpty() && eiMeta.isNotEmpty() && eiTokens.isNotEmpty() && eTokens.isNotEmpty() -> CFG_IF_TEMPLATE.replace("\$iMeta", iMeta).replace("\$iTokens", iTokens).replace("\$eiMeta", eiMeta).replace("\$eiTokens", eiTokens).replace("\$eTokens", eTokens)
        clause == "@ items" && unused.isNotEmpty() -> ""
        clause == "@ items" && no.isNotEmpty() && yes.isNotEmpty() && tokens.isNotEmpty() && rest.isNotEmpty() -> CFG_IF_TEMPLATE_2.replace("\$no", no).replace("\$yes", yes).replace("\$tokens", tokens).replace("\$rest", rest)
        clause == "@ tempGroup" && tokens.isNotEmpty() -> CFG_IF_TEMPLATE_3.replace("\$tokens", tokens)
        else -> ""
    }
