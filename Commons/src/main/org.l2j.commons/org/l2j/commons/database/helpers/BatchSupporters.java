/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.database.helpers;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntIterable;
import org.l2j.commons.database.handler.TypeHandler;

import java.util.Iterator;
import java.util.PrimitiveIterator;

/**
 * @author JoeAlisson
 */
public final class BatchSupporters {

    private static final IntIterableBatchSupport intIterableSupporter = new IntIterableBatchSupport();

    interface BatchSupport {
        Iterator<?> getIterator(Object iterable);
        TypeHandler getHandler();
    }

    public static final class IntIterableBatchSupport implements BatchSupport {
        private static final TypeHandler<?> handler = TypeHandler.MAP.get(Integer.TYPE.getName());

        @Override
        public PrimitiveIterator.OfInt getIterator(Object iterable) {
            if(iterable instanceof  IntIterable it) {
                return it.iterator();
            }
            return Containers.EMPTY_INT_ITERATOR;
        }

        @Override
        public TypeHandler getHandler() {
            return handler;
        }
    }

    public static BatchSupport batchSupportHandler(Class<?> argClass) {
        if(IntIterable.class.isAssignableFrom(argClass)) {
            return intIterableSupporter;
        }
        return null;
    }


}
