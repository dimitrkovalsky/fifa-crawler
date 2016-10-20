package com.liberty.service.impl;

import com.liberty.common.RequestHelper;
import com.liberty.model.Club;
import com.liberty.model.League;
import com.liberty.model.Nation;
import com.liberty.service.ImageService;
import com.mongodb.gridfs.GridFSDBFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

  @Override
  public Optional<GridFSDBFile> getClubImage(long clubId) {
    GridFSDBFile image = template
        .findOne(new Query().addCriteria(Criteria.where("filename").is(getClubFileName(clubId))));
    return Optional.ofNullable(image);
  }

  @Override
  public Optional<GridFSDBFile> getLeagueImage(long leagueId) {
    GridFSDBFile image = template.findOne(
        new Query().addCriteria(Criteria.where("filename").is(getLeagueFileName(leagueId))));
    return Optional.ofNullable(image);
  }

  @Override
  public Optional<GridFSDBFile> getNationImage(long nationId) {
    GridFSDBFile image = template.findOne(
        new Query().addCriteria(Criteria.where("filename").is(getNationFileName(nationId))));
    return Optional.ofNullable(image);
  }

  private String getClubFileName(long clubId) {
    return "club-" + clubId + ".png";
  }

  private String getLeagueFileName(long leagueId) {
    return "league-" + leagueId + ".png";
  }

  private String getNationFileName(long nationId) {
    return "nation-" + nationId + ".png";
  }

  @Override
  public void saveClubImage(Club club) {
    if (getClubImage(club.getId()).isPresent()) {
      log.info("Image for " + club.getName() + " club was stored before");
      return;
    }
    if (club.getImageUrls().normal.small == null) {
      return;
    }
    InputStream inputStream = RequestHelper.executeRequest(club.getImageUrls().normal.small);
    template.store(inputStream, getClubFileName(club.getId()), "image/png");
    log.info("Stored image for club : " + club.getName());
  }

  @Override
  public void saveLeagueImage(League league) {
    if (getLeagueImage(league.getId()).isPresent()) {
      log.info("Image for " + league.getName() + " league was stored before");
      return;
    }
    if (league.getImgUrl() == null) {
      return;
    }
    InputStream inputStream = RequestHelper.executeRequest(league.getImgUrl());
    template.store(inputStream, getLeagueFileName(league.getId()), "image/png");
    log.info("Stored image for league : " + league.getName());
  }

  @Override
  public void saveNationImage(Nation nation) {
    try {

      if (getNationImage(nation.getId()).isPresent()) {
        log.info("Image for " + nation.getName() + " nation was stored before");
        return;
      }

      if (nation.getImageUrls() == null || nation.getImageUrls().small == null) {
        return;
      }
      String imageUrl = nation.getImageUrls().small.toString();
      log.info("Trying to request : " + imageUrl);
      InputStream imageStream = readImage(imageUrl);
      template.store(imageStream, getNationFileName(nation.getId()), "image/png");
      log.info("Stored image for nation : " + nation.getName());
      if(!getNationImage(nation.getId()).isPresent()) {
        saveNationImage(nation);
      }
    }catch (Exception e){
      log.error(e.getMessage());
    }
  }

  private InputStream readImage(String imageUrl) throws IOException {
    InputStream inputStream = RequestHelper.executeRequest(imageUrl);
    byte[] bytes = IOUtils.toByteArray(inputStream);
    return new ByteArrayInputStream(bytes);
  }

  private String getFileName(long playerId) {
    return playerId + ".png";
  }
}
