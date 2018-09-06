package typed

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest._

class TestBehaviorStateSpec extends WordSpec with Matchers {

  import TestBehaviorState._

  "A Greeter Actor" must {
    "pass on a greeting message when instructed to" in {
      val testKit = BehaviorTestKit(idle(state = true))

      testKit.run(Publish)

      testKit.expectEffectType[Spawned[DelayedPublisher.type]]
    }

    "cancel publishing when flipped" in {
      val testKit = BehaviorTestKit(idle(state = true))
      testKit.run(Publish)

      testKit.run(Flip)

      testKit.childInbox("publisher").expectMessage(DelayedPublisher.Cancel)
    }

    "Allow publishing twice" in {
      val testKit = BehaviorTestKit(idle(state = true))
      testKit.run(Publish)
      val publisher = testKit.expectEffectType[Spawned[DelayedPublisher.type]]

      // I'd like to have control over the 'publisher' as a TestProbe here:
      // simulate sending a message to its parent and stopping itself

      testKit.run(Publish)
      testKit.expectEffectType[Spawned[DelayedPublisher.type]]
    }
  }
}