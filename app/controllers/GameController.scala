package controllers

import java.io.PrintWriter
import java.io.FileOutputStream
import java.io.File

import javax.inject._
import play.api.mvc._
import lcrgame.GameLogic

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  // Initialize the game and store its state
  private var game = GameLogic.initGame(3) // start with 3 players, for example

  def startGame(numPlayers: Int) = Action {
    game = GameLogic.initGame(numPlayers)
    Ok(views.html.game(game)) // Render a view with the game state
  }

def showGame = Action {
    val initialGameState = GameLogic.initGame(3) // Or however you initialize the game state
    Ok(views.html.game(initialGameState))
}
def playTurn(playerId: Int) = Action { implicit request =>
  // Check if the player ID is valid
  if (playerId < 1 || playerId > game.length) {
    BadRequest("Invalid player ID")
  } else {
    // Play the turn and log the outcome
    val maybeCurrentPlayer = game.lift(playerId - 1)
    val outcomes = GameLogic.rollDice(maybeCurrentPlayer)
    val logMsg = s"Player $playerId rolled: $outcomes"
    appendToLogFile("gameLog.txt", logMsg)

    // Update the game state
    val updatedGame = GameLogic.playRound(game, playerId)
    game = updatedGame

    // Check if the game is over
    if (GameLogic.isGameOver(game)) {
      Ok(views.html.gameOver(game)) // Render a game over view
    } else {
      Ok(views.html.game(game)) // Render the updated game state
    }
  }
}
def playRound(playerNumber: Int) = Action { implicit request =>
    // Check if the player number is valid
    if (playerNumber < 1 || playerNumber > game.length) {
      BadRequest("Invalid player number")
    } else {
      // Play the round
      game = GameLogic.playRound(game, playerNumber)

      // Append to log file (optional)
      val logMsg = s"Player $playerNumber played a round."
      appendToLogFile("gameLog.txt", logMsg)

      // Check if the game is over
      if (GameLogic.isGameOver(game)) {
        Ok(views.html.gameOver(game)) // Render a game over view
      } else {
        Ok(views.html.game(game)) // Render the updated game state
      }
    }
  }
private def appendToLogFile(file: String, msg: String): Unit = {
  val pw = new PrintWriter(new FileOutputStream(new File(file), true)) // true for append mode
  try {
    pw.append(s"$msg\n")
  } finally {
    pw.close()
  }
}
}
