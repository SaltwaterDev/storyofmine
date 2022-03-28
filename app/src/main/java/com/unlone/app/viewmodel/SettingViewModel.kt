package com.unlone.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    fun setNewLocale(context: Context, language: String): Boolean {
        return try {
            viewModelScope.launch {
                userRepository.setNewLocale(language, context)
            }
            true
        } catch (e: Exception) {
            false
        }
    }


}