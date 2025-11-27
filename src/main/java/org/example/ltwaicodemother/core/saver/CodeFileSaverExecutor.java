package org.example.ltwaicodemother.core.saver;

import org.example.ltwaicodemother.ai.model.HtmlCodeResult;
import org.example.ltwaicodemother.ai.model.MultiFileCodeResult;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码保存器
 */
public class CodeFileSaverExecutor {
    private static final HtmlCodeFileSaver htmlCodeFileSaver = new HtmlCodeFileSaver();
    private static final MultiFileCodeFileSaver multiFileCodeFileSaver = new MultiFileCodeFileSaver();

    public static File executeSaver(Object result, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) result, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) result, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件类型不存在");
        };
    }
}
