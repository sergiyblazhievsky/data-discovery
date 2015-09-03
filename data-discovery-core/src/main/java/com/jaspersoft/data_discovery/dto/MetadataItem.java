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
package com.jaspersoft.data_discovery.dto;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
public class MetadataItem<ConcreteItem extends MetadataItem> {
    private String name;
    private String label;
    public MetadataItem(){}
    public MetadataItem(MetadataItem source){
        name = source.getName();
        label = source.getLabel();
    }

    public String getName() {
        return name;
    }

    public ConcreteItem setName(String name) {
        this.name = name;
        return (ConcreteItem)this;
    }

    public String getLabel() {
        return label;
    }

    public ConcreteItem setLabel(String label) {
        this.label = label;
        return (ConcreteItem)this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetadataItem)) return false;

        MetadataItem that = (MetadataItem) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetadataItem{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
