package com.github.prontera.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Solar
 */
public interface Business<T> {
    @Transactional
    int persist(T entity);

    @Transactional(readOnly = true)
    T find(int id);

    @Transactional(readOnly = true)
    List<T> findAll();

    @Transactional
    int update(T entity);

    @Transactional
    int delete(int id);

    @Transactional(readOnly = true)
    int getElementCount();
}
