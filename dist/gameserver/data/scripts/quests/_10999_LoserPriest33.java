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

public class _10999_LoserPriest33 extends Quest
{
	
	public final int GERALD = 30650;
	
	//mobs
	public final int KRWMIWA = 21124;
	public final int TRIMDEN = 21125;
	public final int OBOROT = 21126;	
		
	//items
	public final int VOLOSKI = 90303;
	public final int PAUTINA = 90304;
	public final int AMULET = 90305;	
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ KRWMIWA, VOLOSKI, 20, 100 },
			{ TRIMDEN, PAUTINA, 20, 100 },
			{ OBOROT, AMULET, 20, 100 },
							
	};
	
	public _10999_LoserPriest33()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GERALD);
			
		addQuestItem(VOLOSKI);
		addQuestItem(PAUTINA);
		addQuestItem(AMULET);		

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ VOLOSKI, PAUTINA, AMULET });


		addLevelCheck("lvl.htm", 15);
		addRaceCheck("lvl.htm", Race.DWARF);
		addQuestCompletedCheck("questnotdone.htm", 10998);		
	}

	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("GERALD2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("GERALD4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(VOLOSKI, -1);
				st.takeItems(PAUTINA, -1);
				st.takeItems(AMULET, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("GERALD4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(VOLOSKI, -1);
				st.takeItems(PAUTINA, -1);
				st.takeItems(AMULET, -1);				
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

		
		if(npcId == GERALD) {
			if(cond ==0){
						htmltext = "GERALD.htm";
				}	
			if(cond ==2){
						htmltext = "GERALD2.htm";
				}				
			if(cond ==5){
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
			if(npc.getNpcId() == KRWMIWA)
			if(qs.rollAndGive(VOLOSKI, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == TRIMDEN)
			if(qs.rollAndGive(PAUTINA, 1, 1, 20, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == OBOROT)
			if(qs.rollAndGive(AMULET, 1, 1, 20, 100))
				qs.setCond(5);
		}	
		return null;
	}


	@Override
	public int getDescriprionId(int state)
	{
		return 102900 + state;
	}

}