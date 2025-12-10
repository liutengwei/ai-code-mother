package org.example.ltwaicodemother.ai.tool;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具管理器
 */
@Slf4j
@Component
public class ToolManger {

    private final Map<String,BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] tools;

    /**
     * 初始化工具
     */
    public void initTools(){
        for(BaseTool tool: tools){
            toolMap.put(tool.getToolName(), tool);
            log.info("{}初始化完成",tool.getToolName());
        }
        log.info("全部工具加载完成");
    }

    /**
     * 根据工具名称返回工具
     * @param name
     * @return
     */
    public BaseTool getTool(String name){
        return  toolMap.get(name);
    }

    /**
     * 获取所有工具数组
     * @return
     */
    public BaseTool[] getAllTools(){
        return tools;
    }

}
