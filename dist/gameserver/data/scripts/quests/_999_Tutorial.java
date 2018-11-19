package quests;

import java.util.StringTokenizer;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.listener.actor.player.OnPickupItemListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;

/**
 * @author Bonux
**/
/*TODO:
Начать использовать оффлайк TutorialEvent'ы
1. Через Н секунд: Вам нужно найти Помощника Наставника
2. Добавить туториал о перемещении камеры.
3. Реализовать туториал для Эльфов, Темных Эльфов, Орков и Гномов.
*/
public final class _999_Tutorial extends Quest
{
	private class PickupItemListener implements OnPickupItemListener
	{
		@Override
		public void onPickupItem(Player player, ItemInstance item)
		{
			QuestState st = player.getQuestState(_999_Tutorial.this);
			if(st == null)
				return;

			if(item.getItemId() == BLUE_GEMSTONE)
			{
				final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
				if(nh_state == 1)
				{
					st.set(NEWBIE_HELPER_STATE, 2);
					st.playTutorialVoice("tutorial_voice_013");
					st.showQuestionMark(false, 5);
					st.playSound(SOUND_TUTORIAL);
					st.getPlayer().removeListener(_pickupItemListener);
				}
			}
		}
	}

	private class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState st = player.getQuestState(_999_Tutorial.this);
			if(st == null)
			{
				newQuestState(player);
				st = player.getQuestState(_999_Tutorial.this);
			}
			onTutorialEvent(ENTER_WORLD_EVENT, false, "", st);
		}
	}

	private class PlayerExitListener implements OnPlayerExitListener
	{
		@Override
		public void onPlayerExit(Player player)
		{
			QuestState st = player.getQuestState(_999_Tutorial.this);
			if(st == null)
				return;

			st.stopQuestTimers();
		}
	}

	// Var's
	private static final String QUESTION_MARK_STATE = "question_mark_state";
	private static final String NEWBIE_HELPER_STATE = "newbie_helper_state";
	private static final String SHOTS_RECEIVED = "shots_received";

	// Events
	// Данные переменные не изменять, они вшиты в ядро.
	private static final String ENTER_WORLD_EVENT = "EW"; // Вход в мир.
	private static final String QUEST_TIMER_EVENT = "QT"; // Квестовый таймер.
	private static final String QUESTION_MARK_EVENT = "QM"; // Вопросытельный знак.
	private static final String CLIENT_EVENT = "CE"; // Дейтсвия клиента. (100 - Class Change, 200 - Death, 300 - Level UP)
	private static final String TUTORIAL_BYPASS_EVENT = "BYPASS"; // Использование байпасса в туториале.
	private static final String TUTORIAL_LINK_EVENT = "LINK"; // Использование ссылки в туториале.

	// NPC's
	private static final int ROIEN_GRAND_MASTER = 30008; // NPC: Роен Великий Мастер
	private static final int NEWBIE_HELPER_HUMAN_F = 30009; // NPC: Помощник Новичков
	private static final int GALLINT_GRAND_MAGISTER = 30017; // NPC: Галлинт Великий Магистр
	private static final int NEWBIE_HELPER_HUMAN_M = 30019; // NPC: Помощник Новичков
	private static final int MITRAELL_BROWN_ELF_CHIEF = 30129; // NPC: Митраэль Вождь Темных Эльфов
	private static final int NEWBIE_HELPER_DARK_ELF = 30131; // NPC: Помощник Новичков
	private static final int NERUPA = 30370; // NPC: Нерупа
	private static final int NEWBIE_HELPER_ELF = 30400; // NPC: Помощник Новичков
	private static final int LAFERON_FOREMAN = 30528; // NPC: Лаферон Бригадир
	private static final int NEWBIE_HELPER_DWARVEN = 30530; // NPC: Помощник Новичков
	private static final int VULKUS_FLAME_GUARDIAN = 30573; // NPC: Вулькус Хранитель Огня
	private static final int NEWBIE_HELPER_ORC = 30575; // NPC: Помощник Новичков
	private static final int NEWBIE_GUIDE_HUMAN = 30598; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_ELF = 30599; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_DARK_ELF = 30600; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_DWARVEN = 30601; // NPC: Гид Новичков
	private static final int NEWBIE_GUIDE_ORC = 30602; // NPC: Гид Новичков

	private static final int[] NPC_LIST = {
		ROIEN_GRAND_MASTER,
		NEWBIE_HELPER_HUMAN_F,
		GALLINT_GRAND_MAGISTER,
		NEWBIE_HELPER_HUMAN_M,
		MITRAELL_BROWN_ELF_CHIEF,
		NEWBIE_HELPER_DARK_ELF,
		NERUPA,
		NEWBIE_HELPER_ELF,
		LAFERON_FOREMAN,
		NEWBIE_HELPER_DWARVEN,
		VULKUS_FLAME_GUARDIAN,
		NEWBIE_HELPER_ORC,
		NEWBIE_GUIDE_HUMAN,
		NEWBIE_GUIDE_ELF,
		NEWBIE_GUIDE_DARK_ELF,
		NEWBIE_GUIDE_DWARVEN,
		NEWBIE_GUIDE_ORC
	};

	// Monster's
	private static final int GREMLIN = 18342; // Монстр: Гремлин

	// Item's
	private static final int BLUE_GEMSTONE = 6353; // Предмет: Синий Самоцвет
	private static final int SOULSHOT_NOGRADE_NOVICE = 5789; // Заряд Души: Без Ранга для Новичков
	private static final int SPIRITSHOT_NOGRADE_NOVICE = 5790; // Заряд Духа: Без Ранга для Новичков
	private static final int ADVENTURERS_SCROLL_OF_ESCAPE = 10650; // Свиток Телепорта Путешественника
	private static final int HASTE_POTION_FOR_NOVECES = 49036; // Зелье Ускорения для Путешественника
	private static final int RECOMMENDATION_F = 1067; // Рекомендация
	private static final int RECOMMENDATION_M = 1068; // Рекомендация
	private static final int LEAF_OF_THE_MOTHER_TREE = 1069; // Лист Древа Жизни
	private static final int BLOOD_OF_MITRAELL = 1070; // Кровь Митраэля
	private static final int MARK_OF_FLAME = 1496; // Знак Пламени
	private static final int MINING_LICENSE = 1498; // Лицензия Шахтера

	// Other
	private static final double BLUE_GEMSTONE_DROP_CHANCE = 50.; // Шанс выпадения Синего Самоцвета (50%).

	private final OnPickupItemListener _pickupItemListener = new PickupItemListener();
	private final OnPlayerEnterListener _playerEnterListener = new PlayerEnterListener();
	private final OnPlayerExitListener _playerExitListener = new PlayerExitListener();

	public _999_Tutorial()
	{
		super(PARTY_NONE, REPEATABLE);

		addFirstTalkId(NPC_LIST);
		addKillId(GREMLIN);

		CharListenerList.addGlobal(_playerEnterListener);
		CharListenerList.addGlobal(_playerExitListener);
	}

	@Override
	public String onTutorialEvent(final String event, final boolean quest, final String value, final QuestState st)
	{
		//st.getPlayer().sendMessage("onTutorialEvent: " + event + " " + value);
		if(event.equalsIgnoreCase(ENTER_WORLD_EVENT))
			return onEnterWorld(st);

		if(event.equalsIgnoreCase(QUESTION_MARK_EVENT))
			return onQuestionMark(Integer.parseInt(value), st);

		if(event.equalsIgnoreCase(TUTORIAL_LINK_EVENT))
			return onTutorialLink(value, st);

		final StringTokenizer tokenizer = new StringTokenizer(event, "_");
		final String cmd = tokenizer.nextToken();
		if(cmd.equalsIgnoreCase(QUEST_TIMER_EVENT))
		{
			int timerId = tokenizer.hasMoreTokens() ? Integer.parseInt(tokenizer.nextToken()) : 0;
			return onQuestTimer(timerId, st);
		}

		return null;
	}

	@Override
	public String onEvent(final String event, final QuestState st, final NpcInstance npc)
	{
		final StringTokenizer tokenizer = new StringTokenizer(event, "_");
		final String cmd = tokenizer.nextToken();
		if(cmd.equalsIgnoreCase(QUEST_TIMER_EVENT))
		{
			notifyTutorialEvent(event, false, "", st);
			return null;
		}

		String html = null;

		final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
		if(event.equalsIgnoreCase("30008-3.htm") || event.equalsIgnoreCase("30017-3.htm") || event.equalsIgnoreCase("30370-3.htm") || event.equalsIgnoreCase("30129-3.htm") || event.equalsIgnoreCase("30573-3.htm") || event.equalsIgnoreCase("30528-3.htm"))
		{
			if(nh_state == 3)
			{
				html = event;

				st.set(NEWBIE_HELPER_STATE, 4);

				switch(npc.getNpcId())
				{
					case ROIEN_GRAND_MASTER:
						st.takeItems(RECOMMENDATION_F, st.getQuestItemsCount(RECOMMENDATION_F));
						break;
					case GALLINT_GRAND_MAGISTER:
						st.takeItems(RECOMMENDATION_M, st.getQuestItemsCount(RECOMMENDATION_M));
						break;
					case NERUPA:
						st.takeItems(LEAF_OF_THE_MOTHER_TREE, st.getQuestItemsCount(LEAF_OF_THE_MOTHER_TREE));
						break;
					case MITRAELL_BROWN_ELF_CHIEF:
						st.takeItems(BLOOD_OF_MITRAELL, st.getQuestItemsCount(BLOOD_OF_MITRAELL));
						break;
					case VULKUS_FLAME_GUARDIAN:
						st.takeItems(MARK_OF_FLAME, st.getQuestItemsCount(MARK_OF_FLAME));
						break;
					case LAFERON_FOREMAN:
						st.takeItems(MINING_LICENSE, st.getQuestItemsCount(MINING_LICENSE));
						break;
				}

				if(st.getPlayer().isMageClass() && st.getPlayer().getRace() != Race.ORC)
				{
					st.giveItems(SPIRITSHOT_NOGRADE_NOVICE, 100, false);
					st.playTutorialVoice("tutorial_voice_027");
				}
				else
				{
					st.giveItems(SOULSHOT_NOGRADE_NOVICE, 200, false);
					st.playTutorialVoice("tutorial_voice_026");
				}

				st.showQuestionMark(false, 28);
			}
		}

		return html;
	}

	@Override
	public String onFirstTalk(final NpcInstance npc, final Player player)
	{
		QuestState st = player.getQuestState(this);
		if(st == null)
			return null;

		String html = null;

		final int npcId = npc.getNpcId();
		final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
		switch(npcId)
		{
			case ROIEN_GRAND_MASTER:
			case GALLINT_GRAND_MAGISTER:
			case NERUPA:
			case MITRAELL_BROWN_ELF_CHIEF:
			case VULKUS_FLAME_GUARDIAN:
			case LAFERON_FOREMAN:
				if(!checkNpcCondition(npcId, st.getPlayer()) || nh_state == 0 || nh_state == 1 || nh_state == 2)
					html = npc.getNpcId() + "-1.htm";
				else if(nh_state == 3)
					html = npc.getNpcId() + "-2.htm";
				else if(nh_state == 4)
					html = npc.getNpcId() + "-4.htm";
				break;
			case NEWBIE_HELPER_HUMAN_F:
			case NEWBIE_HELPER_HUMAN_M:
			case NEWBIE_HELPER_ELF:
			case NEWBIE_HELPER_DARK_ELF:
			case NEWBIE_HELPER_ORC:
			case NEWBIE_HELPER_DWARVEN:
				if(!checkNpcCondition(npcId, st.getPlayer()))
					html = npc.getNpcId() + "-0.htm";
				else if(nh_state == 0)
				{
					st.set(NEWBIE_HELPER_STATE, 1);
					st.startQuestTimer(QUEST_TIMER_EVENT + "_" + 2, 30000L);
					st.getPlayer().addListener(_pickupItemListener);

					if(player.isMageClass())
						html = npc.getNpcId() + "-1b.htm";
					else
						html = npc.getNpcId() + "-1a.htm";
				}
				else if(nh_state == 1 || nh_state == 2)
				{
					if(st.getQuestItemsCount(BLUE_GEMSTONE) == 0)
					{
						if(player.isMageClass())
							html = npc.getNpcId() + "-2b.htm";
						else
							html = npc.getNpcId() + "-2a.htm";
					}
					else
					{
						st.set(NEWBIE_HELPER_STATE, 3);

						switch(npcId)
						{
							case NEWBIE_HELPER_HUMAN_F:
								st.giveItems(RECOMMENDATION_F, 1, false);
								break;
							case NEWBIE_HELPER_HUMAN_M:
								st.giveItems(RECOMMENDATION_M, 1, false);
								break;
							case NEWBIE_HELPER_ELF:
								st.giveItems(LEAF_OF_THE_MOTHER_TREE, 1, false);
								break;
							case NEWBIE_HELPER_DARK_ELF:
								st.giveItems(BLOOD_OF_MITRAELL, 1, false);
								break;
							case NEWBIE_HELPER_ORC:
								st.giveItems(MARK_OF_FLAME, 1, false);
								break;
							case NEWBIE_HELPER_DWARVEN:
								st.giveItems(MINING_LICENSE, 1, false);
								break;
						}

						if(player.isMageClass() && player.getRace() != Race.ORC)
						{
							st.giveItems(SPIRITSHOT_NOGRADE_NOVICE, 100, false);
							st.playTutorialVoice("tutorial_voice_027");
							html = npc.getNpcId() + "-3b.htm";
						}
						else
						{
							st.giveItems(SOULSHOT_NOGRADE_NOVICE, 200, false);
							st.playTutorialVoice("tutorial_voice_026");
							html = npc.getNpcId() + "-3a.htm";
						}

						st.takeItems(BLUE_GEMSTONE, st.getQuestItemsCount(BLUE_GEMSTONE));
						st.giveItems(ADVENTURERS_SCROLL_OF_ESCAPE, 5, false);
						st.giveItems(HASTE_POTION_FOR_NOVECES, 5, false);
					}
				}
				else if(nh_state == 3)
					html = npc.getNpcId() + "-4.htm";
				else if(nh_state == 4)
					html = npc.getNpcId() + "-5.htm";
				break;
			case NEWBIE_GUIDE_HUMAN:
			case NEWBIE_GUIDE_ELF:
			case NEWBIE_GUIDE_DARK_ELF:
			case NEWBIE_GUIDE_DWARVEN:
			case NEWBIE_GUIDE_ORC:
				if(player.getLevel() <= 20 && player.getClassLevel() == ClassLevel.NONE && st.getInt(SHOTS_RECEIVED) == 0) // TODO: Check conditions.
				{
					st.set(SHOTS_RECEIVED, 1);
					if(st.getPlayer().isMageClass() && st.getPlayer().getRace() != Race.ORC)
						st.giveItems(SPIRITSHOT_NOGRADE_NOVICE, 100, false);
					else
						st.giveItems(SOULSHOT_NOGRADE_NOVICE, 200, false);
				}
				return "";
		}
		return html;
	}

	@Override
	public String onKill(final NpcInstance npc, final QuestState st)
	{
		final int npcId = npc.getNpcId();
		switch(npcId)
		{
			case GREMLIN:
				final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
				if(nh_state == 1 || nh_state == 2)
				{
					st.cancelQuestTimer(QUEST_TIMER_EVENT + "_" + 2);
					if(st.getQuestItemsCount(BLUE_GEMSTONE) < 1)
					{
						if(nh_state == 2 || Rnd.chance(BLUE_GEMSTONE_DROP_CHANCE))
						{
							npc.dropItem(st.getPlayer(), BLUE_GEMSTONE, 1);
							st.playSound(SOUND_TUTORIAL);
						}
					}
				}
				break;
		}
		return null;
	}

	private String onEnterWorld(final QuestState st)
	{
		if(st.getPlayer().getLevel() > 5)
			return null;

		final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
		final int qm_state = st.getInt(QUESTION_MARK_STATE);
		if(nh_state == 0)
		{
			if(qm_state == 0)
			{
				st.set(QUESTION_MARK_STATE, 1);
				st.startQuestTimer(QUEST_TIMER_EVENT + "_" + 1, 10000L);
			}
			else if(qm_state == 1)
			{
				st.showQuestionMark(false, 1);
				st.playSound(SOUND_TUTORIAL);
				st.playTutorialVoice("tutorial_voice_006");
			}
			else if(qm_state == 2)
			{
				st.showQuestionMark(false, 2);
				st.playSound(SOUND_TUTORIAL);
			}
		}
		else if(nh_state == 1 || nh_state == 2)
		{
			if(nh_state == 1)
				st.getPlayer().addListener(_pickupItemListener);

			st.showQuestionMark(false, 2);
			st.playSound(SOUND_TUTORIAL);
		}
		return null;
	}

	private String onQuestTimer(final int timerId, final QuestState st)
	{
		String html = null;

		final int nh_state = st.getInt(NEWBIE_HELPER_STATE);
		final int qm_state = st.getInt(QUESTION_MARK_STATE);
		switch(timerId)
		{
			case 1:
				if(qm_state == 1)
				{
					if(nh_state == 0)
					{
						String voice = null;

						final Player player = st.getPlayer();
						switch(player.getRace())
						{
							case HUMAN:
								if(player.isMageClass())
								{
									html = "tutorial_human_mage001.htm";
									voice = "tutorial_voice_001b";
								}
								else
								{
									html = "tutorial_human_fighter001.htm";
									voice = "tutorial_voice_001a";
								}
								break;
							case ELF:
								if(player.isMageClass())
								{
									html = "tutorial_elven_mage001.htm";
									voice = "tutorial_voice_001d";
								}
								else
								{
									html = "tutorial_elven_fighter001.htm";
									voice = "tutorial_voice_001c";
								}
								break;
							case DARKELF:
								if(player.isMageClass())
								{
									html = "tutorial_delf_mage001.htm";
									voice = "tutorial_voice_001f";
								}
								else
								{
									html = "tutorial_delf_fighter001.htm";
									voice = "tutorial_voice_001e";
								}
								break;
							case ORC:
								if(player.isMageClass())
								{
									html = "tutorial_orc_mage001.htm";
									voice = "tutorial_voice_001h";
								}
								else
								{
									html = "tutorial_orc_fighter001.htm";
									voice = "tutorial_voice_001g";
								}
								break;
							case DWARF:
								html = "tutorial_dwarven_fighter001.htm";
								voice = "tutorial_voice_001i";
								break;
						}

						st.playTutorialVoice(voice);
						st.cancelQuestTimer(QUEST_TIMER_EVENT);
					}
				}
				break;
			case 2:
				if(nh_state == 1)
					st.playTutorialVoice("tutorial_voice_009a");
				break;
		}

		return html;
	}

	private String onQuestionMark(final int markId, final QuestState st)
	{
		String html = null;

		final Player player = st.getPlayer();
		if(markId == 1)
		{
			st.set(QUESTION_MARK_STATE, 2);
			player.sendPacket(new ExShowScreenMessage(NpcString.CONVERSATION_WITH_NEWBIE_HELPER, 5000, ScreenMessageAlign.TOP_CENTER, true));
			html = "tutorial_02.htm";
		}
		else if(markId == 2)
		{
			switch(player.getRace())
			{
				case HUMAN:
					if(player.isMageClass())
						html = "tutorial_human_mage002.htm";
					else
						html = "tutorial_human_fighter002.htm";
					break;
				case ELF:
					html = "tutorial_elven002.htm";
					break;
				case DARKELF:
					html = "tutorial_delf002.htm";
					break;
				case ORC:
					html = "tutorial_orc002.htm";
					break;
				case DWARF:
					html = "tutorial_dwarven002.htm";
					break;
			}
		}
		else if(markId == 5)
		{
			switch(player.getRace())
			{
				case HUMAN:
					if(player.isMageClass())
						st.addRadarWithMap(-91036, 248044, -3568);
					else
						st.addRadarWithMap(-71424, 258336, -3109);
					break;
				case ELF:
					st.addRadarWithMap(46112, 41200, -3504);
					break;
				case DARKELF:
					st.addRadarWithMap(28384, 11056, -4232);
					break;
				case ORC:
					st.addRadarWithMap(-56736, -113680, -672);
					break;
				case DWARF:
					st.addRadarWithMap(108567, -173994, -408);
					break;
			}
			html = "tutorial_03.htm";
		}
		else if(markId == 28)
		{
			switch(player.getRace())
			{
				case HUMAN:
					st.addRadarWithMap(-84081, 243277, -3723);
					break;
				case ELF:
					st.addRadarWithMap(45475, 48359, -3060);
					break;
				case DARKELF:
					st.addRadarWithMap(12111, 16686, -4582);
					break;
				case ORC:
					st.addRadarWithMap(-45032, -113598, -192);
					break;
				case DWARF:
					st.addRadarWithMap(115632, -177996, -905);
					break;
			}
		}

		return html;
	}

	private String onTutorialLink(final String value, final QuestState st)
	{
		String html = null;

		final StringTokenizer tokenizer = new StringTokenizer(value, "_");
		final String cmd = tokenizer.nextToken();
		if(cmd.equalsIgnoreCase("tutorial"))
		{
			final String cmd2 = tokenizer.nextToken();
			if(cmd2.equalsIgnoreCase("close"))
			{
				if(tokenizer.hasMoreTokens())
				{
					int actionId = Integer.parseInt(tokenizer.nextToken());
					switch(actionId)
					{
						case 1:
							st.showQuestionMark(false, 1);
							break;
						case 2:
							html = "tutorial_00.htm";
							break;
						case 7:
							html = "tutorial_01.htm";
							break;
					}
				}
				st.getPlayer().sendPacket(TutorialCloseHtmlPacket.STATIC);
			}
		}
		return html;
	}

	private boolean checkNpcCondition(int npcId, Player player)
	{
		switch(npcId)
		{
			case ROIEN_GRAND_MASTER:
			case NEWBIE_HELPER_HUMAN_F:
				return player.getRace() == Race.HUMAN && !player.isMageClass();
			case GALLINT_GRAND_MAGISTER:
			case NEWBIE_HELPER_HUMAN_M:
				return player.getRace() == Race.HUMAN && player.isMageClass();
			case NERUPA:
			case NEWBIE_HELPER_ELF:
				return player.getRace() == Race.ELF;
			case MITRAELL_BROWN_ELF_CHIEF:
			case NEWBIE_HELPER_DARK_ELF:
				return player.getRace() == Race.DARKELF;
			case VULKUS_FLAME_GUARDIAN:
			case NEWBIE_HELPER_ORC:
				return player.getRace() == Race.ORC;
			case LAFERON_FOREMAN:
			case NEWBIE_HELPER_DWARVEN:
				return player.getRace() == Race.DWARF;
		}
		return false;
	}

	@Override
	public boolean isVisible(final Player player)
	{
		return false;
	}
}