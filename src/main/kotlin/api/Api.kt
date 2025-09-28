package api

import data.models.Issue
import data.models.IssueRequest
import data.models.Project
import data.models.ProjectRef
import data.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPInputStream
import kotlin.collections.mapOf
import kotlin.io.encoding.Base64

fun millisToIso(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    return DateTimeFormatter.ISO_INSTANT.format(instant) // "2025-09-27T18:45:05.080Z"
}

class Api(private val baseUrl: String, private val apiToken: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(apiToken, "")
                }
            }
        }
    }

    suspend fun getIssues(): MutableList<Issue> {
        val textBody = client.get("$baseUrl/api/issues?fields=updated,id,summary,description,comments(id,text,author(login),created),customFields(name,value(name,login))&customFields=type&customFields=assignee") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }.bodyAsText()

        return Json { ignoreUnknownKeys = true }
            .decodeFromString(ListSerializer(serializer<Issue>()), textBody) as MutableList<Issue>
    }

    suspend fun getStarredIssues(): List<Issue> {
        val textBody = client.get("$baseUrl/api/issues?fields=updated,id,summary,description,comments(id,text,author(login),created),customFields(name,value(name,login))&customFields=type&customFields=assignee&customFields=priority&customFields=type&customFields=state&query=has:star") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }.bodyAsText()

        val issues = Json { ignoreUnknownKeys = true }
            .decodeFromString(ListSerializer(kotlinx.serialization.serializer<Issue>()), textBody)
            .map {
                it.starred = true
                it
            }

        return issues
    }

    suspend fun getUserInformation(): User {
        return client.get("${baseUrl}/api/users/me?fields=id,name") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }.body()
    }

    suspend fun createIssue(summary: String, name: String) {
        val textBody = client.get("$baseUrl/api/admin/projects?fields=id,name") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }.bodyAsText()

        val project = Json { ignoreUnknownKeys = true }
            .decodeFromString(ListSerializer(kotlinx.serialization.serializer<ProjectRef>()), textBody)
            .find { it.name == name }

        val issueRequest = IssueRequest(
            project = project,
            summary = summary
        )

        println(issueRequest)

        client.post("${baseUrl}/api/issues") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            contentType(ContentType.Application.Json)
            setBody(issueRequest)
        }
    }
}