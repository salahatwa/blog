package cn.celess.blog.controller;

import cn.celess.blog.BaseTest;
import cn.celess.blog.entity.User;
import cn.celess.blog.entity.model.UserModel;
import cn.celess.blog.entity.request.UserReq;
import cn.celess.blog.mapper.UserMapper;
import cn.celess.blog.util.MD5Util;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static cn.celess.blog.enmu.ResponseEnum.*;

public class UserControllerTest extends BaseTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void login() {
        assertNotNull(userLogin());
        assertNotNull(adminLogin());
    }

    @Test
    public void registration() {
        // 自行手动测试！
        // TODO :
    }

    @Test
    public void logout() throws Exception {
        mockMvc.perform(get("/logout")).andDo(result -> assertEquals(SUCCESS.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(get("/logout").header("Authorization", userLogin())).andDo(result -> assertEquals(SUCCESS.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
    }

    @Test
    public void updateInfo() throws Exception {
        String desc = UUID.randomUUID().toString().substring(0, 4);
        String disPlayName = UUID.randomUUID().toString().substring(0, 4);
        mockMvc.perform(put("/user/userInfo/update?desc=" + desc + "&displayName=" + disPlayName).header("Authorization", userLogin()))
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    UserModel u = (UserModel) JSONObject.toBean(object.getJSONObject(Result), UserModel.class);
                    assertEquals(desc, u.getDesc());
                    assertEquals(disPlayName, u.getDisplayName());
                    assertNotNull(u.getId());
                });
    }

    @Test
    public void getUserInfo() throws Exception {
        mockMvc.perform(get("/user/userInfo").header("Authorization", userLogin()))
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    UserModel u = (UserModel) JSONObject.toBean(object.getJSONObject(Result), UserModel.class);
                    assertNotNull(u.getId());
                    assertNotNull(u.getEmail());
                    assertNotNull(u.getDisplayName());
                    assertNotNull(u.getEmailStatus());
                    assertNotNull(u.getAvatarImgUrl());
                    assertNotNull(u.getDesc());
                    assertNotNull(u.getRecentlyLandedDate());
                    assertNotNull(u.getRole());
                });
    }

    @Test
    public void upload() throws Exception {
        File logoFile = new File("C:\\Users\\zh564\\Pictures\\logo.png");
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream(logoFile));
        mockMvc.perform(multipart("/user/imgUpload").file(file)).andDo(result -> assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(multipart("/user/imgUpload").file(file).header("Authorization", userLogin())).andDo(result -> {
            JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
            assertEquals(SUCCESS.getCode(), object.getInt(Code));
            assertNotNull(object.getString(Result));
        });
    }

    @Test
    public void sendResetPwdEmail() {
        // ignore
    }

    @Test
    public void sendVerifyEmail() {
        // ignore
    }

    @Test
    public void emailVerify() {
        // ignore
    }

    @Test
    public void resetPwd() {
        // ignore
    }

    @Test
    public void multipleDelete() throws Exception {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String s = UUID.randomUUID().toString();
            String email = s.substring(s.length() - 4) + "@celess.cn";
            String pwd = MD5Util.getMD5("123456789");
            int i1 = userMapper.addUser(email, pwd);
            if (i1 == 0) {
                continue;
            }
            userList.add(userMapper.findByEmail(email));
            if (i == 9) {
                //设置一个管理员
                userMapper.setUserRole(userMapper.findByEmail(email).getId(), "admin");
            }
        }
        List<Long> idList = new ArrayList<>();
        userList.forEach(user -> idList.add(user.getId()));
        System.out.println("id :: == > " + idList.toString());
        mockMvc.perform(delete("/admin/user/delete").content(idList.toString()).contentType("application/json"))
                .andDo(result -> assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(delete("/admin/user/delete").content(idList.toString()).contentType("application/json").header("Authorization", userLogin()))
                .andDo(result -> assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(delete("/admin/user/delete").content(idList.toString()).contentType("application/json").header("Authorization", adminLogin()))
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    JSONArray jsonArray = object.getJSONArray(Result);
                    jsonArray.forEach(o -> {
                        JSONObject json = JSONObject.fromObject(o);
                        // 判断响应数据中是否包含输入的id
                        assertTrue(idList.contains((long) json.getInt("id")));
                        // 判断处理状态
                        boolean status = json.getBoolean("status");
                        if (json.containsKey("msg"))
                            assertFalse(status);
                        else
                            assertTrue(status);
                    });
                });

    }

    @Test
    public void updateInfoByAdmin() throws Exception {
        UserReq userReq = new UserReq();
        String email = UUID.randomUUID().toString().substring(0, 4) + "@celess.cn";
        userMapper.addUser(email, MD5Util.getMD5("123456789"));
        User userByDb = userMapper.findByEmail(email);
        userReq.setId(userByDb.getId());
        userReq.setPwd(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        userReq.setDesc(UUID.randomUUID().toString());
        userReq.setEmailStatus(new Random().nextBoolean());
        userReq.setRole("admin");
        userReq.setDisplayName(UUID.randomUUID().toString().substring(0, 4));
        userReq.setEmail(UUID.randomUUID().toString().substring(0, 5) + "@celess.cn");
        mockMvc.perform(put("/admin/user").contentType("application/json").content(JSONObject.fromObject(userReq).toString()))
                .andDo(result -> assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(put("/admin/user").contentType("application/json").header("Authorization", userLogin()).content(JSONObject.fromObject(userReq).toString()))
                .andDo(result -> assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(put("/admin/user").contentType("application/json").header("Authorization", adminLogin()).content(JSONObject.fromObject(userReq).toString()))
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    UserModel user = (UserModel) JSONObject.toBean(object.getJSONObject(Result), UserModel.class);
                    assertEquals(userReq.getId(), user.getId());
                    assertEquals(userReq.getRole(), user.getRole());
                    assertEquals(userReq.getEmail(), user.getEmail());
                    assertEquals(userReq.getDesc(), user.getDesc());
                    assertEquals(userReq.getDisplayName(), user.getDisplayName());
                });
    }

    @Test
    public void getAllUser() throws Exception {
        mockMvc.perform(get("/admin/users?page=1&count=10"))
                .andDo(result -> assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(get("/admin/users?page=1&count=10").header("authorization", userLogin()))
                .andDo(result -> assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code)));
        mockMvc.perform(get("/admin/users?page=1&count=10").header("Authorization", adminLogin()))
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    // 结果集非空
                    assertNotNull(object.getJSONObject(Result));
                    // 判断pageInfo是否包装完全
                    JSONObject resultJson = JSONObject.fromObject(object.getJSONObject(Result));
                    PageInfo pageInfo = (PageInfo) JSONObject.toBean(resultJson, PageInfo.class);
                    assertNotEquals(0, pageInfo.getTotal());
                    assertNotEquals(0, pageInfo.getStartRow());
                    assertNotEquals(0, pageInfo.getEndRow());
                    assertEquals(1, pageInfo.getPageNum());
                    assertEquals(10, pageInfo.getPageSize());
                    // 内容完整
                    for (Object user : pageInfo.getList()) {
                        UserModel u = (UserModel) JSONObject.toBean(JSONObject.fromObject(user), UserModel.class);
                        assertNotNull(u.getId());
                        assertNotNull(u.getEmail());
                        assertNotNull(u.getRole());
                        assertNotNull(u.getEmailStatus());
                        assertNotNull(u.getDisplayName());
                    }
                });
    }

    @Test
    public void getEmailStatus() throws Exception {
        String email = UUID.randomUUID().toString().substring(0, 4) + "@celess.cn";
        mockMvc.perform(get("/emailStatus/" + email)).andDo(result -> {
            String content = result.getResponse().getContentAsString();
            assertEquals(SUCCESS.getCode(), JSONObject.fromObject(content).getInt(Code));
            assertFalse(JSONObject.fromObject(content).getBoolean(Result));
        });
        email = "a@celess.cn";
        mockMvc.perform(get("/emailStatus/" + email)).andDo(result -> {
            String content = result.getResponse().getContentAsString();
            assertEquals(SUCCESS.getCode(), JSONObject.fromObject(content).getInt(Code));
            assertTrue(JSONObject.fromObject(content).getBoolean(Result));
        });
    }
}