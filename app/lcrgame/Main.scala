package lcrgame

import scala.annotation.tailrec
import java.io.{File, PrintWriter}

import lcrgame.types.Game

object Main extends App {
  println("Starting the game of LCR with 4 players.")
  val game = GameLogic.initGame(4) // Initialize the game with 3 players
  val finalState = emulateCompleteGame(game, 1, "gameLog.txt")
  println("Final game state:")
  println(finalState)

  @tailrec
  def emulateCompleteGame(game: Game, currentPlayerId: Int, logFile: String): Game = {
    if (GameLogic.isGameOver(game)) {
      game
    } else {
      val maybeCurrentPlayer = game.lift(currentPlayerId - 1)
      val outcomes = GameLogic.rollDice(maybeCurrentPlayer)
      val logMsg = s"Player $currentPlayerId rolled: $outcomes"
      appendToLogFile(logFile, logMsg)
      val updatedGame = GameLogic.playRound(game, currentPlayerId)
      println(s"Game state after player $currentPlayerId's turn: $updatedGame")
      val nextPlayerId = if (currentPlayerId == game.length) 1 else currentPlayerId + 1
      emulateCompleteGame(updatedGame, nextPlayerId, logFile)
    }
  }

  def appendToLogFile(file: String, msg: String): Unit = {
    val pw = new PrintWriter(new File(file))
    try {
      pw.append(s"$msg\n")
    } finally {
      pw.close()
    }
  }
}
