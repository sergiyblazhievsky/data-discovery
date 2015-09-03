/*
* Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.data_discovery.jdbc;

import com.jaspersoft.data_discovery.MetadataBuilder;
import com.jaspersoft.data_discovery.dto.MetadataElementItem;
import com.jaspersoft.data_discovery.dto.MetadataGroupItem;
import com.jaspersoft.data_discovery.dto.MetadataItem;
import com.jaspersoft.data_discovery.exception.DataDiscoveryException;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
public class JdbcMetadataBuilder implements MetadataBuilder<JdbcConnectionContainer> {
    private static final Map<Integer, String> JDBC_TYPES_BY_CODE = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(Types.BIGINT, "java.lang.Long");
        put(Types.BIT, "java.lang.Boolean");
        put(Types.BOOLEAN, "java.lang.Boolean");
        put(Types.CHAR, "java.lang.String");
        put(Types.DATE, "java.util.Date");
        put(Types.DECIMAL, "java.math.BigDecimal");
        put(Types.DOUBLE, "java.lang.Double");
        put(101, "java.lang.Double");
        put(Types.FLOAT, "java.lang.Float");
        put(100, "java.lang.Float");
        put(Types.INTEGER, "java.lang.Integer");
        put(Types.LONGVARCHAR, "java.lang.String");
        put(Types.NUMERIC, "java.math.BigDecimal");
        put(Types.REAL, "java.math.Double");
        put(Types.SMALLINT, "java.lang.Short");
        put(Types.TIME, "java.sql.Time");
        put(Types.TIMESTAMP, "java.sql.Timestamp");
        put(-101, "java.sql.Timestamp");
        put(-102, "java.sql.Timestamp");
        put(Types.TINYINT, "java.lang.Byte");
        put(Types.VARCHAR, "java.lang.String");
        put(Types.NVARCHAR, "java.lang.String");
    }});

    @Override
    public MetadataItem build(JdbcConnectionContainer connection, Map<String, String[]> options) {
        final String[] expands = options != null ? options.get("expand") : null;
        final String[] includes = options != null ? options.get("include") : null;
        final String[] recursives = options != null ? options.get("recursive") : null;
        List<MetadataItem> items;
        try {
            final DatabaseMetaData metaData = connection.getConnection().getMetaData();
            if(recursives != null && recursives.length > 0) {
                final Map<String, List<String[]>> recursiveMap = new HashMap<String, List<String[]>>();
                for (String recursive : recursives) {
                    if (recursive != null) {
                        final String[] path = recursive.split("\\.");
                        recursiveMap.put(path[0], null);
                    }
                }
                items = expandMetadata(recursiveMap, metaData);
            }
            if (includes != null && includes.length > 0) {
                items = includeMetadata(includes, metaData);
            } else {
                final Map<String, List<String[]>> expandsMap = new HashMap<String, List<String[]>>();
                if (expands != null) {
                    for (String expand : expands) {
                        final String[] tokens = expand.split("\\.");
                        List<String[]> tokensList = expandsMap.get(tokens[0]);
                        if (tokensList == null) {
                            tokensList = new ArrayList<String[]>();
                            expandsMap.put(tokens[0], tokensList);
                        }
                        if (tokens.length > 1) {
                            tokensList.add(Arrays.copyOfRange(tokens, 1, tokens.length));
                        }
                    }
                }
                items = expandMetadata(expandsMap, metaData);
            }
        } catch (SQLException e) {
            throw new DataDiscoveryException(e);
        }

        return items.size() == 1 ? items.get(0) : new MetadataGroupItem().setName("root").setItems(items);
    }

    protected List<MetadataItem> expandMetadata(Map<String, List<String[]>> expandsMap, DatabaseMetaData metaData) throws SQLException {
        List<MetadataItem> result = new ArrayList<MetadataItem>();
        final ResultSet schemas = metaData.getSchemas();
        while (schemas.next()) {
            String schema = schemas.getString("TABLE_SCHEM");
            if(expandsMap != null) {
                result.add(getSchemaMetadata(schema, expandsMap.get(schema), metaData));
            } else {
                result.add(getSchemaMetadata(schema, null, metaData));
            }
        }
        return result;

    }

    protected List<MetadataItem> includeMetadata(String[] includes, DatabaseMetaData metaData) {
        List<MetadataItem> result = new ArrayList<MetadataItem>();
        for (String include : includes) {
            if (include != null) {
                final String[] path = include.split("\\.");
                switch (path.length) {
                    case 1:
                        result.add(getSchemaMetadata(path[0], new ArrayList<String[]>(), metaData));
                        break;
                    case 2:
                        result.add(getTableMetadata(path[0], path[1], true, metaData));
                        break;
                }
            }
        }
        return result;
    }

    protected MetadataItem getSchemaMetadata(String schema, List<String[]> expand, DatabaseMetaData metaData) {
        List<MetadataItem> tableItems = new ArrayList<MetadataItem>();
        final Set<String> tableNamesToExpand = new HashSet<String>();
        if (expand != null) {
            for (String[] strings : expand) {
                tableNamesToExpand.add(strings[0]);
            }
        }
        try {
            final ResultSet tables = metaData.getTables(null, schema, null, new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"});
            while (tables.next()) {
                final String tableName = tables.getString("TABLE_NAME");
                tableItems.add(getTableMetadata(schema, tableName, (tableNamesToExpand.size() == 0 || tableNamesToExpand.contains(tableName)), metaData));
            }
        } catch (SQLException e) {
            throw new DataDiscoveryException(e);
        }
        MetadataGroupItem result = new MetadataGroupItem().setName(schema);
        if(tableItems.size() > 0) {
            result = result.setItems(tableItems);
        } else {
            result = result.setItems(null);
        }
        return result;
    }

    protected MetadataItem getTableMetadata(String schema, String table, boolean expand, DatabaseMetaData metaData) {
        MetadataGroupItem result = new MetadataGroupItem().setName(table);
        if (expand) {
            final List<MetadataItem> columnsMetadata = new ArrayList<MetadataItem>();
            try {
                final ResultSet columns = metaData.getColumns(null, schema, table, null);
                ResultSet primaryKeySet = metaData.getPrimaryKeys(null, schema, table);
                List<String> primaryKeys = new ArrayList<String>();
                while (primaryKeySet.next()) {
                    primaryKeys.add(primaryKeySet.getString(4));
                }
                final ResultSet foreignKeysSet = metaData.getImportedKeys(null, schema, table);
                Map<String, String> foreignKeyMap = new HashMap<String, String>();
                while (foreignKeysSet.next()) {
                    String foreignKeyColumnName = foreignKeysSet.getString("FKCOLUMN_NAME");
                    String primaryKeyTableName = foreignKeysSet.getString("PKTABLE_NAME");
                    String primaryKeyColumnName = foreignKeysSet.getString("PKCOLUMN_NAME");
                    String primaryKeySchemaName = foreignKeysSet.getString("PKTABLE_SCHEM");
                    foreignKeyMap.put(foreignKeyColumnName, primaryKeySchemaName + "." + primaryKeyTableName + "." + primaryKeyColumnName);
                }
                while (columns.next()) {
                    final String columnName = columns.getString("COLUMN_NAME");
                    int typeCode = columns.getInt("DATA_TYPE");
                    final MetadataElementItem columnItem = new MetadataElementItem().setName(columnName).setType(JDBC_TYPES_BY_CODE.get(typeCode));
                    if (primaryKeys.contains(columnName)) {
                        columnItem.setIsIdentifier(true);
                    }
                    if (foreignKeyMap.containsKey(columnName)) {
                        columnItem.setReferenceTo(foreignKeyMap.get(columnName));
                    }
                    columnsMetadata.add(columnItem);
                }
            } catch (SQLException e) {
                throw new DataDiscoveryException(e);
            }
            result.setItems(columnsMetadata);
        }
        return result;
    }
}
