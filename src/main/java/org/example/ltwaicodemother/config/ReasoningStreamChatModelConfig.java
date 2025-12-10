package org.example.ltwaicodemother.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("langchain4j.open-ai.chat-model")
@Data
public class ReasoningStreamChatModelConfig {

    private String baseUrl;

    private String apiKey;

    @Bean
    public StreamingChatModel reasoningStreamChatModel(){
        final String modelName="deepseek-reasoner";
        final int maxTokens=32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
