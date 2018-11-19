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

public class _11016_PrepareForTrade23 extends Quest
{
	
	public final int VOLODOS = 30137;
	
	//mobs
	public final int KAMHR = 20380;
	public final int ZVER = 20418;
	public final int PSKI = 20034;
	public final int YADPAUK = 20038;
	public final int TRIMDEN = 20043;
		
	//items
	public final int YADRO = 90254;
	public final int POROWOK = 90255;
	public final int WKYRA = 90256;
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ KAMHR, YADRO, 20, 100 },
			{ ZVER, POROWOK, 10, 100 },
			{ PSKI, WKYRA, 20, 100 },
			{ YADPAUK, WKYRA, 20, 100 },
			{ TRIMDEN, WKYRA, 20, 100 },			
	};
	
	public _11016_PrepareForTrade23()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(VOLODOS);			
		addQuestItem(YADRO);
		addQuestItem(POROWOK);
		addQuestItem(WKYRA);

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ YADRO, POROWOK, WKYRA });


		addLevelCheck("lvl.htm", 15);
		addRaceCheck("lvl.htm", Race.DARKELF);
		addQuestCompletedCheck("questnotdone.htm", 11015);		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("volodos2.htm"))
		{
			st.setCond(2);
		}
		
		
		else if(event.equalsIgnoreCase("volodos4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(YADRO, -1);
				st.takeItems(POROWOK, -1);
				st.takeItems(WKYRA, -1);
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("volodos4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(YADRO, -1);
				st.takeItems(POROWOK, -1);
				st.takeItems(WKYRA, -1);
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

		if(npcId == VOLODOS) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "volodos.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==2){
						htmltext = "volodos2.htm";
				}
					if(cond ==5){
						htmltext = "volodos3.htm";
				}					
			}				 
		
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == KAMHR)
			if(qs.rollAndGive(YADRO, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == ZVER)
			if(qs.rollAndGive(POROWOK, 1, 1, 10, 100))
				qs.setCond(4);
		}	
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == PSKI || npc.getNpcId() == YADPAUK || npc.getNpcId() == TRIMDEN)
			if(qs.rollAndGive(WKYRA, 1, 1, 20, 100))
				qs.setCond(5);
		}
		return null;
	}
}