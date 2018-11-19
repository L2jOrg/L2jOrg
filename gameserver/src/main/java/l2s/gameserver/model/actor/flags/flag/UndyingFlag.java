package l2s.gameserver.model.actor.flags.flag;

import java.util.concurrent.atomic.AtomicBoolean;

public class UndyingFlag extends DefaultFlag
{
	private final AtomicBoolean _flag = new AtomicBoolean(false);

	public AtomicBoolean getFlag()
	{
		return _flag;
	}

	@Override
	public boolean start(Object owner)
	{
		_flag.set(false);
		return super.start(owner);
	}

	@Override
	public boolean start()
	{
		_flag.set(false);
		return super.start();
	}

	@Override
	public boolean stop(Object owner)
	{
		_flag.set(false);
		return super.stop(owner);
	}

	@Override
	public boolean stop()
	{
		_flag.set(false);
		return super.stop();
	}

	@Override
	public void clear()
	{
		_flag.set(false);
		super.clear();
	}
}