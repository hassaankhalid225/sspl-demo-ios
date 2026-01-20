package com.sspl.core.models

import org.jetbrains.compose.resources.DrawableResource

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/02/2025.
 * se.muhammadimran@gmail.com
 */
data class SocietyMember(
    val name: String,
    val designation: String,
    val picture: DrawableResource,
    val message: String = ""
)
