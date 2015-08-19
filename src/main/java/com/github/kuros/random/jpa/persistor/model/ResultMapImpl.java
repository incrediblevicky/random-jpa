package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.types.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kumarro on 19/08/15.
 */
public final class ResultMapImpl implements ResultMap {

    private Map<Class<?>, List<Object>> resultMap;
    private String constructionTree;

    private ResultMapImpl(final Map<Class<?>, List<Object>> resultMap, final String constructionTree) {
        this.resultMap = resultMap;
        this.constructionTree = constructionTree;
    }

    public static ResultMapImpl newInstance(final ResultNodeTree nodeTree) {
        return new ResultMapImpl(nodeTree.getCreatedEntities(), nodeTree.getConstructionTree());
    }

    public void put(final Class<?> type, final Object object) {
        List<Object> objects = resultMap.get(type);
        if (objects == null) {
            objects = new ArrayList<Object>();
            resultMap.put(type, objects);
        }
        objects.add(object);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return get(type, 0);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        final List<?> objects = resultMap.get(type);
        return objects == null ? null : (T) objects.get(index);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(final Class<T> type) {
        return (List<T>) resultMap.get(type);
    }

    public Map<Class<?>, List<Object>> getCreatedEntities() {
        return resultMap;
    }

    public void print(final Printer printer) {
        printer.print(constructionTree);
    }
}
