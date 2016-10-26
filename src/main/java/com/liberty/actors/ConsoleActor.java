package com.liberty.actors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Component
@Scope("prototype")
public class ConsoleActor extends UntypedActor {


  @Override
  public void onReceive(Object message) throws Exception {
    System.out.println("ConsoleActor received message : " + message);
  }
}