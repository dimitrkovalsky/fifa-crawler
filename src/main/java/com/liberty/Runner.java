package com.liberty;

import com.liberty.config.Config;
import com.liberty.config.SpringExtension;
import com.liberty.model.Tag;
import com.liberty.repositories.TagRepository;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class Runner {

  public static void main(String[] args) throws IOException {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    // CrawlerService service = context.getBean(CrawlerService.class);
//


    // get hold of the actor system
    ActorSystem system = context.getBean(ActorSystem.class);
    SpringExtension springExtension = context.getBean(SpringExtension.class);
    // use the Spring Extension to create props for a named actor bean
    ActorRef consoleActor = system.actorOf(springExtension.props("consoleActor"), "console-actor");

    // tell it to count three times
    consoleActor.tell("Hello", null);

    //  service.fetchAllPlayers();
//    initTags(context);
//    TagService tagService = context.getBean(TagService.class);
//    tagService.executeUpdate();
    // System.exit(0);
  }

  private static void initTags(ApplicationContext context) {
    TagRepository tagRepository = context.getBean(TagRepository.class);
    Set<Tag> tags = new HashSet<>();
    tags.add(new Tag("cheap"));
    tags.add(new Tag("medium"));
    tags.add(new Tag("rich"));
    tags.add(new Tag("expensive"));
    tags.add(new Tag("top"));
    tags.add(new Tag("inform"));

    tagRepository.deleteAll();
    tagRepository.save(tags);
    System.out.println(tags.size() + " tags successfully stored");
  }
}
