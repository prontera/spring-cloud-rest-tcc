package com.github.prontera.persistence;

import com.github.prontera.annotation.MyBatisRepository;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@SuppressWarnings({"InterfaceNeverImplemented", "MybatisMapperMethodInspection"})
@MyBatisRepository
public interface CrudMapper<T> {

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

}
