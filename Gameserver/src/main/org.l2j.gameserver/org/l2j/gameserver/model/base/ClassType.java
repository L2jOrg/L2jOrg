package org.l2j.gameserver.model.base;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.CustomMessage;

public enum ClassType
{
	FIGHTER,
	MYSTIC;

	public static final ClassType[] VALUES = values();

	public boolean isMagician()
	{
		return this != FIGHTER;
	}

	public final String getName(Player player)
	{
		return new CustomMessage("org.l2j.gameserver.model.base.ClassType.name." + ordinal()).toString(player);
	}
}