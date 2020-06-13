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
package quests.Q10673_SagaOfLegend;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;


/**
 * Saga of Legend (10673)
 * @URL https://l2wiki.com/classic/Saga_of_Legend
 * @TODO: Retail htmls.
 * @author Dmitri, Mobius
 * @edited Vicochips
 */
public class Q10673_SagaOfLegend extends Quest
{
	// NPCs
	private static final int ORVEN = 30857;
	// Monsters
	private static final int[] MOBS =
	{
		20965, // Chimera Piece 72
		20970, // Soldier of Ancient Times 73
		20966, // Mutated Creation 74
		20971, // Warrior of Ancient Times 74
		20967, // Creature of the Past 75
		20973, // Forgotten Ancient People 75
		20968, // Forgotten Face 75
		20969, // Giant's Shadow 75
		20972, // Shaman of Ancient Times 75
		24025, // Bloody Purple 70
		24046, // Floating Eye Seer 70
		24032, // Seer 70
		24041, // Bloody Mourner 71
		24026, // Clipher 71
		24042, // Clumsy Wimp 71
		24047, // Floating Eye Seer 71
		24033, // Guardian Spirit 71
		24048, // Immortal Spirit 71
		24043, // Mysterious Creature 71
		24050, // Starving Spirit 71
		24049, // Immortal Spirit 72
		24034, // Midnight Sairon 72
		24027, // Sairon 72
		24052, // Starving Soldier 72
		24051, // Starving Spirit 72
		24035, // Daymen 73
		24028, // Demon Warrior 73
		24053, // Starving Soldier 73
		24054, // Starving Warrior 73
		24036, // Dolores 74
		24037, // Maiden Doll 74
		24055, // Starving Warrior 74
		24030, // Stone Vanul 74
		24029, // Veil Master 74
		24044, // Zaken's Treasure Chest 74
		24045, // Zaken's Treasure Chest 74
		24031, // Death Flyer 75
		24040, // Midnight Nightmare 75
		24039, // Pearl Horror 75
		24038, // Tor Scorpion 75
	};
	// Rewards
	private static final int MAGICAL_TABLET = 90045;
	private static final int SPELLBOOK_HUMAN = 90038; // Spellbook: Mount Golden Lion
	private static final int SPELLBOOK_ELF = 90039; // Spellbook: Mount Pegasus
	private static final int SPELLBOOK_DELF = 90040; // Spellbook: Mount Saber Tooth Cougar
	private static final int SPELLBOOK_ORC = 90042; // Spellbook: Mount Black Bear
	private static final int SPELLBOOK_DWARF = 90041; // Spellbook: Mount Kukuru
	private static final int SPELLBOOK_KAMAEL = 91946; // Spellbook: Mount Griffin
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10673_SagaOfLegend()
	{
		super(10673);
		addStartNpc(ORVEN);
		addTalkId(ORVEN);
		addKillId(MOBS);
		addCondMinLevel(MIN_LEVEL, "30857-00.htm");
		addCondInCategory(CategoryType.THIRD_CLASS_GROUP, "30857-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (isNull(qs)) {
			return htmltext;
		}
		
		switch (event)
		{
			case "30857-02.htm":
			case "30857-03.htm":
			case "30857-04.htm":
			case "30857-06.html":
			{
				htmltext = event;
				break;
			}
			case "30857-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30857-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30857-07a.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30857-10.html":
			{
				if (qs.isCond(4))
				{
					giveItems(player, MAGICAL_TABLET, 10);
					qs.exitQuest(false, true);
					if (CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId()))
					{
						player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
					}
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30857-01.htm";
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30857-05.htm";
						break;
					}
					case 2:
					{
						htmltext = "30857-08.html";
						break;
					}
					case 3:
					{
						htmltext = "30857-08a.html";
						break;
					}
					case 4:
					{
						htmltext = "30857-09.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && (qs.getCond() > 1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < 700)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			}
			else
			{
				qs.setCond(4, true);
				qs.unset(KILL_COUNT_VAR);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.getCond() > 1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.ORVEN_S_REQUEST.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId()))
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCompleted())
		{
			player.sendPacket(ExRequestClassChangeUi.STATIC_PACKET);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onProfessionChange(OnPlayerProfessionChange event)
	{
		final Player player = event.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, player.getClassId().getId()))
		{
			return;
		}
		
		// Avoid reward more than once.
		if (player.getVariables().getBoolean("ITEMS_REWARDED", false))
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCompleted())
		{
			player.getVariables().set("ITEMS_REWARDED", true);
			
			switch (player.getRace())
			{
				case ELF:
				{
					giveItems(player, SPELLBOOK_ELF, 1);
					break;
				}
				case DARK_ELF:
				{
					giveItems(player, SPELLBOOK_DELF, 1);
					break;
				}
				case ORC:
				{
					giveItems(player, SPELLBOOK_ORC, 1);
					break;
				}
				case DWARF:
				{
					giveItems(player, SPELLBOOK_DWARF, 1);
					break;
				}
				case JIN_KAMAEL:
				{
					giveItems(player, SPELLBOOK_KAMAEL, 1);
					break;
				}
				case HUMAN:
				{
					giveItems(player, SPELLBOOK_HUMAN, 1);
					break;
				}
			}
		}
	}
}