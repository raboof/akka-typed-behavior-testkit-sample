package typed

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }

object TestBehaviorState {

  sealed trait Command
  final case object Flip extends Command
  final case object Same extends Command

  final case object Publish extends Command
  final case object Published extends Command

  def apply(): Behavior[Command] = idle(state = true)

  def idle(state: Boolean): Behavior[Command] = Behaviors.receive { (ctx, message) =>
    message match {
      case Flip => active(!state)
      case Same => Behaviors.same
      case Publish => publish(ctx, state, timeout = 10)
    }
  }

  def active(state: Boolean): Behavior[Command] = Behaviors.receive { (ctx, message) =>
    message match {
      case Flip => active(!state)
      case Same if state => publish(ctx, state, timeout = 100)
      case Same => Behaviors.same
      case Publish => publish(ctx, state, timeout = 10)
    }
  }

  def publish(ctx: ActorContext[Command], state: Boolean, timeout: Long): Behavior[Command] = {
    val publisher = ctx.spawn(DelayedPublisher(state), "publisher")
    Behaviors.receiveMessage {
        case Published =>
          idle(!state)
        case Flip =>
          publisher ! DelayedPublisher.Cancel
          active(!state)
        case Same =>
          Behaviors.same
      }
    }
}


