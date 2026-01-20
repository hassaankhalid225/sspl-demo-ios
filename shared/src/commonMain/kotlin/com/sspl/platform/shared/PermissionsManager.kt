package com.sspl.platform.shared

import androidx.compose.runtime.Composable
import shared.PermissionHandler
import shared.PermissionStatus
import shared.PermissionType

//abstract class PermissionsManager(callback: PermissionCallback) : PermissionHandler

interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}

@Composable
expect fun createPermissionsManager(callback: PermissionCallback): PermissionHandler