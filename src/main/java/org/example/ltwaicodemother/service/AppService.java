package org.example.ltwaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.ltwaicodemother.model.dto.app.AppAddRequest;
import org.example.ltwaicodemother.model.dto.app.AppQueryRequest;
import org.example.ltwaicodemother.model.entity.App;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.vo.AppVO;
import reactor.core.publisher.Flux;
import java.util.List;

public interface AppService extends IService<App> {


    Long createApp(AppAddRequest appAddRequest, User loginUser);

    AppVO getAppVo(App app);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> records);

    /**
     * 聊天生成代码
     * @param appId
     * @param prompt
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String prompt,User loginUser);

    /**
     * 应用部署
     * @param appId
     * @param loginUser
     * @return 可访问的部署地址
     */
    String deployApp(Long appId,User loginUser);
}