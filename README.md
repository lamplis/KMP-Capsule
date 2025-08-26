# Capsule

Capsule is a Jetpack Compose library that creates **G3 / G2 continuous** rounded rectangles.

![All corner types supported by Capsule](docs/all_continuities.jpg)

Curvature of G3 continuous rounded corner in Capsule (in purple):

<img alt="G3 curvature" height="400" src="docs/curvature_g3.jpg"/>

Curvature of G2 continuous rounded corner in Capsule:

<img alt="G2 curvature" height="400" src="docs/curvature_g2.jpg"/>

## [Playground app](./app/release/app-release.apk)

<img alt="Screenshot of the playground app" height="400" src="docs/playground_app.png"/>

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
val g1 = G1Continuity
val g2 = G2Continuity(circleFraction = 0.25f, extendedFraction = 1f)
val g3 = G3Continuity(extendedFraction = 1f)

// create shapes with custom continuity
ContinuousRoundedRectangle(16.dp, continuity = g3)
ContinuousCapsule(continuity = g3)
```

## Performance

Drawing cubic Bézier curves on Android performs poorly. However, the Capsule library uses a very efficient method to
calculate the control points, achieving optimal theoretical performance.

When the shape area is large (almost fullscreen) and the corner radius is constantly changing, performance may decrease.
Use `animatedShape.copy(continuity = G1Continuity)` to temporarily disable corner smoothing during the
animation.

## How it works

Each corner consists of a part of **circle (C)** and two **cubic Bézier curves (B)** that connect the circle to the
straight edges (L) of the rectangle.

The proportion of the circular section is defined by the `circleFraction` (f_c) and the extended length relative to the
corner radius (R) is defined by the `extendedFraction` (f_e) in `CornerSmoothness` class.

![Schematic](docs/schematic.png)

It uses mathematical calculations to determine the control points of the cubic Bézier curves to achieve G2 continuity.
