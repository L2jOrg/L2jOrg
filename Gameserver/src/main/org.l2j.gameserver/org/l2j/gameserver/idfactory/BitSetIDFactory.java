package org.l2j.gameserver.idfactory;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.util.PrimeFinder;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public final class BitSetIDFactory extends IdFactory {
    private BitSet freeIds;
    private AtomicInteger freeIdCount;
    private AtomicInteger _nextFreeId;

    BitSetIDFactory() {
        synchronized (BitSetIDFactory.class) {
            ThreadPoolManager.scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
            initialize();
        }
        LOGGER.info("{} Identifiers available", freeIds.size());
    }

    public void initialize() {
        try {
            freeIds = new BitSet(PrimeFinder.nextPrime(100000));
            freeIds.clear();
            freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

            for (int usedObjectId : extractUsedObjectIDTable()) {
                final int objectID = usedObjectId - FIRST_OID;
                if (objectID < 0) {
                    LOGGER.warn("Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
                    continue;
                }
                freeIds.set(usedObjectId - FIRST_OID);
                freeIdCount.decrementAndGet();
            }

            _nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
            initialized = true;
        } catch (Exception e) {
            initialized = false;
            LOGGER.error(getClass().getSimpleName() + ": Could not be initialized properly: " + e.getMessage());
        }
    }

    @Override
    public synchronized void releaseId(int objectID) {
        if ((objectID - FIRST_OID) > -1) {
            freeIds.clear(objectID - FIRST_OID);
            freeIdCount.incrementAndGet();
        } else {
            LOGGER.warn("Release objectID " + objectID + " failed (< " + FIRST_OID + ")");
        }
    }

    @Override
    public synchronized int getNextId() {
        final int newID = _nextFreeId.get();
        freeIds.set(newID);
        freeIdCount.decrementAndGet();

        final int nextFree = freeIds.nextClearBit(newID) < 0 ? freeIds.nextClearBit(0) : freeIds.nextClearBit(newID);

        if (nextFree < 0) {
            if (freeIds.size() >= FREE_OBJECT_ID_SIZE) {
                throw new NullPointerException("Ran out of valid Id's.");
            }
            increaseBitSetCapacity();
        }

        _nextFreeId.set(nextFree);

        return newID + FIRST_OID;
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
