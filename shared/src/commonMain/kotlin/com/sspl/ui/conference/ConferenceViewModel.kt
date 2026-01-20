package com.sspl.ui.conference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.Conference
import com.sspl.core.models.ConferenceType
import com.sspl.core.usecases.GetConferencesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/01/2025.
 * se.muhammadimran@gmail.com
 */
class ConferenceViewModel(
    private val conferenceRepository: GetConferencesUseCase
) : ViewModel() {

    private val _conferences = MutableStateFlow<ApiStates<List<Conference>>>(ApiStates.Loading)
    val conferences = _conferences.asStateFlow()

    //
//    fun getConferenceById(conferenceId: Int) {
//        viewModelScope.launch {
//            conferenceRepository.getConferenceById(conferenceId,"days").flowOn(Dispatchers.IO).collect { responseState->
//                println("Conference Response is $responseState")
//                _conferences.update { responseState }
//            }
//        }
//    }

    fun getAllConferences(type: ConferenceType = ConferenceType.ALL) {
        viewModelScope.launch {
            println(">>>Conference Request:Type =$type")
            conferenceRepository.getAllConferences(type = type).flowOn(Dispatchers.IO)
                .collect { responseState ->
                    println("Conference Response is $responseState")
                    _conferences.update { responseState }
                }
        }
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