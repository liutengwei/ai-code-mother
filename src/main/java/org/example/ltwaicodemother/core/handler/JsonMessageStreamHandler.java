package org.example.ltwaicodemother.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.ai.model.*;
import org.example.ltwaicodemother.ai.tool.BaseTool;
import org.example.ltwaicodemother.ai.tool.ToolManger;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import org.example.ltwaicodemother.service.ChatHistoryService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class JsonMessageStreamHandler {

    @Resource
    private ToolManger toolManger;

    public Flux<String> handle(Flux<String> oririnFlux,
                                ChatHistoryService chatHistoryService,
                                long appId, User loginUser){

        // 收集数据用于生成后端记忆格式
        StringBuilder chatHistoryStringBulder=new StringBuilder();
        // 用于跟踪已经见过的工具ID，判断是否是第一次调用
        Set<String> seenToolIds=new HashSet<>();

        return oririnFlux.map(chunk-> handlerJsonMessageChunk(chunk,chatHistoryStringBulder,seenToolIds))
                         .filter(StrUtil::isEmpty)
                         .doOnComplete(()->{
                             // 流式响应完毕之后，添加AI消息到对话历史
                             String aiResponse= chatHistoryStringBulder.toString();
                             chatHistoryService.addChatMessage(appId,loginUser.getId(), ChatHistoryMessageTypeEnum.AI.getValue(), aiResponse);
                         })
                        .doOnError(error->{
                            // 如果Ai回复失败，也记录错误消息
                            String errorMessage="AI 回复失败："+error.getMessage();
                            chatHistoryService.addChatMessage(appId, loginUser.getId(), ChatHistoryMessageTypeEnum.AI.getValue(),errorMessage);
                        });
    }

    /**
     * 解析并收集 TokenStream 数据
     * @return
     */
    private String handlerJsonMessageChunk(String chunk,StringBuilder chatHistoryStringBuilder,Set<String> seenToolIds){
        // 解析 Json
        StreamMessage streamMessage = JSONUtil.toBean(chunk, StreamMessage.class);
        StreamMessageTypeEnum typeEnum = StreamMessageTypeEnum.getEnumByvallue(streamMessage.getType());
        if (typeEnum != null) {
            switch (typeEnum){
                case AI_REPONSE -> {
                    AiResponseMessage aiMessage = JSONUtil.toBean(chunk, AiResponseMessage.class);
                    String data=aiMessage.getData();
                     chatHistoryStringBuilder.append(data);
                     return data;
                }
                case TOOL_REQUEST -> {
                    ToolRequestMessage toolRequestMessage = JSONUtil.toBean(chunk, ToolRequestMessage.class);
                    String tooId = toolRequestMessage.getId();
                    String toolName = toolRequestMessage.getName();
                    if(tooId!=null && !seenToolIds.contains(tooId)){
                        // 第一次调用该工具
                        seenToolIds.add(tooId);
                        // 根据工具名称获取名称实例
                        BaseTool tool = toolManger.getTool(toolName);
                        return tool.generateToolRequestResponse();
                    }else{
                        // 如果不是第一次调用这个工具，直接返回空
                        return "";
                    }
                }
                case TOOL_EXECUTED -> {
                    ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(chunk, ToolExecutedMessage.class);
                    JSONObject jsonObject=JSONUtil.parseObj(toolExecutedMessage.getArguments());
                    // 根据工具名称获取工具实例
                    String tooName = toolExecutedMessage.getName();
                    BaseTool tool = toolManger.getTool(tooName);
                    String result = tool.generateToolExecutedResult(jsonObject);
                    // 输出前端和要持久化的内容
                    String output=String.format("\n\n%s\n\n", result);
                    chatHistoryStringBuilder.append(output);
                    return output;
                }
                default -> {
                    log.error("不支持的消息类型：{}",typeEnum);
                    return "";
                }
            }
        }
        log.error("消息类型为空");
        return "";
    }

}
