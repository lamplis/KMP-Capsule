# KMP-Capsule (fork of Kyant0/Capsule)

KMP-Capsule is a Kotlin Multiplatform port of the excellent Capsule library by [Kyant0](https://github.com/Kyant0). 

This fork adapts the capsule shape to KMP (Android + iOS) with a simplified API focused on a portable `ContinuousCapsule` shape.

> Huge thanks to Kyant0 for the original work on Capsule and the deep thought put into G1/G2 continuity and performant curve construction. This project would not exist without their contribution to the Compose ecosystem.

> Note: This fork currently exposes a portable `ContinuousCapsule` API for KMP. Advanced continuity profiles (`G2Continuity`, curvature combs, etc.) are part of the original Android-only implementation and are not included in the KMP surface yet.

Original Android playground and visuals: see the upstream repository.

## Installation

This project is designed to be consumed as a local submodule/module while KMP packaging is stabilized.

```kotlin
// settings.gradle.kts (root)
include(":libs:KMP-Capsule")
project(":libs:KMP-Capsule").projectDir = File(rootDir, "libs/KMP-capsule/capsule")

// composeApp/build.gradle.kts (commonMain)
commonMain.dependencies {
    implementation(project(":libs:KMP-Capsule"))
}
```

## Usage (KMP)

Replace rounded shapes with `ContinuousCapsule` from the KMP module:

```kotlin
import com.amp_digital.capsule.ContinuousCapsule

// create a capsule shape
Box(
    modifier = Modifier
        .clip(ContinuousCapsule)
        .background(color)
)
```

Advanced continuity (G2 profiles) is available in the original Android-only library: please refer to the upstream docs if you need those features today.

### API surface

- `ContinuousCapsule`: platform-agnostic capsule shape for Compose Multiplatform
- Stable import path: `com.amp_digital.capsule.ContinuousCapsule`

## Credits & License

- Upstream project: [Kyant0/Capsule](https://github.com/Kyant0/Capsule) by [Kyant0](https://github.com/Kyant0)
- This fork: KMP-Capsule (Android + iOS simplified API)

Licensed under Apache-2.0, same as the upstream project.