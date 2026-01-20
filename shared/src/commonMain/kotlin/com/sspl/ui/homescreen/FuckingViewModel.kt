package com.sspl.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.usecases.UpdateUserBasicInfoUseCase
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
class FuckingViewModel(
    private val updateUserBasicInfoUseCase: UpdateUserBasicInfoUseCase
) : ViewModel() {
    fun callMe(){
        println(">>> Fucking 1")
        viewModelScope.launch {
            println(">>> Fucking 2")
            updateUserBasicInfoUseCase.invoke(
                firstName = "Muhamamd",
                lastName = "Imran",
                phone = "+923216855879"
            ).collect {
                println(">>> $it")
            }
        }
    }
}