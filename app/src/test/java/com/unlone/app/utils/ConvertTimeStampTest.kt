package com.unlone.app.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ConvertTimeStampTest {
    @Test
    fun convertTimeStamp_toDefaultFormat() {
        assertThat(convertTimeStamp("1637941923000", "en")).isEqualTo("23:52   26th Nov")
    }
    @Test
    fun convertTimeStamp_toNullFormat() {
        assertThat(convertTimeStamp("1637941923000", "null")).isEqualTo("23:52   26th Nov")
    }
    @Test
    fun convertTimeStamp_toCommentFormat() {
        assertThat(convertTimeStamp("1637941923000", "COMMENT")).isEqualTo("26-11-2021   23:52")
    }
    @Test
    fun convertTimeStamp_toChineseFormat() {
        assertThat(convertTimeStamp("1637941923000", "zh")).isEqualTo("11月26日   |   23:52")
    }
}