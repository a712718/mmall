package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mahong on 2022/8/26.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
