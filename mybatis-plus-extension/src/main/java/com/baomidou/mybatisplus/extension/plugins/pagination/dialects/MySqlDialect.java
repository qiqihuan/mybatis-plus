/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * MYSQL 数据库分页语句组装实现
 *
 * @author hubin
 * @since 2016-01-23
 */
public class MySqlDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        // 转小写，避免截取出错
        originalSql = originalSql.toLowerCase();
        // 按from拆分
        String[] froms = originalSql.split("from");
        // 按空格拆分
        String[] fragment = originalSql.split(StringPool.SPACE);
        int tableIndex = originalSql.indexOf("from") + 1;
        String tableName = fragment[tableIndex];
        String selectStr = froms[0];
        // 替换冲突字
        selectStr = selectStr.replace("id", "t2.id");
        String[] where = originalSql.split(tableName);
        StringBuilder stringBuilder = new StringBuilder(selectStr);
        // 拼接sql
        stringBuilder.append(" from ( select id from ").append(tableName)
            .append(" limit ")
            .append(FIRST_MARK)
            .append(StringPool.COMMA)
            .append(SECOND_MARK)
            .append(" ) t1 inner join ")
            .append(tableName)
            .append(" t2 on t1.id = t2.id ");
        // 如果有where判断，就拼接
        if (where.length > 1) {
            stringBuilder.append(where[1].replace("id", "t2.id"));
        }
        return new DialectModel(stringBuilder.toString(), offset, limit).setConsumerChain();
    }
}
