# KMP-Capsule API Reference

## Overview

KMP-Capsule provides a single, platform-agnostic API for creating perfect capsule shapes in Compose Multiplatform.

## Package

```kotlin
package com.amp_digital.capsule
```

## Main API

### ContinuousCapsule

The primary shape provided by KMP-Capsule.

```kotlin
object ContinuousCapsule : CornerBasedShape
```

**Type:** `CornerBasedShape`  
**Platform:** Android, iOS  
**Behavior:** Creates a perfect capsule that adapts to container dimensions

#### Usage

```kotlin
import com.amp_digital.capsule.ContinuousCapsule

// Basic usage
Box(
    modifier = Modifier
        .size(200.dp, 60.dp)
        .clip(ContinuousCapsule)
        .background(Color.Blue)
)

// With background shape
Box(
    modifier = Modifier
        .size(200.dp, 60.dp)
        .background(
            color = Color.Blue,
            shape = ContinuousCapsule
        )
)
```

## Platform Implementations

### Android Implementation

```kotlin
// libs/KMP-capsule/capsule/src/androidMain/kotlin/com/amp_digital/capsule/ContinuousCapsule.android.kt
actual object ContinuousCapsule {
    actual val capsule: CornerBasedShape = Kyant0ContinuousCapsule()
    actual fun capsule(cornerRadius: Dp): CornerBasedShape = Kyant0ContinuousCapsule(cornerRadius)
    actual fun capsule(percent: Int): CornerBasedShape = Kyant0ContinuousCapsule(percent)
}
```

**Features:**
- G2 continuous curves
- Advanced curvature profiles
- Hardware-accelerated rendering
- Delegates to original Kyant0 library

### iOS Implementation

```kotlin
// libs/KMP-capsule/capsule/src/iosMain/kotlin/com/amp_digital/capsule/ContinuousCapsule.ios.kt
actual object ContinuousCapsule {
    actual val capsule: CornerBasedShape = RoundedCornerShape(percent = 50)
    actual fun capsule(cornerRadius: Dp): CornerBasedShape = RoundedCornerShape(cornerRadius)
    actual fun capsule(percent: Int): CornerBasedShape = RoundedCornerShape(percent = percent)
}
```

**Features:**
- G1 continuous curves
- Native iOS optimization
- Core Graphics acceleration
- Consistent performance

## Common Patterns

### Button Shapes

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

### Card Shapes

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

### Navigation Elements

```kotlin
@Composable
fun CapsuleTabBar(
    tabs: List<TabItem>
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            CapsuleTab(tab = tab)
        }
    }
}
```

## Performance Considerations

### Android
- Use for buttons, cards, and navigation elements
- Excellent for large shapes (> 32dp)
- Consider G1 continuity for animations if needed

### iOS
- Optimal for all size ranges
- Excellent for high-frequency updates
- Consistent performance across iOS versions

### Best Practices
- Use `clip()` for content clipping
- Use `background(shape = ...)` for background shapes
- Avoid very small elements (< 16dp)
- Test on both platforms for consistency

## Migration Guide

### From RoundedCornerShape

```kotlin
// Before
RoundedCornerShape(percent = 50)

// After
ContinuousCapsule
```

### From Custom Shapes

```kotlin
// Before
CustomShape { size ->
    // Complex shape logic
}

// After
ContinuousCapsule // Much simpler!
```

## Troubleshooting

### Common Issues

1. **Unresolved reference**: Ensure proper import and dependency
2. **Build errors**: Check submodule initialization
3. **Platform differences**: Test on both Android and iOS
4. **Performance issues**: Consider platform-specific optimizations

### Debug Tips

```kotlin
// Visualize shape boundaries
Modifier.border(1.dp, Color.Red, ContinuousCapsule)

// Check container dimensions
Modifier.onSizeChanged { size ->
    println("Container size: $size")
}
```
