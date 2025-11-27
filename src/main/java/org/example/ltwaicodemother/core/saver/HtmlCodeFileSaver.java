package org.example.ltwaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import org.example.ltwaicodemother.ai.model.HtmlCodeResult;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;

public class HtmlCodeFileSaver extends CodeFileSaverTemplete<HtmlCodeResult> {

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String uniqueDir) {
        writeToFile(uniqueDir,"index.html",result.getHtmlCode());
    }
}
