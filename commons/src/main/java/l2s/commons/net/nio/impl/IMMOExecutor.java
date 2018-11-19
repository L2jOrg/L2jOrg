package l2s.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IMMOExecutor<T extends MMOClient>
{
	public void execute(Runnable r);
}