package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _410_PathToPalusKnight extends Quest
{
	//npc
	public final int VIRGIL = 30329;
	public final int KALINTA = 30422;
	//mobs
	public final int POISON_SPIDER = 20038;
	public final int ARACHNID_TRACKER = 20043;
	public final int LYCANTHROPE = 20049;
	//items
	public final int PALLUS_TALISMAN_ID = 1237;
	public final int LYCANTHROPE_SKULL_ID = 1238;
	public final int VIRGILS_LETTER_ID = 1239;
	public final int MORTE_TALISMAN_ID = 1240;
	public final int PREDATOR_CARAPACE_ID = 1241;
	public final int TRIMDEN_SILK_ID = 1242;
	public final int COFFIN_ETERNAL_REST_ID = 1243;
	public final int GAZE_OF_ABYSS_ID = 1244;

	public _410_PathToPalusKnight()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(VIRGIL);

		addTalkId(KALINTA);

		addKillId(POISON_SPIDER);
		addKillId(ARACHNID_TRACKER);
		addKillId(LYCANTHROPE);

		addQuestItem(new int[]{
				PALLUS_TALISMAN_ID,
				VIRGILS_LETTER_ID,
				COFFIN_ETERNAL_REST_ID,
				MORTE_TALISMAN_ID,
				PREDATOR_CARAPACE_ID,
				TRIMDEN_SILK_ID,
				LYCANTHROPE_SKULL_ID
		});

		addLevelCheck("master_virgil_q0410_02.htm", 19);
		addClassIdCheck("master_virgil_q0410_03.htm", 31);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			st.setCond(1);
			htmltext = "master_virgil_q0410_06.htm";
			st.giveItems(PALLUS_TALISMAN_ID, 1);
		}
		else if(event.equalsIgnoreCase("410_1"))
		{
			htmltext = "master_virgil_q0410_05.htm";
		}
		else if(event.equalsIgnoreCase("30329_2"))
		{
			htmltext = "master_virgil_q0410_10.htm";
			st.takeItems(PALLUS_TALISMAN_ID, -1);
			st.takeItems(LYCANTHROPE_SKULL_ID, -1);
			st.giveItems(VIRGILS_LETTER_ID, 1);
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("30422_1"))
		{
			htmltext = "kalinta_q0410_02.htm";
			st.takeItems(VIRGILS_LETTER_ID, -1);
			st.giveItems(MORTE_TALISMAN_ID, 1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("30422_2"))
		{
			htmltext = "kalinta_q0410_06.htm";
			st.takeItems(MORTE_TALISMAN_ID, -1);
			st.takeItems(TRIMDEN_SILK_ID, -1);
			st.takeItems(PREDATOR_CARAPACE_ID, -1);
			st.giveItems(COFFIN_ETERNAL_REST_ID, 1);
			st.setCond(6);
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
			case VIRGIL:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(GAZE_OF_ABYSS_ID) > 0)
						htmltext = "master_virgil_q0410_04.htm";
					else
						htmltext = "master_virgil_q0410_01.htm";
				}
				else if (cond == 1)
				{
					if (st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) > 0 && st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) < 13)
						htmltext = "master_virgil_q0410_08.htm";
					else
						htmltext = "master_virgil_q0410_07.htm";
				}
				else if (cond == 2)
						htmltext = "master_virgil_q0410_09.htm";
				else if (cond > 2 && cond < 6)
					htmltext = "master_virgil_q0410_12.htm";
				else if (cond == 6)
				{
					htmltext = "master_virgil_q0410_11.htm";
					st.takeItems(COFFIN_ETERNAL_REST_ID, -1);
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE)) {
						st.giveItems(GAZE_OF_ABYSS_ID, 1);
						if (!st.getPlayer().getVarBoolean("prof1")) {
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5087);
						}
					}
					st.finishQuest();
				}
			break;

			case KALINTA:
				if (cond == 3)
					htmltext = "kalinta_q0410_01.htm";
				else if (cond == 4)
				{
					if (st.getQuestItemsCount(TRIMDEN_SILK_ID) < 1 && st.getQuestItemsCount(PREDATOR_CARAPACE_ID) < 1)
						htmltext = "kalinta_q0410_03.htm";
					else
						htmltext = "kalinta_q0410_04.htm";
				}
				else if (cond == 5)
					htmltext = "kalinta_q0410_05.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1)
		{
		if(npcId == LYCANTHROPE)
			{
				st.rollAndGive(LYCANTHROPE_SKULL_ID, 1, 1, 13, 100);
				if(st.getQuestItemsCount(LYCANTHROPE_SKULL_ID) > 12)
					st.setCond(2);
			}
		}

		if(cond == 4)
		{
			if (npcId == POISON_SPIDER)
				st.rollAndGive(PREDATOR_CARAPACE_ID, 1, 1, 1, 100);
			if (npcId == ARACHNID_TRACKER)
				st.rollAndGive(TRIMDEN_SILK_ID, 1, 1, 5, 100);
			if (st.getQuestItemsCount(TRIMDEN_SILK_ID) > 4 && st.getQuestItemsCount(PREDATOR_CARAPACE_ID) > 0)
				st.setCond(5);
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x20)
			return "master_virgil_q0410_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}