package org.example.ltwaicodemother.service;

import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.ltwaicodemother.model.entity.User;
import org.example.ltwaicodemother.model.vo.LoginUserVO;
import org.example.ltwaicodemother.model.vo.UserVO;

/**
 *  服务层。
 *
 * @author Lenovo
 * @since 2025-11-17
 */
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    LoginUserVO getLoginUserVO(User loginUser);

    boolean userLogout(HttpServletRequest request);

    UserVO getUserVO(User user);

}
