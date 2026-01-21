package com.ltw.ltwaicodemother.innerservice;

import com.ltw.ltwaicodemother.model.entity.User;
import com.ltw.ltwaicodemother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.example.ltwaicodemother.exception.BusinessException;
import org.example.ltwaicodemother.exception.ErrorCode;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static org.example.ltwaicodemother.constant.UserConstant.USER_LOGIN_STATE;

public interface InnerUserService {

    List<User> listByIds(Collection<? extends Serializable> ids);

    User getById(Serializable id);

    UserVO getUserVO(User user);

    // 静态方法，避免跨服务调用
    static User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}
