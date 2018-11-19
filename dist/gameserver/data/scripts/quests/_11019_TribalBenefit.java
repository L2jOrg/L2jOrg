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

public class _11019_TribalBenefit extends Quest
{
	public final int GID = 30602;
	public final int TIKU = 30582;
	
	//mobs
	public final int VOLKKHA = 20475;
	public final int LESNVOLK = 20477;
	public final int BES = 20312;
	public final int GOBLINGRAB = 20319;
	public final int GORNGRIB = 20365;
	public final int OBOROTEN = 20363;
	public final int OKOZLA = 20426;
	
	
	
	//items
	public final int MEH = 90262;
	public final int PRAH = 90263;
	public final int OJBESA = 90264;
	public final int SPORU = 90265;
	public final int KOGOT = 90266;
	public final int SLEZU = 90267;
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ VOLKKHA, MEH, 10, 100 },
			{ LESNVOLK, MEH, 10, 100 },
			{ BES, OJBESA, 10, 100 },
			{ GORNGRIB, SPORU, 10, 100 },
			{ GOBLINGRAB, PRAH, 10, 100 },
			{ OBOROTEN, KOGOT, 10, 100 },
			{ OKOZLA, SLEZU, 10, 100 },			
	};
	
	public _11019_TribalBenefit()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GID);
		addTalkId(TIKU);		
		addQuestItem(MEH);
		addQuestItem(OJBESA);
		addQuestItem(PRAH);
		addQuestItem(SPORU);
		addQuestItem(KOGOT);
		addQuestItem(SLEZU);


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ MEH, OJBESA, PRAH, SPORU, KOGOT, SLEZU });


		addLevelCheck("lvl.htm", 2, 20);
		addRaceCheck("lvl.htm", Race.ORC);
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
		else if(event.equalsIgnoreCase("tiku2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("tiku4.htm"))
		{
			if(st.getCond() == 7)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29497, 2);
				st.giveItems(29471, 1);
				st.giveItems(49052, 1);
				
				st.takeItems(MEH, -1);
				st.takeItems(OJBESA, -1);
				st.takeItems(PRAH, -1);
				st.takeItems(SPORU, -1);
				st.takeItems(KOGOT, -1);
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
		if(npcId == TIKU) {
			if(cond ==1){
						htmltext = "tiku.htm";
				}	
			if(cond ==2){
						htmltext = "tiku2.htm";
				}				
			if(cond ==7){
						htmltext = "tiku3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == VOLKKHA || npc.getNpcId() == LESNVOLK)
			if(qs.rollAndGive(MEH, 1, 1, 10, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == GOBLINGRAB)
			if(qs.rollAndGive(PRAH, 1, 1, 10, 100));
				
			if(npc.getNpcId() == BES)
			if(qs.rollAndGive(OJBESA, 1, 1, 10, 100));
			if(qs.getQuestItemsCount(PRAH) == 10 && qs.getQuestItemsCount(OJBESA) == 10)
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == GORNGRIB)
			if(qs.rollAndGive(SPORU, 1, 1, 10, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == OBOROTEN)
			if(qs.rollAndGive(KOGOT, 1, 1, 10, 100))			
					qs.setCond(6);
				
		}
		if(qs.getCond() == 6 )
		{
			if(npc.getNpcId() == OKOZLA)
			if(qs.rollAndGive(SLEZU, 1, 1, 10, 100))			
					qs.setCond(7);
				
		}
		return null;
	}
}