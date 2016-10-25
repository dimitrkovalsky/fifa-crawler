package com.liberty.actors;

import javax.inject.Named;

import akka.actor.UntypedActor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Named("consoleActor")
public class ConsoleActor extends UntypedActor {


  @Override
  public void onReceive(Object message) throws Exception {
    System.out.println("ConsoleActor received message : " + message);
  }
}