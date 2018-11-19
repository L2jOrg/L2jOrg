package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _333_BlackLionHunt extends Quest
{
	//Technical relatet Items
	private int BLACK_LION_MARK = 1369;

	//Drops
	private int UNDEAD_ASH = 3848;
	private int BLOODY_AXE_INSIGNIAS = 3849;
	private int DELU_FANG = 3850;
	private int STAKATO_TALONS = 3851;
	private int SOPHIAS_LETTER1 = 3671;
	private int SOPHIAS_LETTER2 = 3672;
	private int SOPHIAS_LETTER3 = 3673;
	private int SOPHIAS_LETTER4 = 3674;

	//Rewards
	private int LIONS_CLAW = 3675;
	private int GUILD_COIN = 3677;
	private int ALACRITY_POTION = 735;
	private int SOULSHOT_D = 1463;
	private int SPIRITSHOT_D = 2510;
	private int HEALING_POTION = 1061;

	// NPC
	private final int Sophya = 30735;

	int[][] DROPLIST = {
			//Execturion Ground - Part 1
			{
					20160,
					1,
					1,
					67,
					29,
					UNDEAD_ASH
			},
			//Neer Crawler
			{
					20171,
					1,
					1,
					76,
					31,
					UNDEAD_ASH
			},
			//pecter
			{
					20197,
					1,
					1,
					89,
					25,
					UNDEAD_ASH
			},
			//Sorrow Maiden
			{
					20200,
					1,
					1,
					60,
					28,
					UNDEAD_ASH
			},
			//Strain
			{
					20201,
					1,
					1,
					70,
					29,
					UNDEAD_ASH
			},
			//Ghoul
			{
					20202,
					1,
					0,
					60,
					24,
					UNDEAD_ASH
			},
			//Dead Seeker (not official Monster for this Quest)
			{
					20198,
					1,
					1,
					60,
					35,
					UNDEAD_ASH
			},
			//Neer Ghoul Berserker
			//Fortress of Resistance - Part 2
			{
					20207,
					2,
					1,
					69,
					29,
					BLOODY_AXE_INSIGNIAS
			},
			//Ol Mahum Guerilla
			{
					20208,
					2,
					1,
					67,
					32,
					BLOODY_AXE_INSIGNIAS
			},
			//Ol Mahum Raider
			{
					20209,
					2,
					1,
					62,
					33,
					BLOODY_AXE_INSIGNIAS
			},
			//Ol Mahum Marksman
			{
					20210,
					2,
					1,
					78,
					23,
					BLOODY_AXE_INSIGNIAS
			},
			//Ol Mahum Sergeant
			{
					20211,
					2,
					1,
					71,
					22,
					BLOODY_AXE_INSIGNIAS
			},
			//Ol Mahum Captain
			//Delu Lizzardmans near Giran - Part 3
			{
					20251,
					3,
					1,
					70,
					30,
					DELU_FANG
			},
			//Delu Lizardman
			{
					20252,
					3,
					1,
					67,
					28,
					DELU_FANG
			},
			//Delu Lizardman Scout
			{
					20253,
					3,
					1,
					65,
					26,
					DELU_FANG
			},
			//Delu Lizardman Warrior
			{
					27151,
					3,
					1,
					69,
					31,
					DELU_FANG
			},
			//Delu Lizardman Headhunter
			{
					20781,
					3,
					1,
					69,
					31,
					DELU_FANG
			},
			//Delu Lizardman Shaman
			{
					21104,
					3,
					1,
					69,
					31,
					DELU_FANG
			},
			//Delu Lizardman Shaman
			{
					21105,
					3,
					1,
					69,
					31,
					DELU_FANG
			},
			//Delu Lizardman Elite
			{
					21107,
					3,
					1,
					69,
					31,
					DELU_FANG
			},
			//Delu Lizardman Commander
			//Cruma Area - Part 4
			{
					20157,
					4,
					1,
					66,
					32,
					STAKATO_TALONS
			},
			//Marsh Stakato
			{
					20230,
					4,
					1,
					68,
					26,
					STAKATO_TALONS
			},
			//Marsh Stakato Worker
			{
					20232,
					4,
					1,
					67,
					28,
					STAKATO_TALONS
			},
			//Marsh Stakato Soldier
			{
					20234,
					4,
					1,
					69,
					32,
					STAKATO_TALONS
			},
			//Marsh Stakato Drone
			{
					27152,
					4,
					1,
					69,
					32,
					STAKATO_TALONS
			}
			//Marsh Stakato Marquess
	};

	public _333_BlackLionHunt()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(Sophya);


		for(int i = 0; i < DROPLIST.length; i++)
			addKillId(DROPLIST[i][0]);

		addQuestItem(LIONS_CLAW, GUILD_COIN, UNDEAD_ASH, BLOODY_AXE_INSIGNIAS, DELU_FANG, STAKATO_TALONS, SOPHIAS_LETTER1, SOPHIAS_LETTER2, SOPHIAS_LETTER3, SOPHIAS_LETTER4);

		addLevelCheck("sophia_q0333_01.htm", 25/*, 39*/);
		addItemHaveCheck("sophia_q0333_02.htm", BLACK_LION_MARK, 1); 
	}

	public void giveRewards(QuestState st, int item, long count)
	{
		long reward = 10 * count;
		st.giveItems(ADENA_ID, reward, 1000);
		st.takeItems(item, count);
		if(count >= 20 && count  <= 49)
			st.giveItems(LIONS_CLAW, 1, true);
		else if(count >= 50 && count  <= 99)
			st.giveItems(LIONS_CLAW, 2, true);
		else if(count >= 100)
			st.giveItems(LIONS_CLAW, 3, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int part = st.getInt("part");
		if(event.equalsIgnoreCase("start"))
		{
			st.setCond(1);
			return "sophia_q0333_04.htm";
		}
		else if(event.equalsIgnoreCase("p1_t"))
		{
			st.set("part", "1");
			st.giveItems(SOPHIAS_LETTER1, 1);
			return "sophia_q0333_10.htm";
		}
		else if(event.equalsIgnoreCase("p2_t"))
		{
			st.set("part", "2");
			st.giveItems(SOPHIAS_LETTER2, 1);
			return "sophia_q0333_11.htm";
		}
		else if(event.equalsIgnoreCase("p3_t"))
		{
			st.set("part", "3");
			st.giveItems(SOPHIAS_LETTER3, 1);
			return "sophia_q0333_12.htm";
		}
		else if(event.equalsIgnoreCase("p4_t"))
		{
			st.set("part", "4");
			st.giveItems(SOPHIAS_LETTER4, 1);
			return "sophia_q0333_13.htm";
		}
		else if(event.equalsIgnoreCase("exit"))
		{
			st.finishQuest();
			return "sophia_q0333_26.htm";
		}
		else if(event.equalsIgnoreCase("continue"))
		{
			long claw = st.getQuestItemsCount(LIONS_CLAW);
			if(claw > 9)
			{
				while(claw > 9)
				{
					st.takeItems(LIONS_CLAW, 10);
					int n = Rnd.get(4);
					if(n == 0)
						st.giveItems(ALACRITY_POTION, 1);
					else if(n == 1)
						st.giveItems(SOULSHOT_D, 100);
					else if(n == 2)
						st.giveItems(SPIRITSHOT_D, 50);
					else if(n == 3)
						st.giveItems(HEALING_POTION, 10);
					claw -= 10;
				}
				return "sophia_q0333_18b.htm";
			}
			return "sophia_q0333_16.htm";
		}
		else if(event.equalsIgnoreCase("leave"))
		{
			int order;
			if(part == 1)
				order = SOPHIAS_LETTER1;
			else if(part == 2)
				order = SOPHIAS_LETTER2;
			else if(part == 3)
				order = SOPHIAS_LETTER3;
			else if(part == 4)
				order = SOPHIAS_LETTER4;
			else
				order = 0;
			st.set("part", "0");
			if(order > 0)
				st.takeItems(order, 1);
			return "sophia_q0333_20.htm";
		}

		else if(event.equalsIgnoreCase("start_parts"))
			return "sophia_q0333_05.htm";
		else if(event.equalsIgnoreCase("start_chose_parts"))
			return "sophia_q0333_05.htm";
		else if(event.equalsIgnoreCase("p1_explanation"))
			return "sophia_q0333_06.htm";
		else if(event.equalsIgnoreCase("p2_explanation"))
			return "sophia_q0333_07.htm";
		else if(event.equalsIgnoreCase("p3_explanation"))
			return "sophia_q0333_08.htm";
		else if(event.equalsIgnoreCase("p4_explanation"))
			return "sophia_q0333_09.htm";
		else if(event.equalsIgnoreCase("r_exit"))
			return "sophia_q0333_21.htm";
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;

		switch (npcId)
		{
			case Sophya:
				if (cond == 0)
				{
					st.set("part", "0");
					st.set("text", "0");
					return "sophia_q0333_03.htm";
				}
				else if (cond > 0)
				{
					int part = st.getInt("part");
					int item;
					if (part == 1)
						item = UNDEAD_ASH;
					else if (part == 2)
						item = BLOODY_AXE_INSIGNIAS;
					else if (part == 3)
						item = DELU_FANG;
					else if (part == 4)
						item = STAKATO_TALONS;
					else
						return "sophia_q0333_20.htm";

					long count = st.getQuestItemsCount(item);
					if (count > 0)
						giveRewards(st, item, count);
					else
						return "sophia_q0333_22.htm";
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		boolean on_npc = false;
		int part = 0;
		int allowDrop = 0;
		int chancePartItem = 0;
		int partItem = 0;
		for(int i = 0; i < DROPLIST.length; i++)
			if(DROPLIST[i][0] == npcId)
			{
				part = DROPLIST[i][1];
				allowDrop = DROPLIST[i][2];
				chancePartItem = DROPLIST[i][3];
				partItem = DROPLIST[i][5];
				on_npc = true;
			}
		if(on_npc)
		{
			int rand = Rnd.get(1, 100);
			if(allowDrop == 1 && st.getInt("part") == part)
				if(rand < chancePartItem)
				{
					st.giveItems(partItem, npcId == 27152 ? 8 : 1, true);
					st.playSound(SOUND_ITEMGET);
				}
		}

		// Delu Lizardman, Delu Lizardman Scout, Delu Lizardman Warrior
		if(Rnd.chance(4) && (npcId == 20251 || npcId == 20252 || npcId == 20253))
		{
			// Delu Lizardman Headhunter
			st.addSpawn(21105);
			st.addSpawn(21105);
		}

		// Marsh Stakato, Marsh Stakato Worker, Marsh Stakato Soldier, Marsh Stakato Drone
		if(npcId == 20157 || npcId == 20230 || npcId == 20232 || npcId == 20234)
		{
			// Marsh Stakato Marquess
			if(Rnd.chance(2))
				st.addSpawn(27152);
		}

		return null;
	}
}
