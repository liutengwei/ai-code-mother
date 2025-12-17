package org.example.ltwaicodemother.service;

public interface ScreenShotService {

    /**
     * 生成并上传网页截图
     * @param url
     * @return
     */
    String generateAndUploadScreenShot(String url);
}
