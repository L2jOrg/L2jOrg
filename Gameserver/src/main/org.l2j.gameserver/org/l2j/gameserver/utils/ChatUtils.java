package org.l2j.gameserver.utils;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.NSPacket;
import org.l2j.gameserver.network.l2.s2c.SayPacket2;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class ChatUtils
{
	private static void say(Player activeChar, GameObject activeObject, Iterable<Player> players, int range, SayPacket2 cs)
	{
		for(Player player : players)
		{
			if (player.isBlockAll())
				continue;

			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			//Персонаж находится рядом с наблюдателем или точкой наблюдения
			if(activeObject.isInRangeZ(obj, range))
				if(!player.getBlockList().contains(activeChar) && activeChar.canTalkWith(player))
				{
					cs.setCharName(activeChar.getVisibleName(player));
					player.sendPacket(cs);
				}
		}
	}

	public static void say(Player activeChar, SayPacket2 cs)
	{
		GameObject activeObject = activeChar.getObservePoint();
		if (activeObject == null)
			activeObject = activeChar;

		say(activeChar, activeObject, World.getAroundObservers(activeObject), getSettings(ServerSettings.class).chatRange(), cs);
	}

	public static void say(Player activeChar, Iterable<Player> players, SayPacket2 cs)
	{
		GameObject activeObject = activeChar.getObservePoint();
		if (activeObject == null)
			activeObject = activeChar;

		say(activeChar, activeObject, players, getSettings(ServerSettings.class).chatRange(), cs);
	}

	public static void say(Player activeChar, int range, SayPacket2 cs)
	{
		GameObject activeObject = activeChar.getObservePoint();
		if (activeObject == null)
			activeObject = activeChar;

		say(activeChar, activeObject, World.getAroundObservers(activeObject), range, cs);
	}

	public static void shout(Player activeChar, SayPacket2 cs)
	{
		GameObject activeObject = activeChar.getObservePoint();
		if (activeObject == null)
			activeObject = activeChar;

		int rx = MapUtils.regionX(activeObject);
		int ry = MapUtils.regionY(activeObject);

		ServerSettings serverSettings = getSettings(ServerSettings.class);
		for(Player player : GameObjectsStorage.getPlayers())
		{
			if(player == activeChar || player.isBlockAll())
				continue;

			if (player.canSeeAllShouts() && !player.getBlockList().contains(activeChar) && activeChar.canTalkWith(player))
			{
				player.sendPacket(cs);
				continue;
			}

			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if(activeObject.getReflection() != obj.getReflection())
				continue;

			int tx = MapUtils.regionX(obj) - rx;
			int ty = MapUtils.regionY(obj) - ry;

			if (tx*tx + ty*ty <= serverSettings.shoutSquareOffset() || activeObject.isInRangeZ(obj, serverSettings.chatRange()))
				if (!player.getBlockList().contains(activeChar) && activeChar.canTalkWith(player))
					player.sendPacket(cs);
		}
	}

	public static void announce(Player activeChar, SayPacket2 cs)
	{
		for(Player player : GameObjectsStorage.getPlayers())
		{
			if(player == activeChar || player.isBlockAll())
				continue;

			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if (!player.getBlockList().contains(activeChar) && activeChar.canTalkWith(player))
				player.sendPacket(cs);
		}
	}

	public static void chat(NpcInstance activeChar, ChatType type, NpcString npcString, String... params)
	{
		switch (type)
		{
			case ALL:
			case NPC_ALL:
				say(activeChar, npcString, params);
				break;
			case SHOUT:
			case NPC_SHOUT:
				shout(activeChar, npcString, params);
				break;
		}
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, int range, NSPacket cs)
	{
		for(Player player : players)
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			//Персонаж находится рядом с наблюдателем или точкой наблюдения
			if(activeChar.isInRangeZ(obj, range))
				player.sendPacket(cs);
		}
	}

	public static void say(NpcInstance activeChar, NSPacket cs)
	{
		say(activeChar, World.getAroundObservers(activeChar), getSettings(ServerSettings.class).chatRange(), cs);
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, NSPacket cs)
	{
		say(activeChar, players, getSettings(ServerSettings.class).chatRange(), cs);
	}

	public static void say(NpcInstance activeChar, int range, NSPacket cs)
	{
		say(activeChar, World.getAroundObservers(activeChar), range, cs);
	}

	public static void say(NpcInstance activeChar, int range, NpcString npcString, String... params)
	{
		say(activeChar, range, new NSPacket(activeChar, ChatType.NPC_ALL, npcString, params));
	}

	public static void say(NpcInstance npc, NpcString npcString, String... params)
	{
		say(npc, getSettings(ServerSettings.class).chatRange(), npcString, params);
	}

	public static void shout(NpcInstance activeChar, NSPacket cs)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);
		ServerSettings serverSettings = getSettings(ServerSettings.class);

		for(Player player : GameObjectsStorage.getPlayers())
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if(activeChar.getReflection() != obj.getReflection())
				continue;

			int tx = MapUtils.regionX(obj) - rx;
			int ty = MapUtils.regionY(obj) - ry;

			if (tx*tx + ty*ty <= serverSettings.shoutSquareOffset() || activeChar.isInRangeZ(obj, serverSettings.chatRange()))
				player.sendPacket(cs);
		}
	}

	public static void shout(NpcInstance activeChar, NpcString npcString, String... params)
	{
		shout(activeChar, new NSPacket(activeChar, ChatType.NPC_SHOUT, npcString, params));
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, int range, CustomMessage cm)
	{
		for(Player player : players)
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			//Персонаж находится рядом с наблюдателем или точкой наблюдения
			if(activeChar.isInRangeZ(obj, range))
				player.sendPacket(new NSPacket(activeChar, ChatType.NPC_SHOUT, cm.toString(player)));
		}
	}

	public static void say(NpcInstance activeChar, CustomMessage cm)
	{
		say(activeChar, World.getAroundObservers(activeChar), getSettings(ServerSettings.class).chatRange(), cm);
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, CustomMessage cm)
	{
		say(activeChar, players, getSettings(ServerSettings.class).chatRange(), cm);
	}

	public static void say(NpcInstance activeChar, int range, CustomMessage cm)
	{
		say(activeChar, World.getAroundObservers(activeChar), range, cm);
	}

	public static void shout(NpcInstance activeChar, CustomMessage cm)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);
		ServerSettings serverSettings = getSettings(ServerSettings.class);
		for(Player player : GameObjectsStorage.getPlayers())
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if(activeChar.getReflection() != obj.getReflection())
				continue;

			int tx = MapUtils.regionX(obj) - rx;
			int ty = MapUtils.regionY(obj) - ry;

			if (tx*tx + ty*ty <= serverSettings.shoutSquareOffset() || activeChar.isInRangeZ(obj, serverSettings.chatRange()))
				player.sendPacket(new NSPacket(activeChar, ChatType.NPC_SHOUT, cm.toString(player)));
		}
	}
}
