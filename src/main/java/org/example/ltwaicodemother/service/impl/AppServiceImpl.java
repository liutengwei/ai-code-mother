package org.example.ltwaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import org.example.ltwaicodemother.constant.AppConstant;
import org.example.ltwaicodemother.core.AiCodeGeneratorFacade;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.exception.ThrowUtils;
import org.example.ltwaicodemother.mapper.AppMapper;
import org.example.ltwaicodemother.model.dto.app.AppAddRequest;
import org.example.ltwaicodemother.model.dto.app.AppQueryRequest;
import org.example.ltwaicodemother.model.entity.App;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.enums.CodeGenTypeEnum;
import org.example.ltwaicodemother.model.vo.AppVO;
import org.example.ltwaicodemother.model.vo.UserVO;
import org.example.ltwaicodemother.service.AppService;
import org.example.ltwaicodemother.service.ChatHistoryService;
import org.example.ltwaicodemother.service.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  服务层实现。
 *
 * @author Lenovo
 * @since 2025-11-27
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService {
    @Resource
    private UserService userService;
    @Resource
    private AppService appService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private ChatHistoryService chatHistoryService;

    public AppServiceImpl() {
    }

    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR,"初始化 prompt 不能为空");
        App app = new App();
        // 构造入库对象
        BeanUtil.copyProperties(appAddRequest, app);
        // 应用名称为 prompt 的前12位
        app.setAppName(initPrompt.substring(0,Math.min(12,initPrompt.length())));
        // todo 待升级  暂定为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        boolean save = this.save(app);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR);
        return app.getId();
    }

    @Override
    public AppVO getAppVo(App app){
        ThrowUtils.throwIf(app==null, ErrorCode.PARAMS_ERROR);
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        if(app.getId()!=null){
            User user = userService.getById(app.getId());
            UserVO loginUserVO = userService.getUserVO(user);
            appVO.setUser(loginUserVO);
        }
        return appVO;
    }

    // todo like的字符串为空会导致查不出数据
    /**
     * 构造查询参数
     * @param appQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> records) {
        if(CollUtil.isEmpty(records)){
            return new ArrayList<>();
        }
        Set<Long> idSet=records.stream().map(App::getId).collect(Collectors.toSet());
        Map<Long, UserVO> collect = userService.listByIds(idSet).stream().collect(Collectors.toMap(User::getId, userService::getUserVO));
        return records.stream().map(app->{
            AppVO appVO = getAppVo(app);
            UserVO userVO = collect.get(appVO.getId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    /**
     * 和模型沟通交流
     * @param appId
     * @param userMessage
     * @param loginUser
     * @return
     */
    @Override
    public Flux<String> chatToGenCode(Long appId, String userMessage, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(appId==null || appId<=0,ErrorCode.PARAMS_ERROR,"appId 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage),ErrorCode.PARAMS_ERROR,"提示词不能为空");

        App app = appService.getById(appId);
        ThrowUtils.throwIf(app==null, ErrorCode.NOT_FOUND_ERROR,"应用不存在");

        // 用户只能查询自己的app
        ThrowUtils.throwIf(app.getUserId().equals(loginUser.getId()),ErrorCode.NO_AUTH_ERROR,"无权限访问该应用");

        // 获取应用类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum enumByValue = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(enumByValue==null,ErrorCode.NOT_FOUND_ERROR,"应用类型不存在");

        // 调用 AI 生成代码（流式）
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(userMessage, enumByValue, appId);

    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(appId==null || appId<=0,ErrorCode.PARAMS_ERROR,"应用 ID 错误");
        App app = this.getById(appId);
        ThrowUtils.throwIf(app==null,ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        ThrowUtils.throwIf(!Objects.equals(app.getUserId(), loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");

        // 检查是否已经有 deployKey
        String deployKey = app.getDeployKey();
        if(StrUtil.isBlank(deployKey)){
            deployKey = RandomUtil.randomString(6);
        }
        // 获取代码生成类型，获取原始代码生辰路径（应用访问目录）
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath= AppConstant.CODE_OUTPUT_ROOT_DIR+ File.separator+sourceDirName;
        File sourceFile = new File(sourceDirPath);
        if(!sourceFile.exists() || !sourceFile.isDirectory()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用代码路径不存在，请先生成应用");
        }
        // 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_HOST + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceFile, new File(deployDirPath),true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用部署失败："+e.getMessage());
        }
        // 更新数据库
        App updateApp =new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult,ErrorCode.SYSTEM_ERROR,"应用部署更新失败");
        // 返回 URL 地址
        return deployDirPath;
    }

    @Override
    public boolean removeById(Serializable id){
        if(id==null){
            return false;
        }
        long appId=Long.parseLong(id.toString());
        if(appId<=0) return false;
        try{
            chatHistoryService.deleteById(appId);
        }catch(Exception e){
            throw new BusinessException();
        }
    }
}
