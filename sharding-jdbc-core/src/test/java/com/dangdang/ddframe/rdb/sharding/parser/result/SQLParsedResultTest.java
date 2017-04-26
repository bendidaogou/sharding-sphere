/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parser.result;

import com.dangdang.ddframe.rdb.sharding.parser.result.merger.MergeContext;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.Condition;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.Condition.BinaryOperator;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.Condition.Column;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.ConditionContext;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.RouteContext;
import com.dangdang.ddframe.rdb.sharding.parser.result.router.SQLBuilder;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.AggregationSelectItemContext;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.AggregationType;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.GroupByContext;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.LimitContext;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.OrderByContext;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.OrderByType;
import com.dangdang.ddframe.rdb.sharding.parser.sql.context.TableContext;
import com.google.common.base.Optional;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public final class SQLParsedResultTest {
    
    @Test
    @Ignore
    public void assertToString() throws IOException {
        SQLParsedResult actual = new SQLParsedResult();
        generateRouteContext(actual.getRouteContext());
        actual.getConditionContexts().add(generateConditionContext());
        generateMergeContext(actual.getMergeContext());
        assertThat(actual.toString(), is("SQLParsedResult(routeContext=RouteContext(tables=[TableContext(originalLiterals=order, name=order, alias=Optional.of(o)), "
                + "TableContext(originalLiterals=order_item, name=order_item, alias=Optional.absent())], sqlStatementType=null, sqlBuilder=SELECT * FROM [Token(order)]), " 
                + "generatedKeyContext=GeneratedKeyContext(columns=[], columnNameToIndexMap={}, valueTable={}, rowIndex=0, columnIndex=0, autoGeneratedKeys=0, columnIndexes=null, columnNames=null), "
                + "conditionContexts=[ConditionContext(conditions={Condition.Column(columnName=id, tableName=order)=Condition(column=Condition.Column(columnName=id, tableName=order), "
                + "operator=IN, values=[1, 2, 3], valueIndices=[])})], mergeContext=MergeContext(orderByContexts=[OrderByContext(super=AbstractSortableColumn(owner=Optional.absent(), "
                + "name=Optional.of(id), alias=Optional.of(a), orderByType=DESC), index=Optional.absent(), columnIndex=0)], groupByContexts=[GroupByContext("
                + "super=AbstractSortableColumn(owner=Optional.absent(), name=Optional.of(id), alias=Optional.of(d), orderByType=ASC), columnIndex=0)], "
                + "aggregationColumns=[AggregationColumn(expression=COUNT(id), aggregationType=COUNT, alias=Optional.of(c), option=Optional.absent(), derivedColumns=[], columnIndex=-1)], "
                + "limit=Limit(offset=0, rowCount=10, offsetParameterIndex=-1, rowCountParameterIndex=-1, multiShardingOffset=0, multiShardingRowCount=10)))"));
    }
    
    private void generateRouteContext(final RouteContext routeContext) throws IOException {
        routeContext.getTables().add(new TableContext("order", Optional.of("o")));
        routeContext.getTables().add(new TableContext("order_item", Optional.<String>absent()));
        routeContext.setSqlBuilder(generateSqlBuilder());
    }
    
    private SQLBuilder generateSqlBuilder() throws IOException {
        SQLBuilder result = new SQLBuilder();
        result.append("SELECT * FROM ");
        result.appendToken("order");
        return result;
    }
    
    private ConditionContext generateConditionContext() {
        ConditionContext result = new ConditionContext();
        Condition condition = new Condition(new Column("id", "order"), BinaryOperator.IN);
        condition.getValues().addAll(Arrays.asList(1, 2, 3));
        result.add(condition);
        return result;
    }
    
    private void generateMergeContext(final MergeContext mergeContext) {
        mergeContext.getAggregationColumns().add(new AggregationSelectItemContext("COUNT(id)", Optional.of("c"), -1, AggregationType.COUNT));
        mergeContext.getOrderByContexts().add(new OrderByContext("id", OrderByType.DESC, Optional.of("a")));
        mergeContext.getGroupByContexts().add(new GroupByContext(Optional.<String>absent(), "id", OrderByType.ASC, Optional.of("d")));
        mergeContext.setLimit(new LimitContext(0, 10, -1, -1));
    }
}
