package com.amp_digital.capsule

import androidx.compose.foundation.shape.CornerBasedShape

// Expect a platform-provided capsule shape.
// Android actual delegates to Kyant0/Capsule original implementation.
// iOS actual uses RoundedCornerShape(percent = 50) for a pill shape.
expect val ContinuousCapsule: CornerBasedShape
