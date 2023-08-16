package com.baomidou.mybatisplus.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 支持 TDengine 的 BaseMapper
 *
 * @author Gerrit
 * @since 2023-08-16
 */
public interface TDengineBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入, 自动建子表(以list第一条数据填充子表名和标签值)
     */
    int insertBatch(@NonNull @Param(Constants.LIST) List<T> list);

    /**
     * 查询满足条件的最后一条非 NULL 值数据
     */
    default T selectLastOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> list = this.selectLastList(queryWrapper);
        // 抄自 DefaultSqlSession#selectOne
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    /**
     * 查询满足条件的最后一条数据
     */
    default T selectLastRowOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> list = this.selectLastRowList(queryWrapper);
        // 抄自 DefaultSqlSession#selectOne
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    /**
     * 查询满足条件的最后一条非 NULL 值数据分组列表
     */
    List<T> selectLastList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 查询满足条件的最后一条数据分组列表
     */
    List<T> selectLastRowList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
