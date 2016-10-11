package com.liberty.service.impl;

import com.liberty.common.RequestHelper;
import com.liberty.service.ImageService;
import com.mongodb.gridfs.GridFSDBFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

  @Autowired
  private GridFsTemplate template;

  @Override
  public void saveImage(String url, long playerId) {
    if (getImage(playerId).isPresent()) {
      log.info("Image for " + playerId + " was stored before");
      return;
    }
    InputStream inputStream = RequestHelper.executeRequest(url);
    template.store(inputStream, getFileName(playerId), "image/png");
    log.info("Stored image for : " + playerId);
  }

  @Override
  public Optional<GridFSDBFile> getImage(long playerId) {
    GridFSDBFile image = template
        .findOne(new Query().addCriteria(Criteria.where("filename").is(getFileName(playerId))));
    return Optional.ofNullable(image);
  }

  private String getFileName(long playerId) {
    return playerId + ".png";
  }
}
