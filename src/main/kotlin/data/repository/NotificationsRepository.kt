package data.repository

import api.Api
import data.models.Comment
import data.models.Issue
import data.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class ChangeItem(
    val old: String? = null,
    val new: String? = null,
    val issueId: String? = null,
    val issueName: String? = null,
    var name: String? = null,
    var comment: Comment? = null,
)

data class ChangeResult(
    var assignmentChange: MutableList<ChangeItem?> = mutableListOf(),
    var mentionsChange: MutableList<List<ChangeItem>> = mutableListOf(),
    var fieldChange: MutableList<List<ChangeItem>> = mutableListOf(),
)


class NotificationsRepository(
    private val api: Api,
    private var lastTimestamp: Long,
    private val previousIssuesState: MutableMap<String, Issue> = mutableMapOf(),
    private var counter: Int = 0,
    private val username: String,
) {
    suspend fun updateIssues(): ChangeResult = coroutineScope {
        counter++

        val issuesDeferred = async { api.getIssues() }
        val starredIssuesDeferred = async { api.getStarredIssues() }

        val issues = issuesDeferred.await()
        val starredIssues = starredIssuesDeferred.await()

        val merged: List<Issue> = (issues + starredIssues)
            .groupBy { it.id }
            .map { (_, iss) ->
                iss.find { it.starred != null } ?: iss.first()
            }

        val changeResult = ChangeResult()

        merged.forEach { issue ->
            val old = previousIssuesState[issue.id]

            if (old != null || (counter > 1)) {
                val assignmentChange = detectAssignmentChanges(old, issue)
                val mentionsChange = detectMentionsChanges(old, issue)
                val fieldChange = detectOtherFieldsChangesStarred(old, issue)

                changeResult.assignmentChange.add(assignmentChange)
                changeResult.mentionsChange.add(mentionsChange)
                changeResult.fieldChange.add(fieldChange)
            }

            previousIssuesState[issue.id] = issue
        }

        changeResult
    }

    fun detectNewComments(old: Issue, new: Issue): List<Comment> {
        val oldIds = old.comments.map { it.id }.toSet()
        return new.comments.filter { it.id !in oldIds }
    }

    fun detectOtherFieldsChangesStarred(old: Issue?, new: Issue): MutableList<ChangeItem> {
        if (old == null) {
            return mutableListOf()
        }

        val changes = mutableListOf<ChangeItem>()

        if (new.starred == true) {
            val newComments = detectNewComments(old, new)

            newComments.forEach {
                changes.add(ChangeItem(comment = it, issueId = new.id, issueName = new.summary))
            }

            val oldPriority = old.customFields.find { it.name == "Priority" }
            val newPriority = new.customFields.find { it.name == "Priority" }

            val oldType = old.customFields.find { it.name == "Type" }
            val newType = new.customFields.find { it.name == "Type" }

            val oldState = old.customFields.find { it.name == "State" }
            val newState = new.customFields.find { it.name == "State" }

            if (oldType != newType && oldType != null) {
                changes.add(ChangeItem(oldType.value?.name, newType?.value?.name, new.summary, name = newType?.name))
            }

            if (oldPriority != newPriority && oldPriority != null) {
                println("here")
                changes.add(ChangeItem(oldPriority.value?.name, newPriority?.value?.name, new.id, new.summary, name = newPriority?.name))
            }

            if (oldState != newState && oldState != null) {
                changes.add(ChangeItem(oldState.value?.name, newState?.value?.name, new.id, new.summary, name = newState?.name))
            }
        }

        return changes
    }

    fun detectAssignmentChanges(old: Issue?, new: Issue): ChangeItem? {
        val newAssignee = new.customFields.find { it.name == "Assignee" }?.value?.login

        if (old == null) {
            if (!newAssignee.isNullOrEmpty() &&
                (newAssignee == username) || new.starred == true) {
                return ChangeItem("Nobody", newAssignee, new.id, new.summary)
            }

            return null
        }

        val oldAssignee = old.customFields.find { it.name == "Assignee" }?.value?.login

        if (oldAssignee != newAssignee &&
            ((oldAssignee == username ||
            newAssignee == username) || new.starred == true)) {
            if (oldAssignee.isNullOrEmpty() && !newAssignee.isNullOrEmpty()) {
                return ChangeItem("Nobody", newAssignee, new.id, new.summary)
            }

            if (!oldAssignee.isNullOrEmpty() && !newAssignee.isNullOrEmpty()) {
                return ChangeItem(oldAssignee, newAssignee, new.id, new.summary)
            }

            if (!oldAssignee.isNullOrEmpty() && newAssignee.isNullOrEmpty()) {
                return ChangeItem(oldAssignee, "nobody", new.id, new.summary)
            }
        }

        return null
    }

    fun detectMentionsChanges(old: Issue?, new: Issue): List<ChangeItem> {
        val mentions = mutableListOf<ChangeItem>()
        val newDesc = new.description ?: ""

        if (old == null) {
            if (newDesc.contains("@$username")) {
                mentions.add(ChangeItem(issueId = new.id, issueName = new.summary))
            }

            return mentions
        }

        // Description check
        val oldDesc = old.description ?: ""
        if (newDesc.contains("@$username") && !oldDesc.contains("@$username")) {
            mentions.add(ChangeItem(issueId = new.id, issueName = new.summary))
        }

        // Comments
        val oldComments = old.comments.map { it.text }
        val newComments = new.comments.map { it.text }
        newComments.forEachIndexed { index, text ->
            val oldText = oldComments.getOrNull(index) ?: ""
            if (text.contains("@$username") && !oldText.contains("@$username")) {
                mentions.add(ChangeItem(issueId = new.id, issueName = new.summary))
            }
        }

        return mentions
    }

    fun updateLastTimestamp(newTimestamp: Long) {
        lastTimestamp = newTimestamp
    }
}