package l2s.commons.util.concurrent;

public interface Lockable
{
	/**
	 * Lock for access
	 */
	public void lock();
	/**
	 * Unlock after access
	 */
	public void unlock();
}
