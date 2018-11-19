package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;

public abstract class Condition
{
	public static final Condition[] EMPTY_ARRAY = new Condition[0];

	private SystemMsg _message;

	public final void setSystemMsg(int msgId)
	{
		_message = SystemMsg.valueOf(msgId);
	}

	public final SystemMsg getSystemMsg()
	{
		return _message;
	}

	public final boolean test(Env env)
	{
		if(env.character != null)
		{
			for(Event event : env.character.getEvents())
			{
				if (!event.checkCondition(env.character, getClass()))
					return false;
			}
		}
		return testImpl(env);
	}

	protected abstract boolean testImpl(Env env);
}