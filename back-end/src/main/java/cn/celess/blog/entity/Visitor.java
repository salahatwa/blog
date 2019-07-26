package cn.celess.blog.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author : xiaohai
 * @date : 2019/04/02 22:14
 */
@Data
public class Visitor {

    private long id;
    private String ip;
    private Date date;
    private String ua;

    public Visitor(String ip, Date date, String ua) {
        this.ip = ip;
        this.date = date;
        this.ua = ua;
    }

    public Visitor() {
    }
}
