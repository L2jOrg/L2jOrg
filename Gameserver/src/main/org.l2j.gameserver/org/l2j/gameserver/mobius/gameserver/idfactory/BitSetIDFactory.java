/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.idfactory;

import org.l2j.gameserver.mobius.gameserver.util.PrimeFinder;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class ..
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class BitSetIDFactory extends IdFactory {
    private BitSet _freeIds;
    private AtomicInteger _freeIdCount;
    private AtomicInteger _nextFreeId;

    protected BitSetIDFactory() {
        super();

        synchronized (BitSetIDFactory.class) {
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
            initialize();
        }
        LOGGER.info(getClass().getSimpleName() + ": " + _freeIds.size() + " id's available.");
    }

    public void initialize() {
        try {
            _freeIds = new BitSet(PrimeFinder.nextPrime(100000));
            _freeIds.clear();
            _freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

            for (int usedObjectId : extractUsedObjectIDTable()) {
                final int objectID = usedObjectId - FIRST_OID;
                if (objectID < 0) {
                    LOGGER.warning(getClass().getSimpleName() + ": Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
                    continue;
                }
                _freeIds.set(usedObjectId - FIRST_OID);
                _freeIdCount.decrementAndGet();
            }

            _nextFreeId = new AtomicInteger(_freeIds.nextClearBit(0));
            _initialized = true;
        } catch (Exception e) {
            _initialized = false;
            LOGGER.severe(getClass().getSimpleName() + ": Could not be initialized properly: " + e.getMessage());
        }
    }

    @Override
    public synchronized void releaseId(int objectID) {
        if ((objectID - FIRST_OID) > -1) {
            _freeIds.clear(objectID - FIRST_OID);
            _freeIdCount.incrementAndGet();
        } else {
            LOGGER.warning(getClass().getSimpleName() + ": Release objectID " + objectID + " failed (< " + FIRST_OID + ")");
        }
    }

    @Override
    public synchronized int getNextId() {
        final int newID = _nextFreeId.get();
        _freeIds.set(newID);
        _freeIdCount.decrementAndGet();

        final int nextFree = _freeIds.nextClearBit(newID) < 0 ? _freeIds.nextClearBit(0) : _freeIds.nextClearBit(newID);

        if (nextFree < 0) {
            if (_freeIds.size() >= FREE_OBJECT_ID_SIZE) {
                throw new NullPointerException("Ran out of valid Id's.");
            }
            increaseBitSetCapacity();
        }

        _nextFreeId.set(nextFree);

        return newID + FIRST_OID;
    }

    @Override
    public synchronized int size() {
        return _freeIdCount.get();
    }

    /**
     * @return
     */
    protected synchronized int usedIdCount() {
        return _freeIdCount.get() - FIRST_OID;
    }

    /**
     * @return
     */
    protected synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime((usedIdCount() * 11) / 10) > _freeIds.size();
    }

    protected synchronized void increaseBitSetCapacity() {
        final BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((usedIdCount() * 11) / 10));
        newBitSet.or(_freeIds);
        _freeIds = newBitSet;
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
