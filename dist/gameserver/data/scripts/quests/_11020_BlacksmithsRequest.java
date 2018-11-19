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

public class _11020_BlacksmithsRequest extends Quest
{
	public final int SUMARI = 30564;
	public final int TIKU = 30582;
	
	//mobs
	public final int MUWA = 20316;
	public final int PGOBLIN = 20320;
	public final int SERGOLEM = 20333;
	public final int DOKOZLA = 20428;
		
	//items
	public final int KRULO = 90269;
	public final int POYAS = 90270;
	public final int RUDA = 90271;
	public final int WKURA = 90272;
	
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ MUWA, KRULO, 20, 100 },
			{ PGOBLIN, POYAS, 20, 100 },
			{ SERGOLEM, RUDA, 20, 100 },
			{ DOKOZLA, WKURA, 20, 100 },						
	};
	
	public _11020_BlacksmithsRequest()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(TIKU);
		addTalkId(SUMARI);		
		addQuestItem(KRULO);
		addQuestItem(POYAS);
		addQuestItem(RUDA);		
		addQuestItem(WKURA);		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ RUDA, KRULO, POYAS, WKURA });


		addLevelCheck("lvl.htm", 11, 20);
		addRaceCheck("lvl.htm", Race.ORC);
		addQuestCompletedCheck("questnotdone.htm", 11019);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("tiku2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sumari2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("sumari4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90308, 1);
				st.giveItems(90309, 1);
				st.giveItems(5790, 700);
				
				st.takeItems(KRULO, -1);
				st.takeItems(POYAS, -1);
				st.takeItems(RUDA, -1);
				st.takeItems(WKURA, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("sumari4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90306, 1);
				st.giveItems(90307, 1);
				st.giveItems(5789, 1000);
				
				st.takeItems(KRULO, -1);
				st.takeItems(POYAS, -1);
				st.takeItems(RUDA, -1);
				st.takeItems(WKURA, -1);
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

		if(npcId == TIKU) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "tiku.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "tiku2.htm";
				}					
				 
					 }
		if(npcId == SUMARI) {
			if(cond ==1){
						htmltext = "sumari.htm";
				}	
			if(cond ==2){
						htmltext = "sumari2.htm";
				}				
			if(cond ==6){
						htmltext = "sumari3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == MUWA)
			if(qs.rollAndGive(KRULO, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == PGOBLIN)
			if(qs.rollAndGive(POYAS, 1, 1, 20, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == SERGOLEM)
			if(qs.rollAndGive(RUDA, 1, 1, 20, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == DOKOZLA)
			if(qs.rollAndGive(WKURA, 1, 1, 20, 100))			
					qs.setCond(6);				
		}
		return null;
	}
}