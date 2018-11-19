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

public class _11013_ShilensHunt extends Quest
{
	public final int GID = 30600;
	public final int TALLOT = 30141;
	
	//mobs
	public final int PEPVOLK = 20456;
	public final int GOBLIN = 20003;
	public final int BES = 20004;
	public final int STBES = 20005;
	public final int ZELGRIB = 20007;
	public final int VOINBALOR = 20386;
	public final int ADBALOR = 20388;
	public final int GLAVABALOR = 20387;
	
	
	//items
	public final int HVOST = 90238;
	public final int JALO = 90239;
	public final int KRULOBESA = 90240;
	public final int SOK = 90241;
	public final int KLUK = 90242;
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ PEPVOLK, HVOST, 10, 100 },
			{ GOBLIN, JALO, 10, 100 },
			{ BES, KRULOBESA, 10, 100 },
			{ ZELGRIB, SOK, 10, 100 },
			{ STBES, KRULOBESA, 10, 100 },
			{ VOINBALOR, KLUK, 10, 100 },
			{ ADBALOR, KLUK, 10, 100 },
			{ GLAVABALOR, KLUK, 10, 100 },
	};
	
	public _11013_ShilensHunt()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GID);
		addTalkId(TALLOT);		
		addQuestItem(HVOST);
		addQuestItem(KRULOBESA);
		addQuestItem(JALO);
		addQuestItem(SOK);
		addQuestItem(KLUK);


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ HVOST, KRULOBESA, JALO, SOK, KLUK });


		addLevelCheck("lvl.htm", 2, 20);
		addRaceCheck("lvl.htm", Race.DARKELF);
		//addQuestCompletedCheck("questnotdone.htm", );
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gid2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("tallot2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("tallot4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 7)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29497, 2);
				st.giveItems(29471, 1);
				st.giveItems(49050, 1);
				
				st.takeItems(HVOST, -1);
				st.takeItems(KRULOBESA, -1);
				st.takeItems(JALO, -1);
				st.takeItems(SOK, -1);
				st.takeItems(KLUK, -1);
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("tallot4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 7)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29497, 2);
				st.giveItems(29471, 1);
				st.giveItems(49049, 1);
				
				st.takeItems(HVOST, -1);
				st.takeItems(KRULOBESA, -1);
				st.takeItems(JALO, -1);
				st.takeItems(SOK, -1);
				st.takeItems(KLUK, -1);
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
		if(npcId == TALLOT) {
			if(cond ==1){
						htmltext = "tallot.htm";
				}	
			if(cond ==2){
						htmltext = "tallot2.htm";
				}				
			if(cond ==7){
						htmltext = "tallot3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == PEPVOLK)
			if(qs.rollAndGive(HVOST, 1, 1, 10, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == GOBLIN)
			if(qs.rollAndGive(JALO, 1, 1, 10, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == BES || npc.getNpcId() == STBES)
			if(qs.rollAndGive(KRULOBESA, 1, 1, 10, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == ZELGRIB)
			if(qs.rollAndGive(SOK, 1, 1, 10, 100))			
					qs.setCond(6);
				
		}
		if(qs.getCond() == 6 )
		{
			if(npc.getNpcId() == VOINBALOR || npc.getNpcId() == ADBALOR || npc.getNpcId() == GLAVABALOR)
			if(qs.rollAndGive(KLUK, 1, 1, 10, 100))			
					qs.setCond(7);
				
		}
		return null;
	}
}