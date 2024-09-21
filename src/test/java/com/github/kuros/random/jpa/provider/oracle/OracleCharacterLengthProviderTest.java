package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.ColumnNameType;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.testUtil.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
public class OracleCharacterLengthProviderTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private AttributeProvider attributeProvider;
    @Mock
    private Query query;
    private SQLCharacterLengthProvider sqlCharacterLengthProvider;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockEntityManager();
        mockAttributeProvider();
        sqlCharacterLengthProvider = new OracleCharacterLengthProvider(entityManager, attributeProvider);
    }

    @Test
    public void testApplyLengthConstraint() {
        assertEquals("abcde", sqlCharacterLengthProvider.applyLengthConstraint(Department.class.getName(), "departmentName", "abcdefghijkl"));
        assertEquals(789, sqlCharacterLengthProvider.applyLengthConstraint(Department.class.getName(), "departmentId", 123456789));
        assertEquals(345.68, sqlCharacterLengthProvider.applyLengthConstraint(Department.class.getName(), "departmentAmount", 12345.6789));
    }

    private void mockAttributeProvider() {
        final EntityTableMapping departmentTableMapping = new EntityTableMapping(Department.class);
        departmentTableMapping.addAttributeColumnMapping("departmentId", new ColumnNameType("department_id", ColumnNameType.Type.BASIC));
        departmentTableMapping.addAttributeColumnMapping("departmentName", new ColumnNameType("department_name", ColumnNameType.Type.BASIC));
        departmentTableMapping.addAttributeColumnMapping("departmentAmount", new ColumnNameType("department_amount", ColumnNameType.Type.BASIC));
        final List<EntityTableMapping> entityTableMappings = new ArrayList<>();
        entityTableMappings.add(departmentTableMapping);
        when(attributeProvider.get(eq("department"))).thenReturn(entityTableMappings);
    }

    private void mockEntityManager() {
        final Object[] row1 = {"department", "department_name", BigDecimal.valueOf(5), null, null, "NVARCHAR"};
        final Object[] row2 = {"department", "department_id", null, BigDecimal.valueOf(3), null, "NUMBER"};
        final Object[] row3 = {"department", "department_amount", null, BigDecimal.valueOf(3), BigDecimal.valueOf(2), "NUMBER"};

        final List<Object[]> resultList = new ArrayList<>();
        resultList.add(row1);
        resultList.add(row2);
        resultList.add(row3);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);
    }

}
