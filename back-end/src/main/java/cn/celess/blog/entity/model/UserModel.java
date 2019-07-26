package cn.celess.blog.entity.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : xiaohai
 * @date : 2019/04/22 23:13
 */
@Getter
@Setter
public class UserModel {

    private Long id;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String displayName;

    private Boolean emailStatus = false;

    /**
     * 头像地址
     */
    private String avatarImgUrl;

    private String desc;

    private String recentlyLandedDate;

    private String role = "user";
}
