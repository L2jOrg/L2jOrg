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
package org.l2j.gameserver.idfactory;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.PrimeFinder;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author JoeAlisson
 */
public final class BitSetIDFactory extends IdFactory {

    private static final int INITIAL_CAPACITY = 100000;

    private BitSet freeIds;
    private AtomicInteger freeIdCount;
    private AtomicInteger nextFreeId;

    BitSetIDFactory() {
        synchronized (BitSetIDFactory.class) {
            initialize();
            ThreadPool.scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
        }
        LOGGER.info("{} Identifiers available", freeIds.size());
    }

    public void initialize() {
        try {
            freeIds = new BitSet(PrimeFinder.nextPrime(INITIAL_CAPACITY));
            freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

            extractUsedObjectIDTable().forEach(usedObjectId -> {
                final int objectID = usedObjectId - FIRST_OID;
                if (objectID < 0) {
                    LOGGER.warn("Object ID {} in DB is less than minimum ID of {}", usedObjectId, FIRST_OID);
                    return;
                }
                freeIds.set(usedObjectId - FIRST_OID);
                freeIdCount.decrementAndGet();
            });

            nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
            initialized = true;
        } catch (Exception e) {
            initialized = false;
            LOGGER.error("Could not be initialized properly", e);
        }
    }

    @Override
    public synchronized void releaseId(int objectId) {
        if ((objectId - FIRST_OID) > -1) {
            freeIds.clear(objectId - FIRST_OID);
            freeIdCount.incrementAndGet();
        } else {
            LOGGER.warn("Release objectID " + objectId + " failed (< " + FIRST_OID + ")");
        }
    }

    @Override
    public synchronized int getNextId() {
        final int newObjectId = nextFreeId.get();
        freeIds.set(newObjectId);
        freeIdCount.decrementAndGet();

        int nextFree = freeIds.nextClearBit(newObjectId);

        if(nextFree < 0) {
            nextFree = freeIds.nextClearBit(0);
        }

        if (nextFree < 0) {
            if (freeIds.size() >= FREE_OBJECT_ID_SIZE) {
                throw new IllegalStateException("Ran out of valid Id's.");
            }
            increaseBitSetCapacity();
        }

        nextFreeId.set(nextFree);

        return newObjectId + FIRST_OID;
    }

    @Override
    public synchronized int size() {
        return freeIdCount.get();
    }

    private synchronized int usedIdCount() {
        return freeIdCount.get() - FIRST_OID;
    }


    private synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime((usedIdCount() * 11) / 10) > freeIds.size();
    }

    private synchronized void increaseBitSetCapacity() {
        final BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((usedIdCount() * 11) / 10));
        newBitSet.or(freeIds);
        freeIds = newBitSet;
    }

    protected class BitSetCapacityCheck implements Runnable {
        @Override
        public void run() {
            synchronized (BitSetIDFactory.this) {
                if (reachingBitSetCapacity()) {
                    increaseBitSetCapacity();
                }
            }
        }
    }
}
