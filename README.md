# Chinese-Checkers

Chinese checkers game with java and web interfaces.

This game was a univeristy project, done in pairs.

# 1 Technologies

Project uses java 15, spring boot 2.4.2 with Tomcat, Stomp over Websocket for client-server connection.

Java client interface is written in swing, web interface is written in JQuery.

# 2 Setup

## 2.1 Client

To run server:
> gradle :server:bootRun

## 2.2 Server

> gradle :client:bootRun

In order to run the server you need to specify enviromental variables for execution:
- SPRING_DATASOURCE_UR
- SPRING_DATASOURCE_PASSWORD
- SPRING_DATASOURCE_USERNAME
The program used MariaDB for database solution

### 2.2.1 Replay Server

In order to run server in replay mode you need to also add 'SPRING_PROFILES_ACTIVE = replay' enviromental variable for execution.

# 3 Usage

## 3.1 Client

Move your pawn by dragging them on other fields.
You can end turn when you are done, or rollback your moves if you made a mistake.
All important information will be shown in game logs.

## 3.2 Connecting to the game

### 3.2.1 Web client

Connect to localhost://8080 and press connect. If connection is successful, you will see game board on your screen.

### 3.2.2 Java client

Java client connects automatically and outputs logs to command line interface.

## 3.3 Server

Instructions to run server are shown in the command line interface.

### 3.3.1 Game modes

- Basic game mode - for testing purposes, All moves are prohibited, player wins when any of their pawns is in another player's base.
- Standard game mode - standard rules for chinese checkers apply.
 
# 4 Tests

You can see test coverage using 'gradle jacocoaggregatedreport' and navigating to /build/reports/jacoco/jacocoArggregatedReport/
