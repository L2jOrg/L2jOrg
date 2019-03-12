/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.FactionSystem;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public final class FactionSystem extends AbstractNpcAI
{
	// NPCs
	private static final int MANAGER = Config.FACTION_MANAGER_NPCID;
	private static final int GOOD_GUARD = Config.FACTION_GOOD_GUARD_NPCID;
	private static final int EVIL_GUARD = Config.FACTION_EVIL_GUARD_NPCID;
	// Other
	private static final String[] TEXTS =
	{
		Config.FACTION_GOOD_TEAM_NAME + " or " + Config.FACTION_EVIL_TEAM_NAME + "?",
		"Select your faction!",
		"The choice is yours!"
	};
	
	private FactionSystem()
	{
		addSpawnId(MANAGER);
		addStartNpc(MANAGER);
		addTalkId(MANAGER);
		addFirstTalkId(MANAGER);
		addAggroRangeEnterId(EVIL_GUARD, GOOD_GUARD);
		
		if (Config.FACTION_SYSTEM_ENABLED)
		{
			addSpawn(MANAGER, Config.FACTION_MANAGER_LOCATION, false, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "selectGoodFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (L2World.getInstance().getAllGoodPlayers().size() >= (L2World.getInstance().getAllEvilPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_GOOD_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_EVIL_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNoble(true);
				}
				player.setGood();
				player.getAppearance().setNameColor(Config.FACTION_GOOD_NAME_COLOR);
				player.getAppearance().setTitleColor(Config.FACTION_GOOD_NAME_COLOR);
				player.setTitle(Config.FACTION_GOOD_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_GOOD_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_GOOD_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_GOOD_TEAM_NAME, Config.FACTION_GOOD_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				L2World.addFactionPlayerToWorld(player);
				break;
			}
			case "selectEvilFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (L2World.getInstance().getAllEvilPlayers().size() >= (L2World.getInstance().getAllGoodPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_EVIL_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_GOOD_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNoble(true);
				}
				player.setEvil();
				player.getAppearance().setNameColor(Config.FACTION_EVIL_NAME_COLOR);
				player.getAppearance().setTitleColor(Config.FACTION_EVIL_NAME_COLOR);
				player.setTitle(Config.FACTION_EVIL_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_EVIL_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_EVIL_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_EVIL_TEAM_NAME, Config.FACTION_EVIL_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				L2World.addFactionPlayerToWorld(player);
				break;
			}
			case "SPEAK":
			{
				if (npc != null)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, TEXTS[getRandom(TEXTS.length)], 1500);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final String htmltext = null;
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player, "manager.html"));
		packet.replace("%name%", player.getName());
		packet.replace("%good%", Config.FACTION_GOOD_TEAM_NAME);
		packet.replace("%evil%", Config.FACTION_EVIL_TEAM_NAME);
		player.sendPacket(packet);
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == MANAGER)
		{
			startQuestTimer("SPEAK", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	private void broadcastMessageToFaction(String factionName, String message)
	{
		if (factionName.equals(Config.FACTION_GOOD_TEAM_NAME))
		{
			for (L2PcInstance player : L2World.getInstance().getAllGoodPlayers())
			{
				player.sendMessage(message);
			}
		}
		else
		{
			for (L2PcInstance player : L2World.getInstance().getAllEvilPlayers())
			{
				player.sendMessage(message);
			}
		}
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (Config.FACTION_SYSTEM_ENABLED && Config.FACTION_GUARDS_ENABLED && ((player.isGood() && (npc.getId() == EVIL_GUARD)) || (player.isEvil() && (npc.getId() == GOOD_GUARD))))
		{
			addAttackDesire(npc, player);
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new FactionSystem();
	}
}
