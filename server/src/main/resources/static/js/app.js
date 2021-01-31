let board = [];
let playerId;
let colors = [];
let fieldDim = [];

$(function () {
    let cell = $("#game_table").children().children();
    fieldDim = [cell.width(), cell.height()];

    $("#disconnect").click(function () {
        disconnect()
        $(this).prop("disabled", true);
        $("#connect").prop("disabled", false);
    });
    $("#connect").click(function () {
        connect()
        $(this).prop("disabled", true);
        $("#disconnect").prop("disabled", false);
    });
    $("#end_turn").click(function () {
        let packet = new PacketBuilder().code("TURN_END").build();
        sendPacket(packet);
    });
    $("#rollback").click(function () {
        let packet = new PacketBuilder().code("TURN_ROLLBACK").build();
        sendPacket(packet);
    });
    setButtons(true);
});

function gameSetup(packet) {
    let table = $("#game_table");
    playerId = packet.playerId;
    colors = packet.colors;
    board = packet.board;
    generateCellStyles();
    updatePlayerInfo(packet.playerInfo);
    gameSetupTable(table);
    updateBoard();
    setupImage(packet.image);
}

function updatePlayerInfo(playerInfo) {
    let table = $("#player_info");
    table.empty();

    generatePlayerInfoRow(table, playerInfo[0], 0x000000);
    for (let i = 1; i < playerInfo.length; i++) {
        generatePlayerInfoRow(table, playerInfo[i], colors[i - 1]);
    }
}

function generatePlayerInfoRow(table, data, color) {
    let row = "<tr>";
    row += "<td style='color: " + getHexColor(color) + ";'>" + data[0] + "</td>";
    row += "<td style='color: " + getHexColor(color) + "'>" + data[1] + "</td>";
    row += "</tr>";
    table.append(row);
}

function setBoard(newBoard) { // board needs to be assigned form gameTable.js
    board = newBoard;
}

function log(message) {
    let logs = $("#logs");
    let text = logs.text();
    let out = "> " + message + "\n" + text;
    logs.text(out);
}

function getHexColor(number) {
    return "#" + ((number) >>> 0).toString(16).slice(-6);
}

function min(a, b) {
    if (a < b)
        return a;
    return b;
}
