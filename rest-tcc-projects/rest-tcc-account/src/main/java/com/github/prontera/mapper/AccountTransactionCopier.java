package com.github.prontera.mapper;

import com.github.prontera.account.model.response.QueryAccountTxnResponse;
import com.github.prontera.domain.AccountTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * @author Zhao Junjian
 * @date 2020/01/23
 */
@Mapper
public interface AccountTransactionCopier {

    AccountTransactionCopier INSTANCE = Mappers.getMapper(AccountTransactionCopier.class);

    AccountTransaction copy(AccountTransaction source);

    List<AccountTransaction> copy(Collection<AccountTransaction> source);

    @Mappings({
        @Mapping(target = "state", expression = "java( source.getState().val() )"),
    })
    QueryAccountTxnResponse toQueryAccountTxnResponse(AccountTransaction source);

}
