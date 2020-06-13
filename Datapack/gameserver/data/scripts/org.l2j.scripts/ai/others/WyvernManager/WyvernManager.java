/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.WyvernManager;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Wyvern Manager
 * @author xban1x
 */
public final class WyvernManager extends AbstractNpcAI {

	private enum ManagerType {
		CASTLE,
		CLAN_HALL,
	}
	
	// Misc
	private static final int CRYSTAL_B_GRADE = 1460;
	private static final int WYVERN = 12621;
	private static final int WYVERN_FEE = 25;
	private static final int STRIDER_LVL = 55;
	private static final int[] STRIDERS =
	{
		12526,
		12527,
		12528,
		16038,
		16039,
		16040,
		16068,
		13197
	};
	// NPCS
	private static final Map<Integer, ManagerType> MANAGERS = new HashMap<>();
	static
	{
		MANAGERS.put(35101, ManagerType.CASTLE);
		MANAGERS.put(35143, ManagerType.CASTLE);
		MANAGERS.put(35185, ManagerType.CASTLE);
		MANAGERS.put(35227, ManagerType.CASTLE);
		MANAGERS.put(35275, ManagerType.CASTLE);
		MANAGERS.put(35317, ManagerType.CASTLE);
		MANAGERS.put(35364, ManagerType.CASTLE);
		MANAGERS.put(35510, ManagerType.CASTLE);
		MANAGERS.put(35536, ManagerType.CASTLE);
		MANAGERS.put(35556, ManagerType.CASTLE);
		MANAGERS.put(35419, ManagerType.CLAN_HALL);
	}
	
	private WyvernManager()
	{
		addStartNpc(MANAGERS.keySet());
		addTalkId(MANAGERS.keySet());
		addFirstTalkId(MANAGERS.keySet());
	}
	
	private String mountWyvern(Npc npc, Player player)
	{
		if (player.isMounted() && (player.getMountLevel() >= STRIDER_LVL) && Util.contains(STRIDERS, player.getMountNpcId()))
		{
			if (isOwnerClan(npc, player) && (getQuestItemsCount(player, CRYSTAL_B_GRADE) >= WYVERN_FEE))
			{
				takeItems(player, CRYSTAL_B_GRADE, WYVERN_FEE);
				player.dismount();
				player.mount(WYVERN, 0, true);
				return "wyvernmanager-04.html";
			}
			return replacePart(player, "wyvernmanager-06.html");
		}
		return replacePart(player, "wyvernmanager-05.html");
	}
	
	private boolean isOwnerClan(Npc npc, Player player)
	{
		switch (MANAGERS.get(npc.getId()))
		{
			case CASTLE:
			{
				if ((player.getClan() != null) && (npc.getCastle() != null))
				{
					return (player.isClanLeader() && (player.getClanId() == npc.getCastle().getOwnerId()));
				}
				return false;
			}
			case CLAN_HALL:
			{
				if ((player.getClan() != null) && (npc.getClanHall() != null))
				{
					return (player.isClanLeader() && (player.getClanId() == npc.getClanHall().getOwnerId()));
				}
				return false;
			}
			default:
			{
				return false;
			}
		}
	}
	
	private boolean isInSiege(Npc npc)
	{
		switch (MANAGERS.get(npc.getId()))
		{
			case CASTLE:
			{
				return npc.getCastle().getZone().isActive();
			}
			default:
			{
				return false;
			}
		}
	}
	
	private String getResidenceName(Npc npc)
	{
		switch (MANAGERS.get(npc.getId()))
		{
			case CASTLE:
			{
				return npc.getCastle().getName();
			}
			case CLAN_HALL:
			{
				return npc.getClanHall().getName();
			}
			default:
			{
				return null;
			}
		}
	}
	
	private String replaceAll(Npc npc, Player player)
	{
		return replacePart(player, "wyvernmanager-01.html").replace("%residence_name%", getResidenceName(npc));
	}
	
	private String replacePart(Player player, String htmlFile)
	{
		return getHtm(player, htmlFile).replace("%wyvern_fee%", String.valueOf(WYVERN_FEE)).replace("%strider_level%", String.valueOf(STRIDER_LVL));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "Return":
			{
				if (!isOwnerClan(npc, player))
				{
					htmltext = "wyvernmanager-02.html";
				}
				else if (Config.ALLOW_WYVERN_ALWAYS)
				{
					htmltext = replaceAll(npc, player);
				}
				else if (MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
				{
					htmltext = "wyvernmanager-dusk.html";
				}
				else
				{
					htmltext = replaceAll(npc, player);
				}
				break;
			}
			case "Help":
			{
				htmltext = MANAGERS.get(npc.getId()) == ManagerType.CASTLE ? replacePart(player, "wyvernmanager-03.html") : replacePart(player, "wyvernmanager-03b.html");
				break;
			}
			case "RideWyvern":
			{
				if (!Config.ALLOW_WYVERN_ALWAYS)
				{
					if (!Config.ALLOW_WYVERN_DURING_SIEGE && (isInSiege(npc) || player.isInSiege()))
					{
						player.sendMessage("You cannot summon wyvern while in siege.");
						return null;
					}
					if (MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
					{
						htmltext = "wyvernmanager-dusk.html";
					}
					else
					{
						htmltext = mountWyvern(npc, player);
					}
				}
				else
				{
					htmltext = mountWyvern(npc, player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (!isOwnerClan(npc, player))
		{
			htmltext = "wyvernmanager-02.html";
		}
		else if (Config.ALLOW_WYVERN_ALWAYS)
		{
			htmltext = replaceAll(npc, player);
		}
		else if (MANAGERS.get(npc.getId()) == ManagerType.CASTLE)
		{
			htmltext = "wyvernmanager-dusk.html";
		}
		else
		{
			htmltext = replaceAll(npc, player);
		}
		return htmltext;
	}
	
	public static AbstractNpcAI provider()
	{
		return new WyvernManager();
	}
}
