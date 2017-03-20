package com.github.prontera.service;

import com.github.prontera.model.IdentityDomain;
import com.github.prontera.persistence.CrudMapper;
import com.google.common.base.Preconditions;

import java.time.OffsetDateTime;

/**
 * 支持泛型注入的CrudService的默认实现
 *
 * @author Zhao Junjian
 */
public class CrudServiceImpl<T extends IdentityDomain> implements CrudService<T> {

    private final CrudMapper<T> mapper;

    public CrudServiceImpl(CrudMapper<T> mapper) {
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
        initializeOffsetDateTime(entity);
        return getMapper().insertSelective(entity);
    }

    @Override
    public int persist(T entity) {
        Preconditions.checkNotNull(entity, "persisting entity should not be NULL");
        initializeOffsetDateTime(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int update(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
        return getMapper().updateByPrimaryKey(entity);
    }

    @Override
    public int updateNonNullProperties(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
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

    private void initializeOffsetDateTime(T entity) {
        entity.setCreateTime(OffsetDateTime.now());
        if (entity.getUpdateTime() == null) {
            entity.setUpdateTime(IdentityDomain.DEFAULT_DATE_TIME);
        }
        if (entity.getDeleteTime() == null) {
            entity.setDeleteTime(IdentityDomain.DEFAULT_DATE_TIME);
        }
    }

}
