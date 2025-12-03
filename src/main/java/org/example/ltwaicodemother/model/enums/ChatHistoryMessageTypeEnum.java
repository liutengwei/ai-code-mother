package org.example.ltwaicodemother.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;


/**
 * 历史对话消息枚举类型
 */
@Getter
public enum ChatHistoryMessageTypeEnum {
    USER("用户","user"),
    AI("AI","ai");

    private final String text;
    private final String value;

    ChatHistoryMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value=value;
    }

    /**
     * 根据 value 获取 Enum
     * @param value
     * @return
     */
    public static ChatHistoryMessageTypeEnum getEnumByValue(String value){
        if(StrUtil.isEmpty(value)){
            return null;
        }
        for(ChatHistoryMessageTypeEnum chatHistoryMessageTypeEnum : ChatHistoryMessageTypeEnum.values()){
            if(chatHistoryMessageTypeEnum.value.equals(value)){
                return chatHistoryMessageTypeEnum;
            }
        }
        return null;
    }
}
