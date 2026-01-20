package com.sspl.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sspl.platform.createGpsChecker
import com.sspl.platform.shared.PermissionCallback
import com.sspl.platform.shared.createPermissionsManager
import com.sspl.ui.postcreation.CameraPermissionDeniedDialog
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import shared.PermissionStatus
import shared.PermissionType
import shared.rememberCameraManager
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

//todo (working fine in this way,if don't want any thing related attendance
// type thing in caller place then we can add attendanceVm here and
// just pass the conferenceId and sessionId here but that causes irregular
// behavior as the dialogs will be inside too so once some dialog pops for the
// mark attendance by image it is triggered by no of items in the LazyColumn that needs to be fixed)
@Composable
fun MarkAttendanceButton(
    modifier: Modifier = Modifier,
    apiCoordinates: Coordinates,
    onMarkAttendance: () -> Unit,
    scaffoldState: SnackbarHostState,
    onMarkAttendanceByImage: (ByteArray) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    val locationPermissionController = remember { LocationPermissionController() }
    val geoLocation = remember { MobileGeolocator(locationPermissionController) }
    val gpsChecker = createGpsChecker()
    var launchCamera by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }
    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(
            permissionType: PermissionType, status: PermissionStatus
        ) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    when (permissionType) {
                        PermissionType.CAMERA -> launchCamera = true
                        else -> Unit
                    }
                }

                else -> {
                    permissionRationalDialog = true
                }
            }
        }
    })
    val cameraManager = rememberCameraManager {
        scope.launch {
            withContext(Dispatchers.Default) {
                it?.let { img ->
                    img.toByteArray()?.let { asByteArray ->
                        onMarkAttendanceByImage(asByteArray)
                    }
                }
            }
        }
    }
    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }
    if (launchSetting) {
        permissionsManager.launchSettings()
        launchSetting = false
    }
    if (permissionRationalDialog) {
        CameraPermissionDeniedDialog(onDismiss = { permissionRationalDialog = false },
            onOpenSettings = {
                permissionRationalDialog = false
                launchSetting = true
            })
    }

    var isGettingLocation by remember {
        mutableStateOf(false)
    }


    var locationState by remember { mutableStateOf(LocationState()) }

    LaunchedEffect(false) {
        if (locationState.isPermissionGranted) {
            if (gpsChecker.hasGps()) {
                scaffoldState.showSnackbar("Device Location Is On")


                isGettingLocation = true
                when (val result = geoLocation.current(Priority.HighAccuracy)) {
                    is GeolocatorResult.Error -> {
                        println(result.message)
                        scaffoldState.showSnackbar(result.message)
                        isGettingLocation = false
                    }

                    is GeolocatorResult.Success -> {
                        locationState = locationState.copy(
                            latitude = result.data.coordinates.latitude.toString(),
                            longitude = result.data.coordinates.longitude.toString()
                        )

                        val isWithinRadius = isWithinRadius(
                            apiLatitude = apiCoordinates.latitude,
                            apiLongitude = apiCoordinates.longitude,
                            currentLatitude = result.data.coordinates.latitude,
                            currentLongitude = result.data.coordinates.longitude
                        )


                        if (isWithinRadius) {
                            scaffoldState.showSnackbar("You are within the allowed radius.")
                            onMarkAttendance()
                            println("You are within the allowed radius.")
                        } else {
                            showCameraDialog = true
                            println("You are not within the allowed radius.")
                            scaffoldState.showSnackbar("You are outside the allowed radius")
                        }
                        isGettingLocation = false

                    }

                }
            } else {
                val snackbarResult = scaffoldState.showSnackbar(
                    message = "Device Location Off",
                    actionLabel = "Enable",
                    duration = SnackbarDuration.Indefinite,
                    withDismissAction = true
                )
                when (snackbarResult) {
                    SnackbarResult.ActionPerformed -> {
                        gpsChecker.requestEnableLocationServices()
                        locationState = locationState.copy(isPermissionGranted = false)

                    }

                    SnackbarResult.Dismissed -> {
                        locationState = locationState.copy(isPermissionGranted = false)

                    }
                }
            }

        }
    }

    if (locationState.isPermissionDeniedForever) {
        PermissionDeniedDialog(onDismiss = {
            locationState = locationState.copy(isPermissionDeniedForever = false)
        },
            onGoToSettings = {
                locationState = locationState.copy(isPermissionDeniedForever = false)
                launchSetting = true
            })
    }
    if (showCameraDialog) {
        CameraDialog(onDismiss = { showCameraDialog = false }, onConfirm = {
            showCameraDialog = false
            launchCamera = true
        })
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        AppTextLabel(
            text = "Mark Attendance",
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    requestPermissions(locationPermissionController = locationPermissionController,
                        onPermissionGranted = {
                            locationState = locationState.copy(isPermissionGranted = true)
                            if (locationState.isPermissionGranted) {
                                scope.launch {
                                    if (gpsChecker.hasGps()) {
                                        scaffoldState.showSnackbar("Looking for location...Please Wait")


                                        isGettingLocation = true
                                        when (val result =
                                            geoLocation.current(Priority.HighAccuracy)) {
                                            is GeolocatorResult.Error -> {
                                                println(result.message)
                                                scaffoldState.showSnackbar(result.message)
                                                isGettingLocation = false
                                            }

                                            is GeolocatorResult.Success -> {
                                                locationState = locationState.copy(
                                                    latitude = result.data.coordinates.latitude.toString(),
                                                    longitude = result.data.coordinates.longitude.toString()
                                                ).also {
                                                    isGettingLocation = false
                                                    println(result.data.coordinates.toString() + "result.data.coordinates")
                                                }

                                                val isWithinRadius = isWithinRadius(
                                                    apiLatitude = apiCoordinates.latitude,
                                                    apiLongitude = apiCoordinates.longitude,
                                                    currentLatitude = result.data.coordinates.latitude,
                                                    currentLongitude = result.data.coordinates.longitude
                                                )


                                                if (isWithinRadius) {
                                                    scaffoldState.showSnackbar("You are within the allowed radius.")
                                                    onMarkAttendance()
                                                    println("You are within the allowed radius.")
                                                } else {
                                                    showCameraDialog = true
                                                    println("You are not within the allowed radius.")
                                                    scaffoldState.showSnackbar("You are outside the allowed radius")
                                                }

                                            }

                                        }
                                    } else {
                                        val snackbarResult = scaffoldState.showSnackbar(
                                            message = "Device Location Off",
                                            actionLabel = "Enable",
                                            duration = SnackbarDuration.Indefinite,
                                            withDismissAction = true
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> {
                                                gpsChecker.requestEnableLocationServices()
                                                locationState =
                                                    locationState.copy(isPermissionGranted = false)

                                            }

                                            SnackbarResult.Dismissed -> {
                                                locationState =
                                                    locationState.copy(isPermissionGranted = false)

                                            }
                                        }
                                    }
                                }

                            }
                        },
                        onPermissionDenied = {
                            locationState = locationState.copy(isPermissionDenied = true)
                        },
                        onPermissionDeniedForever = {
                            locationState = locationState.copy(isPermissionDeniedForever = true)
                        },
                        onLocationUndetermined = {
                            locationState = locationState.copy(hasPhoneNoLocation = true)
                        })
                }

            })
        )
        AnimatedVisibility(isGettingLocation && locationState.latitude.isNullOrEmpty()) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp))
        }
    }

}

fun isWithinRadius(
    apiLatitude: Double,
    apiLongitude: Double,
    currentLatitude: Double,
    currentLongitude: Double,
    radiusInMeters: Double = 50.0
): Boolean {
    val earthRadius = 6371000.0

    // Convert latitude and longitude from degrees to radians
    val lat1 = apiLatitude.toRadians()
    val lon1 = apiLongitude.toRadians()
    val lat2 = currentLatitude.toRadians()
    val lon2 = currentLongitude.toRadians()

    // Haversine formula
    val dLat = lat2 - lat1
    val dLon = lon2 - lon1
    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Calculate the distance
    val distance = earthRadius * c

    // Check if the distance is within the specified radius
    return distance <= radiusInMeters
}

// Extension function to convert degrees to radians
fun Double.toRadians(): Double = this * PI / 180.0


@Composable
fun CameraDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(text = "Location Outside Radius") },
        text = { Text(text = "It seems your location is not in the specified radius. Please take a selfie to upload and mark your attendance.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        })
}
data class LocationState(
    val latitude: String? = null,
    val longitude: String? = null,
    val isPermissionGranted: Boolean = false,
    val hasPhoneNoLocation: Boolean = false,
    val isPermissionDenied: Boolean = false,
    val isPermissionDeniedForever: Boolean = false
)

suspend fun requestPermissions(
    locationPermissionController: LocationPermissionController,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermissionDeniedForever: () -> Unit,
    onLocationUndetermined: () -> Unit
) {
    val result = locationPermissionController.requirePermissionFor(Priority.HighAccuracy)
    println("result location is $result")
    when (result) {
        PermissionState.NotDetermined -> onLocationUndetermined()
        PermissionState.Granted -> onPermissionGranted()
        PermissionState.Denied -> onPermissionDenied()
        PermissionState.DeniedForever -> onPermissionDeniedForever()
    }
}
@Composable
fun PermissionDeniedDialog(onDismiss: () -> Unit, onGoToSettings: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppTextTitle("Location Permission Required") },
        text = { AppTextBody("This app needs location permission to function properly. Please grant the permission in Settings.") },
        confirmButton = {
            Button(onClick = onGoToSettings) {
                AppTextSubTitle("Go to Settings", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                AppTextSubTitle("Cancel", color = Color.White)
            }
        }
    )
}

