package org.example.ltwaicodemother.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.exception.ThrowUtils;
import org.example.ltwaicodemother.manager.CosManager;
import org.example.ltwaicodemother.service.ScreenShotService;
import org.example.ltwaicodemother.utils.WebScreenShotUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class ScreenShotServiceImpl implements ScreenShotService {
    @Resource
    private CosManager cosManager;

    /**
     * 生成并上传网页截图
     * @param url 网页地址
     * @return 截图URL
     */
    @Override
    public String generateAndUploadScreenShot(String url) {
        // 参数校验
        if(StrUtil.isBlank(url)) throw new BusinessException(ErrorCode.PARAMS_ERROR,"报错的地址不能为空");
        // 本地截图
        String localFilePath = WebScreenShotUtils.saveWebPageScreenshot(url);
        if(localFilePath == null) throw new BusinessException(ErrorCode.OPERATION_ERROR,"网页截图失败");
        // 上传截图到COS
        try{
            String uploadPath = uploadScreenshootToCos(localFilePath);
            ThrowUtils.throwIf(uploadPath==null,ErrorCode.OPERATION_ERROR,"上传截图失败");
            log.info("上传截图到COS成功，URL：{}",uploadPath);
            return uploadPath;
        }finally {
            // 清理本地文件
            clearLocaFile(localFilePath);
        }
    }

    /**
     * 清理本地截图文件
     * @param localFilePath 本地文件路径
     */
    private void clearLocaFile(String localFilePath) {
        try{
            File file=new File(localFilePath);
            if(file.exists()){
                boolean deleteResult = file.delete();
                if(deleteResult){
                    log.info("本地截图文件删除成功，路径：{}",localFilePath);
                }else{
                    log.warn("本地截图文件删除失败，路径：{}",localFilePath);
                }
            }
        }catch (Exception e){
            log.error("本地截图文件删除异常，路径：{}",localFilePath,e);
        }
    }

    /**
     * 上传截图到COS
     * @param localFilePath 本地文件路径
     * @return 上传后的URL
     */
    private String uploadScreenshootToCos(String localFilePath){
        if(StrUtil.isBlank(localFilePath))return null;
        File file=new File(localFilePath);
        if(!file.exists()) return null;
        String filename = UUID.randomUUID().toString().substring(0,8)+"_compressed" + ".png";
        String key = generateScreenShotKey(filename);
        return cosManager.uploadFile(key, new File(localFilePath));

    }

    /**
     * 生成截图存储Key
     * @param filename 文件名
     * @return 存储Key
     */
    private String generateScreenShotKey(String filename){
        return String.format("/screenshots/%s/%s", DateUtil.format(new Date(), "yyyy/MM/dd"), filename);
    }

}
