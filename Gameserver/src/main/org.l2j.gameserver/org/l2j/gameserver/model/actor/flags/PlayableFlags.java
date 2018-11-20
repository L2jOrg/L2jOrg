package org.l2j.gameserver.model.actor.flags;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.actor.flags.flag.DefaultFlag;

public class PlayableFlags extends CreatureFlags
{
	private final DefaultFlag _silentMoving = new DefaultFlag();

	public PlayableFlags(Playable owner)
	{
		super(owner);
	}

	public DefaultFlag getSilentMoving()
	{
		return _silentMoving;
	}
}