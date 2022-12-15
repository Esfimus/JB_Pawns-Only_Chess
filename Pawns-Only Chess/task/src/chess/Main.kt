package chess

var boardMatrix = MutableList(8) { MutableList(8) { ' ' } }
var name1 = ""
var name2 = ""
var exitCommand = false
var enPassant = MutableList(2) { -1 }

/**
 * Creates a chess-board matrix with initial state with pawns only on their starting places
 */
fun initialStateMatrix(matrix: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> {
    for (y in 0 until 8) {
        for (x in 0 until 8) {
            if (y == 1) {
                matrix[y][x] = 'B'
            } else if (y == 6) {
                matrix[y][x] = 'W'
            }
        }
    }
    return matrix
}

/**
 * Displays the current chess-board according to the predefined formatting
 */
fun displayBoard(boardMatrix: MutableList<MutableList<Char>>) {
    var sideColumnCount = 8
    var bottomRowCharCodeCount = 97
    val formatMatrix = MutableList(18) { MutableList(17) { " " } }
    // creating new matrix with the required formatting
    for (y in 0 until 18) {
        for (x in 0 until 17) {
            // side column
            if (x == 0) {
                if (y == 17) {
                    formatMatrix[y][x] = "  "
                }else if (y % 2 == 0) {
                    formatMatrix[y][x] = "  +"
                } else {
                    formatMatrix[y][x] = "$sideColumnCount |"
                    sideColumnCount--
                }
            // bottom row
            } else if (y == 17) {
                if (x % 2 == 0) {
                    formatMatrix[y][x] = "${bottomRowCharCodeCount.toChar()} "
                    bottomRowCharCodeCount++
                } else {
                    formatMatrix[y][x] = "  "
                }
            // the rest of the board
            } else if (y % 2 == 0 && x % 2 != 0) {
                formatMatrix[y][x] = "--"
            } else if (y % 2 == 0) {
                formatMatrix[y][x] = "-+"
            } else if (x % 2 != 0) {
                formatMatrix[y][x] = " ${boardMatrix[(y - 1) / 2][(x - 1) / 2]}"
            } else {
                formatMatrix[y][x] = " |"
            }
        }
    }
    // displaying the formatted matrix
    for (y in 0 until 18) {
        for (x in 0 until 17) {
            print(formatMatrix[y][x])
        }
        println()
    }
}

/**
 * Sets both players' names and prints the initial chess-board state
 */
fun introduction() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    name1 = readln()
    println("Second Player's name:")
    name2 = readln()
    displayBoard(initialStateMatrix(boardMatrix))
    println()
}

/**
 * Transforms real y coordinate to matrix y coordinate
 */
fun yCoordinate(y: Int): Int {
    return 8 - y
}

/**
 * Transforms real x coordinate to matrix x coordinate
 */
fun xCoordinate(x: String): Int {
    return when (x) {
        "a" -> 0
        "b" -> 1
        "c" -> 2
        "d" -> 3
        "e" -> 4
        "f" -> 5
        "g" -> 6
        else -> 7
    }
}

/**
 * Breaks string user input coordinates to real matrix coordinates and checks for the possibility to move
 */
fun gameStateCheck(userInput: String, symbol: Char): Boolean {
    val (_, x1Str, y1Str, x2Str, y2Str) = userInput.split("")
    val x1 = xCoordinate(x1Str)
    val y1 = yCoordinate(y1Str.toInt())
    val x2 = xCoordinate(x2Str)
    val y2 = yCoordinate(y2Str.toInt())
    var allowedMove = false
    // checking if the initial coordinate is right
    if (boardMatrix[y1][x1] != symbol) {
        if (symbol == 'B') {
            println("No black pawn at $x1Str$y1Str")
        } else if (symbol == 'W') {
            println("No white pawn at $x1Str$y1Str")
        }
    // checking for possible move (1 or 2 cells) for the second coordinate and white pawns
    } else if (symbol == 'W') {
        if (
            // checking for possible move on 1 or 2 cells from the starting position
            (y1 == 6 &&
            y1 - y2 == 2 &&
            x1 == x2 &&
            boardMatrix[y1 - 1][x1] == ' ' && boardMatrix[y1 - 2][x1] == ' ') ||
            // checking for possible move on 1 cell from any position
            (y1 - y2 == 1 &&
            x1 == x2 &&
            boardMatrix[y1 - 1][x1] == ' ')
        ) {
            boardMatrix[y2][x2] = 'W'
            boardMatrix[y1][x1] = ' '
            allowedMove = true
            enPassant = if (y1 == 6 && y2 == 4) {
                mutableListOf(5, x1)
            } else {
                mutableListOf(-1, -1)
            }
        // checking for regular attack move
        } else if(
            y1 - y2 == 1 &&
            kotlin.math.abs(x2 - x1) == 1 &&
            boardMatrix[y2][x2] == 'B'
        ) {
            boardMatrix[y2][x2] = 'W'
            boardMatrix[y1][x1] = ' '
            enPassant = mutableListOf(-1, -1)
            allowedMove = true
            // checking for en passant attack move
        } else if (
            y1 - y2 == 1 &&
            kotlin.math.abs(x2 - x1) == 1 &&
            y2 == enPassant[0] &&
            x2 == enPassant[1]
        ) {
            boardMatrix[y2][x2] = 'W'
            boardMatrix[y1][x1] = ' '
            boardMatrix[y1][x2] = ' '
            enPassant = mutableListOf(-1, -1)
            allowedMove = true
        } else {
            println("Invalid Input")
        }
    // checking for possible move (1 or 2 cells) for the second coordinate and black pawns
    } else if (symbol == 'B') {
        if (
            // checking for possible move on 1 or 2 cells from the starting position
            (y1 == 1 &&
            y2 - y1 == 2 &&
            x1 == x2 &&
            boardMatrix[y1 + 1][x1] == ' ' && boardMatrix[y1 + 2][x1] == ' ') ||
            // checking for possible move on 1 cell from any position
            (y2 - y1 == 1 &&
            x1 == x2 &&
            boardMatrix[y1 + 1][x1] == ' ')
        ) {
            boardMatrix[y2][x2] = 'B'
            boardMatrix[y1][x1] = ' '
            allowedMove = true
            enPassant = if (y1 == 1 && y2 == 3) {
                mutableListOf(2, x1)
            } else {
                mutableListOf(-1, -1)
            }
        // checking for regular attack move
        } else if(
            y2 - y1 == 1 &&
            kotlin.math.abs(x2 - x1) == 1 &&
            boardMatrix[y2][x2] == 'W'
        ) {
            boardMatrix[y2][x2] = 'B'
            boardMatrix[y1][x1] = ' '
            enPassant = mutableListOf(-1, -1)
            allowedMove = true
        // checking for en passant attack move
        } else if (
            y2 - y1 == 1 &&
            kotlin.math.abs(x2 - x1) == 1 &&
            y2 == enPassant[0] &&
            x2 == enPassant[1]
        ) {
            boardMatrix[y2][x2] = 'B'
            boardMatrix[y1][x1] = ' '
            boardMatrix[y1][x2] = ' '
            enPassant = mutableListOf(-1, -1)
            allowedMove = true
        } else {
            println("Invalid Input")
        }
    }
    return allowedMove
}

/**
 * Analyses user input for a valid combination and 'exit' command
 */
fun userMove(name: String, symbol: Char) {
    do {
        try{
            println("$name's turn:")
            val userInput = readln()
            if (userInput == "exit") {
                println("Bye!")
                exitCommand = true
                return
            } else if (Regex("""[a-h][1-8][a-h][1-8]""").matches(userInput)) {
                if (gameStateCheck(userInput, symbol)) {
                    displayBoard(boardMatrix)
                    return
                }
            } else {
                throw Exception("Invalid Input")
            }
        } catch (e: Exception) {
            println("Invalid Input")
        }
    } while (true)
}

/**
 * Checks a game board for a win or stalemate state
 */
fun winCheck(matrix: MutableList<MutableList<Char>>) {
    var wCount = 0
    var bCount = 0
    var wMoveCount = false
    var bMoveCount = false
    for (y in 0 until 8) {
        for (x in 0 until 8) {
            // checking if any opponent reached the opposite line
            if (matrix[0][x] == 'W') {
                println("White Wins!\nBye!")
                exitCommand = true
                return
            }
            if (matrix[7][x] == 'B') {
                println("Black Wins!\nBye!")
                exitCommand = true
                return
            }
            // counting pawns and possible moves of both opponents
            if (matrix[y][x] == 'W') {
                wCount++
                // for the left column only
                if (x == 0) {
                    if (
                        matrix[y - 1][x] == ' ' ||
                        matrix[y - 1][x + 1] == 'B' ||
                        (y - 1 == enPassant[0] && x + 1 == enPassant[1])
                    ) {
                        wMoveCount = true
                    }
                // for the right column only
                } else if (x == 7) {
                    if (
                        matrix[y - 1][x] == ' ' ||
                        matrix[y - 1][x - 1] == 'B' ||
                        (y - 1 == enPassant[0] && x - 1 == enPassant[1])
                    ) {
                        wMoveCount = true
                    }
                // for other columns
                } else {
                    if (
                        matrix[y - 1][x] == ' ' ||
                        matrix[y - 1][x - 1] == 'B' ||
                        matrix[y - 1][x + 1] == 'B' ||
                        (y - 1 == enPassant[0] && x - 1 == enPassant[1]) ||
                        (y - 1 == enPassant[0] && x + 1 == enPassant[1])
                    ) {
                        wMoveCount = true
                    }
                }
            } else if (matrix[y][x] == 'B') {
                bCount++
                // for the left column only
                if (x == 0) {
                    if (
                        matrix[y + 1][x] == ' ' ||
                        matrix[y + 1][x + 1] == 'W' ||
                        (y + 1 == enPassant[0] && x + 1 == enPassant[1])
                    ) {
                        bMoveCount = true
                    }
                // for the right column only
                } else if (x == 7) {
                    if (
                        matrix[y + 1][x] == ' ' ||
                        matrix[y + 1][x - 1] == 'W' ||
                        (y + 1 == enPassant[0] && x - 1 == enPassant[1])
                    ) {
                        bMoveCount = true
                    }
                // for other columns
                } else {
                    if (
                        matrix[y + 1][x] == ' ' ||
                        matrix[y + 1][x - 1] == 'W' ||
                        matrix[y + 1][x + 1] == 'W' ||
                        (y + 1 == enPassant[0] && x - 1 == enPassant[1]) ||
                        (y + 1 == enPassant[0] && x + 1 == enPassant[1])
                    ) {
                        bMoveCount = true
                    }
                }
            }
        }
    }
    // checking if there is no pawns left
    if (wCount == 0) {
        println("Black Wins!\nBye!")
        exitCommand = true
        return
    }
    if (bCount == 0) {
        println("White Wins!\nBye!")
        exitCommand = true
        return
    }
    // checking for stalemate state
    if (!bMoveCount || !wMoveCount) {
        println("Stalemate!\nBye!")
        exitCommand = true
        return
    }
}

/**
 * Pawn-chess game for two players
 */
fun pawnChessGame() {
    introduction()
    var turnCount = 1
    var player: String
    var symbol: Char
    do {
        if (turnCount % 2 != 0) {
            player = name1
            symbol = 'W'
        } else {
            player = name2
            symbol = 'B'
        }
        userMove(player, symbol)
        winCheck(boardMatrix)
        turnCount++
    } while(!exitCommand)
}

fun main() {
    pawnChessGame()
}