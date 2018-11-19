package l2s.gameserver.model.actor.flags.flag;

import java.util.HashSet;
import java.util.Set;

import l2s.commons.util.concurrent.atomic.AtomicState;

public class DefaultFlag
{
	private final AtomicState _state = new AtomicState();
	private final Set<Object> _statusesOwners = new HashSet<Object>();

	public boolean get()
	{
		return _state.get() || !_statusesOwners.isEmpty();
	}

	public boolean start(Object owner)
	{
		return _statusesOwners.add(owner);
	}

	public boolean start()
	{
		return _state.getAndSet(true);
	}

	public boolean stop(Object owner)
	{
		return _statusesOwners.remove(owner);
	}

	public boolean stop()
	{
		return _state.setAndGet(false);
	}

	public void clear()
	{
		_state.set(false);
		_statusesOwners.clear();
	}
}