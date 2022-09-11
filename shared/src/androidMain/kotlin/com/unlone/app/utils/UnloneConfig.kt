package com.unlone.app.utils

import com.unlone.app.BuildConfig


actual val unloneConfig: UnloneConfig = when (BuildConfig.BUILD_TYPE){
    "debug" -> UnloneConfig.Development
    "release"-> UnloneConfig.Development
    "staging"-> UnloneConfig.Staging
    else ->  UnloneConfig.Development
}