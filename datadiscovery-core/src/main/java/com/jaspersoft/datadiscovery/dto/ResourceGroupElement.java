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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceGroupElement<T extends ResourceGroupElement> extends SchemaElement<ResourceGroupElement<T>> {
    private List<SchemaElement> elements;
    private String filterExpression;

    public ResourceGroupElement(){}
    public ResourceGroupElement(ResourceGroupElement<T> source) {
        filterExpression = source.getFilterExpression();
        final List<SchemaElement> sourceElements = source.getElements();
        if(sourceElements != null){
            elements = new ArrayList<SchemaElement>(sourceElements);
        }
    }
    @XmlElementWrapper(name = "elements")
    @XmlElements({
            @XmlElement(name="reference", type = ReferenceElement.class),
            @XmlElement(name="group", type = ResourceGroupElement.class),
            @XmlElement(name="element", type = ResourceSingleElement.Builder.class),
            @XmlElement(name = "element", type = ResourceMetadataSingleElement.class)
    })
    public List<SchemaElement> getElements() {
        return elements;
    }

    public T setElements(List<SchemaElement> elements) {
        this.elements = elements;
        return (T) this;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public T setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceGroupElement)) return false;
        if (!super.equals(o)) return false;

        ResourceGroupElement that = (ResourceGroupElement) o;

        if (elements != null ? !elements.equals(that.elements) : that.elements != null) return false;
        if (filterExpression != null ? !filterExpression.equals(that.filterExpression) : that.filterExpression != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (elements != null ? elements.hashCode() : 0);
        result = 31 * result + (filterExpression != null ? filterExpression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceGroupElement{" +
                "elements=" + elements +
                ", filterExpression='" + filterExpression + '\'' +
                "} " + super.toString();
    }
}