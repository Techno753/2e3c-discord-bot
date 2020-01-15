# 2E3CDiscordBot
Personal discord bot to practice Java and APIs

## Quickstart
1. Add the bot to your server by visiting: https://discordapp.com/api/oauth2/authorize?client_id=664621951037472798&permissions=0&scope=bot
2. Set channels where users can use bot commands in (bot channels) with `^addbc #channel`
3. Set users who can use admin commands with `^addba @user`
4. Optionally, change the command prefix with `^prefix <prefix>`
5. You're set!

## Commands

### Bot Admins
Bot Admins are server owners and other users set by the server owner that can invoke elevated commands. Commands that require the user to be a Bot Admin will be marked with "BA" in the Permissions columns.

### Bot Channels
Bot Channels are channels set by Bot Admins that allow non-Bot Admin users to use commands.

### Command Prefix
The default command prefix is (^). Commands listed here will use the default prefix however this can be changed for a server by Bot Admin of that server.

### Command Parameters
Some commands allow the user to pass in parameters. These will be shown between < > symbols.

### Commands
#### Generic Commands
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^ping | User |  Replies with "Pong!". Basic command to check that the bot is listening. | 
^hello | User | Replies with "World!". Wouldn't be a program without a "Hello, World!" reference. | 
^pa | BA | Pings Apple on Discord. Hi, Apple! | 

#### RNG Commands
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^coin | User | Flips a coin and replies "Heads" or "Tails". |
^dice | User | Rolls a dice and replies the result. | 
^d\<n1\> | User | Rolls an n1 sided dice and replies the result. | ^d10
^d\<n1\> \<n2\> | User | Rolls an n1 sided dice n2 times and replies with the rolls, average, min and max. | ^d20 5 

#### Tag Commands
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^att \<tag\> \<reply\> | User | Creates a text tag. | ^att Marco Polo
^aid \<tag\> | User | Creates an image tag. User needs to upload a single image with the message. | ^att pepehands
^rt \<tag\> | User | Removes a tag if the user created the tag. | ^rt Marco
^t \<tag\> | User | Replies with an image or text depending on the tag called. | ^t pepehands

#### Music Commands (WIP)
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^mplay \<YouTube Link\> | User | Connects to a channel and plays a YouTube video. | ^mplay https://www.youtube.com/watch?v=dQw4w9WgXcQ
^msearch \<Search Terms\> | User | Replies with the first 10 results of a YouTube Search. | ^msearch Rick Roll
^mpick \<1 - 10\> | User | Following a `^msearch` command, will play the chosen video. | ^mpick 3
^mdc | User | Disconneccts the bot from the current voice channel


#### Server Info Commands
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^cinfo | User | Replies with information about the channel (name, topic, NSFW, and ID) | 
^sinfo | User | Replies with information about the server (name, ID) | 

#### Server Config Commands
Command | Perms | Description | Example
:--- | :--- | :--- | :---
^server | BA | Replies with configuration information about the server (name, prefix, bot channels, bot admins and their respective IDs). |
^servers | Tech | Replies with configuration information about all servers. | 
^prefix <\prefix\> | BA | Sets the command prefix for the server. | ^prefix ! 
^addba @user | BA | Adds a new bot admin for the server. The user to add must be @'d in the command. | ^addba @Tech
^remba @user | BA | Removes an existing bot admin for the server. The user to remove must be @'d in the command. | ^remba @Tech
^addba #channel | BA | Adds an existing channel as a bot channel for the server. The channel must be #mentioned in the command. | ^addbc #bot-commands
^remba #channel | BA | Removes an existing bot channel for the server. The channel must be #mentioned in the command. | ^rembc #bot-commands






