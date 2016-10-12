package com.liberty.rest;

import com.liberty.service.ImageService;
import com.mongodb.gridfs.GridFSDBFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/images")
@Slf4j
public class ImageResource {

  @Autowired
  private ImageService imageService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<?> get(@PathVariable Long id) {
    Optional<GridFSDBFile> image = imageService.getImage(id);
    if (!image.isPresent()) {
      return notFound();
    }
    GridFSDBFile gridFsFile = image.get();

    return ResponseEntity.ok()
        .contentLength(gridFsFile.getLength())
        .contentType(MediaType.parseMediaType(gridFsFile.getContentType()))
        .body(new InputStreamResource(gridFsFile.getInputStream()));
  }

  private ResponseEntity<Void> notFound() {
    return ResponseEntity.notFound().build();
  }
}