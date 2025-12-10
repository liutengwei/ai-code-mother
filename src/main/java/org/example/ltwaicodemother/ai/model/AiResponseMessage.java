package org.example.ltwaicodemother.ai.model;

import dev.langchain4j.data.message.AiMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 响应消息
 */
@Data
@NoArgsConstructor
public class AiResponseMessage extends StreamMessage {

    private String data;

    public AiResponseMessage(String data) {
        super(StreamMessageTypeEnum.AI_REPONSE.getValue());
        this.data=data;
    }
}
