package com.unlone.app.utils


actual val unloneConfig: UnloneConfig = if (Platform.isDebugBinary) UnloneConfig.Development else UnloneConfig.Staging