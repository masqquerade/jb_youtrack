package bot

import api.Api
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import data.repository.NotificationsRepository

class JBot (private val token: String,
            private var chatId: ChatId.Id = ChatId.fromId(-1),
            private val api: Api,) {
    fun createBot(): Bot {
        return bot {
            token = this@JBot.token
            logLevel = LogLevel.Network.Body

            dispatch {
                command("start") {
                    chatId = ChatId.fromId(update.message!!.chat.id)
                }

                command("createIssue") {
                    val summary = args[0]
                    val projectName = args[1]
                    println(summary)
                    println(projectName)
                    api.createIssue(summary, projectName)
                }
            }
        }
    }

    fun sendMsg(bot: Bot, msg: String) {
        bot.sendMessage(this.chatId, msg)
    }

}