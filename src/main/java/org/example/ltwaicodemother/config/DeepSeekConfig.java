package org.example.ltwaicodemother.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DeepSeekConfig {

    @org.springframework.beans.factory.annotation.Value("${deepseek.api-key}")
    private String apiKey;

    @org.springframework.beans.factory.annotation.Value("${deepseek.base-url}")
    private String baseUrl;

    @org.springframework.beans.factory.annotation.Value("${deepseek.model}")
    private String model;

    @Bean
    @Primary
    public ChatModel deepSeekModel() {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)   // DeepSeek API 地址
                .modelName(model)
                .build();
    }

}
