package org.l2j.gameserver.model.actor.flags;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.flags.flag.DefaultFlag;

public final class PlayerFlags extends PlayableFlags
{
	private final DefaultFlag _chatBlocked = new DefaultFlag();
	private final DefaultFlag _escapeBlocked = new DefaultFlag();
	private final DefaultFlag _partyBlocked = new DefaultFlag();
	private final DefaultFlag _violetBoy = new DefaultFlag();

	public PlayerFlags(Player owner)
	{
		super(owner);
	}

	public DefaultFlag getChatBlocked()
	{
		return _chatBlocked;
	}

	public DefaultFlag getEscapeBlocked()
	{
		return _escapeBlocked;
	}

	public DefaultFlag getPartyBlocked()
	{
		return _partyBlocked;
	}

	public DefaultFlag getVioletBoy()
	{
		return _violetBoy;
	}
}