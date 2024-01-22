package lcrgame

import scala.util.Random
import lcrgame.types._

object GameLogic {
//Initialize the Game
def initGame(numPlayers: Int): List[Player] = 
  (1 to numPlayers).map(id => Player(id, 3)).toList

//rolling dice
def rollDice(player: Option[Player]): List[DiceOutcome] = player match {
  case Some(p) => List.fill(math.min(3, p.chips))(rollOne)
  case None => List.empty
}

def rollOne: DiceOutcome = {
  val roll = Random.nextInt(6) + 1
  roll match {
    case 1 => GoLeft
    case 2 => GoRight
    case 3 => GoCenter
    case _ => NoEffect
  }
}

// game over?
def isGameOver(game: Game): Boolean = 
  game.count(_.chips > 0) <= 1

//playing a round
def playRound(game: Game, playerNumber: Int): Game = {
  val maybePlayer = game.lift(playerNumber - 1)
  val outcomes = rollDice(maybePlayer)
  maybePlayer.fold(game)(player => applyRules(game, outcomes, playerNumber))
}

//applying rules
def applyRules(game: Game, outcomes: List[DiceOutcome], playerNumber: Int): Game = 
  outcomes.foldLeft(game)((g, outcome) => applyOutcome(g, playerNumber, outcome))

def applyOutcome(game: Game, playerNumber: Int, outcome: DiceOutcome): Game = outcome match {
  case GoLeft => moveChip(game, playerNumber - 1, -1)
  case GoRight => moveChip(game, playerNumber - 1, 1)
  case GoCenter => centerChip(game, playerNumber)
  case NoEffect => game
}

def moveChip(game: Game, currentPlayerIndex: Int, direction: Int): Game = {
  val totalPlayers = game.length
  val nextPlayerIndex = (currentPlayerIndex + direction + totalPlayers) % totalPlayers

  game.zipWithIndex.map { case (player, index) =>
    index match {
      case `currentPlayerIndex` if player.chips > 0 => player.copy(chips = player.chips - 1)
      case `nextPlayerIndex` => player.copy(chips = player.chips + 1)
      case _ => player
    }
  }
}

def centerChip(game: Game, playerNumber: Int): Game = {
  game.zipWithIndex.map { case (player, index) =>
    if (index == playerNumber - 1 && player.chips > 0) {
      player.copy(chips = player.chips - 1)
    } else {
      player
    }
  }
}
}
