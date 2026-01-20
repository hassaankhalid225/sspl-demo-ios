package com.sspl.ui.conference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.ConferenceDetail
import com.sspl.core.usecases.GetConferencesDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ConferenceDetailViewModel(
    private val conferenceRepository: GetConferencesDetailsUseCase
) : ViewModel() {

    private val _conferences = MutableStateFlow<ApiStates<ConferenceDetail>>(ApiStates.Loading)
    val conferences = _conferences.asStateFlow()

    //
    fun getConferenceById(conferenceId: Long) {
        viewModelScope.launch {
            conferenceRepository.getConferenceById(conferenceId,"days").flowOn(Dispatchers.IO).collect { responseState->
                println("Conference Response is $responseState")
                _conferences.update { responseState }
            }
        }
    }

init {

    println("init called")
}

//    @OptIn(ExperimentalResourceApi::class)
//    fun loadData() {
//        viewModelScope.launch(context = Dispatchers.IO) {
//            val readBytes = Res.readBytes("files/conferences.json")
//            val json = readBytes.decodeToString()
//            val data = Json.decodeFromString<List<Conference>>(json)
//            _conferences.update { data+data+data+data+data }
//        }
//    }
}