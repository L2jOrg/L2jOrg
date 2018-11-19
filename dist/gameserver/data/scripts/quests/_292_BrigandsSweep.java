package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Bonux
**/
public final class _292_BrigandsSweep extends Quest
{
	// NPC's
	private static final int SPIRON_GOLDEN_WHEELS = 30532;	// Спирон - Старейшина
	private static final int BALANKI_SILVER_SCALES = 30533;	// Баланки Старейшина

	// Monster's
	private static final int GOBLIN_BRIGAND = 20322;	// Бандит Гоблинов
	private static final int GOBLIN_BRIGAND_LEADER = 20323;	// Предводитель Банды Гоблинов
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 20324;	// Помощник Предводителя Банды Гоблинов
	private static final int GOBLIN_SNOOPER = 20327;	// Шпион Гоблинов
	private static final int GOBLIN_LORD = 20528;	// Лорд Гоблинов

	// Item's
	private static final int GOBLIN_NECKLACE = 1483;	// Ожерелье Гоблина
	private static final int GOBLIN_PENDANT = 1484;	// Подвеска Гоблина
	private static final int GOBLIN_LORD_PENDANT = 1485;	// Подвеска Вожака Гоблинов
	private static final int SUSPICIOUS_MEMO = 1486;	// Подозрительная Записка
	private static final int SUSPICIOUS_CONTRACT = 1487;	// Подозрительный Контракт

	// Other
	private static final int GOBLIN_ACCESSORY_DROP_CHANCE = 40;	// Шанс дропа гоблинских украшений
	private static final int MEMO_DROP_CHANCE = 10;	// Шанс дропа: Подозрительная Записка

	public _292_BrigandsSweep()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(SPIRON_GOLDEN_WHEELS);
		addTalkId(BALANKI_SILVER_SCALES);

		addKillId(GOBLIN_BRIGAND);
		addKillId(GOBLIN_BRIGAND_LEADER);
		addKillId(GOBLIN_BRIGAND_LIEUTENANT);
		addKillId(GOBLIN_SNOOPER);
		addKillId(GOBLIN_LORD);

		addQuestItem(GOBLIN_NECKLACE);
		addQuestItem(GOBLIN_PENDANT);
		addQuestItem(GOBLIN_LORD_PENDANT);
		addQuestItem(SUSPICIOUS_MEMO);
		addQuestItem(SUSPICIOUS_CONTRACT);

		addRaceCheck("elder_spiron_q0292_00.htm", Race.DWARF);
		addLevelCheck("elder_spiron_q0292_01.htm", 5/*, 18*/);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("elder_spiron_q0292_03.htm"))
			qs.setCond(1);
		else if(event.equalsIgnoreCase("elder_spiron_q0292_06.htm"))
			qs.finishQuest();
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case SPIRON_GOLDEN_WHEELS:
				if(cond == 0)
					htmltext = "elder_spiron_q0292_02.htm";
				else if(cond == 1 || cond == 2)
				{
					long reward = st.getQuestItemsCount(GOBLIN_NECKLACE) * 6 + st.getQuestItemsCount(GOBLIN_PENDANT) * 8 + st.getQuestItemsCount(GOBLIN_LORD_PENDANT) * 10 + st.getQuestItemsCount(SUSPICIOUS_CONTRACT) * 100;
					if(reward == 0)
						return "elder_spiron_q0292_04.htm";

					if(st.getQuestItemsCount(SUSPICIOUS_CONTRACT) != 0)
						htmltext = "elder_spiron_q0292_10.htm";
					else if(st.getQuestItemsCount(SUSPICIOUS_MEMO) == 0)
						htmltext = "elder_spiron_q0292_05.htm";
					else if(st.getQuestItemsCount(SUSPICIOUS_MEMO) >= 1)
						htmltext = "elder_spiron_q0292_08.htm";
					else
						htmltext = "elder_spiron_q0292_09.htm";

					st.takeItems(GOBLIN_NECKLACE, -1);
					st.takeItems(GOBLIN_PENDANT, -1);
					st.takeItems(GOBLIN_LORD_PENDANT, -1);
					st.takeItems(SUSPICIOUS_CONTRACT, -1);
					st.giveItems(ADENA_ID, reward, 1000);
				}
				break;
			case BALANKI_SILVER_SCALES:
				if(cond == 2)
				{
					if(st.getQuestItemsCount(SUSPICIOUS_CONTRACT) == 0)
						htmltext = "balanki_q0292_01.htm";
					else
					{
						st.takeItems(SUSPICIOUS_CONTRACT, -1);
						st.giveItems(ADENA_ID, 100, true);
						st.setCond(1);
						htmltext = "balanki_q0292_02.htm";
					}
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1 || cond == 2)
		{
			if(st.getQuestItemsCount(SUSPICIOUS_CONTRACT) == 0 && Rnd.chance(MEMO_DROP_CHANCE))
			{
				if(st.getQuestItemsCount(SUSPICIOUS_MEMO) < 3)
				{
					st.giveItems(SUSPICIOUS_MEMO, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else
				{
					st.takeItems(SUSPICIOUS_MEMO, -1);
					st.giveItems(SUSPICIOUS_CONTRACT, 1);
					st.setCond(2);
				}
			}
			else
			{
				int rewardId;
				switch(npcId)
				{
					case GOBLIN_BRIGAND:
					case GOBLIN_BRIGAND_LEADER:
					case GOBLIN_SNOOPER:
						rewardId = GOBLIN_NECKLACE;
						break;
					case GOBLIN_BRIGAND_LIEUTENANT:
						rewardId = GOBLIN_PENDANT;
						break;
					case GOBLIN_LORD:
						rewardId = GOBLIN_LORD_PENDANT;
						break;
					default:
						return null;
				}

				st.rollAndGive(rewardId, 1, GOBLIN_ACCESSORY_DROP_CHANCE);
			}
		}
		return null;
	}
}