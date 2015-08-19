package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;

import java.util.List;

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
public class FindByUniqueIdentities<T> implements Function<T> {

    private static final Logger LOGGER = LogFactory.getLogger(PersistFunction.class);
    private final UniqueConstraintProvider uniqueConstraintProvider;
    private final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
    private final Finder finder;

    public FindByUniqueIdentities(final Cache cache) {
        this.uniqueConstraintProvider = cache.getUniqueConstraintProvider();
        this.multiplePrimaryKeyProvider = cache.getMultiplePrimaryKeyProvider();
        this.finder = new Finder(cache);
    }

    public T apply(final T typeObject) {
        final Class<?> tableClass = typeObject.getClass();
        final List<String> uniqueCombinationAttributes = uniqueConstraintProvider.getUniqueCombinationAttributes(tableClass);
        final List<String> multiplePrimaryKeyAttributes = multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(tableClass);

        final T t = find(typeObject, uniqueCombinationAttributes);
        if (t != null) {
            return t;
        } else {
            return find(typeObject, multiplePrimaryKeyAttributes);
        }
    }

    private T find(final T typeObject, final List<String> attributes) {
        return attributes != null ? finder.findByAttributes(typeObject, attributes) : null;
    }
}
