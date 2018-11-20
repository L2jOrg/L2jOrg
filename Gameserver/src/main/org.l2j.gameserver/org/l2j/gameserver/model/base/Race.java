package org.l2j.gameserver.model.base;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.CustomMessage;

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
		return new CustomMessage("org.l2j.gameserver.model.base.Race.name." + ordinal()).toString(player);
	}
}