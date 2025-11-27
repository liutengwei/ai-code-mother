package org.example.ltwaicodemother.ai;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import java.time.Duration;

/**
 * AI 服务创建工厂
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "deepSeekModel")
    private ChatModel chatModel;
//    @Resource(name = "deepSeekStreamingChatModel")
//    private StreamingChatModel streamingChatModel;
    @Autowired(required = false)
    private RedisChatMemoryStore redisChatMemoryStore;


    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
//        return AiServices.builder(AiCodeGeneratorService.class).chatModel(chatModel).streamingChatModel(streamingChatModel).build();
        return null;
    }

//    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
//        log.info("为 appId: {}创建新的 AI 服务实例 ", appId);
//        // 根据 appId 构建独立的对话记忆
//        ChatMemory chatMemory = MessageWindowChatMemory
//                .builder()
//                .id(appId)
//                .chatMemoryStore(redisChatMemoryStore)
//                .maxMessages(20)
//                .build();
////     从数据库中加载对话历史到记忆中
////        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
//        return switch (codeGenType) {
//            // Vue 项目生成，使用工具调用和推理模型
//            case VUE_PROJECT -> {
//                // 使用多例模式的 StreamingChatModel 解决并发问题
//                StreamingChatModel reasoningStreamingChatModel = SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
//                yield AiServices.builder(AiCodeGeneratorService.class)
//                        .chatModel(chatModel)
//                        .streamingChatModel(reasoningStreamingChatModel)
//                        .chatMemoryProvider(memoryId -> chatMemory)
//                        .tools(toolManager.getAllTools())
//                        // 处理工具调用幻觉问题
//                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
//                                ToolExecutionResultMessage.from(toolExecutionRequest,
//                                        "Error: there is no tool called " + toolExecutionRequest.name())
//                        )
//                        .maxSequentialToolsInvocations(20)  // 最多连续调用 20 次工具
//                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
////                        .outputGuardrails(new RetryOutputGuardrail()) // 添加输出护轨，为了流式输出，这里不使用
//                        .build();
//            }
//            // HTML 和 多文件生成，使用流式对话模型
//            case HTML, MULTI_FILE -> {
//                // 使用多例模式的 StreamingChatModel 解决并发问题
//                StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
//                yield AiServices.builder(AiCodeGeneratorService.class)
//                        .chatModel(chatModel)
//                        .streamingChatModel(openAiStreamingChatModel)
//                        .chatMemory(chatMemory)
//                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
////                        .outputGuardrails(new RetryOutputGuardrail()) // 添加输出护轨，为了流式输出，这里不使用
//                        .build();
//            }
//            default ->
//                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType.getValue());
//        };
//        return null;
//}

/**
 * 根据 appId 获取服务
 * @param appId
 * @param codeGenType
 * @return
 */
public AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
    String cacheKey = buildCacheKey(appId, codeGenType);
//    return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    return null;
}

/**
 * 构造缓存键
 *
 * @param appId
 * @param codeGenType
 * @return
 */
 private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
    return appId + "_" + codeGenType.getValue();
    }
}
