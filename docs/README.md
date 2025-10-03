# KMP-Capsule

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7C3AED?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose)](https://www.jetpackcompose.dev/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Kotlin Multiplatform port of the excellent [Kyant0/Capsule](https://github.com/Kyant0/Capsule) library, providing perfect capsule shapes for Compose Multiplatform.

## 🎯 What is KMP-Capsule?

KMP-Capsule extends the brilliant work of [Kyant0](https://github.com/Kyant0) to iOS and other platforms, maintaining the same API while providing platform-optimized implementations. While the original library is Android-only, KMP-Capsule brings perfect capsule shapes to your entire KMP project.

## ✨ Features

- 🎨 **Perfect Capsules**: Mathematically perfect capsule shapes with G1/G2 continuity
- 📱 **Cross-Platform**: Works seamlessly on Android and iOS
- ⚡ **High Performance**: Platform-specific optimizations for optimal rendering
- 🔧 **Easy Integration**: Simple submodule setup with expect/actual pattern
- 🎯 **Consistent API**: Same API across all platforms

## 📦 Installation

### 1. Add as Submodule

```bash
git submodule add https://github.com/lamplis/KMP-Capsule.git libs/KMP-capsule
```

### 2. Configure Gradle

```kotlin
// settings.gradle.kts (root)
include(":libs:KMP-Capsule")
project(":libs:KMP-Capsule").projectDir = File(rootDir, "libs/KMP-capsule/capsule")
```

### 3. Add Dependency

```kotlin
// composeApp/build.gradle.kts (commonMain)
commonMain.dependencies {
    implementation(project(":libs:KMP-Capsule"))
}
```

### 4. Initialize Submodule

```bash
git submodule update --init --recursive
```

## 🚀 Quick Start

Replace your rounded shapes with `ContinuousCapsule`:

```kotlin
import com.amp_digital.capsule.ContinuousCapsule

@Composable
fun MyCapsuleButton() {
    Box(
        modifier = Modifier
            .size(200.dp, 60.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { /* Handle click */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Capsule Button",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
```

## 📖 API Reference

### ContinuousCapsule

The main shape provided by KMP-Capsule.

```kotlin
object ContinuousCapsule : CornerBasedShape
```

**Type:** `CornerBasedShape`  
**Platform:** Android, iOS  
**Behavior:** Creates a perfect capsule that adapts to container dimensions

#### Usage

```kotlin
// Basic usage
Box(modifier = Modifier.clip(ContinuousCapsule))

// With background
Box(
    modifier = Modifier.background(
        color = Color.Blue,
        shape = ContinuousCapsule
    )
)
```

## 🏗️ Architecture

KMP-Capsule uses the expect/actual pattern for platform-specific implementations:

- **Android**: Delegates to original Kyant0 `ContinuousCapsule` for G2 continuity
- **iOS**: Uses `RoundedCornerShape(percent = 50)` for optimal performance
- **Common**: Provides unified API across platforms

## 📱 Platform Differences

| Feature | Android | iOS |
|---------|---------|-----|
| **Implementation** | Kyant0 ContinuousCapsule | RoundedCornerShape(50%) |
| **Continuity** | G2 continuous curves | G1 continuous |
| **Performance** | Hardware accelerated | Core Graphics optimized |
| **Features** | Advanced curvature profiles | Standard pill shape |

## 💻 Examples

### Basic Button

```kotlin
@Composable
fun CapsuleButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
```

### Navigation Bar

```kotlin
@Composable
fun CapsuleNavigationBar(
    items: List<NavigationItem>
) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            CapsuleNavItem(item = item)
        }
    }
}
```

### Card with Capsule Shape

```kotlin
@Composable
fun CapsuleCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ContinuousCapsule,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        content()
    }
}
```

## ⚡ Performance

### Android
- Uses efficient Bézier curve calculations from Kyant0
- Hardware-accelerated rendering
- Optimal for large shapes and frequent updates

### iOS
- Leverages native Core Graphics optimization
- Minimal computational overhead
- Excellent for high-frequency updates

### Best Practices
- Use for buttons, cards, and navigation elements
- Avoid very small elements (< 16dp)
- Test on both platforms for consistency

## 🔧 Troubleshooting

### Common Issues

**Error: "Unresolved reference: ContinuousCapsule"**
- Ensure you've added the dependency to `commonMain.dependencies`
- Import `com.amp_digital.capsule.ContinuousCapsule`

**Error: Submodule not found**
- Run `git submodule update --init --recursive`

**Platform differences**
- Test on both Android and iOS
- Check container dimensions

### Debug Tips

```kotlin
// Visualize shape boundaries
Modifier.border(1.dp, Color.Red, ContinuousCapsule)

// Check container dimensions
Modifier.onSizeChanged { size ->
    println("Container size: $size")
}
```

## 📚 Documentation

- [API Reference](docs/api.md) - Complete API documentation
- [Examples](docs/examples.md) - Comprehensive usage examples
- [GitHub Pages](https://lamplis.github.io/KMP-Capsule) - Beautiful documentation website

## 🙏 Credits

Huge thanks to [Kyant0](https://github.com/Kyant0) for the original work on Capsule and the deep thought put into G1/G2 continuity and performant curve construction. This project would not exist without their contribution to the Compose ecosystem.

### Original Library
- **Repository**: [Kyant0/Capsule](https://github.com/Kyant0/Capsule)
- **Author**: [Kyant0](https://github.com/Kyant0)
- **License**: Apache-2.0

### This Fork
- **Repository**: [lamplis/KMP-Capsule](https://github.com/lamplis/KMP-Capsule)
- **License**: Apache-2.0 (same as upstream)
- **Purpose**: KMP compatibility and cross-platform support

## 📄 License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

- [GitHub Issues](https://github.com/lamplis/KMP-Capsule/issues)
- [GitHub Discussions](https://github.com/lamplis/KMP-Capsule/discussions)

---

Made with ❤️ for the KMP community
