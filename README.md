# KMP-Capsule

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7C3AED?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose)](https://www.jetpackcompose.dev/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Kotlin Multiplatform port of the excellent [Kyant0/Capsule](https://github.com/Kyant0/Capsule) library, providing perfect capsule shapes for Compose Multiplatform.

## 🎯 What is KMP-Capsule?

KMP-Capsule extends the brilliant work of [Kyant0](https://github.com/Kyant0) to iOS and other platforms, maintaining the same API while providing platform-optimized implementations. While the original library is Android-only, KMP-Capsule brings perfect capsule shapes to your entire KMP project.

> Huge thanks to Kyant0 for the original work on Capsule and the deep thought put into G1/G2 continuity and performant curve construction. This project would not exist without their contribution to the Compose ecosystem.

## 📚 Documentation

- **[📖 Complete Documentation](docs/README.md)** - Comprehensive guide with examples
- **[🔧 API Reference](docs/api.md)** - Detailed API documentation  
- **[💻 Examples](docs/examples.md)** - Extensive usage examples
- **[🌐 GitHub Pages](https://lamplis.github.io/KMP-Capsule)** - Beautiful documentation website

## 🚀 Quick Start

### Installation

```bash
# Add as submodule
git submodule add https://github.com/lamplis/KMP-Capsule.git libs/KMP-capsule
```

```kotlin
// settings.gradle.kts (root)
include(":libs:KMP-Capsule")
project(":libs:KMP-Capsule").projectDir = File(rootDir, "libs/KMP-capsule/capsule")

// composeApp/build.gradle.kts (commonMain)
commonMain.dependencies {
    implementation(project(":libs:KMP-Capsule"))
}
```

### Usage

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

## ✨ Features

- 🎨 **Perfect Capsules**: Mathematically perfect capsule shapes with G1/G2 continuity
- 📱 **Cross-Platform**: Works seamlessly on Android and iOS
- ⚡ **High Performance**: Platform-specific optimizations for optimal rendering
- 🔧 **Easy Integration**: Simple submodule setup with expect/actual pattern

## 🙏 Credits

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

---

Made with ❤️ for the KMP community