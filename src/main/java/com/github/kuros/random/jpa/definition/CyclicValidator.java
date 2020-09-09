package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.exception.RandomJPAException;

import java.util.HashSet;
import java.util.Set;

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
public class CyclicValidator {

    private HierarchyGraph hierarchyGraph;

    public CyclicValidator(final HierarchyGraph hierarchyGraph) {
        this.hierarchyGraph = hierarchyGraph;
    }


    public void validate() {
        final Set<Class<?>> keySet = hierarchyGraph.getKeySet();
        final Set<Class<?>> result = new HashSet<>();
        for (Class<?> aClass : keySet) {
            isCyclic(result, aClass);
            result.remove(aClass);
        }
    }

    private void isCyclic(final Set<Class<?>> result, final Class<?> aClass) {

        if (result.contains(aClass)) {
            throw new RandomJPAException("Cyclic dependency found for class: " + aClass.getName());
        }

        result.add(aClass);
        final Set<Class<?>> parents = hierarchyGraph.getParents(aClass);

        for (Class<?> parent : parents) {
            if(parent.equals(aClass))
                continue;
            isCyclic(result, parent);

            result.remove(parent);
        }
    }
}
