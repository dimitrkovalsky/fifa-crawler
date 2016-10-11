package com.liberty.service;

import com.mongodb.gridfs.GridFSDBFile;

import java.util.Optional;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
public interface ImageService {

  void saveImage(String url, long playerId);

  Optional<GridFSDBFile> getImage(long playerId);
}
