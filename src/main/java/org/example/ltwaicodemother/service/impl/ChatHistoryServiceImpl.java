package org.example.ltwaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.ltwaicodemother.constant.UserConstant;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;
import org.example.ltwaicodemother.exception.ThrowUtils;
import org.example.ltwaicodemother.model.dto.chatHistory.ChatHistoryQueryRequest;
import org.example.ltwaicodemother.model.entity.App;
import org.example.ltwaicodemother.model.entity.ChatHistory;
import org.example.ltwaicodemother.mapper.ChatHistoryMapper;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import org.example.ltwaicodemother.service.AppService;
import org.example.ltwaicodemother.service.ChatHistoryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 *  服务层实现。
 *
 * @author Lenovo
 * @since 2025-12-03
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{
    @Resource
    @Lazy
    private AppService appService;

    @Override
    public boolean addChatMessage(Long appId, Long userId, String messageType, String message) {
        // 基础校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");

        if(ChatHistoryMessageTypeEnum.getEnumByValue(messageType)==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"对话历史消息类型错误");
        }

        ChatHistory chatHistory =ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .messageType(messageType)
                .message(message)
                .build();
        this.save(chatHistory);
        return false;
    }

    @Override
    public boolean deleteById(long appId) {
        ThrowUtils.throwIf(appId<=0,ErrorCode.PARAMS_ERROR,"无效应用ID");
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("appId",appId);
        return this.remove(queryWrapper);
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：只有应用创建者和管理员可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
        // 构建查询条件
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(appId);
        queryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(queryRequest);
        // 查询数据
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if(chatHistoryQueryRequest==null) return  queryWrapper;
        Long userId = chatHistoryQueryRequest.getUserId();
        String message = chatHistoryQueryRequest.getMessage();
        Long appId = chatHistoryQueryRequest.getAppId();
        LocalDateTime createTime = chatHistoryQueryRequest.getCreateTime();
        String messageType = chatHistoryQueryRequest.getMessageType();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        queryWrapper.eq("appId",appId)
                    .eq("userId",userId)
                    .eq("messageType",messageType)
                    .eq("createTime",createTime)
                    .like("message",message);

        // 游标查询
        if(createTime!=null){
            queryWrapper.le("createTime",lastCreateTime);
        }

        // 排序规则
        if(StrUtil.isNotBlank(sortField)){
            queryWrapper.orderBy(sortField,"ascend".equals(sortOrder));
        }else{
            // 默认按照时间排序
            queryWrapper.orderBy("createTime",false);
        }
        return queryWrapper;
    }
}
