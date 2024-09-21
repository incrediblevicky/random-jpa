package com.github.kuros.random.jpa.random.simple;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.github.kuros.random.jpa.random.generator.RandomFieldGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
public class SimpleRandomGeneratorTest {

    @Test
    public void testRandomObjectIsCreatedWithSimpleAttributes() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertNotNull(random.i);
        assertNotNull(random.s);
        assertNotNull(random.d);
    }

    @Test
    public void testRandomObjectIsCreatedWithSimpleAttributesWhenNoPackageNameIsProvided() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertNotNull(random.i);
        assertNotNull(random.s);
        assertNotNull(random.d);
    }

    @Test
    public void testRandomObjectIsCreatedForComplexObjectsButCustomAttributesIsNotGenerated() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestClass2 testClass2 = simpleRandomGenerator.getRandom(TestClass2.class);

        assertNotNull(testClass2);
        assertNotNull(testClass2.i);
        final TestClass1 testClass1 = testClass2.testClass1;
        assertNotNull(testClass1);
        assertNull(testClass1.i);
        assertNull(testClass1.s);
        assertNull(testClass1.d);

    }

    @Test
    public void testExplicitGenerationOfClass() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .with(new RandomClassGenerator() {
                    public Collection<Class<?>> getTypes() {
                        final Set<Class<?>> classes = new HashSet<>();
                        classes.add(Integer.class);
                        return classes;
                    }

                    public Object doGenerate(final Class<?> aClass) {
                        return 1234;
                    }
                })
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertEquals(1234, random.i.intValue());
    }

    @Test
    public void testEnumIsGenerated() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestEnum random = simpleRandomGenerator.getRandom(TestEnum.class);
        assertNotNull(random);

        final TestEnumClass random2 = simpleRandomGenerator.getRandom(TestEnumClass.class);
        assertNotNull(random2);
        assertNotNull(random2.i);
        assertNotNull(random2.testEnum);
    }

    @Test
    public void testCollectionIsNotGenerated() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestCollection random = simpleRandomGenerator.getRandom(TestCollection.class);
        assertNotNull(random);
        assertNull(random.list);
        assertNull(random.set);
        assertNull(random.map);
        assertNull(simpleRandomGenerator.getRandom(Map.class));
        assertNull(simpleRandomGenerator.getRandom(List.class));
        assertNull(simpleRandomGenerator.getRandom(Set.class));
    }

    @Test
    public void testArrayIsGenerated() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestArray random = simpleRandomGenerator.getRandom(TestArray.class);
        assertNotNull(random.i);
        assertNotNull(random.testClass1s);
        assertNotNull(random.testEnums);
    }

    @Test
    public void shouldGenerateRandomFieldAsProvidedInRandomGenerator() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .with(new RandomFieldGenerator() {
                    public Class<?> getType() {
                        return TestClass1.class;
                    }

                    public Set<String> getFieldNames() {
                        final Set<String> fieldNames = new HashSet<>();
                        fieldNames.add("i");
                        fieldNames.add("s");
                        return fieldNames;
                    }

                    public Object doGenerate(final String fieldName) {
                        return "i".equals(fieldName) ? 1234 : "randomString";
                    }
                })
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertEquals(1234, random.i.intValue());
        assertEquals("randomString", random.s);

        final TestClass2 random2 = simpleRandomGenerator.getRandom(TestClass2.class);
        assertNotEquals(1234, random2.i.intValue());
    }

    @Test
    public void shouldThrowExceptionWhenFieldIsNotFound() {
        assertThrows(RandomJPAException.class, () ->
            SimpleRandomGeneratorFactory
                    .newInstance()
                    .with(new RandomFieldGenerator() {
                        public Class<?> getType() {
                            return TestClass1.class;
                        }

                        public Set<String> getFieldNames() {
                            final Set<String> fieldNames = new HashSet<>();
                            fieldNames.add("fieldNameThatNotExists");
                            return fieldNames;
                        }

                        public Object doGenerate(final String fieldName) {
                            return null;
                        }
                    })
                    .create());
    }

    @Test
    public void shouldCreateRandomObjectWithConstructorInvocation() {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory.newInstance().create();

        final TestConstructorClass random = simpleRandomGenerator.getRandom(TestConstructorClass.class);
        assertNotNull(random);
        assertNotNull(random.i);
        assertNotNull(random.l);
        assertNotNull(random.date);
        assertNotNull(random.time);
        assertNotNull(random.class1);

    }

    private class TestClass1 {
        private Integer i;
        private String s;
        private Date d;
    }

    private class TestClass2 {
        private Integer i;
        private TestClass1 testClass1;
    }

    private class TestEnumClass {
        private int i;
        private TestEnum testEnum;
    }

    private class TestArray {
        private int[] i;
        private TestClass1[] testClass1s;
        private TestEnum[] testEnums;
    }

    private enum TestEnum {
        VAL1,
        VAL2,
        VAL3
    }

    private class TestCollection {
        private List<TestClass1> list;
        private Set<Integer> set;
        private Map<Integer, String> map;
    }

    private class TestConstructorClass {
        private final int i = 0;
        private Long l;
        private Date date;
        private long time;
        private TestClass1 class1;

        TestConstructorClass(final Long l, final Date date, final TestClass1 class1) {
            this.l = l;
            this.date = date;
            this.time = date.getTime();
            this.class1 = class1;
        }
    }
}
