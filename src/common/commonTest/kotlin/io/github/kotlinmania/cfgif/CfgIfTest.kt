package io.github.kotlinmania.cfgif

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val CFG_TEST: Boolean = true
private const val CFG_FOO: Boolean = false
private const val CFG_NO_SUCH_OS: Boolean = false
private const val CFG_FEATURE_BLAH: Boolean = false

private fun works1(): UInt? =
    cfgIfSelect(
        CFG_TEST,
        { 1u },
    ) {
        null
    }

private fun works2(): Boolean =
    cfgIfSelect(
        CFG_FOO,
        { false },
        CFG_TEST,
        { true },
    ) {
        false
    }

private fun works3(): Boolean =
    cfgIfSelect(
        CFG_FOO,
        { false },
    ) {
        true
    }

private fun works4(): UInt? =
    cfgIfSelectOrNull(CFG_TEST) {
        1u
    }

private fun works5(): Boolean? =
    cfgIfSelectOrNull(
        CFG_FOO,
        { false },
        CFG_TEST,
        { true },
    )

private fun works6(): Boolean? =
    cfgIfSelectOrNull(
        false,
        { false },
        true,
        { true },
    )

private interface TestTrait {
    fun blah(): String
}

private class TestStruct : TestTrait {
    override fun blah(): String =
        cfgIfSelect(
            CFG_FEATURE_BLAH,
            { "blah" },
        ) {
            "default-blah"
        }
}

public class CfgIfTest {
    @Test
    public fun itWorks() {
        assertNotNull(works1())
        assertEquals(1u, works1())
        assertTrue(works2())
        assertTrue(works3())
        assertNotNull(works4())
        assertEquals(1u, works4())
        assertEquals(true, works5())
        assertEquals(true, works6())
    }

    @Test
    public fun testUsageWithinAFunction() {
        val log = mutableListOf<String>()
        val ran =
            cfgIfRun(
                branches =
                    listOf(
                        CfgIfActionBranch(enabled = false) {
                            log.add("false-branch")
                        },
                        CfgIfActionBranch(enabled = CFG_TEST) {
                            // Multiple items in one matching block both execute (issue #90 semantics)
                            assertTrue(CFG_TEST)
                            assertEquals(4, 2 + 2)
                            log.add("first")
                            log.add("second")
                        },
                        CfgIfActionBranch(enabled = true) {
                            log.add("should-not-run")
                        },
                    ),
                fallback = {
                    assertNotNull(works1())
                    assertEquals(10, 5 + 5)
                    log.add("fallback")
                },
            )
        assertTrue(ran)
        assertEquals(listOf("first", "second"), log)
    }

    @Test
    public fun testIssue90MultipleItemsAndFallback() {
        var aliasA = ""
        var aliasB = ""
        val ran =
            cfgIfRun(
                branches =
                    listOf(
                        CfgIfActionBranch(enabled = CFG_NO_SUCH_OS) {
                            aliasA = "usize"
                            aliasB = "usize"
                        },
                    ),
                fallback = {
                    aliasA = "i32"
                    aliasB = "i32"
                },
            )
        assertTrue(ran)
        assertEquals("i32", aliasA)
        assertEquals("i32", aliasB)
        assertEquals("default-blah", TestStruct().blah())
    }

    @Test
    public fun testCascadeHelpersAndFirstMatchWins() {
        assertEquals(-1, cfgIfFirstEnabledIndex(listOf(false, false, false)))
        assertEquals(1, cfgIfFirstEnabledIndex(listOf(false, true, true)))

        assertFalse(
            cfgIfRun(
                branches = listOf(CfgIfActionBranch(enabled = false) {}),
                fallback = null,
            ),
        )

        var laterCalled = false
        val selectedInt =
            cfgIfSelectInt(
                branches =
                    listOf(
                        CfgIfIntBranch(enabled = false) { 1 },
                        CfgIfIntBranch(enabled = true) { 2 },
                        CfgIfIntBranch(enabled = true) {
                            laterCalled = true
                            3
                        },
                    ),
                fallback = { 99 },
            )
        assertEquals(2, selectedInt)
        assertFalse(laterCalled)

        val selectedString =
            cfgIfSelectString(
                branches =
                    listOf(
                        CfgIfStringBranch(enabled = false) { "unix" },
                        CfgIfStringBranch(enabled = false) { "wasm" },
                    ),
                fallback = { "fallback" },
            )
        assertEquals("fallback", selectedString)

        val selectedBool =
            cfgIfSelectBool(
                branches = listOf(CfgIfBoolBranch(enabled = true) { true }),
                fallback = { false },
            )
        assertTrue(selectedBool)

        assertNull(
            cfgIfSelectOrNull(false) { "never" },
        )
    }

    @Test
    public fun testMacroTemplateExpansion() {
        val top =
            cfgIf(
                iMeta = "unix",
                iTokens = "fn foo() {}",
                eiMeta = "windows",
                eiTokens = "fn foo() {}",
                eTokens = "fn foo() {}",
                clause = "ifBranch # cfg elseBranch ifBranch # cfg elseBranch",
            )
        assertTrue(top.contains("unix"))
        assertTrue(top.contains("windows"))
        assertEquals("$" + "(bar)*", cfgIf(tokens = "bar", clause = "@tempGroup"))
        assertEquals("", cfgIf(unused = "done", clause = "@items"))
    }
}
