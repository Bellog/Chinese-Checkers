let stompClient = null;
let board = [];

function connect() {
    stompClient = Stomp.client("ws://localhost:8080/sternhalma")
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/user/queue/game", function (message) {
            handlePacket(JSON.parse(message.body));
        });
        // let item = $("table td");
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
    }
    if (packet.message !== null) {
        console.log(packet.message);
    }
}

function gameSetup(packet) {
    let table = $("#game_table");
    board = packet.board;

    gameSetupTable(table);

    // change logs height
    $("#logs").css("height", table.css("height"));
}

function gameSetupTable(table) {
    let row = "<tr>";
    table.empty();
    for (let i = 0; i < board[0].length; i++) {
        row += "<td></td>";
    }
    row += "</tr>";
    for (let i = 0; i < board.length; i++) {
        table.append(row);
    }
}

$(function () {
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect()
    });
});

$(function () {
    let begin;
    $("#game_table td")
        .mousedown(function () {
            begin = this;

            if (board[this.parentNode.rowIndex][this.cellIndex] === -1) {
                begin = null;
                return false;
            }

            $(this).addClass("highlighted");
            return false; // prevent text selection
        })
        .bind("selectstart", function () {
            return false; // block text selection (I think)
        })
        .mouseup(function () {
            $(begin).removeClass("highlighted");
            if (begin == null || board[this.parentNode.rowIndex - 1][this.cellIndex] === -1)
                return;
            let packet = new PacketBuilder().code("CODE_INFO").build();
            //alert("(" + begin.cellIndex + ", " + begin.parentNode.rowIndex + ") -> (" + this.cellIndex + ", " + this.parentNode.rowIndex + ")");
        })
});

class FieldDim {
    constructor(width, height) {
        this._width = width;
        this._height = height;
    }

    _width = 0;

    get width() {
        return this._width;
    }

    _height = 0;

    get height() {
        return this._height;
    }

    toJSON() {
        return {
            width: this._width,
            height: this._height
        }
    }
}

class Pos {
    constructor(x, y) {
        this._x = x;
        this._y = y;
    }

    _x = 0;

    get x() {
        return this._x;
    }

    _y = 0;

    get y() {
        return this._y;
    }

    toJSON() {
        return {
            x: this._x,
            y: this._y
        }
    }
}

class Packet {
    constructor(builder) {
        this._code = builder._code;
        this._board = builder._board;
        this._playerInfo = builder._playerInfo;
        this._playerId = builder._playerId;
        this._fieldDim = builder._fieldDim;
        this._startPos = builder._startPos;
        this._endPos = builder._endPos;
        this._colors = builder._colors;
        this._image = builder._image;
    }

    _code = "";

    get code() {
        return this._code;
    }

    _board = [];

    get board() {
        return this._board;
    }

    _playerInfo = [];

    get playerInfo() {
        return this._playerInfo;
    }

    _playerId = -1;

    get playerId() {
        return this._playerId;
    }

    _fieldDim = new FieldDim(0, 0);

    get fieldDim() {
        return this._fieldDim;
    }

    _message = "";

    get message() {
        return this._message;
    }

    _startPos = new Pos(0, 0);

    get startPos() {
        return this._startPos;
    }

    _endPos = new Pos(0, 0);

    get endPos() {
        return this._endPos;
    }

    _colors = [];

    get colors() {
        return this._colors;
    }

    _image = null;

    get image() {
        return this._image;
    }

    toJSON() {
        return {
            code: this._code,
            board: this._board,
            playerInfo: this._playerInfo,
            playerId: this._playerId,
            fieldDim: this._fieldDim,
            message: this._message,
            startPos: this._startPos,
            endPos: this._endPos,
            colors: this._colors,
            image: this._image
        }
    }
}

class PacketBuilder {
    _code = "";
    _board = [];
    _playerInfo = [];
    _playerId = -1;
    _fieldDim = new FieldDim(1, 1);
    _message = "";
    _startPos = new Pos(0, 0);
    _endPos = new Pos(0, 0);
    _colors = [];
    _image = null;

    code(value) {
        this._code = value;
        return this;
    }

    board(value) {
        this._board = value;
        return this;
    }

    playerInfo(value) {
        this._playerInfo = value;
        return this;
    }

    playerId(value) {
        this._playerId = value;
    }

    fieldDim(value) {
        this._fieldDim = value;
        return this;
    }

    message(value) {
        this._message = value;
        return this;
    }

    startPos(value) {
        this._startPos = value;
        return this;
    }

    endPos(value) {
        this._endPos = value;
        return this;
    }

    colors(value) {
        this._colors = value;
        return this;
    }

    image(value) {
        this._image = value;
        return this;
    }

    build() {
        return new Packet(this);
    }
}

