package cn.celess.blog.mapper;

import cn.celess.blog.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: 小海
 * @Date： 2019/07/03 00:23
 * @Description：
 */
@Repository
@Mapper
public interface VisitorMapper {
    int insert(Visitor visitor);

    int delete(long id);

    List<Visitor> findAll();

    long count();
}
