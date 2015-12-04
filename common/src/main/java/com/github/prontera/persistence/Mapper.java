package com.github.prontera.persistence;

import java.util.List;

/**
 * @author Solar
 * @date 2015/8/24
 */
public interface Mapper<T> {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(T entity);

    T selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(T entity);

    int selectCount();

    List<T> selectAll();

}
