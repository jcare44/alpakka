/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.amqp.javadsl

import java.util.concurrent.CompletionStage

import akka.Done
import akka.japi.Pair
import akka.japi.function.Function
import akka.stream.alpakka.amqp._
import akka.stream.javadsl.{Flow, Keep}
import akka.util.ByteString

import scala.compat.java8.FutureConverters._

object AmqpFlow {

  /**
   * Java API: Creates an [[AmqpFlow]] that accepts (ByteString, passthrough) elements.
   *
   * This stage materializes to a CompletionStage<Done>, which can be used to know when the Flow completes, either normally
   * or because of an amqp failure
   */
  def createSimple[O](settings: AmqpWriteSettings): Flow[Pair[ByteString, O], O, CompletionStage[Done]] =
    Flow
      .fromFunction(new Function[Pair[ByteString, O], (ByteString, O)] {
        override def apply(pair: Pair[ByteString, O]): (ByteString, O) = pair.toScala
      })
      .viaMat[O, CompletionStage[Done], CompletionStage[Done]](
        scaladsl.AmqpFlow
          .simple[O](settings)
          .mapMaterializedValue(f => f.toJava)
          .asJava
          .asInstanceOf[Flow[(ByteString, O), O, CompletionStage[Done]]],
        Keep.right
      )

  /**
   * Java API: Creates an [[AmqpFlow]] that accepts ([[WriteMessage]], passthrough) elements.
   *
   * This stage materializes to a CompletionStage<Done>, which can be used to know when the Flow completes, either normally
   * or because of an amqp failure
   */
  def create[O](settings: AmqpWriteSettings): Flow[Pair[WriteMessage, O], O, CompletionStage[Done]] =
    Flow
      .fromFunction(new Function[Pair[WriteMessage, O], (WriteMessage, O)] {
        override def apply(pair: Pair[WriteMessage, O]): (WriteMessage, O) = pair.toScala
      })
      .viaMat[O, CompletionStage[Done], CompletionStage[Done]](
        scaladsl
          .AmqpFlow[O](settings)
          .mapMaterializedValue(f => f.toJava)
          .asJava
          .asInstanceOf[Flow[(WriteMessage, O), O, CompletionStage[Done]]],
        Keep.right
      )
}
