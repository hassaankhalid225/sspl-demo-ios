package com.sspl.core.apis

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/scenarios")
class ScenarioRequest {
    @Serializable
    @Resource("sessions/{joinCode}/join")
    data class Join(val parent: ScenarioRequest = ScenarioRequest(), val joinCode: String)
    
    @Serializable
    @Resource("sessions/{joinCode}/auto-join")
    data class AutoJoin(val parent: ScenarioRequest = ScenarioRequest(), val joinCode: String)
}
