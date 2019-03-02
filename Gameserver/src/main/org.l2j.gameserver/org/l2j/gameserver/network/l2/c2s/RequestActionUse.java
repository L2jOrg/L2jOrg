package org.l2j.gameserver.network.l2.c2s;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.holder.PetDataHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.instancemanager.BotReportManager;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.Servitor.AttackMode;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.base.PetType;
import org.l2j.gameserver.model.instances.*;
import org.l2j.gameserver.model.instances.residences.SiegeFlagInstance;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.ExAskCoupleAction;
import org.l2j.gameserver.network.l2.s2c.ExInzoneWaitingInfo;
import org.l2j.gameserver.network.l2.s2c.ExTacticalSign;
import org.l2j.gameserver.network.l2.s2c.PrivateStoreBuyManageList;
import org.l2j.gameserver.network.l2.s2c.PrivateStoreManageList;
import org.l2j.gameserver.network.l2.s2c.RecipeShopManageListPacket;
import org.l2j.gameserver.network.l2.s2c.SocialActionPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.utils.TradeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * packet type id 0x56
 * format:		cddc
 */
public class RequestActionUse extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestActionUse.class);

	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/* type:
	 * 0 - action
	 * 1 - pet action
	 * 2 - summon action
	 * 3 - social
	 * 4 - couple social
	 * 5 - one more summons actions
	 */
	public static enum Action
	{
		// Действия персонажей
		ACTION0(0, 0, 0), // Сесть/встать
		ACTION1(1, 0, 0), // Изменить тип передвижения, шаг/бег
		ACTION7(7, 0, 0), // Next Target
		ACTION10(10, 0, 0), // Запрос на создание приватного магазина продажи
		ACTION28(28, 0, 0), // Запрос на создание приватного магазина покупки
		ACTION37(37, 0, 0), // Создание магазина Common Craft
		ACTION38(38, 0, 0), // Mount
		ACTION51(51, 0, 0), // Создание магазина Dwarven Craft
		ACTION61(61, 0, 0), // Запрос на создание приватного магазина продажи (Package)
		ACTION96(96, 0, 0), // Quit Party Command Channel?
		ACTION97(97, 0, 0), // Request Party Command Channel Info?
		ACTION65(65, 0, 0), // кнопка "Сообщить о боте"

		ACTION76(76, 0, 0), // Приглашение друга
		ACTION78(78, 0, 0), // Использование Знака 1
		ACTION79(79, 0, 0), // Использование Знака 2
		ACTION80(80, 0, 0), // Использование Знака 3
		ACTION81(81, 0, 0), // Использование Знака 4
		ACTION82(82, 0, 0), // Автоприцел Знаком 1
		ACTION83(83, 0, 0), // Автоприцел Знаком 2
		ACTION84(84, 0, 0), // Автоприцел Знаком 3
		ACTION85(85, 0, 0), // Автоприцел Знаком 4
		ACTION86(86, 0, 0), // Начать/прервать автоматический поиск группы
		ACTION90(90, 0, 0), // Подземелье

		// Действия петов
		ACTION15(15, 1, 0), // Pet Follow
		ACTION16(16, 1, 0), // Атака петом
		ACTION17(17, 1, 0), // Отмена действия у пета
		ACTION19(19, 1, 0), // Отзыв пета
		ACTION53(54, 1, 0), // Передвинуть пета к цели

		// Скиллы петов
		ACTION1001(1001, 1, 0), // Предельный Ускоритель
		ACTION1003(1003, 1, 4710), // Дикое Оглушение
		ACTION1004(1004, 1, 4711), // Дикая Защита
		ACTION1005(1005, 1, 4712), // Яркая Вспышка
		ACTION1006(1006, 1, 4713), // Исцеляющий Свет
		ACTION1041(1041, 1, 5442), // Укус
		ACTION1042(1042, 1, 5444), // Кувалда
		ACTION1043(1043, 1, 5443), // Волчий Рык
		ACTION1044(1044, 1, 5445), // Пробуждение
		ACTION1045(1045, 1, 5584), // Волчий Вой
		ACTION1046(1046, 1, 5585), // Рев Ездового Дракона
		ACTION1061(1061, 1, 5745), // Удар Смерти
		ACTION1062(1062, 1, 5746), // Двойная Атака
		ACTION1063(1063, 1, 5747), // Вихревая Атака
		ACTION1064(1064, 1, 5748), // Метеоритный дождь
		ACTION1065(1065, 1, 5753), // Пробуждение
		ACTION1066(1066, 1, 5749), // Удар Молнии
		ACTION1067(1067, 1, 5750), // Молния
		ACTION1068(1068, 1, 5751), // Световая Волна
		ACTION1069(1069, 1, 5752), // Вспышка
		ACTION1070(1070, 1, 5771), // Контроль Эффекта
		ACTION1071(1071, 1, 5761), // Мощный удар
		ACTION1072(1072, 1, 6046), // Проникающая Атака
		ACTION1073(1073, 1, 6047), // Яростный Ветер
		ACTION1074(1074, 1, 6048), // Удар Копьем
		ACTION1075(1075, 1, 6049), // Боевой Клич
		ACTION1076(1076, 1, 6050), // Мощное Сокрушение
		ACTION1077(1077, 1, 6051), // Шаровая Молния
		ACTION1078(1078, 1, 6052), // Шоковая Волна
		ACTION1079(1079, 1, 6053), // Вой
		ACTION1084(1084, 1, 6054), // Смена Режима
		ACTION1089(1089, 1, 6199), // Хвост
		ACTION1090(1090, 1, 6205), // Укус Ездового Дракона
		ACTION1091(1091, 1, 6206), // Устрашение Ездового Дракона
		ACTION1092(1092, 1, 6207), // Рывок Ездового Дракона
		ACTION1093(1093, 1, 6618), // Удар Магвена
		ACTION1094(1094, 1, 6681), // Легкая Походка Магвена
		ACTION1095(1095, 1, 6619), // Мощный Удар Магвена
		ACTION1096(1096, 1, 6682), // Легкая Походка Элитного Магвена
		ACTION1097(1097, 1, 6683), // Возвращение Магвена
		ACTION1098(1098, 1, 6684), // Групповое Возвращение Магвена
		ACTION5000(5000, 1, 23155), // Погладить
		ACTION5001(5001, 1, 23167), // Искушение Света Розы
		ACTION5002(5002, 1, 23168), // Запредельное Искушение
		ACTION5003(5003, 1, 5749), // Удар Молнии
		ACTION5004(5004, 1, 5750), // Молния
		ACTION5005(5005, 1, 5751), // Световая волна
		ACTION5006(5006, 1, 5771), // Контроль Эффекта
		ACTION5007(5007, 1, 6046), // Проникающая Атака
		ACTION5008(5008, 1, 6047), // Вихревая Атака
		ACTION5009(5009, 1, 6048), // Сокрушение
		ACTION5010(5010, 1, 6049), // Боевой Клич
		ACTION5011(5011, 1, 6050), // Мощное Сокрушение
		ACTION5012(5012, 1, 6051), // Шаровая Молния
		ACTION5013(5013, 1, 6052), // Шоковая Волна
		ACTION5014(5014, 1, 6053), // Воспламенение
		ACTION5015(5015, 1, 6054), // Смена Режима
		ACTION5016(5016, 1, 6054), // Усиление Кота-Рейнджера

		// Действия саммонов
		ACTION21(21, 2, 0), // Pet Follow
		ACTION22(22, 2, 0), // Атака петом
		ACTION23(23, 2, 0), // Отмена действия у пета
		ACTION52(52, 2, 0), // Отзыв саммона
		ACTION54(53, 2, 0), // Передвинуть пета к цели

		// Скиллы саммонов
		ACTION32(32, 2, 4230), // Переключение Режимов
		ACTION36(36, 2, 4259), // Токсичный Дым
		ACTION39(39, 2, 4138), // Взрыв Паразита
		ACTION41(41, 2, 4230), // Дикая Пушка
		ACTION42(42, 2, 4378), // Отражающий Щит
		ACTION43(43, 2, 4137), // Водоворот
		ACTION44(44, 2, 4139), // Взрывная Атака
		ACTION45(45, 2, 4025), // Мастер Перезарядки
		ACTION46(46, 2, 4261), // Удар Шторма
		ACTION47(47, 2, 4260), // Кража Крови
		ACTION48(48, 2, 4068), // Пушка
		ACTION1000(1000, 2, 4079), // Осадный Молот
		ACTION1002(1002, 2, 0), // Враждебность
		ACTION1007(1007, 2, 4699), // Благословение Королевы
		ACTION1008(1008, 2, 4700), // Дар Королевы
		ACTION1009(1009, 2, 4701), // Исцеление Королевы
		ACTION1010(1010, 2, 4702), // Благословение Серафима
		ACTION1011(1011, 2, 4703), // Дар Серафима
		ACTION1012(1012, 2, 4704), // Исцеление Серафима
		ACTION1013(1013, 2, 4705), // Проклятие Тени
		ACTION1014(1014, 2, 4706), // Массовое Проклятие Тени
		ACTION1015(1015, 2, 4707), // Жертва Тени
		ACTION1016(1016, 2, 4709), // Проклятый Импульс
		ACTION1017(1017, 2, 4708), // Проклятый Удар
		ACTION1018(1018, 2, 0), // Проклятие Поглощения Энергии
		ACTION1019(1019, 2, 0), // Умение Кэт 2
		ACTION1020(1020, 2, 0), // Умение Мяу 2
		ACTION1021(1021, 2, 0), // Умение Кая 2
		ACTION1022(1022, 2, 0), // Умение Юпитера 2
		ACTION1023(1023, 2, 0), // Умение Миража 2
		ACTION1024(1024, 2, 0), // Умение Бекара 2
		ACTION1025(1025, 2, 0), // Теневое Умение 1
		ACTION1026(1026, 2, 0), // Теневое Умение 2
		ACTION1027(1027, 2, 0), // Умение Гекаты
		ACTION1028(1028, 2, 0), // Умение Воскрешенного 1
		ACTION1029(1029, 2, 0), // Умение Воскрешенного 2
		ACTION1030(1030, 2, 0), // Умение Порочного 2
		ACTION1031(1031, 2, 5135), // Рассечение
		ACTION1032(1032, 2, 5136), // Режущий Вихрь
		ACTION1033(1033, 2, 5137), // Кошачья Хватка
		ACTION1034(1034, 2, 5138), // Кнут
		ACTION1035(1035, 2, 5139), // Приливная Волна
		ACTION1036(1036, 2, 5142), // Взрыв Трупа
		ACTION1037(1037, 2, 5141), // Случайная Смерть
		ACTION1038(1038, 2, 5140), // Сила Проклятия
		ACTION1039(1039, 2, 5110), // Пушечное Мясо
		ACTION1040(1040, 2, 5111), // Большой Бум
		ACTION1047(1047, 2, 5580), // Укус Божественного Зверя
		ACTION1048(1048, 2, 5581), // Оглушительная Атака Божественного Зверя
		ACTION1049(1049, 2, 5582), // Огненное Дыхание Божественного Зверя
		ACTION1050(1050, 2, 5583), // Рев Божественного Зверя
		ACTION1051(1051, 2, 5638), // Благословение Тела
		ACTION1052(1052, 2, 5639), // Благословение Духа
		ACTION1053(1053, 2, 5640), // Ускорение
		ACTION1054(1054, 2, 5643), // Проницательность
		ACTION1055(1055, 2, 5647), // Чистота
		ACTION1056(1056, 2, 5648), // Воодушевление
		ACTION1057(1057, 2, 5646), // Дикая Магия
		ACTION1058(1058, 2, 5652), // Шепот Смерти
		ACTION1059(1059, 2, 5653), // Фокусировка
		ACTION1060(1060, 2, 5654), // Наведение
		ACTION1080(1080, 2, 6041), // Прилив Феникса
		ACTION1081(1081, 2, 6042), // Очищение Феникса
		ACTION1082(1082, 2, 6043), // Пылающее Перо Феникса
		ACTION1083(1083, 2, 6044), // Пылающий Клюв Феникса
		ACTION1086(1086, 2, 6094), // Натиск Пантеры
		ACTION1087(1087, 2, 6095), // Темный Коготь Пантеры
		ACTION1088(1088, 2, 6096), // Смертоносный Коготь Пантеры
		ACTION1113(1113, 2, 10051), // Львиный Рев
		ACTION1114(1114, 2, 10052), // Львиный Коготь
		ACTION1115(1115, 2, 10053), // Львиный Бросок
		ACTION1116(1116, 2, 10054), // Львиное Пламя
		ACTION1117(1117, 2, 10794), // Полет Громового Змея
		ACTION1118(1118, 2, 10795), // Очищение Громового Змея
		ACTION1120(1120, 2, 10797), // Стрельба Перьями Громового Змея
		ACTION1121(1121, 2, 10798), // Острые Когти Громового Змея
		ACTION1122(1122, 2, 11806), // Благословение Жизни
		ACTION1123(1123, 2, 14767), // Осадный Удар TODO: Проверить SkillId
		ACTION1142(1142, 2, 10087), // Рев Пантеры
		ACTION1143(1143, 2, 10088), // Стремительный Бросок Пантеры

		// Дейтсвия одного и более саммонов
		ACTION1099(1099, 5, 0), // Атака
		ACTION1100(1100, 5, 0), // Перемещение
		ACTION1101(1101, 5, 0), // Прекращение
		ACTION1102(1102, 5, 0), // Отмена  призыва
		ACTION1103(1103, 5, 0), // Пассивность
		ACTION1104(1104, 5, 0), // Защита

		ACTION1106(1106, 5, 11278), // Коготь Медведя
		ACTION1107(1107, 5, 11279), // Топот Медведя
		ACTION1108(1108, 5, 11280), // Укус Кугуара
		ACTION1109(1109, 5, 11281), // Прыжок Кугуара
		ACTION1110(1110, 5, 11282), // Прикосновение Потрошителя
		ACTION1111(1111, 5, 11283), // Сила Потрошителя
		ACTION1124(1124, 5, 11323), // Агрессия Кошки
		ACTION1125(1125, 5, 11324), // Кошачье Оглушение
		ACTION1126(1126, 5, 11325), // Укус Кошки
		ACTION1127(1127, 5, 11326), // Атакующий Прыжок Кошки
		ACTION1128(1128, 5, 11327), // Прикосновение Кошки
		ACTION1129(1129, 5, 11328), // Сила Кошки
		ACTION1130(1130, 5, 11332), // Агрессия Единорога
		ACTION1131(1131, 5, 11333), // Оглушение Единорога
		ACTION1132(1132, 5, 11334), // Укус Единорога
		ACTION1133(1133, 5, 11335), // Атакующий Прыжок Единорога
		ACTION1134(1134, 5, 11336), // Прикосновение Единорога
		ACTION1135(1135, 5, 11337), // Сила Единорога
		ACTION1136(1136, 5, 11341), // Агрессия Фантома
		ACTION1137(1137, 5, 11342), // Фантомное Оглушение
		ACTION1138(1138, 5, 11343), // Укус Фантома
		ACTION1139(1139, 5, 11344), // Атакующий Прыжок Фантома
		ACTION1140(1140, 5, 11345), // Прикосновение Фантома
		ACTION1141(1141, 5, 11346), // Сила Фантома

		// Социальные действия
		ACTION12(12, 3, SocialActionPacket.GREETING),
		ACTION13(13, 3, SocialActionPacket.VICTORY),
		ACTION14(14, 3, SocialActionPacket.ADVANCE),
		ACTION24(24, 3, SocialActionPacket.YES),
		ACTION25(25, 3, SocialActionPacket.NO),
		ACTION26(26, 3, SocialActionPacket.BOW),
		ACTION29(29, 3, SocialActionPacket.UNAWARE),
		ACTION30(30, 3, SocialActionPacket.WAITING),
		ACTION31(31, 3, SocialActionPacket.LAUGH),
		ACTION33(33, 3, SocialActionPacket.APPLAUD),
		ACTION34(34, 3, SocialActionPacket.DANCE),
		ACTION35(35, 3, SocialActionPacket.SORROW),
		ACTION62(62, 3, SocialActionPacket.CHARM),
		ACTION66(66, 3, SocialActionPacket.SHYNESS),
		//TAUTI
		ACTION87(87, 3, SocialActionPacket.PROPOSE),
		ACTION88(88, 3, SocialActionPacket.PROVOKE),
		ACTION89(89, 3, SocialActionPacket.BOASTING),

		// Парные социальные действия
		ACTION71(71, 4, SocialActionPacket.COUPLE_BOW),
		ACTION72(72, 4, SocialActionPacket.COUPLE_HIGH_FIVE),
		ACTION73(73, 4, SocialActionPacket.COUPLE_DANCE);

		public int id;
		public int type;
		public int value;

		private Action(int id, int type, int value)
		{
			this.id = id;
			this.type = type;
			this.value = value;
		}

		public static Action find(int id)
		{
			for(Action action : Action.values())
				if(action.id == id)
					return action;
			return null;
		}
	}

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_actionId = buffer.getInt();
		_ctrlPressed = buffer.getInt() == 1;
		_shiftPressed = buffer.get() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Action action = Action.find(_actionId);
		if(action == null)
		{
			_log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isTransformed() && !activeChar.getTransform().haveAction(action.id))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(action.type == 0) // Действия с игроками
		{
			// dont do anything if player is dead or confused
			if((activeChar.isOutOfControl() || activeChar.isActionsDisabled()) && !(activeChar.isFakeDeath() && _actionId == 0))
			{
				activeChar.sendActionFailed();
				return;
			}

			final GameObject target = activeChar.getTarget();
			switch(action.id)
			{
				case 0: // Сесть/встать
					// На страйдере нельзя садиться
					if(activeChar.isMounted())
					{
						activeChar.sendActionFailed();
						break;
					}

					if(activeChar.isFakeDeath())
					{
						activeChar.breakFakeDeath();
						activeChar.updateAbnormalIcons();
						break;
					}

					if(!activeChar.isSitting())
					{
						if(target != null && target instanceof ChairInstance && ((ChairInstance)target).canSit(activeChar))
							activeChar.sitDown((ChairInstance) target);
						else
							activeChar.sitDown(null);
					}
					else
						activeChar.standUp();

					break;
				case 1: // Изменить тип передвижения, шаг/бег
					if(activeChar.isRunning())
						activeChar.setWalking();
					else
						activeChar.setRunning();
					activeChar.sendUserInfo(true);
					break;
				case 10: // Запрос на создание приватного магазина продажи
				case 61: // Запрос на создание приватного магазина продажи (Package)
				{
					if(activeChar.getSittingTask())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.isInStoreMode())
					{
						activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
						activeChar.storePrivateStore();
						activeChar.standUp();
						activeChar.broadcastCharInfo();
					}
					else if(!TradeHelper.checksIfCanOpenStore(activeChar, _actionId == 61 ? Player.STORE_PRIVATE_SELL_PACKAGE : Player.STORE_PRIVATE_SELL))
					{
						activeChar.sendActionFailed();
						return;
					}
					activeChar.sendPacket(new PrivateStoreManageList(1, activeChar, _actionId == 61));
					activeChar.sendPacket(new PrivateStoreManageList(2, activeChar, _actionId == 61));
					break;
				}
				case 28: // Запрос на создание приватного магазина покупки
				{
					if(activeChar.getSittingTask())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.isInStoreMode())
					{
						activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
						activeChar.storePrivateStore();
						activeChar.standUp();
						activeChar.broadcastCharInfo();
					}
					else if(!TradeHelper.checksIfCanOpenStore(activeChar, Player.STORE_PRIVATE_BUY))
					{
						activeChar.sendActionFailed();
						return;
					}
					activeChar.sendPacket(new PrivateStoreBuyManageList(1, activeChar));
					activeChar.sendPacket(new PrivateStoreBuyManageList(2, activeChar));
					break;
				}
				case 37: // Создание магазина Dwarven Craft
				{
					if(activeChar.getSittingTask())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.getDwarvenRecipeBook().isEmpty())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.isInStoreMode())
					{
						activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
						activeChar.storePrivateStore();
						activeChar.standUp();
						activeChar.broadcastCharInfo();
					}
					else if(!TradeHelper.checksIfCanOpenStore(activeChar, Player.STORE_PRIVATE_MANUFACTURE))
					{
						activeChar.sendActionFailed();
						return;
					}
					activeChar.sendPacket(new RecipeShopManageListPacket(activeChar, true));
					break;
				}
				case 51: // Создание магазина Common Craft
				{
					if(activeChar.getSittingTask())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.getCommonRecipeBook().isEmpty())
					{
						activeChar.sendActionFailed();
						return;
					}
					if(activeChar.isInStoreMode())
					{
						activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
						activeChar.storePrivateStore();
						activeChar.standUp();
						activeChar.broadcastCharInfo();
					}
					else if(!TradeHelper.checksIfCanOpenStore(activeChar, Player.STORE_PRIVATE_MANUFACTURE))
					{
						activeChar.sendActionFailed();
						return;
					}
					activeChar.sendPacket(new RecipeShopManageListPacket(activeChar, false));
					break;
				}
				case 96: // Quit Party Command Channel?
					_log.info("96 Accessed");
					break;
				case 97: // Request Party Command Channel Info?
					_log.info("97 Accessed");
					break;
				case 38: // Mount
				{
					final PetInstance pet = activeChar.getPet();
					if(activeChar.isTransformed())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(pet == null || !pet.isMountable())
					{
						if(activeChar.isMounted())
						{
							if(activeChar.getMount().isHungry())
							{
								//На оффе сообщение нет, просто не дает слезть.
								//activeChar.sendPacket(SystemMsg.A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
								return;
							}

							if(activeChar.isFlying() && !activeChar.checkLandingState()) // Виверна
							{
								activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION, ActionFailPacket.STATIC);
								return;
							}
							activeChar.setMount(null);
						}
					}
					else if(activeChar.isMounted() || activeChar.isInBoat())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isDead())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(pet.isDead())
						activeChar.sendPacket(SystemMsg.A_DEAD_STRIDER_CANNOT_BE_RIDDEN);
					else if(activeChar.isInDuel())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isInCombat() || pet.isInCombat())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isInTrainingCamp())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isSitting())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isFishing())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.getActiveWeaponFlagAttachment() != null)
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isCastingNow())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(activeChar.isDecontrolled())
						activeChar.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
					else if(pet.isHungry())
						activeChar.sendPacket(SystemMsg.A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
					else
					{
						activeChar.getAbnormalList().stop(Skill.SKILL_EVENT_TIMER);
						activeChar.setMount(pet.getControlItemObjId(), pet.getNpcId(), pet.getLevel(), pet.getCurrentFed());
						pet.unSummon(false);
					}
					break;
				}
				case 65: // Кнопка "Сообщить о боте"
					BotReportManager.getInstance().reportBot(activeChar);
					break;
				case 76:
					if(target == null)
						return;

					IBroadcastPacket msg = activeChar.getFriendList().requestFriendInvite(target);
					if(msg != null)
					{
						activeChar.sendPacket(msg);
						activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
					}
					break;
				case 78:
					changeTacticalSign(activeChar, ExTacticalSign.STAR, target);
					break;
				case 79:
					changeTacticalSign(activeChar, ExTacticalSign.HEART, target);
					break;
				case 80:
					changeTacticalSign(activeChar, ExTacticalSign.MOON, target);
					break;
				case 81:
					changeTacticalSign(activeChar, ExTacticalSign.CROSS, target);
					break;
				case 82:
					findTacticalTarget(activeChar, ExTacticalSign.STAR);
					break;
				case 83:
					findTacticalTarget(activeChar, ExTacticalSign.HEART);
					break;
				case 84:
					findTacticalTarget(activeChar, ExTacticalSign.MOON);
					break;
				case 85:
					findTacticalTarget(activeChar, ExTacticalSign.CROSS);
					break;
				case 90:
					activeChar.sendPacket(new ExInzoneWaitingInfo(activeChar, true));
					break;
				default:
					_log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
			}
			return;
		}
		else if(action.type == 1) // Действия петов
		{
			if(activeChar.isDead()) // Мертвый хозяин не может управлять петами.
			{
				activeChar.sendActionFailed();
				return;
			}

			final PetInstance pet = activeChar.getPet();
			if(pet == null || pet.isOutOfControl())
			{
				activeChar.sendActionFailed();
				return;
			}
			if(pet.isDepressed())
			{
				activeChar.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}
			if(action.value > 0)
			{
				if(!servitorUseSkill(activeChar, pet, action.value, action.id))
					activeChar.sendActionFailed();
				return;
			}

			final GameObject target = activeChar.getTarget();
			switch(action.id)
			{
				case 15: // Follow для пета
					pet.setFollowMode(!pet.isFollowMode());
					break;
				case 16: // Атака петом
					if(target == null || !target.isCreature() || target == activeChar || pet == target || pet.isDead())
					{
						activeChar.sendActionFailed();
						return;
					}

					if(activeChar.isInOlympiadMode() && !activeChar.isOlympiadCompStart())
					{
						activeChar.sendActionFailed();
						return;
					}

					// Sin Eater
					if(pet.getData().isOfType(PetType.KARMA))
						return;

					if(pet.isNotControlled())
					{
						activeChar.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
						return;
					}

					pet.getAI().Attack(target, _ctrlPressed, _shiftPressed);
					break;
				case 17: // Отмена действия у пета
					pet.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					break;
				case 19: // Отзыв пета
					if(pet.isDead())
					{
						activeChar.sendPacket(SystemMsg.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM, ActionFailPacket.STATIC);
						return;
					}

					if(pet.isInCombat())
					{
						activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE, ActionFailPacket.STATIC);
						break;
					}

					if(pet.isHungry())
					{
						activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET, ActionFailPacket.STATIC);
						break;
					}

					pet.unSummon(false);
					break;
				case 54: // Передвинуть пета к цели
					if(target != null && pet != target && !pet.isMovementDisabled())
					{
						pet.setFollowMode(false);
						pet.moveToLocation(target.getLoc(), 100, true);
					}
					break;
				case 1070: //TODO: [Bonux] Проверить.
					if(pet instanceof PetBabyInstance)
						((PetBabyInstance) pet).triggerBuff();
					break;
				default:
					_log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
			}
			return;
		}
		else if(action.type == 2) // Действия саммонов
		{
			if(activeChar.isDead()) // Мертвый хозяин не может управлять саммонами.
			{
				activeChar.sendActionFailed();
				return;
			}

			final GameObject target = activeChar.getTarget();

			final SummonInstance summon = activeChar.getSummon();
			if(summon == null || summon.isOutOfControl())
			{
				activeChar.sendActionFailed();
				return;
			}

			if(summon.isDepressed())
			{
				activeChar.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}

			if(action.value > 0)
			{
				// TODO перенести эти условия в скиллы
				if(action.id == 1000 && target != null && !target.isDoor()) // Siege Golem - Siege Hammer
				{
					activeChar.sendActionFailed();
					return;
				}
				if((action.id == 1039 || action.id == 1040) && (target.isDoor() || target instanceof SiegeFlagInstance)) // Swoop Cannon (не может атаковать двери и флаги)
				{
					activeChar.sendActionFailed();
					return;
				}
				servitorUseSkill(activeChar, summon, action.value, action.id);
				return;
			}

			switch(action.id)
			{
				case 21: // Follow для пета
					summon.setFollowMode(!summon.isFollowMode());
					break;
				case 22: // Атака петом
				{
					if(target == null || !target.isCreature() || target == activeChar)
					{
						activeChar.sendActionFailed();
						return;
					}

					if(activeChar.isInOlympiadMode() && !activeChar.isOlympiadCompStart())
					{
						activeChar.sendActionFailed();
						return;
					}

					if(summon == target || summon.isDead())
						return;

					summon.getAI().Attack(target, _ctrlPressed, _shiftPressed);
					break;
				}
				case 23: // Отмена действия у пета
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					summon.setFollowMode(true);
					break;
				case 52: // Отзыв саммона
					if(summon.isInCombat())
					{
						activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
						activeChar.sendActionFailed();
					}
					else
						summon.unSummon(false);
					break;
				case 53: // Передвинуть пета к цели
					if(target != null && summon != target && !summon.isMovementDisabled())
					{
						summon.setFollowMode(false);
						summon.moveToLocation(target.getLoc(), 100, true);
					}
					break;
				default:
					_log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
			}
			return;
		}
		else if(action.type == 5) // Действия одного и более саммона
		{
			if(activeChar.isDead()) // Мертвый хозяин не может управлять саммонами.
			{
				activeChar.sendActionFailed();
				return;
			}

			final GameObject target = activeChar.getTarget();
			if(action.value > 0)
			{
				summonsUseSkill(activeChar, action.value, action.id);
				return;
			}

			final SummonInstance summon = activeChar.getSummon();

			if(summon == null || summon.isOutOfControl())
				return;

			if(summon.isDepressed())
			{
				activeChar.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}

			switch(action.id)
			{
				case 1099: // Атака
				{
					if(target == null || !target.isCreature() || target == activeChar)
					{
						activeChar.sendActionFailed();
						return;
					}

					if(summon == target || summon.isDead())
						return;

					summon.getAI().Attack(target, _ctrlPressed, _shiftPressed);
					break;
				}
				case 1100: // Передвинуть пета к цели
					if(target != null && summon != target && !summon.isMovementDisabled())
					{
						summon.setFollowMode(false);
						summon.moveToLocation(target.getLoc(), 100, true);
					}
					break;
				case 1101: // Прекращение
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					summon.setFollowMode(true);
					break;
				case 1102: // Отзыв саммона
					if(summon.isInCombat())
					{
						activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
						activeChar.sendActionFailed();
					}
					else
						summon.unSummon(false);
					break;
				case 1103:
					summon.getAI().notifyAttackModeChange(AttackMode.PASSIVE);
					break;
				case 1104:
					summon.getAI().notifyAttackModeChange(AttackMode.DEFENCE);
					break;
				default:
					_log.warn("unhandled action type " + _actionId + " by player " + activeChar.getName());
			}
			return;
		}
		else if(action.type == 3) // Социальные действия
		{
			if(activeChar.isOutOfControl() || activeChar.isTransformed() || activeChar.isActionsDisabled() || activeChar.isSitting() || activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || activeChar.isProcessingRequest())
			{
				activeChar.sendActionFailed();
				return;
			}
			if(activeChar.isFishing())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
				return;
			}
			if(activeChar.isInTrainingCamp())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
				return;
			}
			activeChar.broadcastPacket(new SocialActionPacket(activeChar.getObjectId(), action.value));
			if(Config.ALT_SOCIAL_ACTION_REUSE)
			{
				ThreadPoolManager.getInstance().schedule(new SocialTask(activeChar), 2600);
				activeChar.getFlags().getParalyzed().start();
			}

			activeChar.getListeners().onSocialAction(action);

			GameObject target = activeChar.getTarget();
			if(target != null && target.isNpc())
			{
				NpcInstance npc = (NpcInstance)target;
				if(activeChar.checkInteractionDistance(npc))
					npc.onSeeSocialAction(activeChar, action.value);
			}
			for(QuestState state : activeChar.getAllQuestsStates())
				state.getQuest().notifySocialActionUse(state, action.value);
			return;
		}
		else if(action.type == 4) // Парные социальные действия
		{
			if(activeChar.isOutOfControl() || activeChar.isActionsDisabled() || activeChar.isSitting())
			{
				activeChar.sendActionFailed();
				return;
			}

			final GameObject target = activeChar.getTarget();
			if(target == null || !target.isPlayer())
			{
				activeChar.sendActionFailed();
				return;
			}
			final Player pcTarget = target.getPlayer();
			if(pcTarget.isProcessingRequest() && pcTarget.getRequest().isTypeOf(L2RequestType.COUPLE_ACTION))
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION).addName(pcTarget));
				return;
			}
			if(pcTarget.isProcessingRequest())
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(pcTarget));
				return;
			}
			if(!activeChar.isInRange(pcTarget, 300) || activeChar.isInRange(pcTarget, 25) || activeChar.getTargetId() == activeChar.getObjectId() || !GeoEngine.canSeeTarget(activeChar, pcTarget, false))
			{
				activeChar.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
				return;
			}
			if(!activeChar.checkCoupleAction(pcTarget))
				return;

			new Request(L2RequestType.COUPLE_ACTION, activeChar, pcTarget).setTimeout(10000L);
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1).addName(pcTarget));
			pcTarget.sendPacket(new ExAskCoupleAction(activeChar.getObjectId(), action.value));

			if(Config.ALT_SOCIAL_ACTION_REUSE)
			{
				ThreadPoolManager.getInstance().schedule(new SocialTask(activeChar), 2600);
				activeChar.getFlags().getParalyzed().start();
			}
			return;
		}
		activeChar.sendActionFailed();
	}

	private void summonsUseSkill(Player player, int skillId, int actionId)
	{
		if(player.hasSummon())
		{
			SummonInstance s = player.getSummon();

			if(s == null || s.isOutOfControl())
				return;

			if(s.isDepressed())
			{
				player.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return;
			}

			servitorUseSkill(player, s, skillId, actionId);
		}
		else
			player.sendActionFailed();
	}

	private boolean servitorUseSkill(Player player, Servitor servitor, int skillId, int actionId)
	{
		if(servitor == null)
			return false;

		int skillLevel = servitor.getSkillLevel(skillId, 0);
		if(skillLevel == 0)
			return false;

		Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLevel);
		if(skill == null)
			return false;

		if(servitor.isNotControlled()) // TODO: [Bonux] Проверить, распостраняется ли данное правило на саммонов.
		{
			player.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
			return false;
		}

		if(skill.getId() != 6054)
		{
			int npcId = servitor.getNpcId();
			if(npcId == PetDataHolder.SPIRIT_SHAMAN_ID || npcId == PetDataHolder.TOY_KNIGHT_ID || npcId == PetDataHolder.TURTLE_ASCETIC_ID || npcId == 1601 || npcId == 1602 || npcId == 1603 || npcId == PetDataHolder.ROSE_DESELOPH_ID || npcId == PetDataHolder.ROSE_HYUM_ID || npcId == PetDataHolder.ROSE_REKANG_ID || npcId == PetDataHolder.ROSE_LILIAS_ID || npcId == PetDataHolder.ROSE_LAPHAM_ID || npcId == PetDataHolder.ROSE_MAPHUM_ID || npcId == PetDataHolder.IMPROVED_ROSE_DESELOPH_ID || npcId == PetDataHolder.IMPROVED_ROSE_HYUM_ID || npcId == PetDataHolder.IMPROVED_ROSE_REKANG_ID || npcId == PetDataHolder.IMPROVED_ROSE_LILIAS_ID || npcId == PetDataHolder.IMPROVED_ROSE_LAPHAM_ID || npcId == PetDataHolder.IMPROVED_ROSE_MAPHUM_ID)
			{
				if(!servitor.getAbnormalList().contains(6054))
				{
					player.sendPacket(SystemMsg.A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS);
					return false;
				}
			}
		}

		if(skill.isToggle())
		{
			if(servitor.getAbnormalList().contains(skill))
			{
				if(skill.isNecessaryToggle())
					servitor.getAbnormalList().stop(skill.getId());
				return true;
			}
		}

		Creature aimingTarget = skill.getAimingTarget(servitor, player.getTarget());
		if(!skill.checkCondition(servitor, aimingTarget, _ctrlPressed, _shiftPressed, true))
			return false;

		servitor.setUsedSkill(skill, actionId); // TODO: [Bonux] Переделать.
		servitor.getAI().Cast(skill, aimingTarget, _ctrlPressed, _shiftPressed);
		return true;
	}

	static class SocialTask extends RunnableImpl
	{
		Player _player;

		SocialTask(Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			_player.getFlags().getParalyzed().stop();
		}
	}

	private void changeTacticalSign(Player player, int sign, GameObject target)
	{
		if(!player.isInParty())
			return;

		if(target == null || !target.isCreature() || !target.isTargetable(player))
			return;

		player.getParty().changeTacticalSign(player, sign, (Creature) target);
	}

	private void findTacticalTarget(Player player, int sign)
	{
		if(!player.isInParty())
			return;

		Creature target = player.getParty().findTacticalTarget(player, sign);
		if(target == null || target.isAlikeDead() || !target.isTargetable(player))
			return;

		player.setNpcTarget(target);
	}
}