package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            // 1. 获取当前项目的根目录，并在里面创建一个 "uploads" 文件夹
            String projectPath = System.getProperty("user.dir");
            File uploadDir = new File(projectPath, "uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 如果目录不存在，自动创建
            }

            // 2. 生成一个独一无二的文件名 (防止多个人上传同名图片导致覆盖)
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".")); // 获取后缀名 (如 .jpg)
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 3. 将图片保存到硬盘上
            File dest = new File(uploadDir, newFileName);
            file.transferTo(dest);

            // 4. 动态拼接出这张图片的网络访问 URL
            // 例如：http://192.168.1.105:8080/uploads/xxxx.jpg
            String serverUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String imageUrl = serverUrl + "/uploads/" + newFileName;

            // 返回这个图片的 URL 给手机端
            return Result.success(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }
}