package org.example.ltwaicodemother.ai.model;

import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.sql.DataSourceDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工具调用消息
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ToolExecutedMessage extends  StreamMessage {

    private String id;

    private String name;

    private String arguments;

    private String result;

    public ToolExecutedMessage(ToolExecution toolExecution) {
        super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
        this.id = toolExecution.request().id();
        this.name = toolExecution.request().name();
        this.arguments = toolExecution.request().arguments();
        this.result = toolExecution.result();
    }
}
