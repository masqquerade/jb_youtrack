package data.models

import io.ktor.util.collections.StringMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Issue(
    val id: String,
    val summary: String,
    val updated: Long,
    @SerialName("\$type")
    val type: String? = null,
    val customFields: List<CustomField>,
    val comments: List<Comment> = emptyList(),
    val description: String? = null,
    val priority: String? = null,
    val state: String? = null,
    var starred: Boolean? = null,
)

@Serializable
data class CustomField(
    val name: String,
    @SerialName("\$type")
    val type: String,
    val value: FieldValue? = null
)

@Serializable
data class FieldValue(
    val name: String? = null,
    @SerialName("\$type")
    val type: String,
    val login: String? = null
)

@Serializable
data class Author(
    val login: String,
    @SerialName("\$type") val type: String,
)

@Serializable
data class Comment(
    val author: Author,
    val text: String,
    val created: Long,
    val id: String,
    @SerialName("\$type") val type: String,
)

@Serializable
data class User(
    val name: String,
    val id: String,
    @SerialName("\$type") val type: String,
)

@Serializable
data class ProjectRef(
    val id: String,
    val name: String,
    @SerialName("\$type") val type: String,
)

@Serializable
data class IssueRequest(
    val project: ProjectRef? = null,
    val summary: String
)