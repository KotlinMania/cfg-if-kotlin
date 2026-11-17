import Testing
import CfgIf

private final class ActionLog {
    var entries: [String] = []
}

@Suite("CfgIf exported behavior")
struct CfgIfExportTests {
    @Test func selectCascadeEvaluatesFirstMatchingBranchOnly() {
        let log = ActionLog()
        let branches: [CfgIfActionBranch] = [
            CfgIfActionBranch(enabled: false, action: cfgAction { log.entries.append("first") }),
            CfgIfActionBranch(enabled: true, action: cfgAction { log.entries.append("second") }),
            CfgIfActionBranch(enabled: true, action: cfgAction { log.entries.append("third") }),
        ]
        let ran = cfgIfRun(branches: branches, fallback: cfgAction { log.entries.append("fallback") })
        #expect(ran)
        #expect(log.entries == ["second"])
    }

    @Test func selectHelpersEvaluateFallbackAndIndex() {
        #expect(cfgIfFirstEnabledIndex(conditions: [false, true, true]) == 1)
        #expect(cfgIfFirstEnabledIndex(conditions: [false, false]) == -1)

        let intResult = cfgIfSelectInt(
            branches: [
                CfgIfIntBranch(enabled: false, supplier: cfgIntSupplier { 10 }),
                CfgIfIntBranch(enabled: false, supplier: cfgIntSupplier { 20 }),
            ],
            fallback: cfgIntSupplier { 42 }
        )
        #expect(intResult == 42)

        let stringResult = cfgIfSelectString(
            branches: [
                CfgIfStringBranch(enabled: false, supplier: cfgStringSupplier { "unix" }),
                CfgIfStringBranch(enabled: true, supplier: cfgStringSupplier { "apple" }),
            ],
            fallback: cfgStringSupplier { "fallback" }
        )
        #expect(stringResult == "apple")

        let boolResult = cfgIfSelectBool(
            branches: [CfgIfBoolBranch(enabled: false, supplier: cfgBoolSupplier { false })],
            fallback: cfgBoolSupplier { true }
        )
        #expect(boolResult)
    }
}
