package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kumar Rohit on 06/10/17.
 */
public class EclipseLinkProviderTest {

    private EntityManager entityManager;

    private EclipseLinkProvider hibernateProvider;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        entityManager = EntityManagerProvider.getEclipseLinkEntityManager();
        hibernateProvider = new EclipseLinkProvider(entityManager);
    }

    @Test
    public void shouldGenerateAttributeDetails() {

        final EntityTableMapping entityTableMappingByClass = hibernateProvider.get(R.class);
        final List<EntityTableMapping> entityTableMappingByNames = hibernateProvider.get("R");

        assertEquals(1, entityTableMappingByNames.size());

        final EntityTableMapping entityTableMappingByName = entityTableMappingByNames.get(0);

        assertEquals(entityTableMappingByClass, entityTableMappingByName);

        final List<String> attributeIds = entityTableMappingByClass.getAttributeIds();
        assertEquals(1, attributeIds.size());
        assertTrue(attributeIds.contains("id"));

        final List<String> columnIds = entityTableMappingByClass.getColumnIds();
        assertEquals(1, columnIds.size());
        assertTrue(columnIds.contains("id"));

        assertEquals("pId", entityTableMappingByClass.getAttributeName("p_id"));
        assertEquals("p_id", entityTableMappingByClass.getColumnName("pId"));

        final Set<String> attributeNames = entityTableMappingByClass.getAttributeNames();
        assertEquals(2, attributeNames.size());
        assertTrue(attributeNames.contains("id"));
        assertTrue(attributeNames.contains("pId"));

        final Set<String> columnNames = entityTableMappingByClass.getColumnNames();
        assertEquals(2, columnNames.size());
        assertTrue(columnNames.contains("id"));
        assertTrue(columnNames.contains("p_id"));

        assertEquals(R.class, entityTableMappingByClass.getEntityClass());
        assertEquals("com.github.kuros.random.jpa.testUtil.entity.R", entityTableMappingByClass.getEntityName());
        assertEquals("r", entityTableMappingByClass.getTableName());

    }

    @Test
    public void shouldGenerateAttributeDetailsForRelationalClasses() {

        final EntityTableMapping entityTableMapping = hibernateProvider.get(RelationEntity.class);
        assertNotNull(entityTableMapping);

        final Set<String> attributeNames = entityTableMapping.getAttributeNames();
        assertEquals(3, attributeNames.size());

        assertTrue(attributeNames.contains("id"));
        assertTrue(attributeNames.contains("relationOneToOne"));
        assertTrue(attributeNames.contains("relationManyToOne"));

        final Set<String> columnNames = entityTableMapping.getColumnNames();
        assertEquals(1, columnNames.size());

        assertTrue(columnNames.contains("id"));

    }


    @AfterEach
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

}