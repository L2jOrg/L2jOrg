package l2s.gameserver.idfactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.math.PrimeFinder;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitSetIDFactory extends IdFactory
{
	private static final Logger _log = LoggerFactory.getLogger(BitSetIDFactory.class);

	private BitSet freeIds;
	private AtomicInteger freeIdCount;
	private AtomicInteger nextFreeId;

	public class BitSetCapacityCheck extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(reachingBitSetCapacity())
				increaseBitSetCapacity();
		}
	}

	protected BitSetIDFactory()
	{
		super();
		initialize();

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
	}

	private void initialize()
	{
		try
		{
			freeIds = new BitSet(PrimeFinder.nextPrime(100000));
			freeIds.clear();
			freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

			for(int usedObjectId : extractUsedObjectIDTable())
			{
				int objectID = usedObjectId - FIRST_OID;
				if(objectID < 0)
				{
					_log.warn("Object ID " + usedObjectId + " in DB is less than minimum ID of " + FIRST_OID);
					continue;
				}
				freeIds.set(usedObjectId - FIRST_OID);
				freeIdCount.decrementAndGet();
			}

			nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
			initialized = true;

			_log.info("IdFactory: " + freeIds.size() + " id's available.");
		}
		catch(Exception e)
		{
			initialized = false;
			_log.error("BitSet ID Factory could not be initialized correctly!", e);
		}
	}

	@Override
	public synchronized void releaseId(int objectID)
	{
		if(objectID - FIRST_OID > -1)
		{
			freeIds.clear(objectID - FIRST_OID);
			freeIdCount.incrementAndGet();
			super.releaseId(objectID);
		}
		else
			_log.warn("BitSet ID Factory: release objectID " + objectID + " failed (< " + FIRST_OID + ")");
	}

	@Override
	public synchronized int getNextId()
	{
		int newID = nextFreeId.get();
		freeIds.set(newID);
		freeIdCount.decrementAndGet();

		int nextFree = freeIds.nextClearBit(newID);

		if(nextFree < 0)
			nextFree = freeIds.nextClearBit(0);
		if(nextFree < 0)
			if(freeIds.size() < FREE_OBJECT_ID_SIZE)
				increaseBitSetCapacity();
			else
				throw new NullPointerException("Ran out of valid Id's.");

		nextFreeId.set(nextFree);

		return newID + FIRST_OID;
	}

	@Override
	public synchronized int size()
	{
		return freeIdCount.get();
	}

	protected synchronized int usedIdCount()
	{
		return size() - FIRST_OID;
	}

	protected synchronized boolean reachingBitSetCapacity()
	{
		return PrimeFinder.nextPrime(usedIdCount() * 11 / 10) > freeIds.size();
	}

	protected synchronized void increaseBitSetCapacity()
	{
		BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(usedIdCount() * 11 / 10));
		newBitSet.or(freeIds);
		freeIds = newBitSet;
	}
}