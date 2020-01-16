package com.github.prontera.domain.type.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.ParameterizedType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Zhao Junjian
 */
public abstract class GenericTypeHandler<T extends Enum<T>> extends BaseTypeHandler<T> {
    private final Class<T> type;
    private final T[] enums;

    protected GenericTypeHandler() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        enums = type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    public abstract int getEnumIntegerValue(T parameter);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, getEnumIntegerValue(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumIntegerValue(i);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumIntegerValue(i);
        }
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return locateEnumIntegerValue(i);
        }
    }

    private T locateEnumIntegerValue(int code) {
        for (T status : enums) {
            if (Objects.equals(getEnumIntegerValue(status), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("unknown enum integer code：" + code + ", type name is: " + type.getSimpleName());
    }
}
