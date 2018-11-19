package quests;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


public final class _226_TestOfHealer extends Quest
{

	/**
	 * The Girl of Prophecy
	 * Ancient revelations tell of a girl that will be born to save the world from the forces of darkness.
	 * A girl has been born who claims to be the saint described in the prophecy. Speak with Perrin in Gludin Village.
	 */
	private static final int COND1 = 1;
	/**
	 * Perrin's Bodyguard
	 * Perrin's bodyguard has appeared! Kill him!
	 */
	private static final int COND2 = 2;
	/**
	 * The Evil Perrin
	 * You've defeated Tatoma, Perrin's bodyguard. Listen to what Perrin has to say.
	 */
	private static final int COND3 = 3;
	/**
	 * The Ringleader
	 * Perrin's companion, Allana is behind this. Find her.
	 */
	private static final int COND4 = 4;
	/**
	 * An Orphan Girl
	 * Allana scoffs and claims she only saw such a girl once, in the orphanage near Gludio Castle.
	 */
	private static final int COND5 = 5;
	/**
	 * A Poor Orphanage
	 * The orphanage is having serious financial difficulties. They could survive if only they had 100,000 adena.
	 */
	private static final int COND6 = 6;
	/**
	 * A Girl of the Wastelands
	 * The head of the orphanage gratefully accepts your 100,000 adena. But one of the girls seems to be missing.
	 */
	private static final int COND7 = 7;
	/**
	 * A Mysterious Girl
	 * Windy is a mysterious girl who talks to monsters. She says she'll return to the orphanage and asks you to go there first. Return to the orphanage.
	 */
	private static final int COND8 = 8;
	/**
	 * An Elf from Gludio
	 * You haven't find out anything about the saint, but an Elf from Gludio named Sorius might know something.
	 */
	private static final int COND9 = 9;
	/**
	 * Tracking the Followers of Shilen
	 * The girl was kidnapped by the followers of Shilen. Meet Daurin Hammercrush in the western part of the Turek Orc Camp!
	 */
	private static final int COND10 = 10;
	/**
	 * Destroy the Leto Lizardmen!
	 */
	private static final int COND11 = 11;
	/**
	 * The First Secret Letter
	 * You've defeated the Leto Lizardmen and obtained the first secret letter! Return to Daurin.
	 */
	private static final int COND12 = 12;
	/**
	 * To the Obelisk!
	 * The secret letter describes a ceremony that would take place at the obelisk in the Dark Elven Forest. You may find a clue there. Hurry up! Look for help on the way!
	 */
	private static final int COND13 = 13;
	/**
	 * Subjugate the Forces of Darkness! - Part 1
	 * The mysterious Dark Elf has summoned new recruits! Kill them!
	 */
	private static final int COND14 = 14;
	/**
	 * Tracking the Dark Elf - Part 1
	 * You defeated the Leto Lizardmen. Now attack the mysterious Dark Elf!
	 */
	private static final int COND15 = 15;
	/**
	 * Subjugate the Forces of Darkness! - Part 2
	 * More recruits! Defeat them all!
	 */
	private static final int COND16 = 16;
	/**
	 * Tracking the Dark Elf - Part 2
	 * You've defeated the Leto Lizardmen. Now attack the mysterious Dark Elf!
	 */
	private static final int COND17 = 17;
	/**
	 * Subjugate the Forces of Darkness! - Part 3
	 * More recruits! Kill the enemy one more time!
	 */
	private static final int COND18 = 18;
	/**
	 * Tracking the Dark Elf - Part 3
	 * You've defeated the Leto Lizardmen. This is the end! Keep attacking the mysterious Dark Elf!
	 */
	private static final int COND19 = 19;
	/**
	 * Where is the Saint?
	 * The spell that imprisoned the saint has been broken. But where is the saint? Daurin's people may know. Look for clues nearby!
	 */
	private static final int COND20 = 20;
	/**
	 * Finding the Saint
	 * The saint can be found in a tent near the Dark Elven Altar of Rites. Find her!
	 */
	private static final int COND21 = 21;
	/**
	 * The Duty of the Saint
	 * The Goddess has begun to open her eyes.
	 * Kristina says she has things to do here and asks you to deliver a letter to Master Sorius on her behalf.
	 */
	private static final int COND22 = 22;
	/**
	 * The Trial of the Healer
	 * Sorius thanks you for a job well done and promises to speak to Bandellos on your behalf. Return to Priest Bandellos.
	 */
	private static final int COND23 = 23;

	private static final int Bandellos = 30473;
	private static final int Perrin = 30428;
	private static final int OrphanGirl = 30659;
	private static final int Allana = 30424;
	private static final int FatherGupu = 30658;
	private static final int Windy = 30660;
	private static final int Sorius = 30327;
	private static final int Daurin = 30674;
	private static final int Piper = 30662;
	private static final int Slein = 30663;
	private static final int Kein = 30664;
	private static final int MysteryDarkElf = 30661;
	private static final int Kristina = 30665;

	private static final int REPORT_OF_PERRIN_ID = 2810;
	private static final int CRISTINAS_LETTER_ID = 2811;
	private static final int PICTURE_OF_WINDY_ID = 2812;
	private static final int GOLDEN_STATUE_ID = 2813;
	private static final int WINDYS_PEBBLES_ID = 2814;
	private static final int ORDER_OF_SORIUS_ID = 2815;
	private static final int SECRET_LETTER1_ID = 2816;
	private static final int SECRET_LETTER2_ID = 2817;
	private static final int SECRET_LETTER3_ID = 2818;
	private static final int SECRET_LETTER4_ID = 2819;
	private static final int MARK_OF_HEALER_ID = 2820;

	private static Map<Integer, Integer[]> DROPLIST = new HashMap<Integer, Integer[]>();

	static
	{
		DROPLIST.put(27134, new Integer[]{
				COND2,
				COND3,
				0
		});
		DROPLIST.put(27123, new Integer[]{
				COND11,
				COND12,
				SECRET_LETTER1_ID
		});
		DROPLIST.put(27124, new Integer[]{
				COND14,
				COND15,
				SECRET_LETTER2_ID
		});
		DROPLIST.put(27125, new Integer[]{
				COND16,
				COND17,
				SECRET_LETTER3_ID
		});
		DROPLIST.put(27127, new Integer[]{
				COND18,
				COND19,
				SECRET_LETTER4_ID
		});
	}

	public _226_TestOfHealer()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(30473);

		addTalkId(30327, 30424, 30428, 30473, 30658, 30659, 30660, 30661, 30662, 30663, 30664, 30665, 30674);

		addKillId(20150, 27123, 27124, 27125, 27127, 27134);

		addQuestItem(REPORT_OF_PERRIN_ID, CRISTINAS_LETTER_ID, PICTURE_OF_WINDY_ID, GOLDEN_STATUE_ID, //
				WINDYS_PEBBLES_ID, ORDER_OF_SORIUS_ID, SECRET_LETTER1_ID, SECRET_LETTER2_ID, SECRET_LETTER3_ID, SECRET_LETTER4_ID);

		addClassIdCheck("30473-01.htm", ClassId.KNIGHT, ClassId.CLERIC, ClassId.ORACLE, ClassId.ELVEN_KNIGHT);
		addLevelCheck("30473-02.htm", 39);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			htmltext = "30473-04.htm";
			st.setCond(COND1);
			st.giveItems(REPORT_OF_PERRIN_ID, 1);
		}
		else if(event.equalsIgnoreCase("30473_1"))
			htmltext = "30473-08.htm";
		else if(event.equalsIgnoreCase("30473_2"))
		{
			htmltext = "30473-09.htm";
			st.takeItems(GOLDEN_STATUE_ID, -1);
			st.giveItems(MARK_OF_HEALER_ID, 1);
			st.addExpAndSp(113400, 0);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("30428_1"))
		{
			htmltext = "30428-02.htm";
			st.setCond(COND2);
			st.addSpawn(27134, -93254, 147559, -2679);
		}
		else if(event.equalsIgnoreCase("30658_1"))
			if(st.getQuestItemsCount(ADENA_ID) >= 100000)
			{
				htmltext = "30658-02.htm";
				st.takeItems(ADENA_ID, 100000);
				st.giveItems(PICTURE_OF_WINDY_ID, 1);
				st.setCond(COND7);
			}
			else
				htmltext = "30658-05.htm";
		else if(event.equalsIgnoreCase("30658_2"))
		{
			st.setCond(COND6);
			htmltext = "30658-03.htm";
		}
		else if(event.equalsIgnoreCase("30660-03.htm"))
		{
			st.takeItems(PICTURE_OF_WINDY_ID, 1);
			st.giveItems(WINDYS_PEBBLES_ID, 1);
			st.setCond(COND8);
		}
		else if(event.equalsIgnoreCase("30674_1"))
		{
			htmltext = "30674-02.htm";
			st.setCond(COND11, SOUND_BEFORE_BATTLE);
			st.takeItems(ORDER_OF_SORIUS_ID, 1);
			st.addSpawn(27122);
			st.addSpawn(27122);
			st.addSpawn(27123);
		}
		else if(event.equalsIgnoreCase("30665_1"))
		{
			htmltext = "30665-02.htm";
			st.takeItems(SECRET_LETTER1_ID, 1);
			st.takeItems(SECRET_LETTER2_ID, 1);
			st.takeItems(SECRET_LETTER3_ID, 1);
			st.takeItems(SECRET_LETTER4_ID, 1);
			st.giveItems(CRISTINAS_LETTER_ID, 1);
			st.setCond(COND22);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_HEALER_ID) > 0)
			return COMPLETED_DIALOG;

		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case Bandellos:
				if(cond == 0)
					htmltext = "30473-03.htm";
				else if(cond == COND23)
				{
					if(st.getQuestItemsCount(GOLDEN_STATUE_ID) == 0)
					{
						st.giveItems(MARK_OF_HEALER_ID, 1);
						htmltext = "30473-06.htm";
						if(!st.getPlayer().getVarBoolean("prof2.3"))
						{
							st.addExpAndSp(172800, 0);
							st.getPlayer().setVar("prof2.3", "1", -1);
						}
						st.finishQuest();
					}
					else
						htmltext = "30473-07.htm";
				}
				else
					htmltext = "30473-05.htm";
				break;

			case Perrin:
				if(cond == COND1)
					htmltext = "30428-01.htm";
				else if(cond == COND3)
				{
					htmltext = "30428-03.htm";
					st.takeItems(REPORT_OF_PERRIN_ID, 1);
					st.setCond(COND4);
				}
				else if(cond != COND2)
					htmltext = "30428-04.htm";
				break;

			case OrphanGirl:
				int n = Rnd.get(5);
				if(n == 0)
					htmltext = "30659-01.htm";
				else if(n == 1)
					htmltext = "30659-02.htm";
				else if(n == 2)
					htmltext = "30659-03.htm";
				else if(n == 3)
					htmltext = "30659-04.htm";
				else if(n == 4)
					htmltext = "30659-05.htm";
				break;
			case Allana:
				if(cond == COND4)
				{
					htmltext = "30424-01.htm";
					st.setCond(COND5);
				}
				else
					htmltext = "30424-02.htm";
				break;

			case FatherGupu:
				if(cond == COND5)
					htmltext = "30658-01.htm";
				else if(cond == COND7)
					htmltext = "30658-04.htm";
				else if(cond == COND8)
				{
					htmltext = "30658-06.htm";
					st.giveItems(GOLDEN_STATUE_ID, 1);
					st.takeItems(WINDYS_PEBBLES_ID, 1);
					st.setCond(COND9);
				}
				else if(cond == COND6)
				{
					st.setCond(COND9);
					htmltext = "30658-07.htm";
				}
				else if(cond == COND9)
					htmltext = "30658-07.htm";
				break;

			case Windy:
				if(cond == COND7)
					htmltext = "30660-01.htm";
				else if(cond == COND8)
					htmltext = "30660-04.htm";
				break;

			case Sorius:
				if(cond == COND9)
				{
					htmltext = "30327-01.htm";
					st.giveItems(ORDER_OF_SORIUS_ID, 1);
					st.setCond(COND10);
				}
				else if(cond > COND9 && cond < COND22)
					htmltext = "30327-02.htm";
				else if(cond == COND22)
				{
					htmltext = "30327-03.htm";
					st.takeItems(CRISTINAS_LETTER_ID, 1);
					st.setCond(COND23);
				}
				break;

			case Daurin:
				if(cond == COND10 && st.getQuestItemsCount(ORDER_OF_SORIUS_ID) > 0)
					htmltext = "30674-01.htm";
				else if(cond == COND12 && st.getQuestItemsCount(SECRET_LETTER1_ID) > 0)
				{
					htmltext = "30674-03.htm";
					st.setCond(COND13);
				}
				break;

			case Piper:
			case Slein:
			case Kein:
				if(cond == COND13)
					htmltext = npcId + "-01.htm";
				else if(cond == COND15)
					htmltext = npcId + "-02.htm";
				else if(cond == COND20)
				{
					st.setCond(COND21);
					htmltext = npcId + "-03.htm";
				}
				else if(cond == 21)
					htmltext = npcId + "-04.htm";
				break;

			case MysteryDarkElf:
				if(cond == COND13)
				{
					htmltext = "30661-01.htm";
					st.addSpawn(27124);
					st.addSpawn(27124);
					st.addSpawn(27124);
					st.setCond(COND14, SOUND_BEFORE_BATTLE);
				}
				else if(cond == COND15)
				{
					htmltext = "30661-02.htm";
					st.addSpawn(27125);
					st.addSpawn(27125);
					st.addSpawn(27125);
					st.setCond(COND16, SOUND_BEFORE_BATTLE);
				}
				else if(cond == COND17)
				{
					htmltext = "30661-03.htm";
					st.addSpawn(27126);
					st.addSpawn(27126);
					st.addSpawn(27127);
					st.setCond(COND18, SOUND_BEFORE_BATTLE);
				}
				else if(cond == COND19)
				{
					htmltext = "30661-04.htm";
					st.setCond(COND20);
				}
				break;

			case Kristina:
				if(cond == COND20 || cond == COND21)
					htmltext = "30665-01.htm";
				else
					htmltext = "30665-03.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Integer[] d = DROPLIST.get(npc.getNpcId());
		if(st.getCond() == d[0] && (d[2] == 0 || st.getQuestItemsCount(d[2]) == 0))
		{
			if(d[2] != 0)
				st.giveItems(d[2], 1);
			st.setCond(d[1]);
		}
		return null;
	}
}