package com.github.prontera.mapper;

import com.github.prontera.domain.Order;
import com.github.prontera.model.request.CheckoutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Mapper
public interface OrderRequestCopier {

    OrderRequestCopier INSTANCE = Mappers.getMapper(OrderRequestCopier.class);

    CheckoutRequest copy(CheckoutRequest source);

    List<CheckoutRequest> copy(Collection<CheckoutRequest> source);

    Order toDomainModel(CheckoutRequest source);

}
