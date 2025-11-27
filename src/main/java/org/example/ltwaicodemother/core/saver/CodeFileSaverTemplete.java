package org.example.ltwaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模板模式
 * @param <T>
 */
public abstract class CodeFileSaverTemplete<T> {

    /**
     * 文件保存的根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    public final File saveCode(T result,Long appId) {
        // 校验参数
        validateInput(result);
        // 构建唯一路径
        String uniqueDir = buildUniqueDir(appId);
        // 保存文件
        saveFiles(result,uniqueDir);
        // 返回文件目录对象
        return new File(uniqueDir);
    }

    /**
     * 验证输入参数（可由子类覆盖）
     */
    protected void validateInput(T result) {
        if(result == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"数据不能为空");
        }
    }

    /**
     * 构建文件的唯一路径：tmp/code_output/bizType_雪花 ID
     *
     * @param appId 应用 ID
     * @return 目录路径
     */
    protected String buildUniqueDir(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存单个文件
     *
     * @param dirPath
     * @param filename
     * @param content
     */
    protected final  void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件具体实现交给子类
     * @param result
     * @param uniqueDir
     */
    protected abstract void saveFiles(T result,String uniqueDir) ;
}
