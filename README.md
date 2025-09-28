# Demo
[YouTube video](https://youtu.be/gfjFNEjtavs)

# Getting started
## Telegram bot
- Install Telegram
- Use @BotFather to create a new bot
- Retrieve token from the bot
- Create .env file in the root of the repository and add new variable TG_BOT_TOKEN with your token as a value.

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
