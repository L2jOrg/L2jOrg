package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _411_PathToAssassin extends Quest
{
	//npc
	public final int TRISKEL = 30416;
	public final int LEIKAN = 30382;
	public final int ARKENIA = 30419;
	//mobs
	public final int MOONSTONE_BEAST = 20369;
	public final int CALPICO = 27036;
	//items
	public final int SHILENS_CALL_ID = 1245;
	public final int ARKENIAS_LETTER_ID = 1246;
	public final int LEIKANS_NOTE_ID = 1247;
	public final int ONYX_BEASTS_MOLAR_ID = 1248;
	public final int LEIKANS_KNIFE_ID = 1249;
	public final int SHILENS_TEARS_ID = 1250;
	public final int ARKENIA_RECOMMEND_ID = 1251;
	public final int IRON_HEART_ID = 1252;

	public _411_PathToAssassin()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(TRISKEL);

		addTalkId(LEIKAN);
		addTalkId(ARKENIA);

		addKillId(MOONSTONE_BEAST);
		addKillId(CALPICO);

		addQuestItem(new int[]{
				SHILENS_CALL_ID,
				LEIKANS_NOTE_ID,
				LEIKANS_KNIFE_ID,
				ARKENIA_RECOMMEND_ID,
				ARKENIAS_LETTER_ID,
				ONYX_BEASTS_MOLAR_ID,
				SHILENS_TEARS_ID
		});

		addLevelCheck("triskel_q0411_03.htm", 19);
		addClassIdCheck("triskel_q0411_02.htm", 31);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			st.setCond(1);
			st.giveItems(SHILENS_CALL_ID, 1);
			htmltext = "triskel_q0411_05.htm";
		}
		else if(event.equalsIgnoreCase("30419_1"))
		{
			htmltext = "arkenia_q0411_05.htm";
			st.takeItems(SHILENS_CALL_ID, -1);
			st.giveItems(ARKENIAS_LETTER_ID, 1);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30382_1"))
		{
			htmltext = "guard_leikan_q0411_03.htm";
			st.takeItems(ARKENIAS_LETTER_ID, -1);
			st.giveItems(LEIKANS_NOTE_ID, 1);
			st.setCond(3);
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
			case TRISKEL:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(IRON_HEART_ID) > 0)
						htmltext = "triskel_q0411_04.htm";
					else
						htmltext = "triskel_q0411_01.htm";
				}
				else if (cond == 1)
					htmltext = "triskel_q0411_11.htm";
				else if (cond == 2)
					htmltext = "triskel_q0411_07.htm";
				else if (cond > 2 && cond < 5)
						htmltext = "triskel_q0411_08.htm";
					else if (cond > 4 && cond < 7)
						if (st.getQuestItemsCount(SHILENS_TEARS_ID) < 1)
							htmltext = "triskel_q0411_09.htm";
						else
							htmltext = "triskel_q0411_10.htm";
				else if (cond == 7)
				{
					htmltext = "triskel_q0411_06.htm";
					st.takeItems(ARKENIA_RECOMMEND_ID, -1);
						if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
						{
							st.giveItems(IRON_HEART_ID, 1);
								if (!st.getPlayer().getVarBoolean("prof1"))
								{
									st.getPlayer().setVar("prof1", "1", -1);
									st.addExpAndSp(80314, 5087);
								}
						}
					st.finishQuest();
				}
				break;

			case ARKENIA:
				if (cond == 1)
					htmltext = "arkenia_q0411_01.htm";
				else if (cond == 2)
					htmltext = "arkenia_q0411_07.htm";
				else if (cond > 2 && cond < 5)
					htmltext = "arkenia_q0411_10.htm";
				else if (cond == 5)
					htmltext = "arkenia_q0411_11.htm";
				else if (cond == 6)
				{
					htmltext = "arkenia_q0411_08.htm";
					st.takeItems(SHILENS_TEARS_ID, -1);
					st.takeItems(LEIKANS_KNIFE_ID, -1);
					st.giveItems(ARKENIA_RECOMMEND_ID, 1);
					st.setCond(7);
				}
				else if (cond == 7)
					htmltext = "arkenia_q0411_09.htm";
				break;

			case LEIKAN:
				if (cond == 2)
					htmltext = "guard_leikan_q0411_01.htm";
				else if (cond == 3)
				{
					if (st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 1)
						htmltext = "guard_leikan_q0411_05.htm";
					else
						htmltext = "guard_leikan_q0411_06.htm";
				}
				else if (cond == 4)
				{
					htmltext = "guard_leikan_q0411_07.htm";
					st.takeItems(ONYX_BEASTS_MOLAR_ID, -1);
					st.takeItems(LEIKANS_NOTE_ID, -1);
					st.giveItems(LEIKANS_KNIFE_ID, 1);
					st.setCond(5);
				}
				else if (cond == 5)
					htmltext = "guard_leikan_q0411_09.htm";
				else if (cond == 6)
					htmltext = "guard_leikan_q0411_08.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (cond == 3)
		{
			if (npcId == MOONSTONE_BEAST)
			{
				st.rollAndGive(ONYX_BEASTS_MOLAR_ID, 1, 1, 10, 100);
				if (st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) > 9)
					st.setCond(4);
			}
		}
		else if (cond == 5)
		{
			if(npcId == CALPICO)
			{
				st.giveItems(SHILENS_TEARS_ID, 1);
				st.setCond(6);
			}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x23)
			return "triskel_q0411_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}