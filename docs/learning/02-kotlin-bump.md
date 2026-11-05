# PR #7 — Kotlin 2.4.21 bump

[Learning guide index](README.md) · [PR #7](https://github.com/anhtuta/lili-music-android/pull/7)

**What was built.** Dependabot Kotlin bump resolved onto main: KSP 2.3.13, Gradle wrapper 9.6.0 (for AGP 9.5.0), compileSdk 37 (for core-ktx 1.19.1). Task [004](../tasks/004-kotlin-2.4.21-bump.md).

**Status:** Merged.

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| AGP | Android Gradle Plugin | Spring Boot Gradle plugin |
| Gradle Wrapper | `gradlew` pins Gradle for everyone | Volta / corepack for Node |
| KSP ↔ Kotlin | KSP version must track Kotlin | typescript ↔ loader alignment |

1. [libs.versions.toml](../../gradle/libs.versions.toml) — kotlin 2.4.21, ksp 2.3.13, agp 9.5.0.
2. [gradle-wrapper.properties](../../gradle/wrapper/gradle-wrapper.properties) — Gradle 9.6.0.
3. [004-kotlin-2.4.21-bump.md](../tasks/004-kotlin-2.4.21-bump.md) — why each bump.
