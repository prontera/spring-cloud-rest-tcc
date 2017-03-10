package com.github.prontera.common.service;

import com.github.prontera.common.model.IdentityEntity;
import com.github.prontera.common.persistence.CrudMapper;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * 支持泛型注入的CrudService的默认实现
 *
 * @author Zhao Junjian
 */
@Service
public class CrudServiceImpl<T extends IdentityEntity> implements CrudService<T> {
    @Autowired
    private CrudMapper<T> mapper;

    @Override
    public T find(Long id) {
        Preconditions.checkNotNull(id, "type of id should not be NULL");
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public int persistNonNullProperties(T entity) {
        Preconditions.checkNotNull(entity, "persisting entity should not be NULL");
        initializeOffsetDateTime(entity);
        return mapper.insertSelective(entity);
    }

    @Override
    public int persist(T entity) {
        Preconditions.checkNotNull(entity, "persisting entity should not be NULL");
        initializeOffsetDateTime(entity);
        return mapper.insert(entity);
    }

    @Override
    public int update(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
        return mapper.updateByPrimaryKey(entity);
    }

    @Override
    public int updateNonNullProperties(T entity) {
        Preconditions.checkNotNull(entity, "entity in updating should not be NULL");
        entity.setUpdateTime(OffsetDateTime.now());
        return mapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public int delete(Long id) {
        Preconditions.checkNotNull(id, "type of id should not be NULL");
        return mapper.deleteByPrimaryKey(id);
    }

    private void initializeOffsetDateTime(T entity) {
        if (entity.getUpdateTime() == null) {
            entity.setUpdateTime(IdentityEntity.DEFAULT_DATE_TIME);
        }
        if (entity.getDeleteTime() == null) {
            entity.setDeleteTime(IdentityEntity.DEFAULT_DATE_TIME);
        }
    }

}
