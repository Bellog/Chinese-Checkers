function setupImage(base64) {
    let image = new Image();
    image.src = "data:image/jpg;base64," + base64;

    $("#game_table")
        .css("background-image", "url(" + image.src + ")")
        .css("background-position", "-" + fieldDim[0] + "px -" + fieldDim[1] + "px");
}

function gameSetupTable(table) {
    let tb = "";
    table.empty();
    for (let y = 0; y < board.length; y++) {
        tb += "<tr>";
        for (let x = 0; x < board[0].length; x++) {
            tb += "<td></td>";
        }
        tb += "</tr>";
    }

    table.append(tb);
    mouseListener();
}

let mouseListener = function () {
    let begin;
    $("#game_table td")
        .mousedown(function () {
            begin = this;
            if (board[this.parentNode.rowIndex][this.cellIndex] === playerId) {
                $(this).empty();
                let c = "circle-" + board[this.parentNode.rowIndex][this.cellIndex] + "h";
                $(this).append("<span class='" + c + "'></span>");
                return false;
            }
            begin = null;
            return false; // prevent text selection
        })
        .mouseup(function () {
            $(begin).empty();
            let c = "circle-" + board[begin.parentNode.rowIndex][begin.cellIndex];
            $(begin).append("<span class='" + c + "'></span>");
            if (begin == null || board[this.parentNode.rowIndex][this.cellIndex] === -1) {
                console.log("move");
                let packet = new PacketBuilder().code("TURN_MOVE")
                    .startPos(new Pos(begin.cellIndex, begin.parentNode.rowIndex))
                    .endPos(new Pos(this.cellIndex, this.parentNode.rowIndex))
                    .build();
                sendPacket(packet);
            }
        })
};

function updateBoard() {
    $("#game_table tr").each(function (y) {
        let $cells = $(this).children();
        $($cells).each(function (x) {
            $(this).empty();
            if (board[y][x] !== null) {
                $(this).append("<span class='" + "circle-" + board[y][x] + "'></span>");
            }
        });
    });
}

function generateCellStyles() {
    let style = document.createElement('style');
    $("head").append(style);
    let sheet = style.sheet;
    // document.getElementsByTagName('head')[0].appendChild(style);
    // document.getElementById('someElementId').className = 'cssClass';
    for (let i = 0; i < colors.length; i++) {
        let text = ".circle-" + i + " {\n" + //normal
            "    background-color: " + getHexColor(colors[i]) + ";\n" +
            "    display: block;\n" +
            "    height: " + min(fieldDim[0], fieldDim[1]) + "px;\n" +
            "    width: " + min(fieldDim[0], fieldDim[1]) + "px;\n" +
            "    border-radius: 50%;\n" +
            "    border: 2px solid #000;\n" +
            "    margin: auto;\n" +
            "}\n"
        sheet.insertRule(text);
        text = ".circle-" + i + "h" + " {\n" + //highlighted
            "    background-color: " + getHexColor(colors[i]) + ";\n" +
            "    display: block;\n" +
            "    height: " + min(fieldDim[0], fieldDim[1]) + "px;\n" +
            "    width: " + min(fieldDim[0], fieldDim[1]) + "px;\n" +
            "    border-radius: 50%;\n" +
            "    border: 4px solid #000;\n" +
            "    margin: auto;\n" +
            "}\n"
        sheet.insertRule(text);
    }
}
