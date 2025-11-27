package org.example.ltwaicodemother.core;

import jakarta.annotation.Resource;
import lombok.Locked;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        File result = aiCodeGeneratorFacade.generateAndSaveCode("帮我做一个博客页面，不超过20行代码", CodeGenTypeEnum.HTML, 1L);
        Assertions.assertNotNull(result);
    }

    @Test
    void generateAndSaveCodeStream() {
    }
}