package quests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
//SanyaDC
public class _662_AGameOfCards extends Quest
{
	// NPCs
	private final static int KLUMP = 30845;
	// Mobs
	private final static int[] mobs = {
			21004,
			21008,
			21010,
			20671,
			20673,
			20674,
			20955,
			20962,
			20959,
			20677,
			21116

	};
	// Quest Items
	private final static int RED_GEM = 8765;
	// Items
	private final static int adena = 57;
	private final static int en_weap_d = 49482;
	private final static int en_arm_d = 49481;
	private final static int sop = 1875;
	private final static int orihak = 1874;
	private final static int lak = 1865;
	private final static int ZIGGOS_GEMSTONE = 8868;
	// Chances
	private final static int drop_chance = 35;

	

	public _662_AGameOfCards()
	{
		super(PARTY_ALL, REPEATABLE);
		addStartNpc(KLUMP);
		addKillId(mobs);
		addQuestItem(RED_GEM);
		addLevelCheck("30845_00.htm", 55);	
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("30845_02.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30845_07.htm"))
		{
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("30845_03.htm") && st.getQuestItemsCount(RED_GEM) >= 100)
			return "30845_04.htm";
		else if(event.equalsIgnoreCase("30845_10.htm") && st.getQuestItemsCount(RED_GEM) < 100)
		{			
				return "30845_10a.htm";
		//	st.takeItems(RED_GEM, 100);
		}
		else if(event.equalsIgnoreCase("30845_10.htm") && st.getQuestItemsCount(RED_GEM) >= 100)
		{	
			
			if(Rnd.chance(60)){										
					st.giveItems(lak, 1);
					st.giveItems(adena, 931);
					st.takeItems(RED_GEM, 100);
					return "1.htm";}
			else if(Rnd.chance(50))	{									
					st.giveItems(orihak, 1);
					st.giveItems(adena, 8534);
					st.takeItems(RED_GEM, 100);
					return "2.htm";}
			else if(Rnd.chance(40)){
					st.giveItems(sop, 1);
					st.giveItems(adena, 17068);
					st.takeItems(RED_GEM, 100);
					return "3.htm";}
			else if(Rnd.chance(30)){
						st.giveItems(adena, 1000000);
					st.giveItems(en_arm_d, 3);
					st.giveItems(en_weap_d, 1);
					st.takeItems(RED_GEM, 100);
					return "32.htm";}
			else if(Rnd.chance(30)){
					st.giveItems(adena, 200000);
					st.giveItems(en_arm_d, 3);
					st.giveItems(en_weap_d, 1);
					st.takeItems(RED_GEM, 100);
					return "4.htm";}
			else {st.giveItems(ZIGGOS_GEMSTONE, 2);
					st.giveItems(en_arm_d, 3);
					st.giveItems(en_weap_d, 1);
					st.takeItems(RED_GEM, 100);
					return "5.htm";}
			
		}
		return event;
		
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(npc.getNpcId() != KLUMP)
			return NO_QUEST_DIALOG;
		if(st.isNotAccepted())
			return "30845_01.htm";
		else if(st.isStarted())
			return st.getQuestItemsCount(RED_GEM) < 100 ? "30845_03.htm" : "30845_04.htm";

		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.isStarted())
			qs.rollAndGive(RED_GEM, 1, drop_chance);
		return null;
	}

	
	}
