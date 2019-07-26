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
package quests.Q00227_TestOfTheReformer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

import java.util.Arrays;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Test Of The Reformer (227)
 * @author ivantotov
 */
public final class Q00227_TestOfTheReformer extends Quest
{
	// NPCs
	private static final int PRIESTESS_PUPINA = 30118;
	private static final int PREACHER_SLA = 30666;
	private static final int RAMUS = 30667;
	private static final int KATARI = 30668;
	private static final int KAKAN = 30669;
	private static final int NYAKURI = 30670;
	private static final int OL_MAHUM_PILGRIM = 30732;
	// Items
	private static final int BOOK_OF_REFORM = 2822;
	private static final int LETTER_OF_INTRODUCTION = 2823;
	private static final int SLAS_LETTER = 2824;
	private static final int GREETINGS = 2825;
	private static final int Ol_MAHUM_MONEY = 2826;
	private static final int KATARIS_LETTER = 2827;
	private static final int NYAKURIS_LETTER = 2828;
	private static final int UNDEAD_LIST = 2829;
	private static final int RAMUSS_LETTER = 2830;
	private static final int RIPPED_DIARY = 2831;
	private static final int HUGE_NAIL = 2832;
	private static final int LETTER_OF_BETRAYER = 2833;
	private static final int BONE_FRAGMENT4 = 2834;
	private static final int BONE_FRAGMENT5 = 2835;
	private static final int BONE_FRAGMENT6 = 2836;
	private static final int BONE_FRAGMENT7 = 2837;
	private static final int BONE_FRAGMENT8 = 2838;
	private static final int KAKANS_LETTER = 3037;
	private static final int LETTER_GREETINGS1 = 5567;
	private static final int LETTER_GREETINGS2 = 5568;
	// Rewards
	private static final int MARK_OF_REFORMER = 2821;
	// Monsters
	private static final int MISERY_SKELETON = 20022;
	private static final int SKELETON_ARCHER = 20100;
	private static final int SKELETON_MARKSMAN = 20102;
	private static final int SKELETON_LORD = 20104;
	private static final int SILENT_HORROR = 20404;
	// Quest Monsters
	private static final int NAMELESS_REVENANT = 27099;
	private static final int ARURAUNE = 27128;
	private static final int OL_MAHUM_INSPECTOR = 27129;
	private static final int OL_MAHUM_BETRAYER = 27130;
	private static final int CRIMSON_WEREWOLF = 27131;
	private static final int KRUDEL_LIZARDMAN = 27132;
	// Skills
	private static final int DISRUPT_UNDEAD = 1031;
	private static final int SLEEP = 1069;
	private static final int VAMPIRIC_TOUCH = 1147;
	private static final int CURSE_WEAKNESS = 1164;
	private static final int CURSE_POISON = 1168;
	private static final int WIND_STRIKE = 1177;
	private static final int ICE_BOLD = 1184;
	private static final int DRYAD_ROOT = 1201;
	private static final int WIND_SHACKLE = 1206;
	private static final List<Integer> SKILLS = Arrays.asList(DISRUPT_UNDEAD, SLEEP, VAMPIRIC_TOUCH, CURSE_WEAKNESS, CURSE_POISON, WIND_STRIKE, ICE_BOLD, DRYAD_ROOT, WIND_SHACKLE);
	// Location
	private static final Location MOVE_TO = new Location(36787, -3709, 10000);
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00227_TestOfTheReformer()
	{
		super(227);
		addStartNpc(PRIESTESS_PUPINA);
		addTalkId(PRIESTESS_PUPINA, PREACHER_SLA, RAMUS, KATARI, KAKAN, NYAKURI, OL_MAHUM_PILGRIM);
		addAttackId(NAMELESS_REVENANT, CRIMSON_WEREWOLF);
		addKillId(MISERY_SKELETON, SKELETON_ARCHER, SKELETON_MARKSMAN, SKELETON_LORD, SILENT_HORROR, NAMELESS_REVENANT, ARURAUNE, OL_MAHUM_INSPECTOR, OL_MAHUM_BETRAYER, OL_MAHUM_BETRAYER, CRIMSON_WEREWOLF, KRUDEL_LIZARDMAN);
		addSpawnId(OL_MAHUM_PILGRIM, OL_MAHUM_INSPECTOR, OL_MAHUM_BETRAYER, CRIMSON_WEREWOLF, KRUDEL_LIZARDMAN);
		registerQuestItems(BOOK_OF_REFORM, LETTER_OF_INTRODUCTION, SLAS_LETTER, GREETINGS, Ol_MAHUM_MONEY, KATARIS_LETTER, NYAKURIS_LETTER, UNDEAD_LIST, RAMUSS_LETTER, RAMUSS_LETTER, RIPPED_DIARY, HUGE_NAIL, LETTER_OF_BETRAYER, BONE_FRAGMENT4, BONE_FRAGMENT5, BONE_FRAGMENT6, BONE_FRAGMENT7, BONE_FRAGMENT8, KAKANS_LETTER, LETTER_GREETINGS1, LETTER_GREETINGS2);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if ("DESPAWN".equals(event))
		{
			final int SPAWNED = npc.getVariables().getInt("SPAWNED", 0);
			if (SPAWNED < 60)
			{
				npc.getVariables().set("SPAWNED", SPAWNED + 1);
			}
			else
			{
				npc.deleteMe();
			}
			return super.onAdvEvent(event, npc, player);
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "ACCEPT":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setMemoState(1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, BOOK_OF_REFORM, 1);
				}
				break;
			}
			case "30118-06.html":
			{
				if (hasQuestItems(player, BOOK_OF_REFORM))
				{
					takeItems(player, BOOK_OF_REFORM, 1);
					giveItems(player, LETTER_OF_INTRODUCTION, 1);
					takeItems(player, HUGE_NAIL, 1);
					qs.setMemoState(4);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30666-02.html":
			case "30666-03.html":
			case "30669-02.html":
			case "30669-05.html":
			case "30670-02.html":
			{
				htmltext = event;
				break;
			}
			case "30666-04.html":
			{
				takeItems(player, LETTER_OF_INTRODUCTION, 1);
				giveItems(player, SLAS_LETTER, 1);
				qs.setMemoState(5);
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30669-03.html":
			{
				qs.setCond(12, true);
				if (npc.getSummonedNpcCount() < 1)
				{
					Npc pilgrim = addSpawn(OL_MAHUM_PILGRIM, -9282, -89975, -2331, 0, false, 0);
					Npc wolf = addSpawn(CRIMSON_WEREWOLF, -9382, -89852, -2333, 0, false, 0);
					((Attackable) wolf).addDamageHate(pilgrim, 99999, 99999);
					wolf.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, pilgrim);
				}
				htmltext = event;
				break;
			}
			case "30670-03.html":
			{
				qs.setCond(15, true);
				if (npc.getSummonedNpcCount() < 1)
				{
					Npc pilgrim = addSpawn(OL_MAHUM_PILGRIM, 125947, -180049, -1778, 0, false, 0);
					Npc lizard = addSpawn(KRUDEL_LIZARDMAN, 126019, -179983, -1781, 0, false, 0);
					((Attackable) lizard).addDamageHate(pilgrim, 99999, 99999);
					lizard.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, pilgrim);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getId())
			{
				case NAMELESS_REVENANT:
				{
					if (skill != null)
					{
						if (skill.getId() == DISRUPT_UNDEAD)
						{
							npc.setScriptValue(1);
						}
						else
						{
							npc.setScriptValue(2);
						}
					}
					break;
				}
				case CRIMSON_WEREWOLF:
				{
					if ((skill == null) || !SKILLS.contains(skill.getId()))
					{
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.COWARDLY_GUY));
						npc.deleteMe();
					}
					if (isPlayer(attacker))
					{
						npc.setScriptValue(attacker.getObjectId());
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case MISERY_SKELETON:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, BONE_FRAGMENT7))
					{
						giveItems(killer, BONE_FRAGMENT7, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, BONE_FRAGMENT4, BONE_FRAGMENT5, BONE_FRAGMENT6, BONE_FRAGMENT8))
						{
							qs.setMemoState(17);
							qs.setCond(19);
						}
					}
					break;
				}
				case SKELETON_ARCHER:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, BONE_FRAGMENT8))
					{
						giveItems(killer, BONE_FRAGMENT8, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, BONE_FRAGMENT4, BONE_FRAGMENT5, BONE_FRAGMENT6, BONE_FRAGMENT7))
						{
							qs.setMemoState(17);
							qs.setCond(19);
						}
					}
					break;
				}
				case SKELETON_MARKSMAN:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, BONE_FRAGMENT6))
					{
						giveItems(killer, BONE_FRAGMENT6, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, BONE_FRAGMENT4, BONE_FRAGMENT5, BONE_FRAGMENT7, BONE_FRAGMENT8))
						{
							qs.setMemoState(17);
							qs.setCond(19);
						}
					}
					break;
				}
				case SKELETON_LORD:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, BONE_FRAGMENT5))
					{
						giveItems(killer, BONE_FRAGMENT5, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, BONE_FRAGMENT4, BONE_FRAGMENT6, BONE_FRAGMENT7, BONE_FRAGMENT8))
						{
							qs.setMemoState(17);
							qs.setCond(19);
						}
					}
					break;
				}
				case SILENT_HORROR:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, BONE_FRAGMENT4))
					{
						giveItems(killer, BONE_FRAGMENT4, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, BONE_FRAGMENT5, BONE_FRAGMENT6, BONE_FRAGMENT7, BONE_FRAGMENT8))
						{
							qs.setMemoState(17);
							qs.setCond(19);
						}
					}
					break;
				}
				case NAMELESS_REVENANT:
				{
					if (qs.isMemoState(1) && npc.isScriptValue(1) && !hasQuestItems(killer, HUGE_NAIL) && hasQuestItems(killer, BOOK_OF_REFORM) && (getQuestItemsCount(killer, RIPPED_DIARY) < 7))
					{
						if (getQuestItemsCount(killer, RIPPED_DIARY) == 6)
						{
							addSpawn(ARURAUNE, npc, true, 0, false);
							takeItems(killer, RIPPED_DIARY, -1);
							qs.setCond(2);
						}
						else
						{
							giveItems(killer, RIPPED_DIARY, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ARURAUNE:
				{
					if (!hasQuestItems(killer, HUGE_NAIL))
					{
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.THE_CONCEALED_TRUTH_WILL_ALWAYS_BE_REVEALED));
						giveItems(killer, HUGE_NAIL, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						qs.setMemoState(3);
						qs.setCond(3);
					}
					break;
				}
				case OL_MAHUM_INSPECTOR:
				{
					if (qs.isMemoState(6))
					{
						qs.setMemoState(7);
						qs.setCond(7, true);
					}
					break;
				}
				case OL_MAHUM_BETRAYER:
				{
					if (qs.isMemoState(8))
					{
						qs.setMemoState(9);
						qs.setCond(9);
						giveItems(killer, LETTER_OF_BETRAYER, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case CRIMSON_WEREWOLF:
				{
					if (npc.isScriptValue(killer.getObjectId()) && qs.isMemoState(11))
					{
						qs.setMemoState(12);
						qs.setCond(13, true);
					}
					break;
				}
				case KRUDEL_LIZARDMAN:
				{
					if (qs.isMemoState(13))
					{
						qs.setMemoState(14);
						qs.setCond(16, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == PRIESTESS_PUPINA)
			{
				if ((player.getClassId() == ClassId.CLERIC) || (player.getClassId() == ClassId.SHILLIEN_ORACLE))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30118-03.htm";
					}
					else
					{
						htmltext = "30118-01.html";
					}
				}
				else
				{
					htmltext = "30118-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PRIESTESS_PUPINA:
				{
					if (memoState == 3)
					{
						if (hasQuestItems(player, HUGE_NAIL))
						{
							htmltext = "30118-05.html";
						}
					}
					else if ((memoState >= 1) && (memoState < 3))
					{
						htmltext = "30118-04a.html";
					}
					else if (memoState >= 4)
					{
						htmltext = "30118-07.html";
					}
					break;
				}
				case PREACHER_SLA:
				{
					if (memoState == 4)
					{
						if (hasQuestItems(player, LETTER_OF_INTRODUCTION))
						{
							htmltext = "30666-01.html";
						}
					}
					else if ((memoState >= 11) && (memoState < 18))
					{
						htmltext = "30666-06b.html";
					}
					else if (memoState == 5)
					{
						if (hasQuestItems(player, SLAS_LETTER))
						{
							htmltext = "30666-05.html";
						}
					}
					else if (memoState == 10)
					{
						if (hasQuestItems(player, Ol_MAHUM_MONEY))
						{
							takeItems(player, Ol_MAHUM_MONEY, 1);
							giveItems(player, GREETINGS, 1);
							giveItems(player, LETTER_GREETINGS1, 1);
							giveItems(player, LETTER_GREETINGS2, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
							htmltext = "30666-06.html";
						}
						else
						{
							giveItems(player, GREETINGS, 1);
							giveItems(player, LETTER_GREETINGS1, 1);
							giveItems(player, LETTER_GREETINGS2, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
							htmltext = "30666-06a.html";
						}
					}
					else if (memoState == 18)
					{
						if (hasQuestItems(player, KATARIS_LETTER, KAKANS_LETTER, NYAKURIS_LETTER, RAMUSS_LETTER))
						{
							giveAdena(player, 226528, true);
							giveItems(player, MARK_OF_REFORMER, 1);
							addExpAndSp(player, 1252844, 85972);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30666-07.html";
						}
					}
					break;
				}
				case RAMUS:
				{
					if (memoState == 15)
					{
						if (hasQuestItems(player, LETTER_GREETINGS2) && !hasQuestItems(player, UNDEAD_LIST))
						{
							giveItems(player, UNDEAD_LIST, 1);
							takeItems(player, LETTER_GREETINGS2, 1);
							qs.setMemoState(16);
							qs.setCond(18, true);
							htmltext = "30667-01.html";
						}
					}
					else if (memoState == 16)
					{
						htmltext = "30667-02.html";
					}
					else if (memoState == 17)
					{
						if (hasQuestItems(player, UNDEAD_LIST))
						{
							takeItems(player, UNDEAD_LIST, 1);
							giveItems(player, RAMUSS_LETTER, 1);
							takeItems(player, BONE_FRAGMENT4, 1);
							takeItems(player, BONE_FRAGMENT5, 1);
							takeItems(player, BONE_FRAGMENT6, 1);
							takeItems(player, BONE_FRAGMENT7, 1);
							takeItems(player, BONE_FRAGMENT8, 1);
							qs.setMemoState(18);
							qs.setCond(20, true);
							htmltext = "30667-03.html";
						}
					}
					break;
				}
				case KATARI:
				{
					if ((memoState == 5) || (memoState == 6))
					{
						takeItems(player, SLAS_LETTER, 1);
						qs.setMemoState(6);
						qs.setCond(6, true);
						if (npc.getSummonedNpcCount() < 1)
						{
							Npc pilgrim = addSpawn(OL_MAHUM_PILGRIM, -4015, 40141, -3664, 0, false, 0);
							Npc inspector = addSpawn(OL_MAHUM_INSPECTOR, -4034, 40201, -3665, 0, false, 0);
							((Attackable) inspector).addDamageHate(pilgrim, 99999, 99999);
							inspector.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, pilgrim);
						}
						htmltext = "30668-01.html";
					}
					else if ((memoState == 7) || (memoState == 8))
					{
						if (memoState == 7)
						{
							qs.setMemoState(8);
						}
						qs.setCond(8, true);
						if (npc.getSummonedNpcCount() < 3)
						{
							addSpawn(OL_MAHUM_BETRAYER, -4106, 40174, -3660, 0, false, 0);
						}
						htmltext = "30668-02.html";
					}
					else if (memoState == 9)
					{
						if (hasQuestItems(player, LETTER_OF_BETRAYER))
						{
							giveItems(player, KATARIS_LETTER, 1);
							takeItems(player, LETTER_OF_BETRAYER, 1);
							qs.setMemoState(10);
							qs.setCond(10, true);
							htmltext = "30668-03.html";
						}
					}
					else if (memoState >= 10)
					{
						htmltext = "30668-04.html";
					}
					break;
				}
				case KAKAN:
				{
					if (memoState == 11)
					{
						if (hasQuestItems(player, GREETINGS))
						{
							htmltext = "30669-01.html";
						}
					}
					else if (memoState == 12)
					{
						if (hasQuestItems(player, GREETINGS) && !hasQuestItems(player, KAKANS_LETTER))
						{
							takeItems(player, GREETINGS, 1);
							giveItems(player, KAKANS_LETTER, 1);
							qs.setMemoState(13);
							qs.setCond(14, true);
							htmltext = "30669-04.html";
						}
					}
					break;
				}
				case NYAKURI:
				{
					if (memoState == 13)
					{
						if (hasQuestItems(player, LETTER_GREETINGS1))
						{
							htmltext = "30670-01.html";
						}
					}
					else if (memoState == 14)
					{
						if (hasQuestItems(player, LETTER_GREETINGS1) && !hasQuestItems(player, NYAKURIS_LETTER))
						{
							giveItems(player, NYAKURIS_LETTER, 1);
							takeItems(player, LETTER_GREETINGS1, 1);
							qs.setMemoState(15);
							qs.setCond(17, true);
							htmltext = "30670-04.html";
						}
					}
					break;
				}
				case OL_MAHUM_PILGRIM:
				{
					if (memoState == 7)
					{
						giveItems(player, Ol_MAHUM_MONEY, 1);
						qs.setMemoState(8);
						htmltext = "30732-01.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == PRIESTESS_PUPINA)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case OL_MAHUM_INSPECTOR:
			case CRIMSON_WEREWOLF:
			case KRUDEL_LIZARDMAN:
			case OL_MAHUM_PILGRIM:
			{
				startQuestTimer("DESPAWN", 5000, npc, null, true);
				npc.getVariables().set("SPAWNED", 0);
				break;
			}
			case OL_MAHUM_BETRAYER:
			{
				startQuestTimer("DESPAWN", 5000, npc, null, true);
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MOVE_TO);
				npc.getVariables().set("SPAWNED", 0);
				break;
			}
		}
		return super.onSpawn(npc);
	}
}