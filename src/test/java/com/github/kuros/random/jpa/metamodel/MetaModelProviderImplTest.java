package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.testUtil.MockEntityManagerProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
public class MetaModelProviderImplTest {

    private MockEntityManagerProvider entityManagerProvider;
    private EntityManager entityManager;
    private MetaModelProvider metaModelProvider;

    @Before @Ignore
    public void setUp() throws Exception {
        entityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        entityManager = entityManagerProvider.getEntityManager();
        final Cache cache = null;
        metaModelProvider = new MetaModelProviderImpl(cache);
    }

    @Test @Ignore
    public void mapMetaModelsToTheirTableNames() {
        final Map<String, List<FieldWrapper>> result = metaModelProvider.getFieldsByTableName();

        final List<FieldWrapper> fieldWrappers = result.get("Employee");
        assertEquals(3, fieldWrappers.size());
        validate("employee_id", fieldWrappers.get(0).getOverriddenFieldName());
        validate("person_id", fieldWrappers.get(1).getOverriddenFieldName());
        validate("salary", fieldWrappers.get(2).getFieldName());
        validate("", fieldWrappers.get(2).getOverriddenFieldName());
    }

    private void validate(final String expected, final String actual) {
        assertEquals(expected, actual);
    }
}
