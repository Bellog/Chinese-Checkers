let stompClient = null;

function setButtons(disabled) {
    $("#rollback").prop("disabled", disabled);
    $("#end_turn").prop("disabled", disabled);
}

function connect() {
    stompClient = Stomp.client("ws://localhost:8080/sternhalma")
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/user/queue/game", function (message) {
            handlePacket(JSON.parse(message.body));
        });

        let packet = new PacketBuilder().code("CONNECT")
            .fieldDim(new FieldDim(28, 48))
            .build();
        sendPacket(packet);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendPacket(packet) {
    stompClient.send("/app/game", {}, JSON.stringify(packet));
}

function handlePacket(packet) {
    switch (packet.code) {
        case "GAME_SETUP":
            gameSetup(packet);
            break;
        case "TURN_START":
        case "GAME_RESUME" :
            setButtons(false);
            break;
        case "TURN_END":
        case "GAME_PAUSE":
        case "DISCONNECT":
        case "GAME_END":
            setButtons(true);
            break;
        case "BOARD_UPDATE":
            setBoard(packet.board);
            updateBoard();
            break;
        case "PLAYER_UPDATE":
            updatePlayerInfo(packet);
            break;
    }
    if (packet.message !== null) {
        log(packet.message);
    }
}