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

public class _11022_RedGemNecklace23 extends Quest
{
	
	public final int ASKA = 30560;
	
	//mobs
	public final int MEDVED = 20479;
	public final int PAUKK = 20474;
	public final int KPAUKK = 20476;
	public final int OPAUKK = 20478;	
		
	//items
	public final int KOST = 90278;
	public final int LAPKA = 90279;	
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ MEDVED, KOST, 20, 100 },
			{ PAUKK, LAPKA, 30, 100 },
			{ KPAUKK, LAPKA, 30, 100 },
			{ OPAUKK, LAPKA, 30, 100 },				
	};
	
	public _11022_RedGemNecklace23()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ASKA);				
		addQuestItem(KOST);
		addQuestItem(LAPKA);		

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ KOST, LAPKA });


		addLevelCheck("lvl.htm", 15, 20);
		addRaceCheck("lvl.htm", Race.ORC);
		addQuestCompletedCheck("questnotdone.htm", 11021);		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("aska2.htm"))
		{
			st.setCond(2);
		}		
		else if(event.equalsIgnoreCase("aska4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(KOST, -1);
				st.takeItems(LAPKA, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("aska4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(KOST, -1);
				st.takeItems(LAPKA, -1);				
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

		
		if(npcId == ASKA) {
			if(cond ==0){
						htmltext = "aska.htm";
				}	
			if(cond ==2){
						htmltext = "aska2.htm";
				}				
			if(cond ==4){
						htmltext = "aska3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == MEDVED)
			if(qs.rollAndGive(KOST, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == PAUKK || npc.getNpcId() == KPAUKK || npc.getNpcId() == OPAUKK)
			if(qs.rollAndGive(LAPKA, 1, 1, 30, 100))
				qs.setCond(4);
		}		
		return null;
	}
}