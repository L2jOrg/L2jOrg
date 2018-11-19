package l2s.commons.util.concurrent;

public interface ReadWriteLockable
{
	/**
	 * Lock for writing
	 */
	public void writeLock();
	/**
	 * Unlock after writing
	 */
	public void writeUnlock();
	/**
	 * Lock for reading
	 */
	public void readLock();
	/**
	 * Unlock after reading
	 */
	public void readUnlock();
}
