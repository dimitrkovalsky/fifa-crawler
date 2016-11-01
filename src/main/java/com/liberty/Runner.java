package com.liberty;

import com.liberty.config.Config;
import com.liberty.model.Tag;
import com.liberty.repositories.TagRepository;
import com.liberty.service.ClassificationService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class Runner {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    ClassificationService service = context.getBean(ClassificationService.class);
//    service.fetchAllPlayers();
//
//    service.fetchAllPlayers();
//    initTags(context);
//    TagService tagService = context.getBean(TagService.class);
//    tagService.executeUpdate();
//    initTags(context);
//    service.bestPremierLeague();
//    service.bestPremierLeague();
//    service.bestGermanLeague();
//    service.bestSpainLeague();
//    service.bestItalyLeague();
    service.bestWingers();
    System.exit(0);
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
    tags.add(new Tag("BPL Fast"));
    tags.add(new Tag("BPL Shot"));
    tags.add(new Tag("GER Fast"));
    tags.add(new Tag("GER Shot"));
    tags.add(new Tag("ESP Fast"));
    tags.add(new Tag("ESP Shot"));
    tags.add(new Tag("ITALY Fast"));
    tags.add(new Tag("ITALY Shot"));
    tags.add(new Tag("custom"));

    tagRepository.deleteAll();
    tagRepository.save(tags);
    System.out.println(tags.size() + " tags successfully stored");
  }
}
