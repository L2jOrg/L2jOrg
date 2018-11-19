package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.Functions;

//Edited by Evil_dnk
//Tested

public final class _403_PathToRogue extends Quest
{
	//npc
	public final int BEZIQUE = 30379;
	public final int NETI = 30425;
	//mobs
	public final int TRACKER_SKELETON = 20035;
	public final int TRACKER_SKELETON_LEADER = 20042;
	public final int SKELETON_SCOUT = 20045;
	public final int SKELETON_BOWMAN = 20051;
	public final int RUIN_SPARTOI = 20054;
	public final int RAGING_SPARTOI = 20060;
	public final int CATS_EYE_BANDIT = 27038;
	//items
	public final int BEZIQUES_LETTER_ID = 1180;
	public final int SPATOIS_BONES_ID = 1183;
	public final int HORSESHOE_OF_LIGHT_ID = 1184;
	public final int WANTED_BILL_ID = 1185;
	public final int STOLEN_JEWELRY_ID = 1186;
	public final int STOLEN_TOMES_ID = 1187;
	public final int STOLEN_RING_ID = 1188;
	public final int STOLEN_NECKLACE_ID = 1189;
	public final int BEZIQUES_RECOMMENDATION_ID = 1190;
	public final int NETIS_BOW_ID = 1181;
	public final int NETIS_DAGGER_ID = 1182;
	//MobsTable {MOB_ID,CHANCE}
	public final int[][] MobsTable = {
			{
					TRACKER_SKELETON,
					2
			},
			{
					TRACKER_SKELETON_LEADER,
					3
			},
			{
					SKELETON_SCOUT,
					2
			},
			{
					SKELETON_BOWMAN,
					2
			},
			{
					RUIN_SPARTOI,
					8
			},
			{
					RAGING_SPARTOI,
					8
			}
	};

	public final int[] STOLEN_ITEM = {
			STOLEN_JEWELRY_ID,
			STOLEN_TOMES_ID,
			STOLEN_RING_ID,
			STOLEN_NECKLACE_ID
	};

	public _403_PathToRogue()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(BEZIQUE);

		addTalkId(NETI);

		addKillId(CATS_EYE_BANDIT);
		addAttackId(CATS_EYE_BANDIT);

		for(int[] element : MobsTable)
		{
			addKillId(element[0]);
			addAttackId(element[0]);
		}

		addQuestItem(STOLEN_ITEM);
		addQuestItem(new int[]{
				NETIS_BOW_ID,
				NETIS_DAGGER_ID,
				WANTED_BILL_ID,
				HORSESHOE_OF_LIGHT_ID,
				BEZIQUES_LETTER_ID,
				SPATOIS_BONES_ID
		});

		addLevelCheck("captain_bezique_q0403_03.htm", 19);
		addClassIdCheck("captain_bezique_q0403_02.htm", 0);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30379_2"))
		{
			htmltext = "captain_bezique_q0403_05.htm";
		}
		else if(event.equalsIgnoreCase("1"))
		{
			st.setCond(1);
			st.giveItems(BEZIQUES_LETTER_ID, 1);
			htmltext = "captain_bezique_q0403_06.htm";
		}
		else if(event.equalsIgnoreCase("30425_1"))
		{
			st.takeItems(BEZIQUES_LETTER_ID, 1);
			if(st.getQuestItemsCount(NETIS_BOW_ID) < 1)
				st.giveItems(NETIS_BOW_ID, 1);
			if(st.getQuestItemsCount(NETIS_DAGGER_ID) < 1)
				st.giveItems(NETIS_DAGGER_ID, 1);
			st.setCond(2);
			htmltext = "neti_q0403_05.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch(npcId) {
			case BEZIQUE:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(BEZIQUES_RECOMMENDATION_ID) > 0)
						htmltext = "captain_bezique_q0403_04.htm";
					else
						htmltext = "captain_bezique_q0403_01.htm";
				}
				else if (cond == 1)
					htmltext = "captain_bezique_q0403_07.htm";
				else if (cond == 2 || cond == 3)
					htmltext = "captain_bezique_q0403_10.htm";
				else if (cond == 4)
				{
					htmltext = "captain_bezique_q0403_08.htm";
					st.takeItems(HORSESHOE_OF_LIGHT_ID, 1);
					st.giveItems(WANTED_BILL_ID, 1);
					st.setCond(5);
				}
				else if (cond == 5)
					htmltext = "captain_bezique_q0403_11.htm";
				if (cond == 6)
				{
					htmltext = "captain_bezique_q0403_09.htm";
					st.takeItems(NETIS_BOW_ID, 1);
					st.takeItems(NETIS_DAGGER_ID, 1);
					st.takeItems(WANTED_BILL_ID, 1);
					for (int i : STOLEN_ITEM)
						st.takeItems(i, -1);
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(BEZIQUES_RECOMMENDATION_ID, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5087);
						}
					}
					st.finishQuest();
				}
			break;

			case NETI:
				if (cond == 1)
					htmltext = "neti_q0403_01.htm";
				else if (cond == 2)
					htmltext = "neti_q0403_06.htm";
				else if (cond == 3)
				{
					htmltext = "neti_q0403_07.htm";
					st.takeItems(SPATOIS_BONES_ID, -1);
					st.giveItems(HORSESHOE_OF_LIGHT_ID, 1);
					st.setCond(4);
				}
				else if (cond == 4)
					htmltext = "neti_q0403_08.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int netis_cond = st.getInt("netis_cond");
		if(netis_cond == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_BOW_ID || st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == NETIS_DAGGER_ID)
		{
			Functions.npcSay(npc, "I must do something about this shameful incident...");
			switch(cond)
			{
				case 2:
					for(int[] element : MobsTable)
						if(npcId == element[0] && Rnd.chance(10 * element[1]) && st.getQuestItemsCount(SPATOIS_BONES_ID) < 10)
						{
							st.giveItems(SPATOIS_BONES_ID, 1, true);
							if(st.getQuestItemsCount(SPATOIS_BONES_ID) >= 10)
								st.setCond(3);
							else
								st.playSound(SOUND_ITEMGET);
						}
					break;
				case 5:
					if(npcId == CATS_EYE_BANDIT)
						if(st.getQuestItemsCount(WANTED_BILL_ID) > 0)
						{
							if(st.getQuestItemsCount(STOLEN_ITEM[1]) == 0)
							{
								st.giveItems(STOLEN_ITEM[1], 1);
								if(st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) < 4)
									st.playSound(SOUND_ITEMGET);
								else
									st.setCond(6);
							}
							else if(st.getQuestItemsCount(STOLEN_ITEM[2]) == 0)
							{
								st.giveItems(STOLEN_ITEM[2], 1);
								if(st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) < 4)
									st.playSound(SOUND_ITEMGET);
								else
									st.setCond(6);
							}
							else if(st.getQuestItemsCount(STOLEN_ITEM[3]) == 0)
							{
								st.giveItems(STOLEN_ITEM[3], 1);
								if(st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) < 4)
									st.playSound(SOUND_ITEMGET);
								else
									st.setCond(6);
							}
							else if(st.getQuestItemsCount(STOLEN_ITEM[0]) == 0)
							{
								st.giveItems(STOLEN_ITEM[0], 1);
								if(st.getQuestItemsCount(STOLEN_JEWELRY_ID) + st.getQuestItemsCount(STOLEN_TOMES_ID) + st.getQuestItemsCount(STOLEN_RING_ID) + st.getQuestItemsCount(STOLEN_NECKLACE_ID) < 4)
									st.playSound(SOUND_ITEMGET);
								else
									st.setCond(6);
							}
						}
					break;
			}
		}
		return null;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		int netis_cond = st.getInt("netis_cond");
		if(st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_BOW_ID && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != NETIS_DAGGER_ID)
			st.set("netis_cond", "0");
		else if(netis_cond == 0)
		{
			st.set("netis_cond", "1");
			Functions.npcSay(npc, "You childish fool, do you think you can catch me?");
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x07)
			return "captain_bezique_q0403_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}