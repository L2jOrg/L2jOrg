package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;


//SanyaDC

	public class _11002_HelpWithTempleRestoration extends Quest
{
	public final int HAR = 30035;
	public final int ALT = 30283;
	
	//mobs
	public final int ORCGLAV = 20098;
	public final int ORCAD = 20096;
	public final int OHOTOB = 20343;
	public final int VOZAKOB = 20342;
	public final int KAMGOL = 20016;
	public final int RAZR = 20101;
	
	
	//items
	public final int DERSTOLB = 90205;
	public final int CHASTIDV = 90206;
	public final int KAMPOROW = 90207;
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ ORCGLAV, DERSTOLB, 20, 100 },
			{ ORCAD, DERSTOLB, 20, 100 },
			{ OHOTOB, CHASTIDV, 25, 100 },
			{ VOZAKOB, CHASTIDV, 25, 100 },
			{ KAMGOL, KAMPOROW, 20, 100 },
			{ RAZR, KAMPOROW, 20, 100 },
			
	};
	
	public _11002_HelpWithTempleRestoration()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ALT);
		addTalkId(HAR);		
		addQuestItem(DERSTOLB);
		addQuestItem(CHASTIDV);
		addQuestItem(KAMPOROW);
		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ DERSTOLB, CHASTIDV, KAMPOROW });


		addLevelCheck("lvl.htm", 11, 20);
		addRaceCheck("lvl.htm", Race.HUMAN);
		addQuestCompletedCheck("questnotdone.htm", 11001);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("alt2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("har2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("har6.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90308, 1);
				st.giveItems(90309, 1);
				st.giveItems(5790, 700);
				st.takeItems(DERSTOLB, -1);
				st.takeItems(CHASTIDV, -1);
				st.takeItems(KAMPOROW, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("har6.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90306, 1);
				st.giveItems(90307, 1);
				st.giveItems(5789, 1000);
				st.takeItems(DERSTOLB, -1);
				st.takeItems(CHASTIDV, -1);
				st.takeItems(KAMPOROW, -1);				
				st.finishQuest();			
			}
		}
		
			return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == ALT) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "alt.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "alt2.htm";
				}					
				 
					 }
		if(npcId == HAR) {
			if(cond ==1){
						htmltext = "har.htm";
				}	
			if(cond ==2){
						htmltext = "har2.htm";
				}				
			if(cond ==5){
						htmltext = "har5.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == ORCGLAV || npc.getNpcId() == ORCAD)
			if(qs.rollAndGive(DERSTOLB, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == OHOTOB || npc.getNpcId() == VOZAKOB)
			if(qs.rollAndGive(CHASTIDV, 1, 1, 25, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == KAMGOL || npc.getNpcId() == RAZR)
			if(qs.rollAndGive(KAMPOROW, 1, 1, 20, 100))			
					qs.setCond(5);
				
		}
	
		return null;
	}
}