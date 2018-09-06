package typed

import akka.actor.typed.scaladsl.Behaviors

object DelayedPublisher {
  trait Command
  case object Exec extends Command
  case object Cancel extends Command

  def apply(state: Boolean): Behaviors.Receive[Command] = {
    println("Actually constructed")
    // TODO use timers here to Exec after a timeout unless cancelled.
    Behaviors.receiveMessage[Command] { _ ⇒
      Behaviors.same[Command]
    }
  }
}