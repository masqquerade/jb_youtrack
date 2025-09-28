# Demo
[YouTube video](https://youtu.be/IYPfDRw8lI4) (Sorry, I found out that this demo is laggy. Please, see the logs below)
### Logs:
- Your username Artem_Mozoilov was mentioned in issue: Test issue #2
- New assignment in Test issue #2 (3-87): Nobody -> Davyd_Lazarev
- New assignment in Test issue #2 (3-87): Davyd_Lazarev -> Artem_Mozoilov
- New assignment in Test issue #2 (3-87): Artem_Mozoilov -> admin
- Your username Artem_Mozoilov was mentioned in issue: Test issue #2
- New comment from Artem_Mozoilov in Test issue #2: @Artem_Mozoilov
- New comment from Artem_Mozoilov in Test issue #2: Hi guys!

# Getting started
## Telegram bot
- Install Telegram
- Use @BotFather to create a new bot
- Retrieve token from the bot
- Create .env file in the root of the repository or use demo .env file, which is already presented, and add new variable TG_BOT_TOKEN with your token as a value.

## YouTrack
- Create new permanent token
- Add new variable in .env file YOUTRACK_TOKEN with your token as a value.
- Add new variable in .env file YOUTRACK_URL with your YouTrack instance URL as a value (for example: https://testimg.youtrack.cloud)

## Starting the notifications system
- Run Main.kt and wait until telegram sends first response (check the console)
- Type /start command to the telegram bot
Now you will be notified, if something changes.

# Features
- Track changes in favourite issues
- Track mentions
- Creation of issues from the bot

# Commands
- /createIssue SUMMARY PROJECT_NAME
