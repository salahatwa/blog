package cn.celess.blog.entity.model;

import lombok.Data;

/**
 * @author : xiaohai
 * @date : 2019/04/21 22:43
 */
public class QiniuResponse {
    public String key;
    public String hash;
    public String bucket;
    public long fsize;
}
