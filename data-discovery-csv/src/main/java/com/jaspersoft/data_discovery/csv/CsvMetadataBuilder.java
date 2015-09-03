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
package com.jaspersoft.data_discovery.csv;

import com.jaspersoft.data_discovery.MetadataBuilder;
import com.jaspersoft.data_discovery.dto.MetadataElementItem;
import com.jaspersoft.data_discovery.dto.MetadataGroupItem;
import com.jaspersoft.data_discovery.dto.MetadataItem;
import com.jaspersoft.data_discovery.exception.DataDiscoveryException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
public class CsvMetadataBuilder implements MetadataBuilder<JRCsvDataSource> {
    @Override
    public MetadataItem build(JRCsvDataSource connection, Map<String, String[]> options) {
        Map<String, Integer> columnNamesMap = connection.getColumnNames();
        if(columnNamesMap.isEmpty()){
            try {
                connection.next();
                columnNamesMap = connection.getColumnNames();
            } catch (JRException e) {
                throw new DataDiscoveryException(e);
            }
        }
        final Set<String> columnNames = columnNamesMap.keySet();
        MetadataGroupItem group = new MetadataGroupItem().setName("root");
        for (String columnName : columnNames) {
            group.addItem(new MetadataElementItem().setName(columnName).setType(String.class.getName()));
        }
        return group;
    }
}
