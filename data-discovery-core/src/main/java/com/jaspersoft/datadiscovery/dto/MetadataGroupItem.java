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
package com.jaspersoft.datadiscovery.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
@XmlRootElement(name = "group")
public class MetadataGroupItem extends MetadataItem<MetadataGroupItem> {
    private List<MetadataItem> items;

    public MetadataGroupItem(){
        super();
    }

    public MetadataGroupItem(MetadataGroupItem source){
        super(source);
        if(source.getItems() != null){
            items = new ArrayList<MetadataItem>(source.getItems());
        }
    }

    public List<MetadataItem> getItems() {
        return items;
    }

    public MetadataGroupItem setItems(List<MetadataItem> items) {
        this.items = items;
        return this;
    }

    public MetadataGroupItem addItem(MetadataItem item){
        if(items == null){
            items = new ArrayList<MetadataItem>();
        }
        items.add(item);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetadataGroupItem)) return false;
        if (!super.equals(o)) return false;

        MetadataGroupItem that = (MetadataGroupItem) o;

        if (items != null ? !items.equals(that.items) : that.items != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetadataGroupItem{" +
                "items=" + items +
                "} " + super.toString();
    }
}
