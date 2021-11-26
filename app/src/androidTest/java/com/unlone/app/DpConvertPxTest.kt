package com.unlone.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unlone.app.utils.dpConvertPx
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DpConvertPxTest {

    private val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun dpConvertPxTest(){
        assertThat(dpConvertPx(10, context)).isGreaterThan(0)
    }
}