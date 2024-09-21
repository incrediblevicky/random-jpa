package com.github.kuros.random.jpa.metamodel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class EntityTableMapping {

    private final Class<?> entityClass;
    private final String entityName;
    private String tableName;

    private final List<String> attributeIds;
    private final List<String> columnIds;

    private final Map<String, String> attributeColumnMapping;
    private final Map<String, String> columnAttributeMapping;

    public EntityTableMapping(final Class<?> entityClass) {
        this.entityClass = entityClass;
        this.entityName = entityClass.getName();
        this.attributeIds = new ArrayList<>();
        this.columnIds = new ArrayList<>();
        this.attributeColumnMapping = new HashMap<>();
        this.columnAttributeMapping = new HashMap<>();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public void addAttributeIds(final String id) {
        if (id != null) {
            attributeIds.add(id);
        }
    }

    public void addColumnIds(final String... ids) {
        Collections.addAll(columnIds, ids);
    }

    public void addAttributeColumnMapping(final String attributeName, final ColumnNameType columnNameType) {
        final String columnNameInLowerCase = columnNameType.getColumnName().toLowerCase();
        attributeColumnMapping.put(attributeName, columnNameInLowerCase);
        if (columnNameType.getType() == ColumnNameType.Type.BASIC) {
            columnAttributeMapping.put(columnNameInLowerCase, attributeName);
        }
    }

    public String getColumnName(final String attributeName) {
        return attributeColumnMapping.get(attributeName);
    }

    public String getAttributeName(final String columnName) {
        return columnAttributeMapping.get(columnName.toLowerCase());
    }

    public List<String> getColumnIds() {
        return columnIds;
    }

    public List<String> getAttributeIds() {
        return attributeIds;
    }

    public Set<String> getColumnNames() {
        return columnAttributeMapping.keySet();
    }

    public Set<String> getAttributeNames() {
        return attributeColumnMapping.keySet();
    }
}
