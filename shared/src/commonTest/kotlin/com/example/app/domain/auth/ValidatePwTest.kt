package com.example.app.domain.auth

import com.example.app.domain.useCases.auth.ValidPasswordUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class ValidatePwTest : FunSpec({

    val useCase = ValidPasswordUseCase()

    test("validate pw success") {
        useCase.invoke("1234QWEe").shouldBeTrue()
        useCase.invoke("1234QWEe!1!").shouldBeTrue()
    }


    test("validate pw fail") {
        useCase.invoke("").shouldBeFalse()
        useCase.invoke("12341234").shouldBeFalse()
        useCase.invoke("1123Aa").shouldBeFalse()
        useCase.invoke("1243aaaa").shouldBeFalse()
        useCase.invoke("1243aaaa").shouldBeFalse()
    }
})
