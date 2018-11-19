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

public class _10996_TemplesDecorations extends Quest
{
	public final int SUMARI = 30538;
	public final int TIKU = 30516;
	
	//mobs
	public final int MUWA = 20370;
	public final int PUMA = 20510;
	public final int LORDG = 20528;
	public final int PRBG = 20323;
	public final int BAZG = 20521;
	public final int OBSG = 20526;
		
	//items
	public final int KRULO = 90291;
	public final int MEH = 90292;
	public final int KAMEN = 90293;
	public final int RUDA = 90294;
	
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ MUWA, KRULO, 20, 100 },
			{ PUMA, MEH, 20, 100 },
			{ LORDG, KAMEN, 20, 100 },
			{ PRBG, KAMEN, 20, 100 },						
			{ BAZG, RUDA, 20, 100 },
			{ OBSG, RUDA, 20, 100 },
	};
	
	public _10996_TemplesDecorations()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(TIKU);
		addTalkId(SUMARI);		
		addQuestItem(KRULO);
		addQuestItem(MEH);
		addQuestItem(RUDA);		
		addQuestItem(KAMEN);		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ RUDA, KRULO, MEH, KAMEN });


		addLevelCheck("lvl.htm", 11, 20);
		addRaceCheck("lvl.htm", Race.DWARF);
		addQuestCompletedCheck("questnotdone.htm", 10995);
		
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
				st.takeItems(MEH, -1);
				st.takeItems(RUDA, -1);
				st.takeItems(KAMEN, -1);				
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
				st.takeItems(MEH, -1);
				st.takeItems(RUDA, -1);
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
			if(npc.getNpcId() == PUMA)
			if(qs.rollAndGive(MEH, 1, 1, 20, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == LORDG || npc.getNpcId() == PRBG)
			if(qs.rollAndGive(KAMEN, 1, 1, 20, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == BAZG || npc.getNpcId() == OBSG)
			if(qs.rollAndGive(RUDA, 1, 1, 20, 100))			
					qs.setCond(6);				
		}
		return null;
	}


	@Override
	public int getDescriprionId(int state)
	{
		return 102600 + state;
	}
}

