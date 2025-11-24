package org.example.ltwaicodemother.ai;

import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    String codeGenerate(String userMessage);



}
