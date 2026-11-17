// port-lint: tests tests/xcrate.rs
package io.github.kotlinmania.cfgif

import kotlin.test.Test
import kotlin.test.assertTrue

private const val XCRATE_CFG_FOO: Boolean = false
private const val XCRATE_CFG_TEST: Boolean = true

private fun xcrateWorks(): Boolean =
    cfgIfSelect(
        XCRATE_CFG_FOO,
        { false },
        XCRATE_CFG_TEST,
        { true },
    ) {
        false
    }

public class XcrateTest {
    @Test
    public fun smoke() {
        assertTrue(xcrateWorks())
    }
}
