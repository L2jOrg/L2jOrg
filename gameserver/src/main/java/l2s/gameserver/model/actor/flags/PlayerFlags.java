package l2s.gameserver.model.actor.flags;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.flags.flag.DefaultFlag;

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