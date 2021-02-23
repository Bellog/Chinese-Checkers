# Chinese-Checkers

Chinese checkers game with java and web interfaces.

This game was a univeristy project, done in pairs.

# Setup

## Client

To run server:
> gradle :server:bootRun

## Server

> gradle :client:bootRun

In order to run the server you need to specify enviromental variables for execution:
> SPRING_DATASOURCE_UR
> SPRING_DATASOURCE_PASSWORD
> SPRING_DATASOURCE_USERNAME
The program used MariaDB for database solution

### Replay Server

In order to run server in replay mode you need to also add 'SPRING_PROFILES_ACTIVE = replay' enviromental variable for execution.

# Usage

## Client

Move your pawn by dragging them on other fields.
You can end turn when you are done, or rollback your moves if you made a mistake.
All important information will be shown in game logs.

## Connecting to the game

### Web client

Connect to localhost://8080 and press connect. If connection is successful, you will see game board on your screen.

### Java client

Java client connects automatically and outputs logs to command line interface.

## Server

Instructions to run server are shown in the command line interface.

### Game modes

> Basic game mode - for testing purposes, All moves are prohibited, player wins when any of their pawns is in another player's base.

> Standard game mode - standard rules for chinese checkers apply.
