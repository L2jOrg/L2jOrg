package l2s.gameserver.model.base;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;

/**
 * This class defines all races (human, elf, darkelf, orc, dwarf) that a player can chose.<BR><BR>
 */
public enum Race
{
	HUMAN,
	ELF,
	DARKELF,
	ORC,
	DWARF;

	public static final Race[] VALUES = values();

	public final String getName(Player player)
	{
		return new CustomMessage("l2s.gameserver.model.base.Race.name." + ordinal()).toString(player);
	}
}