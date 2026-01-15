package org.example.ltwaicodemother.langchain4j.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.ai.AiCodeGenTypeRoutingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AiConcurrentTest  {
    @Resource
    private AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;


    @Test
    public void testConcurrentRoutingCalls() throws InterruptedException {
        String[] prompts = {"做一个简单的HTML页面",
                "做一个多页面网站项目",
                "做一个Vue管理系统"
        };
        Thread[] thread = new Thread[prompts.length];
        for (int i = 0; i < prompts.length; i++) {
            String prompt = prompts[i];
            final int index = i + 1;
            thread[i] = Thread.ofVirtual().start(() -> {
                var result = aiCodeGenTypeRoutingService.routeCodeGenType(prompt);
                log.info("线程 {}: {} -> {}", index, prompt, result.getValue());
            });
        }
        for (int i = 0; i < prompts.length; i++) {
            thread[i].join();
        }
    }
}
