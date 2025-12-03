package org.example.ltwaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.ltwaicodemother.model.dto.chatHistory.ChatHistoryQueryRequest;
import org.example.ltwaicodemother.model.entity.ChatHistory;
import org.example.ltwaicodemother.model.entity.User;

import java.time.LocalDateTime;

/**
 *  服务层。
 *
 * @author Lenovo
 * @since 2025-12-03
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     * 插入对话消息
     * @param appId
     * @param userId
     * @param messageType
     * @param message
     * @return
     */
    boolean addChatMessage(Long appId, Long userId, String messageType,String message);

    /**
     * 删除对话消息
     * appId
     * @return
     */
    boolean deleteById(long appId);

    /**
     * 分页查询某 APP 的对话记录
     *
     * @param appId
     * @param pageSize
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);
    /**
     * 构造查询条件
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
}
