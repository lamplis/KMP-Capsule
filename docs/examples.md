# KMP-Capsule Examples

This document provides comprehensive examples of using KMP-Capsule in various scenarios.

## Basic Examples

### Simple Capsule Button

```kotlin
@Composable
fun SimpleCapsuleButton() {
    Box(
        modifier = Modifier
            .size(200.dp, 60.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { /* Handle click */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Click Me",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
```

### Capsule with Icon

```kotlin
@Composable
fun CapsuleWithIcon(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
```

## Navigation Examples

### Capsule Tab Bar

```kotlin
@Composable
fun CapsuleTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<TabItem>
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .clip(ContinuousCapsule)
            .background(
                MaterialTheme.colorScheme.surface
                    .copy(alpha = 0.9f)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            CapsuleTab(
                tab = tab,
                isSelected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun CapsuleTab(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(ContinuousCapsule)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = tab.title,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}
```

### Floating Navigation Bar

```kotlin
@Composable
fun FloatingCapsuleNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    items: List<NavigationItem>
) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .clip(ContinuousCapsule)
            .background(
                MaterialTheme.colorScheme.surface
                    .copy(alpha = 0.95f)
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            FloatingNavItem(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Composable
private fun FloatingNavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(ContinuousCapsule)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = item.label,
            fontSize = 10.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
        )
    }
}
```

## Card Examples

### Capsule Card

```kotlin
@Composable
fun CapsuleCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = ContinuousCapsule,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

### Capsule List Item

```kotlin
@Composable
fun CapsuleListItem(
    item: ListItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ContinuousCapsule)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

## Input Examples

### Capsule Text Field

```kotlin
@Composable
fun CapsuleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = ContinuousCapsule
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )
            
            trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### Capsule Search Bar

```kotlin
@Composable
fun CapsuleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )
            
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
```

## Animation Examples

### Animated Capsule Button

```kotlin
@Composable
fun AnimatedCapsuleButton(
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100)
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(100)
    )
    
    Box(
        modifier = Modifier
            .size(200.dp, 60.dp)
            .scale(scale)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
```

### Capsule Progress Indicator

```kotlin
@Composable
fun CapsuleProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(ContinuousCapsule)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(ContinuousCapsule)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
```

## Data Classes

```kotlin
data class TabItem(
    val title: String,
    val icon: ImageVector
)

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

data class ListItem(
    val title: String,
    val subtitle: String
)
```

## Usage Tips

1. **Consistent Sizing**: Use consistent heights for similar elements (e.g., 48.dp for buttons, 56.dp for text fields)

2. **Proper Spacing**: Use appropriate padding and spacing to ensure content doesn't touch the capsule edges

3. **Color Contrast**: Ensure sufficient contrast between background and content colors

4. **Accessibility**: Provide proper content descriptions for icons and interactive elements

5. **Platform Testing**: Test on both Android and iOS to ensure consistent appearance and behavior
