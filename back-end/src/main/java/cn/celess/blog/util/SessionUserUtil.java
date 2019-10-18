package cn.celess.blog.util;

import cn.celess.blog.enmu.ResponseEnum;
import cn.celess.blog.entity.User;
import cn.celess.blog.exception.MyException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import javax.servlet.http.HttpSession;

/**
 * @author : xiaohai
 * @date : 2019/03/08 15:06
 */
public class SessionUserUtil {
    public static User get() {
        Session session = SecurityUtils.getSubject().getSession();
        User user = (User) session.getAttribute("userInfo");
        if (user == null) {
            throw new MyException(ResponseEnum.HAVE_NOT_LOG_IN);
        }
        return user;
    }

    public static User get(HttpSession session) {
        User user = (User) session.getAttribute("userInfo");
        if (user == null) {
            throw new MyException(ResponseEnum.HAVE_NOT_LOG_IN);
        }
        return user;
    }

    public static User getWithOutExc() {
        Session session = SecurityUtils.getSubject().getSession();
        return (User) session.getAttribute("userInfo");
    }

    public static User getWithOutExc(HttpSession session) {
        return (User) session.getAttribute("userInfo");
    }

    public static User set(User user) {
        Session session = SecurityUtils.getSubject().getSession();
        session.removeAttribute("userInfo");
        session.setAttribute("userInfo", user);
        return user;
    }

    public static User set(User user, HttpSession session) {
        session.removeAttribute("userInfo");
        session.setAttribute("userInfo", user);
        return user;
    }
}