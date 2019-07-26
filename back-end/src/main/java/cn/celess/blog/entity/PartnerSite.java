package cn.celess.blog.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 友链
 *
 * @author : xiaohai
 * @date : 2019/05/12 11:33
 */
@Data
public class PartnerSite {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String url;

    private Boolean open;

    public PartnerSite() {
    }

    public PartnerSite(String name, String url, Boolean open) {
        this.name = name;
        this.url = url;
        this.open = open;
    }
}
