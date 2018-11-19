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

public class _11007_NoiseInWoods extends Quest
{
	public final int GID = 30599;
	public final int KENDELL = 30218;
	
	//mobs
	public final int SERVOLK = 20525;
	public final int NALETGOBL = 20325;
	public final int ORCKABU = 20468;
	public final int ORCKABUVOIT = 20470;
	public final int ORCKABUSTRELOK = 20469;
	public final int SPORGRIB = 20509;
	
	
	//items
	public final int HVOST = 90218;
	public final int MEWOK = 90219;
	public final int AMULET = 90220;
	public final int POROWOK = 90221;
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ SERVOLK, HVOST, 10, 100 },
			{ NALETGOBL, MEWOK, 10, 100 },
			{ ORCKABU, AMULET, 10, 100 },
			{ ORCKABUSTRELOK, AMULET, 10, 100 },
			{ ORCKABUVOIT, AMULET, 10, 100 },
			{ SPORGRIB, POROWOK, 20, 100 },
	};
	
	public _11007_NoiseInWoods()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GID);
		addTalkId(KENDELL);		
		addQuestItem(HVOST);
		addQuestItem(AMULET);
		addQuestItem(MEWOK);
		addQuestItem(POROWOK);
		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ HVOST, AMULET, MEWOK, POROWOK });


		addLevelCheck("lvl.htm", 2, 20);
		addRaceCheck("lvl.htm", Race.ELF);
		//addQuestCompletedCheck("questnotdone.htm", 11001);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gid2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("kend2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("kend4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29497, 2);
				st.giveItems(29471, 1);
				st.giveItems(49045, 1);
				
				st.takeItems(HVOST, -1);
				st.takeItems(AMULET, -1);
				st.takeItems(MEWOK, -1);
				st.takeItems(POROWOK, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("kend4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29497, 2);
				st.giveItems(29471, 1);
				st.giveItems(49046, 1);
				
				st.takeItems(HVOST, -1);
				st.takeItems(AMULET, -1);
				st.takeItems(MEWOK, -1);
				st.takeItems(POROWOK, -1);			
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

		if(npcId == GID) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 2)
												
						htmltext = "gid.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "gid2.htm";
				}					
				 
					 }
		if(npcId == KENDELL) {
			if(cond ==1){
						htmltext = "kend.htm";
				}	
			if(cond ==2){
						htmltext = "kend2.htm";
				}				
			if(cond ==6){
						htmltext = "kend3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == SERVOLK)
			if(qs.rollAndGive(HVOST, 1, 1, 10, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == NALETGOBL)
			if(qs.rollAndGive(MEWOK, 1, 1, 10, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == ORCKABU || npc.getNpcId() == ORCKABUSTRELOK || npc.getNpcId() == ORCKABUVOIT)
			if(qs.rollAndGive(AMULET, 1, 1, 10, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == SPORGRIB)
			if(qs.rollAndGive(POROWOK, 1, 1, 20, 100))			
					qs.setCond(6);
				
		}
		return null;
	}
}