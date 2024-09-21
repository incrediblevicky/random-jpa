package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.testUtil.entity.F;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.types.Entity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinimumHierarchyGeneratorTest {


    @Test
    public void shouldGenerateMinimumHierarchyGraphWithSingleDepth() {
        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final List<Entity> entities = new ArrayList<>();
        entities.add(Entity.of(A.class));
        final HierarchyGraph generatedHierarchyGraph = MinimumHierarchyGenerator.generate(hierarchyGraph, entities);

        assertEquals(0, generatedHierarchyGraph.getKeySet().size());

    }

    @Test
    public void shouldGenerateMinimumHierarchyGraphWithDepthAsOne() {
        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final List<Entity> entities = new ArrayList<>();
        entities.add(Entity.of(Z.class));
        final HierarchyGraph generatedHierarchyGraph = MinimumHierarchyGenerator.generate(hierarchyGraph, entities);

        assertEquals(3, generatedHierarchyGraph.getKeySet().size(), "Nodes in graph: X, Y, Z");
        assertEquals(2, generatedHierarchyGraph.getParents(Z.class).size());
        assertTrue(generatedHierarchyGraph.getParents(Z.class).contains(X.class));
        assertTrue(generatedHierarchyGraph.getParents(Z.class).contains(Y.class));

        assertEquals(0, generatedHierarchyGraph.getParents(X.class).size());
        assertEquals(0, generatedHierarchyGraph.getParents(Y.class).size());
    }

    @Test
    public void shouldGenerateMinimumHierarchyGraphWithSingleDepthAndCreateAfterFeature() {
        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final List<Entity> entities = new ArrayList<>();
        entities.add(Entity.of(A.class).createAfter(F.class));
        final HierarchyGraph generatedHierarchyGraph = MinimumHierarchyGenerator.generate(hierarchyGraph, entities);

        assertEquals(2, generatedHierarchyGraph.getKeySet().size());
        assertTrue(generatedHierarchyGraph.getKeySet().contains(A.class));
        assertTrue(generatedHierarchyGraph.getKeySet().contains(F.class));

        assertEquals(1, generatedHierarchyGraph.getParents(A.class).size());
        assertTrue(generatedHierarchyGraph.getParents(A.class).contains(F.class));

    }

    @Test
    public void shouldNotGenerateAssociatedClassWithSingleDepthAndCreateBeforeFeatureIfNotRequired() {
        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final List<Entity> entities = new ArrayList<>();
        entities.add(Entity.of(A.class).createBefore(F.class));
        final HierarchyGraph generatedHierarchyGraph = MinimumHierarchyGenerator.generate(hierarchyGraph, entities);

        assertEquals(2, generatedHierarchyGraph.getKeySet().size());
        assertTrue(generatedHierarchyGraph.getKeySet().contains(A.class));
        assertTrue(generatedHierarchyGraph.getKeySet().contains(F.class));

        assertEquals(0, generatedHierarchyGraph.getParents(A.class).size());
        assertEquals(1, generatedHierarchyGraph.getParents(F.class).size());
        assertTrue(generatedHierarchyGraph.getParents(F.class).remove(A.class), "Parent contains A");
    }

    @Test
    public void shouldThrowExceptionWhenCyclicDependencyFound() {
        assertThrows(RandomJPAException.class, () -> {
            final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

            final List<Entity> entities = new ArrayList<>();
            entities.add(Entity.of(A.class).createAfter(F.class));
            entities.add(Entity.of(F.class).createAfter(A.class));
            MinimumHierarchyGenerator.generate(hierarchyGraph, entities);


        });


    }
}
