package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Before;
import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.D_;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.DependencyHelper;
import com.github.kuros.random.jpa.types.Trigger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JPAContextFactoryTest {

    private EntityManager entityManager;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        entityManager = EntityManagerProvider.getEntityManager();
    }

    @Test
    public void generateWithJpaContextV2() {
        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .generate();

        assertTrue(jpaContext instanceof JPAContextImpl);
    }

    @Test
    public void createJpaContext() {
        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .generate();

        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertTrue(hierarchyGraph.getParents(Z.class).contains(X.class));
        assertTrue(hierarchyGraph.getParents(Z.class).contains(Y.class));

        assertTrue(hierarchyGraph.getParents(Q.class).contains(P.class));
        assertTrue(hierarchyGraph.getParents(R.class).contains(P.class));
    }

    @Test
    public void createJpaContextWithGenerator() {

        final Z z = new Z();

        final Generator generator = Generator.newInstance();
        generator.addClassGenerator(new RandomClassGenerator() {
            public Collection<Class<?>> getTypes() {
                final List<Class<?>> classes = new ArrayList<>();
                classes.add(Z.class);
                return classes;
            }

            public Object doGenerate(final Class<?> aClass) {
                return z;
            }
        });

        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .with(generator)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final RandomGenerator randomGenerator = jpaContextV2.getGenerator();
        final Z random = randomGenerator.generateRandom(Z.class);

        assertEquals(z, random);
    }

    @Test
    public void createJpaContextWithoutPreConditions() {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertFalse(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test
    public void createJpaContextWithPreConditions() {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withPreconditions(Before.of(D.class).create(Z.class))
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertTrue(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test
    public void createJpaContextWithTriggerTables() {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withTriggers(Trigger.of(D.class).withLink(D_.zId, Z_.id))
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final Trigger<?> trigger = cache.getTriggerCache().getTrigger(D.class);

        assertNotNull(trigger);
        assertEquals(D.class, trigger.getTriggerClass());

        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();
        assertTrue(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test
    public void throwExceptionWhenTriggersNotInitializedProperly() {
        assertThrows(RandomJPAException.class, () ->

            JPAContextFactory
                    .newInstance(Database.NONE, entityManager)
                    .withTriggers(Trigger.of(D.class))
                    .generate());

    }

    @Test
    public void createJpaContextWithSkipTruncation() {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withSkipTruncation(Z.class)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final Set<Class<?>> skipTruncation = cache.getSkipTruncation();
        assertEquals(1, skipTruncation.size());
        assertTrue(skipTruncation.contains(Z.class));
    }

    @Test
    public void validateCyclicDependency() {
        assertThrows(RandomJPAException.class, () ->

            JPAContextFactory
                    .newInstance(Database.NONE, entityManager)
                    .withPreconditions(Before.of(D.class).create(Z.class), Before.of(Z.class).create(D.class))
                    .generate());
    }

    @AfterEach
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
