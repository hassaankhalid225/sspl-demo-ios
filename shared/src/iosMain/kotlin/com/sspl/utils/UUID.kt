package com.sspl.utils

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
