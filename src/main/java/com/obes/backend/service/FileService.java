package com.obes.backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.obes.backend.exception.FileStorageException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

  @Value("${app.uploadDir}")
  public String uploadDir;

  public void uploadFile(MultipartFile file, Long id) {
    try {
      Path copyLocation = Paths.get(uploadDir +
      File.separator + id + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
      Files.createDirectories(copyLocation);
      Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      throw new FileStorageException("Could not store file");
    }
  }

  public byte[] getFile(String path) {
    try {
      Path fileLocation = Paths.get(path);
      return Files.readAllBytes(fileLocation);
    } catch (Exception e) {
      throw new FileStorageException("Could not store file");
    }
  }
  
}