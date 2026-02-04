package com.captainslog.mode

enum class AppMode {
    STANDALONE,  // No server configured, local-only operation
    CONNECTED    // Server configured with valid JWT token
}
