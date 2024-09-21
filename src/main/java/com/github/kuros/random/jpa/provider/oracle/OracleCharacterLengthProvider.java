package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.base.AbstractCharacterLengthProvider;

import jakarta.persistence.EntityManager;

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
public final class OracleCharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = "SELECT" +
            " dt.table_name, dt.column_name, dt.char_col_decl_length, dt.DATA_PRECISION, dt.DATA_SCALE, dt.DATA_TYPE" +
            " FROM user_tab_columns dt";

    @VisibleForTesting
    OracleCharacterLengthProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static OracleCharacterLengthProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new OracleCharacterLengthProvider(entityManager, attributeProvider);
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

}
