package com.liberty.rest;

import com.liberty.model.PlayerInfo;
import com.liberty.model.Tag;
import com.liberty.rest.request.TagRequest;
import com.liberty.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/tags")
@Slf4j
public class TagResource {

    @Autowired
    private TagService tagService;

    @RequestMapping(method = RequestMethod.POST)
    public void add(@RequestBody TagRequest request) {
        tagService.addTag(request.getPlayerId(), request.getTag());
    }

    @RequestMapping(path = "/{tag}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String tag, @RequestParam Long playerId) {
        tagService.removeTag(playerId, tag);
    }

    @RequestMapping(path = "/{tag}", method = RequestMethod.GET)
    public List<PlayerInfo> get(@PathVariable String tag) {
        return tagService.getByTag(tag);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Tag> getAll() {
        return tagService.getAllTags();
    }
}
