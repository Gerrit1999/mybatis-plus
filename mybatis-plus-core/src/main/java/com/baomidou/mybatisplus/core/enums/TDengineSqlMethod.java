package com.baomidou.mybatisplus.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支持 TDengine SQL 方法
 *
 * @author Gerrit
 * @since 2023-08-14
 */
@Getter
@AllArgsConstructor
public enum TDengineSqlMethod {

    INSERT_ONE("insert", "插入一条数据（选择字段插入）", "<script>INSERT INTO %s ( %s ) USING %s ( %s ) TAGS %s VALUES %s\n</script>"),
    INSERT_BATCH("insertBatch", "批量插入数据", "<script>INSERT INTO %s ( %s ) USING %s ( %s ) TAGS %s VALUES %s\n</script>"),
    SELECT_LAST_LIST("selectLastList", "查询满足条件的最后一条非 NULL 值数据", "<script>%s SELECT %s FROM %s %s %s\n</script>"),
    SELECT_LAST_ROW_LIST("selectLastRowList", "查询满足条件的最后一条数据", "<script>%s SELECT %s FROM %s %s %s\n</script>"),
    ;

    private final String method;
    private final String desc;
    private final String sql;
}
