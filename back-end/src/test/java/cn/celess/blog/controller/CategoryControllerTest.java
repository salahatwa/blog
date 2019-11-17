package cn.celess.blog.controller;

import cn.celess.blog.BaseTest;
import cn.celess.blog.entity.Category;
import cn.celess.blog.mapper.CategoryMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static cn.celess.blog.enmu.ResponseEnum.*;

public class CategoryControllerTest extends BaseTest {

    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void addOne() throws Exception {
        String categoryName = UUID.randomUUID().toString().substring(0, 4);
        System.out.println("categoryName: ==> " + categoryName);
        // 未登录
        mockMvc.perform(post("/admin/category/create?name=" + categoryName)).andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // User权限
        String token = userLogin();
        mockMvc.perform(post("/admin/category/create?name=" + categoryName)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // Admin权限
        token = adminLogin();
        mockMvc.perform(post("/admin/category/create?name=" + categoryName)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    Category category = (Category) JSONObject.toBean(object.getJSONObject(Result), Category.class);
                    assertEquals(categoryName, category.getName());
                    assertNotNull(category.getId());
                    assertNotNull(category.getArticles());
                });
    }

    @Test
    public void deleteOne() throws Exception {
        Category category = categoryMapper.getLastestCategory();
        // 未登录
        mockMvc.perform(delete("/admin/category/del?id=" + category.getId())).andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // User权限
        String token = userLogin();
        mockMvc.perform(delete("/admin/category/del?id=" + category.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // Admin权限
        token = adminLogin();
        mockMvc.perform(delete("/admin/category/del?id=" + category.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    assertTrue(object.getBoolean(Result));
                });
    }

    @Test
    public void updateOne() throws Exception {
        Category category = categoryMapper.getLastestCategory();
        String name = UUID.randomUUID().toString().substring(0, 4);
        // 未登录
        mockMvc.perform(put("/admin/category/update?id=" + category.getId() + "&name=" + name)).andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(HAVE_NOT_LOG_IN.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // User权限
        String token = userLogin();
        mockMvc.perform(put("/admin/category/update?id=" + category.getId() + "&name=" + name)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    assertEquals(PERMISSION_ERROR.getCode(), JSONObject.fromObject(result.getResponse().getContentAsString()).getInt(Code));
                });
        // Admin权限
        token = adminLogin();
        mockMvc.perform(put("/admin/category/update?id=" + category.getId() + "&name=" + name)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    Category c = (Category) JSONObject.toBean(object.getJSONObject(Result), Category.class);
                    assertEquals(name, c.getName());
                    assertNotNull(c.getArticles());
                    assertNotNull(c.getId());
                });
    }

    @Test
    public void getPage() throws Exception {
        mockMvc.perform(get("/categories")).andExpect(status().isOk())
                .andDo(result -> {
                    JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
                    assertEquals(SUCCESS.getCode(), object.getInt(Code));
                    JSONArray jsonArray = object.getJSONArray(Result);
                    assertNotNull(jsonArray);
                    jsonArray.forEach(o -> {
                        Category c = (Category) JSONObject.toBean(JSONObject.fromObject(o), Category.class);
                        assertNotNull(c.getName());
                        assertNotNull(c.getId());
                        assertNotNull(c.getArticles());
                    });
                });

    }
}