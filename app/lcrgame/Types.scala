package lcrgame

// Types.scala
package object types {
sealed trait DiceOutcome
case object GoLeft extends DiceOutcome
case object GoRight extends DiceOutcome
case object GoCenter extends DiceOutcome
case object NoEffect extends DiceOutcome

case class Player(id: Int, chips: Int) {
  def updateChips(delta: Int): Player = copy(chips = chips + delta)
}


type Game = List[Player]
}
