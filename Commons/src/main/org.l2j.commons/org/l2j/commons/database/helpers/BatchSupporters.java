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
