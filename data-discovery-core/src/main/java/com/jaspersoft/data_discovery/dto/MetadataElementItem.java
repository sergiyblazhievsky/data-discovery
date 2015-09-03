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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 */
@XmlRootElement(name = "element")
public class MetadataElementItem extends MetadataItem<MetadataElementItem> {
    private String type;
    private Boolean isIdentifier;
    private String referenceTo;

    public MetadataElementItem() {
        super();
    }

    public MetadataElementItem(MetadataElementItem source) {
        super(source);
        type = source.getType();
        isIdentifier = source.getIsIdentifier();
        referenceTo = source.getReferenceTo();
    }

    public Boolean getIsIdentifier() {
        return isIdentifier;
    }

    public MetadataElementItem setIsIdentifier(Boolean isIdentifier) {
        this.isIdentifier = isIdentifier;
        return this;
    }

    public String getReferenceTo() {
        return referenceTo;
    }

    public MetadataElementItem setReferenceTo(String referenceTo) {
        this.referenceTo = referenceTo;
        return this;
    }

    public String getType() {
        return type;
    }

    public MetadataElementItem setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetadataElementItem)) return false;
        if (!super.equals(o)) return false;

        MetadataElementItem item = (MetadataElementItem) o;

        if (isIdentifier != null ? !isIdentifier.equals(item.isIdentifier) : item.isIdentifier != null) return false;
        if (referenceTo != null ? !referenceTo.equals(item.referenceTo) : item.referenceTo != null) return false;
        if (type != null ? !type.equals(item.type) : item.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (isIdentifier != null ? isIdentifier.hashCode() : 0);
        result = 31 * result + (referenceTo != null ? referenceTo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetadataElementItem{" +
                "type='" + type + '\'' +
                ", isIdentifier=" + isIdentifier +
                ", referenceTo='" + referenceTo + '\'' +
                "} " + super.toString();
    }
}
