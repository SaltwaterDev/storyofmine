package com.example.app

import io.ktor.client.engine.okhttp.*
import java.util.concurrent.TimeUnit


actual val httpClientEngine = OkHttp.create {
    config {
        retryOnConnectionFailure(true)
        connectTimeout(0, TimeUnit.SECONDS)
    }
}

actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
