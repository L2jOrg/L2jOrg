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

	public class _10995_MutualBenefit extends Quest
{
	public final int GID = 30601;
	public final int ALT = 30516;
	
	//mobs
	public final int VOLK = 20317;
	public final int WPGOBL = 20327;
	
	public final int ORC = 20446;
	public final int ORCS = 20447;
	
	public final int BANDITG = 20322;
	
	public final int OBOROT = 20307;
	
	public final int PREDVG = 20324;
	
	//items
	public final int KLUK = 90284;
	public final int NAVIG = 90285;
	public final int AMULET = 90286;
	public final int ME4 = 90287;
	public final int HVOST = 90288;
	public final int OJEREL = 90289;
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ VOLK, KLUK, 10, 100 },
			{ WPGOBL, NAVIG, 10, 100 },
			{ ORC, AMULET, 10, 100 },
			{ ORCS, AMULET, 10, 100 },
			{ BANDITG, ME4, 10, 100 },
			{ OBOROT, HVOST, 10, 100 },
			{ PREDVG, OJEREL, 10, 100 },
	};
	
	public _10995_MutualBenefit()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GID);
		addTalkId(ALT);		
		addQuestItem(KLUK);
		addQuestItem(NAVIG);
		addQuestItem(AMULET);
		addQuestItem(ME4);
		addQuestItem(HVOST);
		addQuestItem(OJEREL);
		//addKillId(MOBH);


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ NAVIG, AMULET, KLUK, ME4, HVOST, OJEREL });


		addLevelCheck("lvl.htm", 2, 20);
		addRaceCheck("lvl.htm", Race.DWARF);
		
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
			if(st.getCond() == 7)
			{			 			
				st.addExpAndSp(70000, 0);
				st.giveItems(29471, 1);
				st.giveItems(29497, 2);
				st.giveItems(49053, 1);
				st.takeItems(KLUK, -1);
				st.takeItems(NAVIG, -1);
				st.takeItems(AMULET, -1);
				st.takeItems(ME4, -1);
				st.takeItems(HVOST, -1);
				st.takeItems(OJEREL, -1);				
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
			if(cond ==7){
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
			if(npc.getNpcId() == VOLK)
			if(qs.rollAndGive(KLUK, 1, 1, 10, 100));			
			if(npc.getNpcId() == WPGOBL)
			if(qs.rollAndGive(NAVIG, 1, 1, 10, 100));
			if(qs.getQuestItemsCount(KLUK) == 10 && qs.getQuestItemsCount(NAVIG) == 10)
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == ORC || npc.getNpcId() == ORCS)
			if(qs.rollAndGive(AMULET, 1, 1, 10, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == BANDITG)
			if(qs.rollAndGive(ME4, 1, 1, 10, 100))
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == OBOROT)
			if(qs.rollAndGive(HVOST, 1, 1, 10, 100))
					qs.setCond(6);				
		}
		if(qs.getCond() == 6 )
		{
			if(npc.getNpcId() == PREDVG)
			if(qs.rollAndGive(OJEREL, 1, 1, 10, 100))
					qs.setCond(7);				
		}
	
		return null;
	}



	@Override
	public int getDescriprionId(int state)
	{
		return 102500 + state;
	}
}

