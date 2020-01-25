package com.github.prontera.mapper;

import com.github.prontera.domain.ProductTransaction;
import com.github.prontera.product.model.response.QueryProductTxnResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * @author Zhao Junjian
 * @date 2020/01/25
 */
@Mapper
public interface ProductTransactionCopier {

    ProductTransactionCopier INSTANCE = Mappers.getMapper(ProductTransactionCopier.class);

    ProductTransaction copy(ProductTransaction source);

    List<ProductTransaction> copy(Collection<ProductTransaction> source);

    @Mappings({
        @Mapping(target = "state", expression = "java( source.getState().val() )"),
    })
    QueryProductTxnResponse toQueryAccountTxnResponse(ProductTransaction source);

}
