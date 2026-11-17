# cfg-if-kotlin

Kotlin Multiplatform port of Rust [`cfg-if`](https://crates.io/crates/cfg-if) (`1.0.5`) supporting all 17 retained KotlinMania KMP targets and direct Swift Export.

## Installation

Add the dependency in your Gradle version catalog or build script:

```kotlin
dependencies {
    implementation("io.github.kotlinmania:cfg-if-kotlin:0.1.1")
}
```

## Kotlin Usage

```kotlin
import io.github.kotlinmania.cfgif.cfgIfSelect

val label = cfgIfSelect(
    isUnix, { "unix specific functionality" },
    isWasm, { "wasm functionality" },
) {
    "fallback implementation"
}
```

## Swift Usage

```swift
import CfgIf

let label = cfgIfSelectString(
    branches: [
        CfgIfStringBranch(enabled: false, supplier: cfgStringSupplier { "unix" }),
        CfgIfStringBranch(enabled: true, supplier: cfgStringSupplier { "apple" }),
    ],
    fallback: cfgStringSupplier { "fallback" }
)
```

## License

Dual-licensed under [MIT](tmp/cfg-if/LICENSE-MIT) or [Apache-2.0](tmp/cfg-if/LICENSE-APACHE), matching upstream `cfg-if`.
