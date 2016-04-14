package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.X_;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.util.Util;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DependencyResolverTest {

    @Test
    public void shouldResolveDependenciesToRelations() throws Exception {
        EntityManagerProvider.init();
        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(Link.newLink(Z_.xId, X_.id));

        final List<Relation> relations = DependencyResolver.resolveDependency(dependencies);

        assertEquals(1, relations.size());

        assertEquals(Util.getField(Z.class, "xId"), relations.get(0).getFrom().getField());
        assertEquals(Util.getField(X.class, "id"), relations.get(0).getTo().getField());
    }

    @Test
    public void shouldReturnEmptyListIfDependenciesAreNull() throws Exception {
        final List<Relation> relations = DependencyResolver.resolveDependency(null);
        assertEquals(0, relations.size());
    }
}