package com.github.prontera.persistence;

/**
 * MyBatis通用Mapper, 通常搭配MBG一起使用
 *
 * @author Zhao Junjian
 */
@SuppressWarnings({"InterfaceNeverImplemented", "MybatisMapperMethodInspection"})
public interface CrudMapper<T> {

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
