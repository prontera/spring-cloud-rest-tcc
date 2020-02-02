package com.github.prontera.service;

import com.github.prontera.domain.IdenticalDomain;
import com.github.prontera.persistence.CrudMapper;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

/**
 * @author Zhao Junjian
 */
public class IdenticalCrudService<T extends IdenticalDomain> implements CrudService<T> {

    private final CrudMapper<T> mapper;

    public IdenticalCrudService(CrudMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T find(Long id) {
        Preconditions.checkNotNull(id, "type of id should not be NULL");
        return getMapper().selectByPrimaryKey(id);
    }

    @Override
    public int persistNonNullProperties(T entity) {
        Preconditions.checkNotNull(entity, "persisting entity should not be NULL");
        initializeDateTime(entity);
        return getMapper().insertSelective(entity);
    }

    @Override
    public int persist(T entity) {
        Preconditions.checkNotNull(entity, "persisting entity should not be NULL");
        initializeDateTime(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int update(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateAt(LocalDateTime.now());
        return getMapper().updateByPrimaryKey(entity);
    }

    @Override
    public int updateNonNullProperties(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateAt(LocalDateTime.now());
        return getMapper().updateByPrimaryKeySelective(entity);
    }

    @Override
    public int delete(Long id) {
        Preconditions.checkNotNull(id, "type of id should not be NULL");
        return getMapper().deleteByPrimaryKey(id);
    }

    private CrudMapper<T> getMapper() {
        return mapper;
    }

    private void initializeDateTime(T entity) {
        entity.setCreateAt(LocalDateTime.now());
        entity.setUpdateAt(LocalDateTime.now());
    }

}
