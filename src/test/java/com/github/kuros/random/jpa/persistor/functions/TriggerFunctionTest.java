package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.TriggerCache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.X_;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.types.Trigger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TriggerFunctionTest {

    @Mock private Cache cache;
    @Mock private Finder finder;
    private TriggerFunction triggerFunction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(cache.getTriggerCache()).thenReturn(getTriggerCache());
        triggerFunction = new TriggerFunction(cache);
        triggerFunction.setFinder(finder);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldFindObjectIfObjectIsTriggerEntity() {
        final Z z = RandomFixture.create(Z.class);

        Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(z);

        final Z apply = (Z) triggerFunction.apply(z);

        assertEquals(z, apply);
    }

    @Test(expected = RandomJPAException.class) @SuppressWarnings("unchecked")
    public void shouldThrowExceptionIfDataIsNotFound() {
        final Z z = RandomFixture.create(Z.class);

        Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(null);

        triggerFunction.apply(z);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnNullIfObjectIsNotTriggerClass() {
        final X x = RandomFixture.create(X.class);
        assertNull(triggerFunction.apply(x));
    }

    @SuppressWarnings("unchecked")
    private TriggerCache getTriggerCache() {
        EntityManagerProvider.init();
        final Trigger trigger = Trigger.of(Z.class);
        trigger.withLink(Z_.xId, X_.id);

        final List<Trigger<?>> triggers = new ArrayList<Trigger<?>>();
        triggers.add(trigger);

        return TriggerCache.getInstance(triggers);
    }
}