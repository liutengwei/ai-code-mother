package org.example.ltwaicodemother.ai.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jdk.dynalink.StandardOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.constant.AppConstant;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件写入工具
 * 支持AI工具调用写入文件
 */
@Slf4j
public class FileWriteTool {

    @Tool("写入文件到指定路径")
    public String writeFile(@P("文件的相对路径")String relativeFiePath,
                            @P("写入文件的内容") String content,
                            @ToolMemoryId Long appId){
        try{
            Path path= Paths.get(relativeFiePath);

            if(!path.isAbsolute()) {
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFiePath);
            }

            Files.write(path,content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            log.info("写入成功："+path.toAbsolutePath());
            return "写入成功："+path.toAbsolutePath();
        } catch (Exception e) {
            log.error("写入失败："+e.getMessage());
            return ("写入失败："+e.getMessage());
        }
    }
}
