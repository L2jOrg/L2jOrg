/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.instances.MonsterArena;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2j.scripts.instances.AbstractInstance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mobius
 * https://l2wiki.com/classic/Clan_-_Clan_Arena
 */
public class MonsterArena extends AbstractInstance {

	private static final int LEO = 30202;
	private static final int MACHINE = 30203;
	private static final int SUPPLIES = 30204;

	private static final int[] BOSSES = {
		25794, // Kutis
		25795, // Garan
		25796, // Batur
		25797, // Venir
		25798, // Oel
		25799, // Taranka
		25800, // Kasha
		25801, // Dorak
		25802, // Turan
		25803, // Varkan
		25804, // Ketran
		25805, // Death Lord Likan
		25806, // Anbarad
		25807, // Baranos
		25808, // Takuran
		25809, // Nast
		25810, // Keltar
		25811, // Satur
		25812, // Kosnak
		25813, // Garaki
		25834, // Shadai
		25835, // Turabait
		25836, // Tier
		25837, // Cherkya
		25838, // Spicula
	};

	private static final int BATTLE_BOX_1 = 70917;
	private static final int BATTLE_BOX_2 = 70918;
	private static final int BATTLE_BOX_3 = 70919;
	private static final int BATTLE_BOX_4 = 70920;
	private static final int TICKET_L = 90945;
	private static final int TICKET_M = 90946;
	private static final int TICKET_H = 90947;

	private static final Collection<Player> REWARDED_PLAYERS = ConcurrentHashMap.newKeySet();
	private static final int TEMPLATE_ID = 192;

	private MonsterArena() {
		super(TEMPLATE_ID);
		addStartNpc(LEO, MACHINE, SUPPLIES);
		addFirstTalkId(LEO, MACHINE, SUPPLIES);
		addTalkId(LEO, MACHINE, SUPPLIES);
		addKillId(BOSSES);
		addInstanceLeaveId(TEMPLATE_ID);
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		switch (event) {
			case "30202-01.htm":
			case "30202-02.htm":
			case "30202-03.htm":
			case "30203-01.htm": {
				return event;
			}
			case "enter_monster_arena": {
				// If you died, you may return to the arena.
				if (player.isGM()){
					enterArenaInstance(player, npc, TEMPLATE_ID);
					final Instance world = player.getInstanceWorld();
					if (world != null) {
						final Npc machine = world.getNpc(MACHINE);
						machine.setScriptValue(player.getClanId());
						startQuestTimer("machine_talk", 10000, machine, null);
						startQuestTimer("start_countdown", 60000, machine, null);
						startQuestTimer("next_spawn", 60000, machine, null);
						var clan = player.getClan();
						clan.setArenaProgress(1);
					}
				}
				if ((player.getClan() != null) && (player.getCommandChannel() != null))
				{
					for (Player member : player.getCommandChannel().getMembers())
					{
						final Instance world = member.getInstanceWorld();
						if ((world != null) && (world.getTemplateId() == TEMPLATE_ID) && (world.getPlayersCount() < 40) && (player.getClanId() == member.getClanId()))
						{
							player.teleToLocation(world.getNpc(MACHINE), true, world);
							if ((world.getStatus() > 0) && (world.getStatus() < 5)) // Show remaining countdown.
							{
								player.sendPacket(new ExSendUIEvent(player, false, false, (int) (world.getRemainingTime() / 1000), 0, NpcStringId.REMAINING_TIME));
							}
							return null;
						}
					}
				}

				// Clan checks.
				if ((player.getClan() == null) || (player.getClan().getLeaderId() != player.getObjectId()) || (player.getCommandChannel() == null))
				{
					return "30202-03.htm";
				}
				if (player.getClan().getLevel() < 3)
				{
					player.sendMessage("Your clan must be at least level 3.");
					return null;
				}
				for (Player member : player.getCommandChannel().getMembers())
				{
					if ((member.getClan() == null) || (member.getClanId() != player.getClanId()))
					{
						player.sendMessage("Your command channel must be consisted only by clan members.");
						return null;
					}
				}

				enterArenaInstance(player, npc, TEMPLATE_ID);

				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final Npc machine = world.getNpc(MACHINE);
					machine.setScriptValue(player.getClanId());

					var clan = player.getClan();
					if(clan.getArenaProgress() <= 0) {
						clan.setArenaProgress(1);
					} else if(clan.getArenaProgress() > 17) {
						clan.setArenaProgress(17);
					}

					startQuestTimer("machine_talk", 10000, machine, null);
					startQuestTimer("start_countdown", 60000, machine, null);
					startQuestTimer("next_spawn", 60000, machine, null);
				}
				break;
			}
			case "machine_talk": {
				final Instance world = npc.getInstanceWorld();
				if (world != null) {
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WELCOME_TO_THE_ARENA_TEST_YOUR_CLAN_S_STRENGTH);
				}
				break;
			}
			case "start_countdown": {
				final Instance world = npc.getInstanceWorld();
				if (world != null) {
					world.setStatus(1);
					for (Player plr : world.getPlayers()) {
						plr.sendPacket(new ExSendUIEvent(plr, false, false, (int) world.getRemainingTime() / 1000, 0, NpcStringId.REMAINING_TIME));
					}
				}
				break;
			}
			case "next_spawn": {
				final Instance world = npc.getInstanceWorld();
				if (world != null) {
					world.spawnGroup("boss_" + ClanEngine.getInstance().getClan(npc.getScriptValue()).getArenaProgress());
				}
				break;
			}
			case "supply_reward": {
				final Instance world = npc.getInstanceWorld();
				if ((world != null) && (npc.getId() == SUPPLIES) && (player.getLevel() > 39)) {
					if (!REWARDED_PLAYERS.contains(player) && npc.isScriptValue(0)) {
						npc.setScriptValue(1);
						npc.doDie(npc);
						REWARDED_PLAYERS.add(player);
						ThreadPool.schedule(() -> REWARDED_PLAYERS.remove(player), 60000);

						// Mandatory reward.
						final Npc machine = world.getNpc(MACHINE);
						final int progress = ClanEngine.getInstance().getClan(machine.getScriptValue()).getArenaProgress();
						if (progress > 16)
						{
							giveItems(player, BATTLE_BOX_4, 1);
						}
						else if (progress > 11)
						{
							giveItems(player, BATTLE_BOX_3, 1);
						}
						else if (progress > 6)
						{
							giveItems(player, BATTLE_BOX_2, 1);
						}
						else
						{
							giveItems(player, BATTLE_BOX_1, 1);
						}

						// Rare reward.
						if (Rnd.get(100) < 1) // 1% chance.
						{
							giveItems(player, TICKET_L, 1);
						}
						else if (Rnd.get(100) < 1) // 1% chance.
						{
							giveItems(player, TICKET_M, 1);
						}
						else if (Rnd.get(100) < 1) // 1% chance.
						{
							giveItems(player, TICKET_H, 1);
						}
					}
				}
				break;
			}
			case "remove_supplies": {
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					for (Npc aliveNpc : world.getAliveNpcs())
					{
						if ((aliveNpc != null) && (aliveNpc.getId() == SUPPLIES))
						{
							aliveNpc.deleteMe();
						}
					}
				}
				break;
			}
			case "extratime": {
				final Instance world = player.getInstanceWorld();
				int count = world.getParameters().getInt("extratime");
				world.setParameter("extratime", count);
				long remainingTime = world.getRemainingTime();
				int additionnalTime;
				if(Rnd.get(100) < 10)
					additionnalTime = 5;
				else {
					additionnalTime = Rnd.get(4);
				}

				remainingTime += additionnalTime * 60000;
				world.setDuration((int) (remainingTime / 1000) / 60);

				for (Player plr : world.getPlayers()) {
					//FIXME: Do need to cancel current UIEvent ?
					plr.sendPacket(new ExSendUIEvent(plr, false, false, (int) remainingTime / 1000, 0, NpcStringId.REMAINING_TIME));
				}
			}
		}
		return null;
	}

	private void enterArenaInstance(Player player, Npc npc, int templateId) {
		enterInstance(player, npc, templateId);
		final Instance world = player.getInstanceWorld();
		if (world != null) {
			world.setParameter("extratime", 0);
		}
	}

	@Override
	public void onInstanceLeave(Player player, Instance instance) {
		player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.REMAINING_TIME));
	}

	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			// Change world status.
			world.incStatus();

			// Make machine talk.
			final Npc machine = world.getNpc(MACHINE);
			machine.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HA_NOT_BAD);

			// Save progress to global variables.
			var clan = ClanEngine.getInstance().getClan(machine.getScriptValue());
			clan.setArenaProgress(clan.getArenaProgress() + 1);
			ClanRewardManager.getInstance().checkArenaProgress(clan);

			// Spawn reward chests.
			world.spawnGroup("supplies");
			startQuestTimer("remove_supplies", 60000, machine, null);

			// Next boss spawn.
			if (world.getStatus() < 5)
			{
				startQuestTimer("next_spawn", 60000, machine, null);
			}
			else // Finish.
			{
				for (Player plr : world.getPlayers())
				{
					plr.sendPacket(new ExSendUIEvent(plr, false, false, 0, 0, NpcStringId.REMAINING_TIME));
				}
				world.finishInstance();
			}
		}
		return super.onKill(npc, player, isSummon);
	}

	@Override
	public String onFirstTalk(Npc npc, Player player) {
		final Instance world = player.getInstanceWorld();
		if(npc.getId() == MACHINE && world.getParameters().getInt("extratime") < 5) {
			return npc.getId() + "-extratime.htm";
		}
		return npc.getId() + "-01.htm";
	}

	public static AbstractInstance provider() {
		return new MonsterArena();
	}
}
