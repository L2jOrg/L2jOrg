package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _406_PathToElvenKnight extends Quest
{
	//NPC
	private static final int Sorius = 30327;
	private static final int Kluto = 30317;
	//QuestItems
	private static final int SoriussLetter = 1202;
	private static final int KlutoBox = 1203;
	private static final int TopazPiece = 1205;
	private static final int EmeraldPiece = 1206;
	private static final int KlutosMemo = 1276;
	//Items
	private static final int ElvenKnightBrooch = 1204;
	//MOB
	private static final int TrackerSkeleton = 20035;
	private static final int TrackerSkeletonLeader = 20042;
	private static final int SkeletonScout = 20045;
	private static final int SkeletonBowman = 20051;
	private static final int RagingSpartoi = 20060;
	private static final int OlMahumNovice = 20782;

	public _406_PathToElvenKnight()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Sorius);
		addTalkId(Kluto);

		addKillId(TrackerSkeleton);
		addKillId(TrackerSkeletonLeader);
		addKillId(SkeletonScout);
		addKillId(SkeletonBowman);
		addKillId(RagingSpartoi);
		addKillId(OlMahumNovice);

		addQuestItem(new int[]{
				TopazPiece,
				EmeraldPiece,
				SoriussLetter,
				KlutosMemo,
				KlutoBox
		});

		addLevelCheck("master_sorius_q0406_03.htm", 19);
		addClassIdCheck("master_sorius_q0406_02.htm", 18);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("master_sorius_q0406_05.htm"))
			htmltext = "master_sorius_q0406_05.htm";
		else if(event.equalsIgnoreCase("master_sorius_q0406_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("blacksmith_kluto_q0406_02.htm"))
		{
			st.takeItems(SoriussLetter, -1);
			st.giveItems(KlutosMemo, 1);
			st.setCond(4);
		}
		else
			htmltext = NO_QUEST_DIALOG;

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Sorius:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(ElvenKnightBrooch) > 0)
						htmltext = "master_sorius_q0406_04.htm";
					else
						htmltext = "master_sorius_q0406_01.htm";
				}
				else if (cond == 1)
				{
					if (st.getQuestItemsCount(TopazPiece) == 0)
						htmltext = "master_sorius_q0406_07.htm";
					else
						htmltext = "master_sorius_q0406_08.htm";
				}
				else if (cond == 2)
				{
					st.takeItems(TopazPiece, -1);
					st.giveItems(SoriussLetter, 1);
					htmltext = "master_sorius_q0406_09.htm";
					st.setCond(3);
				}
				else if (cond == 3 || cond == 4 || cond == 5)
					htmltext = "master_sorius_q0406_11.htm";
				else if (cond == 6)
				{
					st.takeItems(KlutoBox, -1);
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(ElvenKnightBrooch, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5087);
						}
					}
					st.finishQuest();
					htmltext = "master_sorius_q0406_10.htm";
				}
			break;

			case Kluto:
				if (cond == 3)
					htmltext = "blacksmith_kluto_q0406_01.htm";
				else if (cond == 4)
				{
					if (st.getQuestItemsCount(EmeraldPiece) == 0)
						htmltext = "blacksmith_kluto_q0406_03.htm";
					else
						htmltext = "blacksmith_kluto_q0406_04.htm";
				}
				else if (cond == 5)
				{
					st.takeItems(EmeraldPiece, -1);
					st.takeItems(KlutosMemo, -1);
					st.giveItems(KlutoBox, 1);
					htmltext = "blacksmith_kluto_q0406_05.htm";
					st.setCond(6);
				}
				else if (cond == 6)
					htmltext = "blacksmith_kluto_q0406_06.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (cond == 1)
		{
			if (npcId != OlMahumNovice)
			{
				st.rollAndGive(TopazPiece, 1, 1, 20, 70);
				if (st.getQuestItemsCount(TopazPiece) >= 20)
					st.setCond(2);
			}
		}
		else if (cond == 4)
		{
			if (npcId == OlMahumNovice)
			{
				st.rollAndGive(EmeraldPiece, 1, 1, 20, 50);
				if (st.getQuestItemsCount(EmeraldPiece) >= 20)
					st.setCond(5);
			}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x13)
			return "master_sorius_q0406_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}
