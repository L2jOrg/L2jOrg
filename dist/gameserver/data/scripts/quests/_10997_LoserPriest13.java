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

public class _10997_LoserPriest13 extends Quest
{
	public final int ZIMENF = 30538;
	public final int GERALD = 30650;
	
	//mobs
	public final int TARANT = 20403;
	public final int HIWNIK = 20508;	
		
	//items
	public final int YAD = 90297;
	public final int KAMEN = 90298;	
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ TARANT, YAD, 20, 100 },
			{ HIWNIK, KAMEN, 20, 100 },
							
	};
	
	public _10997_LoserPriest13()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ZIMENF);
		addTalkId(GERALD);		
		addQuestItem(YAD);
		addQuestItem(KAMEN);		

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ YAD, KAMEN });


		addLevelCheck("lvl.htm", 15, 20);
		addRaceCheck("lvl.htm", Race.DWARF);
		addQuestCompletedCheck("questnotdone.htm", 10996);		
	}

	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("ZIMENF2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("GERALD2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("GERALD4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(YAD, -1);
				st.takeItems(KAMEN, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("GERALD4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(YAD, -1);
				st.takeItems(KAMEN, -1);				
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

		if(npcId == ZIMENF) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 15)
												
						htmltext = "ZIMENF.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "ZIMENF2.htm";
				}					
				 
					 }
		if(npcId == GERALD) {
			if(cond ==1){
						htmltext = "GERALD.htm";
				}	
			if(cond ==2){
						htmltext = "GERALD2.htm";
				}				
			if(cond ==4){
						htmltext = "GERALD3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == TARANT)
			if(qs.rollAndGive(YAD, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == HIWNIK)
			if(qs.rollAndGive(KAMEN, 1, 1, 20, 100))
				qs.setCond(4);
		}		
		return null;
	}


	@Override
	public int getDescriprionId(int state)
	{
		return 102700 + state;
	}
}

