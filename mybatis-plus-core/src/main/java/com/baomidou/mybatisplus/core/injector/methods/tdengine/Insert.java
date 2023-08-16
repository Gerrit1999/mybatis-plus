package com.baomidou.mybatisplus.core.injector.methods.tdengine;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.tdengine.SubtableName;
import com.baomidou.mybatisplus.annotation.tdengine.Tag;
import com.baomidou.mybatisplus.core.enums.TDengineSqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 插入一条数据（选择字段插入）
 *
 * @author Gerrit
 * @since 2023-08-15
 */
public class Insert extends AbstractMethod {

    public Insert() {
        super(TDengineSqlMethod.INSERT_ONE.getMethod());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        Map<Boolean, List<TableFieldInfo>> partitionedFields = tableInfo.getFieldList()
                .stream()
                .collect(Collectors.partitioningBy(f -> f.getField().isAnnotationPresent(Tag.class)));
        List<TableFieldInfo> tagFields = partitionedFields.get(true);
        List<TableFieldInfo> columnFields = partitionedFields.get(false);
        // 标签列
        String tags = tagFields.stream().map(TableFieldInfo::getColumn).collect(Collectors.joining(COMMA));
        // 普通列
        String columns = tableInfo.getKeyInsertSqlColumn(true, null, false) + columnFields.stream().map(TableFieldInfo::getColumn).collect(Collectors.joining(COMMA));

        // 标签列值
        String tagSqlProperty = this.filterTableFieldInfo(tagFields, null, i -> i.getInsertSqlProperty(null), EMPTY);
        String tagScript = LEFT_BRACKET + tagSqlProperty.substring(0, tagSqlProperty.length() - 1) + RIGHT_BRACKET;
        // 普通列值
        String columnSqlProperty = tableInfo.getKeyInsertSqlProperty(true, null, false) +
                this.filterTableFieldInfo(columnFields, null, i -> i.getInsertSqlProperty(null), EMPTY);
        String columnScript = SqlScriptUtils.convertTrim(columnSqlProperty, LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);

        // 构造子表名
        Map<String, String> valuesMap = new HashMap<>();
        for (TableFieldInfo tagField : tagFields) {
            String property = tagField.getInsertSqlProperty(null);
            property = property.substring(0, property.length() - 1).replace('#', '$');
            valuesMap.put(tagField.getProperty(), property);
        }
        SubtableName anno = modelClass.getAnnotation(SubtableName.class);
        String subtableName;
        if (anno == null) {
            subtableName = tableInfo.getTableName() + UNDERSCORE + DigestUtils.md5Hex(tags);
        } else {
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            sub.setDisableSubstitutionInValues(true);
            subtableName = sub.replace(anno.value());
        }

        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo.havePK()) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }

        // 填充至sql模板
        TDengineSqlMethod sqlMethod = TDengineSqlMethod.INSERT_BATCH;
        String sql = String.format(sqlMethod.getSql(), subtableName, columns, tableInfo.getTableName(), tags, tagScript, columnScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
