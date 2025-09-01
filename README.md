# Capsule

Capsule is a Jetpack Compose library that creates **G2 continuous** rounded rectangles.

![Different types of rounded rectangles](docs/rounded_rectangles.png)

Customizable curvature combs:

![Different curvature combs](docs/curvature_combs.png)

## [Playground app](./app/release/app-release.apk)

<img alt="Screenshot of the playground app" height="400" src="docs/playground_app.jpg"/>

## Installation

[![JitPack Release](https://jitpack.io/v/Kyant0/Capsule.svg)](https://jitpack.io/#Kyant0/Capsule)

```kotlin
// settings.gradle.kts in root project
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

// build.gradle.kts in module
implementation("com.github.Kyant0:Capsule:<version>")
```

## Usages

Replace the `RoundedCornerShape` with `ContinuousRoundedRectangle` or `ContinuousCapsule`:

```kotlin
// create a basic rounded corner shape
ContinuousRoundedRectangle(16.dp)

// create a capsule shape
ContinuousCapsule
```

Custom continuity:

```kotlin
val g1 = G1Continuity // no corner smoothness
val g2 = G2Continuity(
    config = G2ContinuityConfig.RoundedRectangle.copy(
        extendedFraction = 0.5,
        arcFraction = 1.0 / 3.0,
        bezierCurvatureScale = 1.11,
        arcCurvatureScale = 1.11
    ),
    capsuleConfig = CapsuleConfig.Capsule.copy(
        extendedFraction = 1.0 / 3.0,
        arcFraction = 0.0
    )
)

// create shapes with custom continuity
ContinuousRoundedRectangle(16.dp, continuity = g2)
ContinuousCapsule(continuity = g2)
```

## Tips

### Performance

Drawing cubic Bézier curves on Android performs poorly. However, the Capsule library uses a very efficient method to
calculate the control points, achieving optimal theoretical performance.

When the shape area is large (almost fullscreen) and the corner radius is constantly changing, performance may decrease.
Use `animatedShape.copy(continuity = G1Continuity)` to temporarily disable corner smoothing during the
animation.

### Exact G2 continuity

The following code creates an exact (perfect) G2 continuity. However, it is not recommended to use this in practice
because its curvature is not optimal.

```kotlin
val exactG2 = G2Continuity(
    config = G2ContinuityConfig.RoundedRectangle.copy(
        bezierCurvatureScale = 1.0,
        arcCurvatureScale = 1.0
    )
)
```
