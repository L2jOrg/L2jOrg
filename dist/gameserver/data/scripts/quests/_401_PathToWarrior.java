package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested


public final class _401_PathToWarrior extends Quest
{
	final int AURON = 30010;
	final int SIMPLON = 30253;

	int TRACKER_SKELETON = 20035;
	int POISON_SPIDER = 20038;
	int TRACKER_SKELETON_LD = 20042;
	int ARACHNID_TRACKER = 20043;

	int EINS_LETTER_ID = 1138;
	int WARRIOR_GUILD_MARK_ID = 1139;
	int RUSTED_BRONZE_SWORD1_ID = 1140;
	int RUSTED_BRONZE_SWORD2_ID = 1141;
	int SIMPLONS_LETTER_ID = 1143;
	int POISON_SPIDER_LEG2_ID = 1144;
	int MEDALLION_OF_WARRIOR_ID = 1145;
	int RUSTED_BRONZE_SWORD3_ID = 1142;

	public _401_PathToWarrior()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(AURON);

		addTalkId(SIMPLON);

		addKillId(TRACKER_SKELETON);
		addKillId(POISON_SPIDER);
		addKillId(TRACKER_SKELETON_LD);
		addKillId(ARACHNID_TRACKER);

		addQuestItem(new int[]{
				SIMPLONS_LETTER_ID,
				RUSTED_BRONZE_SWORD2_ID,
				EINS_LETTER_ID,
				WARRIOR_GUILD_MARK_ID,
				RUSTED_BRONZE_SWORD1_ID,
				POISON_SPIDER_LEG2_ID,
				RUSTED_BRONZE_SWORD3_ID
		});

		addLevelCheck("ein_q0401_02.htm", 19);
		addClassIdCheck("ein_q0401_03.htm", 0);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("401_1"))
			htmltext = "ein_q0401_05.htm";
		else if(event.equalsIgnoreCase("401_2"))
			htmltext = "ein_q0401_10.htm";
		else if(event.equalsIgnoreCase("401_3"))
		{
			htmltext = "ein_q0401_11.htm";
			st.takeItems(SIMPLONS_LETTER_ID, 1);
			st.takeItems(RUSTED_BRONZE_SWORD2_ID, 1);
			st.giveItems(RUSTED_BRONZE_SWORD3_ID, 1);
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("1"))
		{
			if(st.getQuestItemsCount(EINS_LETTER_ID) == 0)
			{
				st.setCond(1);
				st.giveItems(EINS_LETTER_ID, 1);
				htmltext = "ein_q0401_06.htm";
			}
		}
		else if(event.equalsIgnoreCase("30253_1"))
		{
			htmltext = "trader_simplon_q0401_02.htm";
			st.takeItems(EINS_LETTER_ID, 1);
			st.giveItems(WARRIOR_GUILD_MARK_ID, 1);
			st.setCond(2);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		  switch (npcId)
		  {
			  case AURON:
				  if (cond == 0)
				  {
					  if (st.getQuestItemsCount(MEDALLION_OF_WARRIOR_ID) > 0)
						  htmltext = "ein_q0401_04.htm";
					  else
						  htmltext = "ein_q0401_01.htm";
				  }
				  else if (cond == 1)
					  htmltext = "ein_q0401_07.htm";
				  else if (st.getQuestItemsCount(WARRIOR_GUILD_MARK_ID) >= 1)
					  htmltext = "ein_q0401_08.htm";
				  else if (cond == 4)
					  htmltext = "ein_q0401_09.htm";
				  else if (cond == 5)
						  htmltext = "ein_q0401_12.htm";
				  else if (cond == 6)
					  {
						  st.takeItems(POISON_SPIDER_LEG2_ID, -1);
						  st.takeItems(RUSTED_BRONZE_SWORD3_ID, -1);
						  if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
						  {
							  st.giveItems(MEDALLION_OF_WARRIOR_ID, 1);
							  if (!st.getPlayer().getVarBoolean("prof1"))
							  {
								  st.getPlayer().setVar("prof1", "1", -1);
								  st.addExpAndSp(80314, 5087);
							  }
						  }
						  htmltext = "ein_q0401_13.htm";
						  st.finishQuest();
					  }
			  break;

			  case SIMPLON:
				  if (cond == 1)
					  htmltext = "trader_simplon_q0401_01.htm";
				  else if (cond == 2)
				  {
					  if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 1)
						  htmltext = "trader_simplon_q0401_03.htm";
					  else if (st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) < 10)
						  htmltext = "trader_simplon_q0401_04.htm";
				  }
				  else if (cond == 3)
					{
						  st.takeItems(WARRIOR_GUILD_MARK_ID, -1);
						  st.takeItems(RUSTED_BRONZE_SWORD1_ID, -1);
						  st.giveItems(RUSTED_BRONZE_SWORD2_ID, 1);
						  st.giveItems(SIMPLONS_LETTER_ID, 1);
						  st.setCond(4);
						  htmltext = "trader_simplon_q0401_05.htm";
					}
				  else if (cond == 4)
					  htmltext = "trader_simplon_q0401_06.htm";
			  break;
		  }

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 2)
		{
			if(npcId == TRACKER_SKELETON || npcId == TRACKER_SKELETON_LD)
			{
				st.rollAndGive(RUSTED_BRONZE_SWORD1_ID, 1, 1, 10, 100);
				if(st.getQuestItemsCount(RUSTED_BRONZE_SWORD1_ID) >= 10)
					st.setCond(3);
			}
		}

		else if (cond == 5 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == RUSTED_BRONZE_SWORD3_ID)
		{
			if (npcId == ARACHNID_TRACKER || npcId == POISON_SPIDER)
			{
					st.rollAndGive(POISON_SPIDER_LEG2_ID, 1, 1, 20 , 100);
					if (st.getQuestItemsCount(POISON_SPIDER_LEG2_ID) >= 20)
						st.setCond(6);
			}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x01)
			return "ein_q0401_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}