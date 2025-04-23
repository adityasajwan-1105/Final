package com.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GridFSService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("File is empty");
                }

                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Only image files are allowed");
                }

                String fileId = gridFsTemplate.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    contentType,
                    null // metadata can be added here if needed
                ).toString();
                fileIds.add(fileId);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
            }
        }
        return fileIds;
    }

    public byte[] getFile(String fileId) {
        try {
            return Optional.ofNullable(gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId))))
                .map(gridFsFile -> {
                    try (InputStream inputStream = gridFsOperations.getResource(gridFsFile).getInputStream();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        return outputStream.toByteArray();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file: " + fileId, e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving file: " + fileId, e);
        }
    }

    public String getContentType(String fileId) {
        return Optional.ofNullable(gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId))))
            .map(gridFsFile -> gridFsFile.getMetadata().get("_contentType").toString())
            .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public void deleteFiles(List<String> fileIds) {
        for (String fileId : fileIds) {
            try {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete file: " + fileId, e);
            }
        }
    }

    public boolean fileExists(String fileId) {
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId))) != null;
    }
} 