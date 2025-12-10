package org.example.ltwaicodemother.ai.model;

import lombok.Getter;

/**
 * 流式消息类型枚举类
 */
@Getter
public enum StreamMessageTypeEnum {
    AI_REPONSE("ai_resposne","AI响应"),
    TOOL_REQUEST("tool_request","工具响应"),
    TOOL_EXECUTED("tool_executed","工具执行结果");

    private final String value;

    private final String text;

    StreamMessageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     */
    public static StreamMessageTypeEnum getEnumByvallue(String value){
        for(StreamMessageTypeEnum typeEnum : StreamMessageTypeEnum.values()){
            if(typeEnum.getValue().equals(value)) return typeEnum;
        }
        return null;
    }
}
