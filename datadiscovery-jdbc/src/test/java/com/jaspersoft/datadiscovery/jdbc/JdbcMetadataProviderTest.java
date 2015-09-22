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
package com.jaspersoft.datadiscovery.jdbc;

import com.jaspersoft.datadiscovery.dto.ResourceGroupElement;
import com.jaspersoft.datadiscovery.dto.SchemaElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by serhii.blazhyievskyi on 9/16/2015.
 */
public class JdbcMetadataProviderTest {

    @Mock
    private DatabaseMetaData databaseMetaDataMock;

    @Spy
    private JdbcMetadataProvider providerSpy = new JdbcMetadataProvider();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnListWithMetaData() throws Exception {
        //Given
        SchemaElement elementSchema = new SchemaElement();
        elementSchema.setName("schema element");

        SchemaElement elementTable = new SchemaElement();
        elementTable.setName("table element");

        Mockito.doReturn(elementSchema).when(providerSpy).getSchemaMetadata("testSchema", new ArrayList<String[]>(), databaseMetaDataMock);
        Mockito.doReturn(elementTable).when(providerSpy).getTableMetadata("testSchema", "testTable", true, databaseMetaDataMock);

        //When
        List<SchemaElement> resultSchema = providerSpy.includeMetadata(new String[]{"testSchema"}, databaseMetaDataMock);
        List<SchemaElement> resultTable = providerSpy.includeMetadata(new String[]{"testSchema/testTable"}, databaseMetaDataMock);

        //Then
        assertNotNull(resultSchema);
        assertEquals(resultSchema.size(), 1);
        assertEquals("schema element", resultSchema.get(0).getName());
        assertNotNull(resultTable);
        assertEquals(resultTable.size(), 1);
        assertEquals("table element", resultTable.get(0).getName());
    }

    @Test
    public void shouldReturnSchemaMetaData() throws Exception {
        //Given
        List<String[]> expandList = new ArrayList<String[]>();
        expandList.add(new String[]{"testTable"});
        SchemaElement resultTableElement = new SchemaElement();
        resultTableElement.setName("testTable");

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMock.getString("TABLE_NAME")).thenReturn("testTable");
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.doReturn(resultTableElement).when(providerSpy).getTableMetadata("testExpandedSchema", "testTable", true, databaseMetaDataMock);
        Mockito.doReturn(resultSetMock).when(databaseMetaDataMock).getTables(null, "testExpandedSchema", null, new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"});

        //When
        SchemaElement resultSchemaElement = providerSpy.getSchemaMetadata("testSchema", null, databaseMetaDataMock);

        ResourceGroupElement resultSchemaExpandedElement = (ResourceGroupElement)providerSpy.getSchemaMetadata("testExpandedSchema", expandList, databaseMetaDataMock);

        //Then
        assertNotNull(resultSchemaElement);
        assertEquals("testSchema", resultSchemaElement.getName());
        assertNotNull(resultSchemaExpandedElement);
        assertEquals("testExpandedSchema", resultSchemaExpandedElement.getName());
        assertNotNull(resultSchemaExpandedElement.getElements());
        assertEquals(resultSchemaExpandedElement.getElements().size(), 1);
        SchemaElement tableElement = (SchemaElement)resultSchemaExpandedElement.getElements().get(0);
        assertNotNull(tableElement);
        assertEquals("testTable", tableElement.getName());
    }

    @Test
    public void shouldReturnTableMetaData() throws Exception {
        //Given
        ResultSet resultSetMockPK = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMockPK.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        ResultSet resultSetMockC = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMockC.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        ResultSet resultSetMockFK = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMockFK.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMockC.getInt("DATA_TYPE")).thenReturn(Types.CHAR);
        Mockito.when(resultSetMockPK.getString(4)).thenReturn("primaryKey");
        Mockito.when(resultSetMockFK.getString("FKCOLUMN_NAME")).thenReturn("foreignKeyColumnName");
        Mockito.when(resultSetMockFK.getString("PKTABLE_NAME")).thenReturn("primaryKeyTableName");
        Mockito.when(resultSetMockFK.getString("PKCOLUMN_NAME")).thenReturn("primaryKeyColumnName");
        Mockito.when(resultSetMockFK.getString("PKTABLE_SCHEM")).thenReturn("primaryKeySchemaName");
        Mockito.doReturn(resultSetMockC).when(databaseMetaDataMock).getColumns(null, "testSchema", "testTable", null);
        Mockito.doReturn(resultSetMockPK).when(databaseMetaDataMock).getPrimaryKeys(null, "testSchema", "testTable");
        Mockito.doReturn(resultSetMockFK).when(databaseMetaDataMock).getImportedKeys(null, "testSchema", "testTable");

        //When
        SchemaElement resultTableElement = providerSpy.getTableMetadata("testSchema", "testTable", false, databaseMetaDataMock);

//        SchemaElement resultExpandedTableElement = providerSpy.getTableMetadata("testSchema", "testExpandedTable", true, databaseMetaDataMock);

        //Then
        assertNotNull(resultTableElement);
        assertEquals("testTable", resultTableElement.getName());
//        assertNotNull(resultExpandedTableElement);
//        assertEquals("testExpandedTable", resultExpandedTableElement.getName());
    }

    //TODO add more detailed tests for build method

    @Test
    public void shouldReturnExpandedMetaData() throws Exception {
        //Given
        Connection connectionMock = Mockito.mock(Connection.class);
        Map<String, String[]> options = new HashMap<String, String[]>();
        options.put("expand", new String[]{"testPath"});

        Map<String, List<String[]>> expandsMap = new HashMap<String, List<String[]>>();
        expandsMap.put("testPath", new ArrayList<String[]>());

        SchemaElement resultTableElement = new SchemaElement();
        resultTableElement.setName("testPath");
        List<SchemaElement> resultList = new ArrayList<SchemaElement>();
        resultList.add(resultTableElement);

        Mockito.doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        Mockito.doReturn(resultList).when(providerSpy).expandMetadata(expandsMap, databaseMetaDataMock);

        //When
        SchemaElement resultElement = providerSpy.build(connectionMock, options);

        //Then
        assertNotNull(resultElement);
        assertEquals("testPath", resultElement.getName());
    }

    @Test
    public void shouldReturnIncludedMetaData() throws Exception {
        //Given
        Connection connectionMock = Mockito.mock(Connection.class);
        Map<String, String[]> options = new HashMap<String, String[]>();
        options.put("include", new String[]{"testPath"});

        String[] includeArray = new String[]{"testPath"};

        SchemaElement resultTableElement = new SchemaElement();
        resultTableElement.setName("testPath");
        List<SchemaElement> resultList = new ArrayList<SchemaElement>();
        resultList.add(resultTableElement);

        Mockito.doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        Mockito.doReturn(resultList).when(providerSpy).includeMetadata(includeArray, databaseMetaDataMock);

        //When
        SchemaElement resultElement = providerSpy.build(connectionMock, options);

        //Then
        assertNotNull(resultElement);
        assertEquals("testPath", resultElement.getName());
    }

    @Test
    public void shouldReturnRecursiveMetaData() throws Exception {
        //Given
        Connection connectionMock = Mockito.mock(Connection.class);
        Map<String, String[]> options = new HashMap<String, String[]>();
        options.put("recursive", new String[]{"testPath"});

        String[] includeArray = new String[]{"testPath"};

        ResourceGroupElement resultTableElement = new ResourceGroupElement();
        resultTableElement.setName("testPath");
        List<SchemaElement> resultList = new ArrayList<SchemaElement>();
        resultList.add(resultTableElement);

        Mockito.doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        Mockito.doReturn(resultList).when(providerSpy).includeMetadata(includeArray, databaseMetaDataMock);

        //When
        SchemaElement resultElement = providerSpy.build(connectionMock, options);

        //Then
        assertNotNull(resultElement);
        assertEquals("testPath", resultElement.getName());
    }

    @Test
    public void shouldReturnRecursiveMetaDataForRoot() throws Exception {
        //Given
        Connection connectionMock = Mockito.mock(Connection.class);
        Map<String, String[]> options = new HashMap<String, String[]>();
        options.put("recursive", new String[]{"root"});

        String[] includeArray = new String[]{"testPath"};

        ResourceGroupElement resultSchemaElement = new ResourceGroupElement();
        resultSchemaElement.setName("testSchema");
        List<SchemaElement> resultSchemaList = new ArrayList<SchemaElement>();
        resultSchemaList.add(resultSchemaElement);

        ResourceGroupElement resultTableElement = new ResourceGroupElement();
        resultTableElement.setName("testPath");
        List<SchemaElement> resultList = new ArrayList<SchemaElement>();
        resultList.add(resultTableElement);

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMock.getString("TABLE_NAME")).thenReturn("testPath");
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.doReturn(resultTableElement).when(providerSpy).getTableMetadata("testSchema", "testPath", true, databaseMetaDataMock);
        Mockito.doReturn(resultSetMock).when(databaseMetaDataMock).getTables(null, "testSchema", null, new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"});

        Mockito.doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        Mockito.doReturn(resultSchemaList).when(providerSpy).expandMetadata(null, databaseMetaDataMock);
        Mockito.doReturn(resultList).when(providerSpy).includeMetadata(includeArray, databaseMetaDataMock);

        //When
        SchemaElement resultElement = providerSpy.build(connectionMock, options);

        //Then
        assertNotNull(resultElement);
        assertEquals("testSchema", resultElement.getName());
    }

    @After
    public void after() throws Exception {
        Mockito.reset(
                databaseMetaDataMock
        );
    }

}
