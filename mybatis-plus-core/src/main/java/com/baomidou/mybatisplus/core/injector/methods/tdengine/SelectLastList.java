package com.baomidou.mybatisplus.core.injector.methods.tdengine;

import com.baomidou.mybatisplus.core.enums.TDengineSqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 查询满足条件的最后一条非 NULL 值数据
 *
 * @author Gerrit
 * @since 2023/8/15
 */
public class SelectLastList extends AbstractMethod {

    public SelectLastList() {
        super(TDengineSqlMethod.SELECT_LAST_LIST.getMethod());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        TDengineSqlMethod sqlMethod = TDengineSqlMethod.SELECT_LAST_LIST;
        String sqlSelectColumns = sqlSelectColumns(tableInfo, true);
        String sqlWhere = sqlWhereEntityWrapper(true, tableInfo);
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlSelectColumns, tableInfo.getTableName(), sqlWhere, sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource, tableInfo);
    }

    @Override
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        /* 假设存在用户自定义的 resultMap 映射返回 */
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || table.isAutoInitResultMap()) {
            /* 未设置 resultMap 或者 resultMap 是自动构建的,视为属于mp的规则范围内 */
            selectColumns = table.getAllSqlSelect();
        }
        return Arrays.stream(selectColumns.split(COMMA))
                .map(col -> String.format("LAST(%s)", col))
                .collect(Collectors.joining(COMMA));
    }
}
