package com.kuro.random.jpa;

import com.kuro.random.jpa.definition.HierarchyGenerator;
import com.kuro.random.jpa.definition.HierarchyGeneratorImpl;
import com.kuro.random.jpa.definition.RelationCreator;
import com.kuro.random.jpa.link.Dependencies;
import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.persistor.Persistor;
import com.kuro.random.jpa.persistor.PersistorImpl;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.provider.ForeignKeyRelation;
import com.kuro.random.jpa.provider.MetaModelProvider;
import com.kuro.random.jpa.provider.RelationshipProvider;
import com.kuro.random.jpa.types.CreationPlan;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public final class JPAContext {

    private final EntityManager entityManager;
    private final Map<String, EntityType<?>> metaModelRelations;
    private HierarchyGraph hierarchyGraph;
    private Dependencies dependencies;
    private RandomFactory randomFactory;

    public static JPAContext newInstance(final EntityManager entityManager) {
        return new JPAContext(entityManager, null);
    }

    public static JPAContext newInstance(final EntityManager entityManager, final Dependencies customDependencies) {
        return new JPAContext(entityManager, customDependencies);
    }

    private JPAContext(final EntityManager entityManager, final Dependencies dependencies) {
        this.entityManager = entityManager;
        this.dependencies = dependencies;

        final MetaModelProvider metaModelProvider = MetaModelProvider.newInstance(entityManager);
        this.metaModelRelations = metaModelProvider.getMetaModelRelations();
        this.randomFactory = new RandomFactory();

        initialize();
    }

    private void initialize() {
        final RelationshipProvider relationshipProvider = RelationshipProvider.newInstance(entityManager);
        final List<ForeignKeyRelation> foreignKeyRelations = relationshipProvider.getForeignKeyRelations();

        final List<Relation> relations = RelationCreator.from(metaModelRelations)
                .with(foreignKeyRelations)
                .with(dependencies)
                .generate();

        final HierarchyGenerator hierarchyGenerator = getHierarchyGenerator();
        hierarchyGraph = hierarchyGenerator.generate(relations);
    }

    public void addRandomGenerator(final RandomGenerator randomGenerator) {
        this.randomFactory.addRandomGenerator(randomGenerator);
    }

    public ResultMap create(final Class<?> aClass) {

        final CreationPlan creationPlan = getCreationPlan(aClass);

        final List<Class<?>> creationPlan1 = creationPlan.getCreationPlan();
        for (Class<?> tableNode : creationPlan1) {
            System.out.println(tableNode);
        }

        final Persistor persistor = PersistorImpl.newInstance(entityManager, randomFactory);
        return persistor.persist(creationPlan);
    }

    private CreationPlan getCreationPlan(final Class<?> type) {
        final CreationPlan creationPlan = CreationPlan.newInstance();

        final Map<Class<?>, TableNode> parentRelations = hierarchyGraph.getParentRelations();

        generateCreationOrder(creationPlan, parentRelations, type);
        return creationPlan;
    }

    private void generateCreationOrder(final CreationPlan creationPlan, final Map<Class<?>, TableNode> parentRelations, final Class<?> type) {
        final TableNode tableNode = parentRelations.get(type);
        for (Class<?> parent : tableNode.getParentClasses()) {
            if (!creationPlan.contains(parent)) {
                generateCreationOrder(creationPlan, parentRelations, parent);
            }
        }
        creationPlan.add(type);
    }

    private HierarchyGenerator getHierarchyGenerator() {
        return new HierarchyGeneratorImpl();
    }


}
