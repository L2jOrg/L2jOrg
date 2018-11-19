package quests;

import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _223_TestOfChampion extends Quest
{

	/**
	 * The Old Soldier's Friend
	 * Veteran Ascalon asks you to find his old friend Mason in the southern part of Dion.
	 */
	private static final int COND1 = 1;
	/**
	 * Mason's Revenge
	 * Mason's entire family was massacred by troops of the Gracian army. Exact his revenge upon the Bloody Axe Elites on the Plains of the Lizardmen.
	 */
	private static final int COND2 = 2;
	/**
	 * Return to Mason
	 * You've collected 100 heads of the Bloody Axe Elite. Now take them to Mason.
	 */
	private static final int COND3 = 3;
	/**
	 * Letter to Ascalon
	 * Mason is satisfied that his family has been avenged. He asks you to deliver a letter to Ascalon.
	 */
	private static final int COND4 = 4;
	/**
	 * Poison of Medusa
	 * Now you must make an antidote for the Giran guards who were poisoned by the medusa. Find Groot in Giran Castle Town.
	 */
	private static final int COND5 = 5;
	/**
	 * An Antidote for Medusa Poisoning
	 * It wasn't a mortal threat to the race, but the number medusa poisonings certainly increased. Create an antidote from a harpy's egg, the medusa's venom and windsus bile.
	 */
	private static final int COND6 = 6;
	/**
	 * Return to Groot
	 * You've gathered the ingredients to make the antidote for the medusa's venom. Return to Magic Trader Groot.
	 */
	private static final int COND7 = 7;
	/**
	 * Fight of the Old Soldiers
	 * Groot thanks you for helping him and tells you that he will make and deliver the antidote. Now return to Ascalon.
	 */
	private static final int COND8 = 8;
	/**
	 * Crisis In the Town of Oren
	 * The Town of Oren is threatened! Meet Captain Mouen there!
	 */
	private static final int COND9 = 9;
	/**
	 * Hunting the Road Scavenger
	 * The crisis in Oren was a bit overblown by Ascalon, but the road scavengers are clearly causing trouble. Bring back 100 of their heads!
	 */
	private static final int COND10 = 10;
	/**
	 * Return to Mouen
	 * You've collected 100 heads of the road scavengers. Return to Captain Mouen.
	 */
	private static final int COND11 = 11;
	/**
	 * Protect Giran Castle
	 * Ascalon was right! the Leto Lizardmen have begun a full frontal assault! You must defend Giran Castle!
	 */
	private static final int COND12 = 12;
	/**
	 * Return to the Captain
	 * You've collected 100 fangs of Leto Lizardmen. Return to Captain Mouen.
	 */
	private static final int COND13 = 13;
	/**
	 * Return to Ascalon
	 * You've saved Giran! Return to Veteran Ascalon.
	 */
	private static final int COND14 = 14;

	private static final int RewardExp = 194400;
	private static final int RewardSP = 0;

	//item
	private static final int MARK_OF_CHAMPION = 3276;
	private static final int ASCALONS_LETTER1 = 3277;
	private static final int MASONS_LETTER = 3278;
	private static final int IRON_ROSE_RING = 3279;
	private static final int ASCALONS_LETTER2 = 3280;
	private static final int WHITE_ROSE_INSIGNIA = 3281;
	private static final int GROOTS_LETTER = 3282;
	private static final int ASCALONS_LETTER3 = 3283;
	private static final int MOUENS_ORDER1 = 3284;
	private static final int MOUENS_ORDER2 = 3285;
	private static final int MOUENS_LETTER = 3286;
	private static final int HARPYS_EGG = 3287;
	private static final int MEDUSA_VENOM = 3288;
	private static final int WINDSUS_BILE = 3289;
	private static final int BLOODY_AXE_HEAD = 3290;
	private static final int ROAD_RATMAN_HEAD = 3291;
	private static final int LETO_LIZARDMAN_FANG = 3292;
	//NPC
	private static final int Ascalon = 30624;
	private static final int Groot = 30093;
	private static final int Mouen = 30196;
	private static final int Mason = 30625;

	private static final int Harpy = 20145;
	private static final int HarpyMatriarch = 27088;
	private static final int Medusa = 20158;
	private static final int Windsus = 20553;
	private static final int RoadScavenger = 20551;
	private static final int LetoLizardman = 20577;
	private static final int LetoLizardmanArcher = 20578;
	private static final int LetoLizardmanSoldier = 20579;
	private static final int LetoLizardmanWarrior = 20580;
	private static final int LetoLizardmanShaman = 20581;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int BloodyAxeElite = 20780;

	private static final int[][] DROPLIST = {
			// cond before, cond after, mob, item, chance, max
			{
					COND2,
					COND3,
					BloodyAxeElite,
					BLOODY_AXE_HEAD,
					20,
					10
			},
			{
					COND6,
					COND7,
					Harpy,
					HARPYS_EGG,
					100,
					30
			},
			{
					COND6,
					COND7,
					HarpyMatriarch,
					HARPYS_EGG,
					100,
					30
			},
			{
					COND6,
					COND7,
					Medusa,
					MEDUSA_VENOM,
					50,
					30
			},
			{
					COND6,
					COND7,
					Windsus,
					WINDSUS_BILE,
					50,
					30
			},
			{
					COND10,
					COND11,
					RoadScavenger,
					ROAD_RATMAN_HEAD,
					20,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardman,
					LETO_LIZARDMAN_FANG,
					20,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardmanArcher,
					LETO_LIZARDMAN_FANG,
					22,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardmanSoldier,
					LETO_LIZARDMAN_FANG,
					24,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardmanWarrior,
					LETO_LIZARDMAN_FANG,
					26,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardmanShaman,
					LETO_LIZARDMAN_FANG,
					28,
					10
			},
			{
					COND12,
					COND13,
					LetoLizardmanOverlord,
					LETO_LIZARDMAN_FANG,
					30,
					10
			},
	};

	public _223_TestOfChampion()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Ascalon);
		addTalkId(Groot);
		addTalkId(Mouen);
		addTalkId(Mason);

		addKillId(Harpy, Medusa, HarpyMatriarch, RoadScavenger, Windsus, //
				LetoLizardman, LetoLizardmanArcher, LetoLizardmanSoldier, LetoLizardmanWarrior, LetoLizardmanShaman, LetoLizardmanOverlord, BloodyAxeElite);

		addQuestItem(MASONS_LETTER, MEDUSA_VENOM, WINDSUS_BILE, WHITE_ROSE_INSIGNIA, HARPYS_EGG,//
				GROOTS_LETTER, MOUENS_LETTER, ASCALONS_LETTER1, IRON_ROSE_RING, BLOODY_AXE_HEAD, ASCALONS_LETTER2, ASCALONS_LETTER3,//
				MOUENS_ORDER1, ROAD_RATMAN_HEAD, MOUENS_ORDER2, LETO_LIZARDMAN_FANG);

		addClassIdCheck("veteran_ascalon_q0223_01.htm", 1, 45);
		addLevelCheck("veteran_ascalon_q0223_02.htm", 39);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			htmltext = "veteran_ascalon_q0223_06.htm";
			st.setCond(COND1);
			st.giveItems(ASCALONS_LETTER1, 1);
		}
		else if(event.equals("30624_1"))
			htmltext = "veteran_ascalon_q0223_05.htm";
		else if(event.equals("30624_2"))
		{
			htmltext = "veteran_ascalon_q0223_10.htm";
			st.setCond(COND5);
			st.takeItems(MASONS_LETTER, -1);
			st.giveItems(ASCALONS_LETTER2, 1);
		}
		else if(event.equals("30624_3"))
		{
			htmltext = "veteran_ascalon_q0223_14.htm";
			st.setCond(COND9);
			st.takeItems(GROOTS_LETTER, -1);
			st.giveItems(ASCALONS_LETTER3, 1);
		}
		else if(event.equals("30625_1"))
			htmltext = "mason_q0223_02.htm";
		else if(event.equals("30625_2"))
		{
			htmltext = "mason_q0223_03.htm";
			st.setCond(COND2);
			st.takeItems(ASCALONS_LETTER1, -1);
			st.giveItems(IRON_ROSE_RING, 1);
		}
		else if(event.equals("30093_1"))
		{
			htmltext = "groot_q0223_02.htm";
			st.setCond(COND6);
			st.takeItems(ASCALONS_LETTER2, -1);
			st.giveItems(WHITE_ROSE_INSIGNIA, 1);
		}
		else if(event.equals("30196_1"))
			htmltext = "mouen_q0223_02.htm";
		else if(event.equals("30196_2"))
		{
			htmltext = "mouen_q0223_03.htm";
			st.setCond(COND10);
			st.takeItems(ASCALONS_LETTER3, -1);
			st.giveItems(MOUENS_ORDER1, 1);
		}
		else if(event.equals("30196_3"))
		{
			htmltext = "mouen_q0223_06.htm";
			st.setCond(COND12);
			st.takeItems(MOUENS_ORDER1, -1);
			st.takeItems(ROAD_RATMAN_HEAD, -1);
			st.giveItems(MOUENS_ORDER2, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_CHAMPION) > 0)
		{
			return COMPLETED_DIALOG;
		}
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		ClassId class_id = st.getPlayer().getClassId();

		switch (npcId)
		{
			case Ascalon:
				if(cond == 0)
				{
					if(class_id == ClassId.WARRIOR)
						htmltext = "veteran_ascalon_q0223_03.htm";
					else
						htmltext = "veteran_ascalon_q0223_04.htm";
				}
				else if(cond == COND1)
					htmltext = "veteran_ascalon_q0223_07.htm";
				else if(cond == COND2 || cond == COND3)
					htmltext = "veteran_ascalon_q0223_08.htm";
				else if(cond == COND4)
					htmltext = "veteran_ascalon_q0223_09.htm";
				else if(cond == COND5)
					htmltext = "veteran_ascalon_q0223_11.htm";
				else if(cond == COND6 || cond == COND7)
					htmltext = "veteran_ascalon_q0223_12.htm";
				else if(cond == COND8)
					htmltext = "veteran_ascalon_q0223_13.htm";
				else if(cond == COND9)
					htmltext = "veteran_ascalon_q0223_15.htm";
				else if(cond > COND9 && cond < COND14)
					htmltext = "veteran_ascalon_q0223_16.htm";
				else if(cond == COND14)
				{
					htmltext = "veteran_ascalon_q0223_17.htm";
					st.takeItems(MOUENS_LETTER, -1);
					st.giveItems(MARK_OF_CHAMPION, 1);
					if(!st.getPlayer().getVarBoolean("prof2.3"))
					{
						st.addExpAndSp(RewardExp, RewardSP);
						st.getPlayer().setVar("prof2.3", "1", -1);
					}
					st.finishQuest();
				}
				break;

			case Mason:
				if(cond == COND1)
					htmltext = "mason_q0223_01.htm";
				else if(cond == COND2)
					htmltext = "mason_q0223_04.htm";
				else if(cond == COND3)
				{
					htmltext = "mason_q0223_05.htm";
					st.takeItems(BLOODY_AXE_HEAD, -1);
					st.takeItems(IRON_ROSE_RING, -1);
					st.giveItems(MASONS_LETTER, 1);
					st.setCond(COND4);
				}
				else if(cond == COND4)
					htmltext = "mason_q0223_06.htm";
				else
					htmltext = "mason_q0223_07.htm";
				break;

			case Groot: //"I am Groot" lol
				if(cond == COND5)
					htmltext = "groot_q0223_01.htm";
				else if(cond == COND6)
					htmltext = "groot_q0223_03.htm";
				else if(cond == COND7)
				{
					htmltext = "groot_q0223_04.htm";
					st.takeItems(WHITE_ROSE_INSIGNIA, -1);
					st.takeItems(HARPYS_EGG, -1);
					st.takeItems(MEDUSA_VENOM, -1);
					st.takeItems(WINDSUS_BILE, -1);
					st.giveItems(GROOTS_LETTER, 1);
					st.setCond(COND8);
				}
				else if(cond == COND8)
					htmltext = "groot_q0223_05.htm";
				else if(cond > COND8)
					htmltext = "groot_q0223_06.htm";
				break;

			case Mouen:
				if(cond == COND9)
					htmltext = "mouen_q0223_01.htm";
				else if(cond == COND10)
					htmltext = "mouen_q0223_04.htm";
				else if(cond == COND11)
					htmltext = "mouen_q0223_05.htm";
				else if(cond == COND12)
					htmltext = "mouen_q0223_07.htm";
				else if(cond == COND13)
				{
					htmltext = "mouen_q0223_08.htm";
					st.takeItems(MOUENS_ORDER2, -1);
					st.takeItems(LETO_LIZARDMAN_FANG, -1);
					st.giveItems(MOUENS_LETTER, 1);
					st.setCond(COND14);
				}
				else if(cond == COND13)
					htmltext = "mouen_q0223_09.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 0)
			return null;

		int npcId = npc.getNpcId();
		for(int[] drop : DROPLIST)
			if(drop[2] == npcId && drop[0] == cond)
			{
				st.rollAndGive(drop[3], 1, 1, drop[5], drop[4]);

				for(int[] drop2 : DROPLIST)
					if(drop2[0] == cond && st.getQuestItemsCount(drop2[3]) < drop2[5])
						return null;

				st.setCond(cond + 1);
				return null;
			}

		return null;
	}
}