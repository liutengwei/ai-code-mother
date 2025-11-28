package org.example.ltwaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
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
import org.example.ltwaicodemother.model.vo.LoginUserVO;
import org.example.ltwaicodemother.model.vo.UserVO;
import org.example.ltwaicodemother.service.AppService;
import org.example.ltwaicodemother.service.UserService;
import org.springframework.stereotype.Service;

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
}
