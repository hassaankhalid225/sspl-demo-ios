package com.sspl.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.models.SocietyMember
import com.sspl.resources.Res
import com.sspl.resources.dr_amir_sohail_publication_secretary
import com.sspl.resources.dr_maila_altaf_joint_secretary
import com.sspl.resources.dr_salman_majeed_finance_sectary
import com.sspl.resources.dr_shahid_farooq_general_secretary
import com.sspl.resources.prof_abdul_hameed
import com.sspl.resources.prof_abul_fazal_ali_khan
import com.sspl.resources.prof_ahmed_uzair_qureshi
import com.sspl.resources.prof_ajmal_farooq
import com.sspl.resources.prof_faisal_masood
import com.sspl.resources.prof_farooq_afzal
import com.sspl.resources.prof_farooq_ahmad_rana
import com.sspl.resources.prof_imran_aslam
import com.sspl.resources.prof_kamran
import com.sspl.resources.prof_kamran_khalid_khwaja
import com.sspl.resources.prof_khizar_hayat_gondal
import com.sspl.resources.prof_nabila_talat
import com.sspl.resources.prof_nadeem_aslam
import com.sspl.resources.prof_qamar_ashfaq_ahmed_vp
import com.sspl.resources.prof_tayyab_abbas
import com.sspl.resources.prof_waris_farooka_president
import com.sspl.resources.prof_yar_muhammad
import com.sspl.resources.ahmad_qasim
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/02/2025.
 * se.muhammadimran@gmail.com
 */
class AboutSocietyViewModel : ViewModel() {
    private val _executiveMembers = MutableStateFlow<List<SocietyMember>>(emptyList())
    val executiveMembers: StateFlow<List<SocietyMember>> = _executiveMembers.asStateFlow()

    private val _officeBearers = MutableStateFlow<List<SocietyMember>>(emptyList())
    val officeBearers: StateFlow<List<SocietyMember>> = _officeBearers.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _executiveMembers.update { getExecutiveMembers() }
            _officeBearers.update { getOfficeBearers() }
        }
    }
}

private fun getExecutiveMembers(): List<SocietyMember> {
    val executiveMembers = listOf<SocietyMember>(
        SocietyMember(
            name = "Prof. M. Waris Farooka",
            designation = "President",
            picture = Res.drawable.prof_waris_farooka_president
        ),
        SocietyMember(
            name = "Prof. Qamar Ashfaq Ahmed",
            designation = "Vice President",
            picture = Res.drawable.prof_qamar_ashfaq_ahmed_vp
        ),
        SocietyMember(
            name = "Dr. M. Shahid Farooq",
            designation = "General Secretary",
            picture = Res.drawable.dr_shahid_farooq_general_secretary
        ),
        SocietyMember(
            name = "Dr. Maila Altaf",
            designation = "Joint Secretary",
            picture = Res.drawable.dr_maila_altaf_joint_secretary
        ),
        SocietyMember(
            name = "Dr. Salman Majeed Chaudry",
            designation = "Finance Secretary",
            picture = Res.drawable.dr_salman_majeed_finance_sectary
        ),
        SocietyMember(
            name = "Dr. M. Amir Sohail",
            designation = "Publication Secretary",
            picture = Res.drawable.dr_amir_sohail_publication_secretary
        )
    )
    return executiveMembers
}

private fun getOfficeBearers(): List<SocietyMember> {
    val executiveMembers = listOf<SocietyMember>(
        SocietyMember(
            name = "Prof. Ahmed Uzair Qureshi",
            designation = "Executive Member",
            picture = Res.drawable.prof_ahmed_uzair_qureshi
        ),
        SocietyMember(
            name = "Prof. M. Farooq Afzal",
            designation = "Executive Member",
            picture = Res.drawable.prof_farooq_afzal
        ),
        SocietyMember(
            name = "Prof. Tayyab Abbas",
            designation = "Executive Member",
            picture = Res.drawable.prof_tayyab_abbas
        ),
        SocietyMember(
            name = "Prof. Kamran Khalid Khwaja",
            designation = "Executive Member",
            picture = Res.drawable.prof_kamran_khalid_khwaja
        ),
        SocietyMember(
            name = "Prof. Imran Aslam",
            designation = "Executive Member",
            picture = Res.drawable.prof_imran_aslam
        ),
        SocietyMember(
            name = "Prof. Faisal Masood",
            designation = "Executive Member",
            picture = Res.drawable.prof_faisal_masood
        ),
        SocietyMember(
            name = "Prof. Ajmal Farooq",
            designation = "Executive Member",
            picture = Res.drawable.prof_ajmal_farooq
        ),
        SocietyMember(
            name = "Prof. Ch. M. Kamran",
            designation = "Executive Member",
            picture = Res.drawable.prof_kamran
        ),
        SocietyMember(
            name = "Prof. Yar Muhammad",
            designation = "Executive Member",
            picture = Res.drawable.prof_yar_muhammad
        ),
        SocietyMember(
            name = "Prof. Abdul Hameed",
            designation = "Executive Member",
            picture = Res.drawable.prof_abdul_hameed
        ),
        SocietyMember(
            name = "Prof. Khizar Hayat Gondal",
            designation = "Executive Member",
            picture = Res.drawable.prof_khizar_hayat_gondal
        ),
        SocietyMember(
            name = "Prof. Nabila Talat",
            designation = "Executive Member",
            picture = Res.drawable.prof_nabila_talat
        ),
        SocietyMember(
            name = "Prof. Abul Fazal Ali Khan",
            designation = "Executive Member",
            picture = Res.drawable.prof_abul_fazal_ali_khan
        ),
        SocietyMember(
            name = "Prof. Farooq Ahmad Rana",
            designation = "Executive Member",
            picture = Res.drawable.prof_farooq_ahmad_rana
        ),
        SocietyMember(
            name = "Prof. M. Nadeem Aslam",
            designation = "Executive Member",
            picture = Res.drawable.prof_nadeem_aslam
        ),
        SocietyMember(
            name = "Ahmad Qasim",
            designation = "Society Secretary",
            picture = Res.drawable.ahmad_qasim
        )
    )
    return executiveMembers
}