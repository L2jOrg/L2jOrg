package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

//SanyaDC

	public class _11001_TombsOfAncestors extends Quest
{
	public final int GID = 30598;
	public final int ALT = 30283;
	
	//mobs
	public final int VOLK = 20120;
	public final int MVOLK = 20442;
	public final int ORC = 20130;
	public final int ORCS = 20006;
	public final int ORCV = 20131;
	public final int VORC = 20093;
	public final int OBOROT = 20132;
	
	//items
	public final int VWKURA = 90200;
	public final int AMORC = 90201;
	public final int KLUK = 90202;
	public final int ME4 = 90203;
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ VOLK, VWKURA, 10, 100 },
			{ MVOLK, VWKURA, 10, 100 },
			{ ORC, AMORC, 10, 100 },
			{ ORCS, AMORC, 10, 100 },
			{ ORCV, AMORC, 10, 100 },
			{ VORC, ME4, 10, 100 },
			{ OBOROT, KLUK, 10, 100 },
	};
	
	public _11001_TombsOfAncestors()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GID);
		addTalkId(ALT);		
		addQuestItem(VWKURA);
		addQuestItem(AMORC);
		addQuestItem(KLUK);
		addQuestItem(ME4);
		//addKillId(MOBH);


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ VWKURA, AMORC, KLUK, ME4 });


		addLevelCheck("lvl.htm", 2, 20);
		addRaceCheck("lvl.htm", Race.HUMAN);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("GID2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("alt2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("alt6.htm"))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29471, 1);
				st.giveItems(29497, 2);
				st.giveItems(49043, 1);
				st.takeItems(VWKURA, -1);
				st.takeItems(AMORC, -1);
				st.takeItems(KLUK, -1);
				st.takeItems(ME4, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("alt7.htm"))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29471, 1);
				st.giveItems(29497, 2);
				st.giveItems(49044, 1);
				st.takeItems(VWKURA, -1);
				st.takeItems(AMORC, -1);
				st.takeItems(KLUK, -1);
				st.takeItems(ME4, -1);				
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
												
						htmltext = "GID.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "GID2.htm";
				}					
				 
					 }
		if(npcId == ALT) {
			if(cond ==1){
						htmltext = "alt1.htm";
				}	
			if(cond ==2){
						htmltext = "alt2.htm";
				}	
			if(cond ==3){
						htmltext = "alt3.htm";
				}
			if(cond ==4){
						htmltext = "alt4.htm";
				}
			if(cond ==5){
						htmltext = "alt5.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == VOLK || npc.getNpcId() == MVOLK)
			if(qs.rollAndGive(VWKURA, 1, 1, 10, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == ORC || npc.getNpcId() == ORCS || npc.getNpcId() == ORCV)
			if(qs.rollAndGive(AMORC, 1, 1, 10, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == VORC){
			qs.rollAndGive(ME4, 1, 1, 10, 100);}
			if(npc.getNpcId() == OBOROT){
			qs.rollAndGive(KLUK, 1, 1, 10, 100);}
			if(qs.getQuestItemsCount(ME4) == 10 && qs.getQuestItemsCount(KLUK) == 10)
					qs.setCond(5);
				
		}
	
		return null;
	}
}