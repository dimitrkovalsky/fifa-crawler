package com.liberty.actors;

import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Component
public class RequestActor extends UntypedActor {


  @Override
  public void onReceive(Object message) throws Exception {
    System.out.println("RequestActor received message : " + message);
  }
}