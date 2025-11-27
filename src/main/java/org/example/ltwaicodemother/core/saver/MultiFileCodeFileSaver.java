package org.example.ltwaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import org.example.ltwaicodemother.ai.model.HtmlCodeResult;
import org.example.ltwaicodemother.ai.model.MultiFileCodeResult;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;

/**
 * 多文件保存器
 */
public class MultiFileCodeFileSaver extends CodeFileSaverTemplete<MultiFileCodeResult>{

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String uniqueDir) {
        // 保存 HTML 文件
        writeToFile(uniqueDir, "index.html", result.getHtmlCode());
        // 保存 CSS 文件
        writeToFile(uniqueDir, "style.css", result.getCssCode());
        // 保存 JavaScript 文件
        writeToFile(uniqueDir, "script.js", result.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
        if (StrUtil.isBlank(result.getCssCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "CSS 代码不能为空");
        }
        if (StrUtil.isBlank(result.getJsCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "JS 代码不能为空");
        }
    }
}
