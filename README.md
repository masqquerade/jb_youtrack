# Demo
[YouTube video](https://youtu.be/OaiNx6z6TXI)
### Logs (the same as in the video):
- Your username Artem_Mozoilov was mentioned in issue: Test issue #2
- New assignment in Test issue #2 (3-87): Nobody -> Davyd_Lazarev
- New assignment in Test issue #2 (3-87): Davyd_Lazarev -> Artem_Mozoilov
- New assignment in Test issue #2 (3-87): Artem_Mozoilov -> admin
- Your username Artem_Mozoilov was mentioned in issue: Test issue #2
- New comment from Artem_Mozoilov in Test issue #2: @Artem_Mozoilov
- New comment from Artem_Mozoilov in Test issue #2: Hi guys!

# Features
- Track changes in favourite issues: Update of Priority, Type, State, Assignee fields of the issue will lead to the notification.
- Track mentions: Even if you don't starred an issue, you will be notified, if you was assigned or unassigned.
- Creation of issues from the bot: The bot can create issues by specifying the project name and the issue summary.

# Commands
- /createIssue SUMMARY PROJECT_NAME

# Getting started
## Telegram bot
- Install Telegram Client
- Use @BotFather to create a new bot
- Retrieve the token of your bot from @BotFather

## YouTrack
- Create new Permanent Token for your account

## .env configuration
- Insert your telegram bot token into TG_BOT_TOKEN variable.
- Insert your YouTrack Permanent Token into YOUTRACK_TOKEN variable.
- Insert the URL of your YouTrack instance (e.g. https://instance.youtrack.cloud) into YOUTRACK_URL variable.

## Starting the notifications system
- Download dependencies of the project.
- Run Main.kt and wait until telegram sends first response (check the console).
- Send __/start__ command to your telegram bot.

__Now you will be notified, if something changes.__

# Trade-offs & Future Improvements
- Due to limited time (I pushed my solution in the last 4 minutes) and unfamiliarity with Kotlin and the YouTrack API, I had to use some non-idiomatic and non-clear techniques to get things working quickly,
- For example, all data classes are stored in one single file (Issue.kt), which is also incorrectly named considering its purposes.
- With more time, I would refactor the project structure diving the modules more cleanly and idiomatic. For instance, the entry point (main.kt) should not contain message-management logic as it currently does.
- Initially, I implemented the logic using __/api/notifications__ endpoint, which at first time seemed ideal for retrieving all kinds of updates. However, I ran into a problem: it did not allow me to identify the recipient of specific notification. This API appears to function more like a shared notification pool and can only be used with administrator rights.
