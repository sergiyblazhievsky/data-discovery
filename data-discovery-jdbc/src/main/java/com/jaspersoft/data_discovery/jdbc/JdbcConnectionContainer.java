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

import com.jaspersoft.data_discovery.exception.DataDiscoveryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
public class JdbcConnectionContainer {
    private final Connection connection;
    private ResultSet resultSet;
    private Statement statement;

    public JdbcConnectionContainer(Connection connection){
        this.connection = connection;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public JdbcConnectionContainer setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this;
    }

    public Statement getStatement() {
        return statement;
    }

    public JdbcConnectionContainer setStatement(Statement statement) {
        this.statement = statement;
        return this;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close(){
        if(resultSet != null) try {
            resultSet.close();
        } catch (SQLException e) {
            throw new DataDiscoveryException(e);
        }
        if(statement != null) try {
            statement.close();
        } catch (SQLException e) {
            throw new DataDiscoveryException(e);
        }
        if(connection != null) try {
            connection.close();
        } catch (SQLException e) {
            throw new DataDiscoveryException(e);
        }
    }
}
