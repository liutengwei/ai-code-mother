package org.example.ltwaicodemother.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.ltwaicodemother.common.BaseResponse;
import org.example.ltwaicodemother.common.ResultUtils;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.exception.ThrowUtils;
import org.example.ltwaicodemother.model.dto.app.AppAddRequest;
import org.example.ltwaicodemother.model.dto.app.AppDeployRequest;
import org.example.ltwaicodemother.model.dto.app.AppQueryRequest;
import org.example.ltwaicodemother.model.dto.app.AppUpdateRequest;
import org.example.ltwaicodemother.model.entity.App;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import org.example.ltwaicodemother.model.vo.AppVO;
import org.example.ltwaicodemother.service.AppService;
import org.example.ltwaicodemother.service.ChatHistoryService;
import org.example.ltwaicodemother.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *  控制层。
 *
 * @author Lenovo
 * @since 2025-11-27
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;
    @Resource
    private ChatHistoryService chatHistoryService;

    @GetMapping(value = "/chat/gen/code",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId,
                                                       @RequestParam String userMessage,
                                                       HttpServletRequest request){
        // 参数校验
        ThrowUtils.throwIf(appId==null || appId<=0,ErrorCode.PARAMS_ERROR,"appId 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage),ErrorCode.PARAMS_ERROR,"提示词不能为空");
        // 登陆当前用户
        User loginUser = userService.getLoginUser(request);
        // SSE 流式输出返回
        Flux<String> contentFlux= appService.chatToGenCode(appId,userMessage,loginUser);
        return contentFlux
                .map(chunk->{
                    Map<String,String> wrapper=Map.of("d",chunk);
                    String jsonStr = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder().data(jsonStr).build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder().
                                event("done").
                                data("").
                                build()
                ));
    }

    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest==null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long appId= appService.createApp(appAddRequest,loginUser);
        return ResultUtils.success(appId);
    }

    /**
     * 更新应用信息
     * @param appUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest,HttpServletRequest request){
        ThrowUtils.throwIf(appUpdateRequest==null || appUpdateRequest.getId()==null, ErrorCode.PARAMS_ERROR);
        String appName = appUpdateRequest.getAppName();
        ThrowUtils.throwIf(StrUtil.isBlank(appName), ErrorCode.PARAMS_ERROR);

        Long id=appUpdateRequest.getId();
        // 确认应用是否存在
        App oldApp=appService.getById(id);
        ThrowUtils.throwIf(oldApp==null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);

        // 仅本人可以更新
        ThrowUtils.throwIf(!id.equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);

        // 组装更新数据;
        App app=new App();
        app.setId(id);
        app.setAppName(appName);

        // 设置编辑时间
        app.setEditTime(LocalDateTime.now());
        // 更新数据库应用信息
        boolean update = appService.updateById(app);
        ThrowUtils.throwIf(update, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(update);
    }

    @GetMapping("/id")
    public AppVO getAppVOById(Long id){
        ThrowUtils.throwIf(id<=0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app==null, ErrorCode.NOT_FOUND_ERROR);
        return appService.getAppVo(app);
    }

    /**
     * 分页获取当前用户的应用列表
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request){
        // 参数校验
        ThrowUtils.throwIf(appQueryRequest==null, ErrorCode.PARAMS_ERROR);
        // 限制每页最多20个
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize>=20, ErrorCode.PARAMS_ERROR,"每页不能超过20");
        int pageNum=appQueryRequest.getPageNum();
        // 只查询当前用户创建的
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @param request          请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        // 检查部署请求是否为空
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取应用 ID
        Long appId = appDeployRequest.getAppId();
        // 检查应用 ID 是否为空
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId, loginUser);
        // 返回部署 URL
        return ResultUtils.success(deployUrl);
    }

}
