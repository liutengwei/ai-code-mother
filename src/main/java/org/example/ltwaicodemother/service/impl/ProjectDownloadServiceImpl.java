package org.example.ltwaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.exception.ThrowUtils;
import org.example.ltwaicodemother.service.ProjectDownloadService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 需要过滤的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
        // 基础校验
        ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(downloadFileName), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
        File projectDir = new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.PARAMS_ERROR, "项目路径不存在");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "项目路径不是一个目录");
        log.info("开始打包下载项目: {} -> {}.zip", projectPath, downloadFileName);

        // 设置 HTTP 响应头
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.zip\"", downloadFileName));

        try{
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, file -> {
                String name = file.getName();
                // 过滤目录名称
                if (IGNORED_NAMES.contains(name)) {
                    return false;
                }
                // 过滤文件扩展名
                for (String ext : IGNORED_EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        return false;
                    }
                }
                return true;
            }, projectDir);
            log.info("打包下载项目成功: {} -> {}.zip", projectPath, downloadFileName);

        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"打包下载项目失败");
        }

        FileFilter filter=file-> isPathAllower(projectDir.toPath(),file.toPath());

        // 压缩
        try {
            // 使用 Hutool 的 ZipUtil 直接将过滤后的目录压缩到响应输出流
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, filter, projectDir);
            log.info("打包下载项目成功: {} -> {}.zip", projectPath, downloadFileName);
        } catch (IOException e) {
            log.error("打包下载项目失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "打包下载项目失败");
        }
    }

    private boolean isPathAllower(Path projectRoot, Path fullPath){
        Path relativize = projectRoot.relativize(fullPath);
        for(Path path:relativize){
            String fileName = path.toString();
            if(IGNORED_NAMES.contains(fileName)){
                return false;
            }
            if(IGNORED_EXTENSIONS.stream().anyMatch(ext->fileName.toLowerCase().endsWith(ext))) return false;
        }
        return true;
    }
}