package com.kuro.random.jpa.persistor;

import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.types.CreationOrder;
import com.kuro.random.jpa.types.CreationPlan;
import com.kuro.random.jpa.types.Node;
import com.kuro.random.jpa.util.NumberUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public final class PersistorImpl implements Persistor {

    private EntityManager entityManager;

    private PersistorImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static Persistor newInstance(final EntityManager entityManager) {
        return new PersistorImpl(entityManager);
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final Node root = Node.newInstance();
        final ResultMap resultMap = ResultMap.newInstance(root);


        final Node creationPlanRoot = creationPlan.getRoot();
        final List<Node> childNodes = creationPlanRoot.getChildNodes();
        for (Node node : childNodes) {
            if (node.getValue() != null) {
                final Node childNode = Node.newInstance(node.getType(), getIndex(resultMap, node.getType()));
                root.addChildNode(childNode);
                persist(childNode, creationPlan.getCreationOrder(), resultMap, node);
            }
        }


//        final List<Class<?>> plan = creationOrder.getOrder();
//        for (Class tableClass : plan) {
//            final Object random = createRandomObject(tableClass, creationOrder, resultMap);
//
//            Object persistedObject;
//            if (getId(tableClass, random) != null
//                    && findElementById(tableClass, random) != null) {
//                persistedObject = findElementById(tableClass, random);
//            } else {
//                persistedObject = persistAndReturnPersistedObject(tableClass, random);
//            }
//
//            resultMap.put(tableClass, persistedObject);
//        }
        return resultMap;
    }

    private int getIndex(final ResultMap resultMap, final Class type) {
        final List<Object> objects = resultMap.getCreatedEntities().get(type);
        return isEmpty(objects) ? 0 : objects.size();
    }

    private boolean isEmpty(final List<Object> objects) {
        return objects == null || objects.isEmpty();
    }

    private void persist(final Node resultNode, final CreationOrder creationOrder, final ResultMap resultMap, final Node node) {
        final Object random = createRandomObject(node, creationOrder, resultMap);
        Object persistedObject;
        if (getId(node.getType(), random) != null
                && findElementById(node.getType(), random) != null) {
            persistedObject = findElementById(node.getType(), random);
        } else {
            persistedObject = persistAndReturnPersistedObject(node.getType(), random);
        }

        resultMap.put(node.getType(), persistedObject);

        final List<Node> childNodes = node.getChildNodes();
        for (Node childNode : childNodes) {
            if (childNode.getValue() != null) {
                final Node resultChildNode = Node.newInstance(childNode.getType(), getIndex(resultMap, childNode.getType()));
                resultNode.addChildNode(resultChildNode);
                persist(resultChildNode, creationOrder, resultMap, childNode);
            }
        }

    }

    private Object persistAndReturnPersistedObject(final Class tableClass, final Object random) {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        final EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(random);
        em.getTransaction().commit();
        em.close();
        return findElementById(tableClass, random);
    }

    private Object findElementById(final Class tableClass, final Object persistedObject) {
        return entityManager.find(tableClass, getId(tableClass, persistedObject));
    }

    private Object getId(final Class tableClass, final Object persistedObject) {
        final Field[] declaredFields = tableClass.getDeclaredFields();
        Field field = null;
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Id.class) != null) {
                field = declaredField;
                field.setAccessible(true);
                break;
            }
        }

        Object id = null;
        try {
            id = field.get(persistedObject);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return id;
    }


    private Object createRandomObject(final Node node, final CreationOrder creationOrder, final ResultMap resultMap) {
        final Object random = node.getValue();

        final TableNode tableNode = creationOrder.getTableNode(node.getType());
        final List<Relation<?, ?>> relations = tableNode.getRelations();

        for (Relation relation : relations) {
            createRelation(resultMap, relation, random);
        }


        return random;
    }

    private <F, T> void createRelation(final ResultMap resultMap, final Relation<F, T> relation, final Object object) {
        try {
            final Object value = getFieldValue(resultMap, relation.getTo());
            setFieldValue(object, relation.getFrom(), value);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private <F> void setFieldValue(final Object object, final FieldValue<F> fieldValue, final Object value) {
        try {

            final Class<?> type = fieldValue.getField().getType();
            fieldValue.getField().setAccessible(true);
            fieldValue.getField().set(object, NumberUtil.castNumber(type, value));
        } catch (final IllegalAccessException e) {
        }
    }



    private <T> Object getFieldValue(final ResultMap resultMap, final FieldValue<T> fieldValue) {
        final Map<Class<?>, List<Object>> createdEntities = resultMap.getCreatedEntities();
        final List<Object> objects = createdEntities.get(fieldValue.getField().getDeclaringClass());
        final Object object = objects.get(objects.size() - 1);
        Object value = null;
        try {
            fieldValue.getField().setAccessible(true);
            value = fieldValue.getField().get(object);
        } catch (final IllegalAccessException e) {
        }
        return value;
    }
}
