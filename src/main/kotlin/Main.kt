import api.Api
import bot.JBot
import data.repository.ChangeResult
import data.repository.NotificationsRepository
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.full.memberProperties

val dotenv = dotenv()

fun main(): kotlin.Unit = runBlocking {
    val api = Api(dotenv["YOUTRACK_URL"], dotenv["YOUTRACK_TOKEN"])
    val userInformation = api.getUserInformation()
    val username = userInformation.name.replace(" ", "_")
    val repo = NotificationsRepository(api, 0, username = username)

    val jbot = JBot(dotenv["TG_BOT_TOKEN"], api=api)
    val bot = jbot.createBot()
    bot.startPolling()

    launch {
        while (true) {
            try {
                val change = repo.updateIssues()
                val assignmentChangeOrNull = change.assignmentChange
                val mentionChange = change.mentionsChange
                val fieldChange = change.fieldChange

                assignmentChangeOrNull.forEach {
                    if (it != null) {
                        jbot.sendMsg(bot, "New assignment in ${it.issueName} (${it.issueId}):\n${it.old} -> ${it.new}")
                    }
                }

                mentionChange.forEach {
                    it.forEach { mention ->
                        jbot.sendMsg(bot, "Your username $username was mentioned in issue: ${mention.issueName}")
                    }
                }

                fieldChange.forEach {
                    it.forEach { field ->
                        if (field.comment != null) {
                            jbot.sendMsg(bot, "New comment from ${field.comment?.author?.login} in ${field.issueName}:\n${field.comment?.text}")
                        } else {
                            jbot.sendMsg(bot, "Issue ${field.issueName} updated:\n[${field.name}]: ${field.old} -> ${field.new}")
                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
            }

            delay(3000)
        }
    }
}