package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleUniqueConstraintProvider;

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
public class UniqueConstraintProviderFactory {

    public static UniqueConstraintProvider getUniqueConstraintProvider() {
        final UniqueConstraintProvider uniqueConstraintProvider;
        switch (Cache.getInstance().getDatabase()) {
            case MS_SQL_SERVER:
                uniqueConstraintProvider = MSSQLUniqueConstraintProvider.getInstance();
                break;
            case ORACLE:
                uniqueConstraintProvider = OracleUniqueConstraintProvider.getInstance();
                break;
            default:
                uniqueConstraintProvider = new DefaultUniqueConstraintProvider();
        }

        return uniqueConstraintProvider;
    }

    private static class DefaultUniqueConstraintProvider implements UniqueConstraintProvider {
        public List<String> getUniqueCombinationAttributes(final Class<?> entityName) {
            return null;
        }
    }
}