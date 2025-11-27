package org.example.ltwaicodemother.core.parser;

import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;

/**
 * 代码解析执行器
 * 根据代码解析类型执行相应的逻辑
 */
public class CodeParserExecutor<T> {

    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum){
        return switch (codeGenTypeEnum){
            case HTML -> new HtmlCodeParser().parseCode(codeContent);
            case MULTI_FILE -> new MultiFileCodeParser().parseCode(codeContent);
            case VUE_PROJECT -> null;
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请求的代码解析类型不存在");
        };
    }
}
