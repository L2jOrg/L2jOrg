package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;

//SanyaDC
public class _11005_PerfectLeatherArmor33 extends Quest
{
	public final int LEK = 30001;
	
	//mobs
	public final int GPAUK = 20103;
	public final int KOGPAUK = 20106;
	public final int OPASNPAUK = 20108;
	public final int UNDINA = 20110;
	public final int STUNDINA = 20113;
	public final int ELUNDINA = 20115;
	
	
	//items
	public final int PAUTINA = 90215;
	public final int ESSENC = 90216;
	
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ GPAUK, PAUTINA, 25, 100 },
			{ KOGPAUK, PAUTINA, 25, 100 },
			{ OPASNPAUK, PAUTINA, 25, 100 },
			{ UNDINA, ESSENC, 20, 100 },
			{ STUNDINA, ESSENC, 20, 100 },
			{ ELUNDINA, ESSENC, 20, 100 },
			
	};
	
	public _11005_PerfectLeatherArmor33()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(LEK);		
		addQuestItem(PAUTINA);
		addQuestItem(ESSENC);
		
		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ PAUTINA, ESSENC });


		addLevelCheck("lvl.htm", 15, 20);
		addRaceCheck("lvl.htm", Race.HUMAN);
		addQuestCompletedCheck("questnotdone.htm", 11004);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("lek2.htm"))
		{
			st.setCond(2);
		}		
		else if(event.equalsIgnoreCase("lek4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);				
				st.giveItems(5790, 700);
				st.takeItems(PAUTINA, -1);
				st.takeItems(ESSENC, -1);						
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("lek4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5789, 1000);
				st.takeItems(PAUTINA, -1);
				st.takeItems(ESSENC, -1);			
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

		if(npcId == LEK) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 15)
												
						htmltext = "lek.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==2){
						htmltext = "lek2.htm";
				}				
			if(cond ==4){
						htmltext = "lek3.htm";
				}					
				 
					 }
	
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == GPAUK || npc.getNpcId() == KOGPAUK || npc.getNpcId() == OPASNPAUK)
			if(qs.rollAndGive(PAUTINA, 1, 1, 25, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == UNDINA || npc.getNpcId() == STUNDINA || npc.getNpcId() == ELUNDINA)
			if(qs.rollAndGive(ESSENC, 1, 1, 20, 100))
				qs.setCond(4);
		}
		
	
		return null;
	}
}