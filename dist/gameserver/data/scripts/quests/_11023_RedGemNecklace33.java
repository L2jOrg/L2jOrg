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

public class _11023_RedGemNecklace33 extends Quest
{
	
	public final int ASKA = 30560;
	
	//mobs
	public final int BOKOZLA = 21257;
	public final int BES = 21117;
			
	//items
	public final int HRUSTALIK = 90282;
	public final int KAMEN = 90281;	
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ BOKOZLA, HRUSTALIK, 20, 100 },
			{ BES, KAMEN, 20, 100 },							
	};
	
	public _11023_RedGemNecklace33()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ASKA);				
		addQuestItem(HRUSTALIK);
		addQuestItem(KAMEN);		

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ HRUSTALIK, KAMEN });


		addLevelCheck("lvl.htm", 15);
		addRaceCheck("lvl.htm", Race.ORC);
		addQuestCompletedCheck("questnotdone.htm", 11022);		
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
				
				st.takeItems(HRUSTALIK, -1);
				st.takeItems(KAMEN, -1);				
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
				
				st.takeItems(HRUSTALIK, -1);
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
			if(npc.getNpcId() == BOKOZLA)
			if(qs.rollAndGive(HRUSTALIK, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == BES)
			if(qs.rollAndGive(KAMEN, 1, 1, 20, 100))
				qs.setCond(4);
		}		
		return null;
	}
}