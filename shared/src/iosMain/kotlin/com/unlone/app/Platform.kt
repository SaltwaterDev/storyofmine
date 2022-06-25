package com.unlone.app

import io.ktor.client.engine.darwin.*
import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual val httpClientEngine = Darwin.create{
    configureRequest {
        setAllowsCellularAccess(true)
    }
}