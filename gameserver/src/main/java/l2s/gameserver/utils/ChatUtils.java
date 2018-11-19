package l2s.gameserver.utils;

import l2s.gameserver.Config;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.NSPacket;
import l2s.gameserver.network.l2.s2c.SayPacket2;

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

		say(activeChar, activeObject, World.getAroundObservers(activeObject), Config.CHAT_RANGE, cs);
	}

	public static void say(Player activeChar, Iterable<Player> players, SayPacket2 cs)
	{
		GameObject activeObject = activeChar.getObservePoint();
		if (activeObject == null)
			activeObject = activeChar;

		say(activeChar, activeObject, players, Config.CHAT_RANGE, cs);
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

			if (tx*tx + ty*ty <= Config.SHOUT_SQUARE_OFFSET || activeObject.isInRangeZ(obj, Config.CHAT_RANGE))
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
		say(activeChar, World.getAroundObservers(activeChar), Config.CHAT_RANGE, cs);
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, NSPacket cs)
	{
		say(activeChar, players, Config.CHAT_RANGE, cs);
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
		say(npc, Config.CHAT_RANGE, npcString, params);
	}

	public static void shout(NpcInstance activeChar, NSPacket cs)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);

		for(Player player : GameObjectsStorage.getPlayers())
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if(activeChar.getReflection() != obj.getReflection())
				continue;

			int tx = MapUtils.regionX(obj) - rx;
			int ty = MapUtils.regionY(obj) - ry;

			if (tx*tx + ty*ty <= Config.SHOUT_SQUARE_OFFSET || activeChar.isInRangeZ(obj, Config.CHAT_RANGE))
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
		say(activeChar, World.getAroundObservers(activeChar), Config.CHAT_RANGE, cm);
	}

	public static void say(NpcInstance activeChar, Iterable<Player> players, CustomMessage cm)
	{
		say(activeChar, players, Config.CHAT_RANGE, cm);
	}

	public static void say(NpcInstance activeChar, int range, CustomMessage cm)
	{
		say(activeChar, World.getAroundObservers(activeChar), range, cm);
	}

	public static void shout(NpcInstance activeChar, CustomMessage cm)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);

		for(Player player : GameObjectsStorage.getPlayers())
		{
			GameObject obj = player.getObservePoint();
			if(obj == null)
				obj = player;

			if(activeChar.getReflection() != obj.getReflection())
				continue;

			int tx = MapUtils.regionX(obj) - rx;
			int ty = MapUtils.regionY(obj) - ry;

			if (tx*tx + ty*ty <= Config.SHOUT_SQUARE_OFFSET || activeChar.isInRangeZ(obj, Config.CHAT_RANGE))
				player.sendPacket(new NSPacket(activeChar, ChatType.NPC_SHOUT, cm.toString(player)));
		}
	}
}
