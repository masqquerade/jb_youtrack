package data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val name: String,
    @SerialName("\$type") val type: String,
)
