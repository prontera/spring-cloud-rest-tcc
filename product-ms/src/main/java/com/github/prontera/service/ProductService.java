package com.github.prontera.service;

import com.github.prontera.persistence.CrudMapper;
import com.github.prontera.domain.Product;
import com.github.prontera.persistence.ProductMapper;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zhao Junjian
 */
@Service
public class ProductService extends CrudServiceImpl<Product> {
    @Autowired
    private ProductMapper mapper;

    @Autowired
    public ProductService(CrudMapper<Product> mapper) {
        super(mapper);
    }

    public List<Product> findAll(int offset, int limited) {
        Preconditions.checkArgument(offset > -1);
        Preconditions.checkArgument(limited > -1);
        return mapper.selectAll(offset, limited);
    }

}
