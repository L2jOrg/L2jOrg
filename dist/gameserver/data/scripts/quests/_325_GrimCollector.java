package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _325_GrimCollector extends Quest
{
	int ZOMBIE_HEAD = 1350;
	int ZOMBIE_HEART = 1351;
	int ZOMBIE_LIVER = 1352;
	int SKULL = 1353;
	int RIB_BONE = 1354;
	int SPINE = 1355;
	int ARM_BONE = 1356;
	int THIGH_BONE = 1357;
	int COMPLETE_SKELETON = 1358;
	int ANATOMY_DIAGRAM = 1349;

	public _325_GrimCollector()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30336);

		addTalkId(30336);
		addTalkId(30342);
		addTalkId(30434);

		addKillId(20026);
		addKillId(20029);
		addKillId(20035);
		addKillId(20042);
		addKillId(20045);
		addKillId(20457);
		addKillId(20458);
		addKillId(20051);
		addKillId(20514);
		addKillId(20515);

		addQuestItem(new int[]{
				ZOMBIE_HEAD,
				ZOMBIE_HEART,
				ZOMBIE_LIVER,
				SKULL,
				RIB_BONE,
				SPINE,
				ARM_BONE,
				THIGH_BONE,
				COMPLETE_SKELETON,
				ANATOMY_DIAGRAM
		});

		addLevelCheck("guard_curtiz_q0325_01.htm", 15);
	}

	private long pieces(QuestState st)
	{
		return st.getQuestItemsCount(ZOMBIE_HEAD) + st.getQuestItemsCount(SPINE) + st.getQuestItemsCount(ARM_BONE) + st.getQuestItemsCount(ZOMBIE_HEART) + st.getQuestItemsCount(ZOMBIE_LIVER) + st.getQuestItemsCount(SKULL) + st.getQuestItemsCount(RIB_BONE) + st.getQuestItemsCount(THIGH_BONE) + st.getQuestItemsCount(COMPLETE_SKELETON);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("guard_curtiz_q0325_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("samed_q0325_03.htm"))
			st.giveItems(ANATOMY_DIAGRAM, 1);
		else if(event.equalsIgnoreCase("samed_q0325_06.htm"))
		{
			long reward = 8 * st.getQuestItemsCount(ZOMBIE_HEAD) + 5 * st.getQuestItemsCount(ZOMBIE_HEART) + 5 * st.getQuestItemsCount(ZOMBIE_LIVER) + 25 * st.getQuestItemsCount(SKULL) + 15 * st.getQuestItemsCount(RIB_BONE) + 5 * st.getQuestItemsCount(SPINE) + 5 * st.getQuestItemsCount(ARM_BONE) + 5 * st.getQuestItemsCount(THIGH_BONE) + 100 * st.getQuestItemsCount(COMPLETE_SKELETON);
			if(pieces(st) > 0)
			{
				st.giveItems(ADENA_ID, reward, 1000);
				st.takeItems(ZOMBIE_HEAD, -1);
				st.takeItems(ZOMBIE_HEART, -1);
				st.takeItems(ZOMBIE_LIVER, -1);
				st.takeItems(SKULL, -1);
				st.takeItems(RIB_BONE, -1);
				st.takeItems(SPINE, -1);
				st.takeItems(ARM_BONE, -1);
				st.takeItems(THIGH_BONE, -1);
				st.takeItems(COMPLETE_SKELETON, -1);
			}
			st.takeItems(ANATOMY_DIAGRAM, -1);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("samed_q0325_07.htm") && pieces(st) > 0)
		{
			long reward = 8 * st.getQuestItemsCount(ZOMBIE_HEAD) + 5 * st.getQuestItemsCount(ZOMBIE_HEART) + 5 * st.getQuestItemsCount(ZOMBIE_LIVER) + 25 * st.getQuestItemsCount(SKULL) + 15 * st.getQuestItemsCount(RIB_BONE) + 5 * st.getQuestItemsCount(SPINE) + 5 * st.getQuestItemsCount(ARM_BONE) + 5 * st.getQuestItemsCount(THIGH_BONE) + 100 * st.getQuestItemsCount(COMPLETE_SKELETON);
			st.giveItems(ADENA_ID, reward, 1000);
			st.takeItems(ZOMBIE_HEAD, -1);
			st.takeItems(ZOMBIE_HEART, -1);
			st.takeItems(ZOMBIE_LIVER, -1);
			st.takeItems(SKULL, -1);
			st.takeItems(RIB_BONE, -1);
			st.takeItems(SPINE, -1);
			st.takeItems(ARM_BONE, -1);
			st.takeItems(THIGH_BONE, -1);
			st.takeItems(COMPLETE_SKELETON, -1);
		}
		else if(event.equalsIgnoreCase("samed_q0325_09.htm"))
		{
			st.giveItems(ADENA_ID, 100 * st.getQuestItemsCount(COMPLETE_SKELETON));
			st.takeItems(COMPLETE_SKELETON, -1);
		}
		else if(event.equalsIgnoreCase("varsak_q0325_03.htm"))
			if(st.getQuestItemsCount(SPINE) != 0 && st.getQuestItemsCount(ARM_BONE) != 0 && st.getQuestItemsCount(SKULL) != 0 && st.getQuestItemsCount(RIB_BONE) != 0 && st.getQuestItemsCount(THIGH_BONE) != 0)
			{
				st.takeItems(SPINE, 1);
				st.takeItems(SKULL, 1);
				st.takeItems(ARM_BONE, 1);
				st.takeItems(RIB_BONE, 1);
				st.takeItems(THIGH_BONE, 1);
				if(Rnd.chance(80))
					st.giveItems(COMPLETE_SKELETON, 1);
				else
					htmltext = "varsak_q0325_04.htm";
			}
			else
				htmltext = "varsak_q0325_02.htm";
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
			case 30336:
				if (cond == 0)
					htmltext = "guard_curtiz_q0325_02.htm";
				else if (cond == 1)
				{
					if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
						htmltext = "guard_curtiz_q0325_04.htm";
					else
						htmltext = "guard_curtiz_q0325_05.htm";
				}
				break;

			case 30434:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
						htmltext = "samed_q0325_01.htm";
					else if (st.getQuestItemsCount(ANATOMY_DIAGRAM) != 0 && pieces(st) == 0)
						htmltext = "samed_q0325_04.htm";
					else if (st.getQuestItemsCount(ANATOMY_DIAGRAM) != 0 && pieces(st) > 0 && st.getQuestItemsCount(COMPLETE_SKELETON) == 0)
						htmltext = "samed_q0325_05.htm";
					else if (st.getQuestItemsCount(ANATOMY_DIAGRAM) != 0 && pieces(st) > 0 && st.getQuestItemsCount(COMPLETE_SKELETON) > 0)
						htmltext = "samed_q0325_08.htm";
				}
				break;

			case 30342:
				if (cond == 1 && st.getQuestItemsCount(ANATOMY_DIAGRAM) > 0)
					htmltext = "varsak_q0325_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (st.getCond() == 1)
		{
			if (st.getQuestItemsCount(ANATOMY_DIAGRAM) == 0)
				return null;

			int n = Rnd.get(100);
			if (npcId == 20026)
			{
				if (n < 90)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 40)
						st.giveItems(ZOMBIE_HEAD, 1, true);
					else if (n < 60)
						st.giveItems(ZOMBIE_HEART, 1, true);
					else
						st.giveItems(ZOMBIE_LIVER, 1, true);
				}
			}
			else if (npcId == 20029)
			{
				st.playSound(SOUND_ITEMGET);
				if (n < 44)
					st.giveItems(ZOMBIE_HEAD, 1, true);
				else if (n < 66)
					st.giveItems(ZOMBIE_HEART, 1, true);
				else
					st.giveItems(ZOMBIE_LIVER, 1, true);
			}
			else if (npcId == 20035)
			{
				if (n < 79)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 5)
						st.giveItems(SKULL, 1, true);
					else if (n < 15)
						st.giveItems(RIB_BONE, 1, true);
					else if (n < 29)
						st.giveItems(SPINE, 1, true);
					else
						st.giveItems(THIGH_BONE, 1, true);
				}
			}
			else if (npcId == 20042)
			{
				if (n < 86)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 6)
						st.giveItems(SKULL, 1, true);
					else if (n < 19)
						st.giveItems(RIB_BONE, 1, true);
					else if (n < 69)
						st.giveItems(ARM_BONE, 1, true);
					else
						st.giveItems(THIGH_BONE, 1, true);
				}
			}
			else if (npcId == 20045)
			{
				if (n < 97)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 9)
						st.giveItems(SKULL, 1, true);
					else if (n < 59)
						st.giveItems(SPINE, 1, true);
					else if (n < 77)
						st.giveItems(ARM_BONE, 1, true);
					else
						st.giveItems(THIGH_BONE, 1, true);
				}
			}
			else if (npcId == 20051)
			{
				if (n < 99)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 9)
						st.giveItems(SKULL, 1, true);
					else if (n < 59)
						st.giveItems(RIB_BONE, 1, true);
					else if (n < 79)
						st.giveItems(SPINE, 1, true);
					else
						st.giveItems(ARM_BONE, 1, true);
				}
			}
			else if (npcId == 20514)
			{
				if (n < 51)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 2)
						st.giveItems(SKULL, 1, true);
					else if (n < 8)
						st.giveItems(RIB_BONE, 1, true);
					else if (n < 17)
						st.giveItems(SPINE, 1, true);
					else if (n < 18)
						st.giveItems(ARM_BONE, 1, true);
					else
						st.giveItems(THIGH_BONE, 1, true);
				}
			}
			else if (npcId == 20515)
			{
				if (n < 60)
				{
					st.playSound(SOUND_ITEMGET);
					if (n < 3)
						st.giveItems(SKULL, 1, true);
					else if (n < 11)
						st.giveItems(RIB_BONE, 1, true);
					else if (n < 22)
						st.giveItems(SPINE, 1, true);
					else if (n < 24)
						st.giveItems(ARM_BONE, 1, true);
					else
						st.giveItems(THIGH_BONE, 1, true);
				}
			}
			else if (npcId == 20457 || npcId == 20458)
			{
				st.playSound(SOUND_ITEMGET);
				if (n < 42)
					st.giveItems(ZOMBIE_HEAD, 1, true);
				else if (n < 67)
					st.giveItems(ZOMBIE_HEART, 1, true);
				else
					st.giveItems(ZOMBIE_LIVER, 1, true);
			}
		}
		return null;
	}
}