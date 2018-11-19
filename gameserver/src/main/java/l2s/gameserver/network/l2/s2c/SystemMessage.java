package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.instances.NpcInstance;
import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.ServerPacketOpcodes;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Deprecated
public class SystemMessage extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

	// d d (d S/d d/d dd)
	// |--------------> 0 - String 1-number 2-textref npcname (1000000-1002655) 3-textref itemname 4-textref skills 5-??
	private static final int TYPE_BYTE = 20;
	private static final int TYPE_CLASS_NAME = 15;
	//14 - NpcString
	private static final int TYPE_SYSTEM_STRING = 13;
	private static final int TYPE_PLAYER_NAME = 12;
	private static final int TYPE_DOOR_NAME = 11;
	private static final int TYPE_INSTANCE_NAME = 10;
	private static final int TYPE_ELEMENT_NAME = 9;
	private static final int TYPE_UNKNOWN_8 = 8;
	private static final int TYPE_ZONE_NAME = 7;
	private static final int TYPE_LONG = 6;
	private static final int TYPE_CASTLE_NAME = 5;
	private static final int TYPE_SKILL_NAME = 4;
	private static final int TYPE_ITEM_NAME = 3;
	private static final int TYPE_NPC_NAME = 2;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_TEXT = 0;
	private int _messageId;
	private List<Arg> args = new ArrayList<Arg>();

	public static final int YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER = 0; // Соединение с сервером потеряно.
	public static final int THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT = 1; // Сервер будет отключен через $s1 сек. Пожалуйста, выйдите из игры.
	public static final int S1_DOES_NOT_EXIST = 2; // Пользователя $1s не существует.
	public static final int S1_IS_NOT_CURRENTLY_LOGGED_IN = 3; // Пользователь $1s не в игре.
	public static final int YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN = 4; // Невозможно принять в клан самого себя.
	public static final int S1_ALREADY_EXISTS = 5; // Клан $1s уже существует.
	public static final int S1_DOES_NOT_EXIST_2 = 6; // Клана $1s не существует.
	public static final int YOU_ARE_ALREADY_A_MEMBER_OF_S1 = 7; // Вы уже состоите в клане $1s.
	public static final int YOU_ARE_WORKING_WITH_ANOTHER_CLAN = 8; // Вы уже состоите в другом клане.
	public static final int S1_IS_NOT_A_CLAN_LEADER = 9; // $1s не является главой клана.
	public static final int S1_IS_WORKING_WITH_ANOTHER_CLAN = 10; // $1s уже состоит в клане.
	public static final int THERE_ARE_NO_APPLICANTS_FOR_THIS_CLAN = 11; // Нет заявок на вступление в клан.
	public static final int APPLICANT_INFORMATION_IS_INCORRECT = 12; // Информация о заявках на вступление в клан не точна.
	public static final int UNABLE_TO_DISPERSE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE = 13; // Вы не можете расформировать клан, т.к. подали заявку на осаду замка.
	public static final int UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS = 14; // Вы не можете расформировать клан, т.к. владеете замком или холлом клана.
	public static final int YOU_ARE_IN_SIEGE = 15; // Вы в зоне осады.
	public static final int YOU_ARE_NOT_IN_SIEGE = 16; // Вы вне зоны осады.
	public static final int CASTLE_SIEGE_HAS_BEGUN = 17; // Осада замка началась.
	public static final int THE_CASTLE_SIEGE_HAS_ENDED = 18; // Осада замка закончилась.
	public static final int THERE_IS_A_NEW_LORD_OF_THE_CASTLE = 19; // Сменился владелец замка!
	public static final int THE_GATE_IS_BEING_OPENED = 20; // Ворота открыты.
	public static final int THE_GATE_IS_BEING_DESTROYED = 21; // Ворота разрушены.
	public static final int YOUR_TARGET_IS_OUT_OF_RANGE = 22; // Цель находится слишком далеко.
	public static final int NOT_ENOUGH_HP = 23; // Недостаточно HP.
	public static final int NOT_ENOUGH_MP = 24; // Недостаточно MP.
	public static final int REJUVENATING_HP = 25; // Восстановление HP.
	public static final int REJUVENATING_MP = 26; // Восстановление MP.
	public static final int YOUR_CASTING_HAS_BEEN_INTERRUPTED = 27; // Чтение заклинания прервано.
	public static final int YOU_HAVE_OBTAINED_S1_ADENA = 28; // Получено: $s1 аден.
	public static final int YOU_HAVE_OBTAINED_S2_S1 = 29; // Получено: $s1 ($s2 шт.)
	public static final int YOU_HAVE_OBTAINED_S1 = 30; // Получено: $s1.
	public static final int YOU_CANNOT_MOVE_WHILE_SITTING = 31; // Вы не можете двигаться, когда сидите.
	public static final int YOU_ARE_NOT_CAPABLE_OF_COMBAT_MOVE_TO_THE_NEAREST_RESTART_POINT = 32; // Вы не можете вступить в битву. Пожалуйста, перейдите к ближайшей точке перезапуска.
	public static final int YOU_CANNOT_MOVE_WHILE_CASTING = 33; // Невозможно двигаться во время чтения заклинания.
	public static final int WELCOME_TO_THE_WORLD_OF_LINEAGE_II = 34; // Добро пожаловать в мир Lineage II.
	//@Deprecated
	public static final int YOU_HIT_FOR_S1_DAMAGE = 35; // заменено C1_HAS_GIVEN_C2_DAMAGE_OF_S3 = 2261
	//@Deprecated
	public static final int C1_HIT_YOU_FOR_S2_DAMAGE = 36; // заменено C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2 = 2262
	public static final int C1_HIT_YOU_FOR_S2_DAMAGE_2 = 37; // $c1 наносит Вам $s2 урона.
	public static final int THE_TGS2002_EVENT_BEGINS = 38; // Начинается ивент TGS2002!
	public static final int THE_TGS2002_EVENT_IS_OVER_THANK_YOU_VERY_MUCH = 39; // Ивент TGS2002 закончился. Большое спасибо за участие!
	public static final int THIS_IS_THE_TGS_DEMO_THE_CHARACTER_WILL_IMMEDIATELY_BE_RESTORED = 40; // Из-за того, что это TGS демо, персонаж возродится немедленно.
	public static final int YOU_CAREFULLY_NOCK_AN_ARROW = 41; // Вы натягиваете тетиву.
	//@Deprecated
	public static final int YOU_HAVE_AVOIDED_C1S_ATTACK = 42; // заменено C1_HAS_EVADED_C2S_ATTACK = 2264
	//@Deprecated
	public static final int YOU_HAVE_MISSED = 43; // заменено C1S_ATTACK_WENT_ASTRAY = 2265
	//@Deprecated
	public static final int CRITICAL_HIT = 44; // заменено C1_HAD_A_CRITICAL_HIT = 2266
	public static final int YOU_HAVE_EARNED_S1_EXPERIENCE = 45; // Получено: $s1 опыта.
	public static final int YOU_USE_S1 = 46; // Вы используете: $s1.
	public static final int YOU_BEGIN_TO_USE_AN_S1 = 47; // Используется: $s1.
	//@Deprecated
	public static final int S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE = 48; // заменено THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME = 2303
	public static final int YOU_HAVE_EQUIPPED_YOUR_S1 = 49; // Надето: $s1.
	public static final int YOUR_TARGET_CANNOT_BE_FOUND = 50; // Нет цели.
	public static final int YOU_CANNOT_USE_THIS_ON_YOURSELF = 51; // Вы не можете использовать это на себя.
	public static final int YOU_HAVE_EARNED_S1_ADENA = 52; // Получено: $s1 аден.
	public static final int YOU_HAVE_EARNED_S2_S1S = 53; // Присвоено: $s1 ($s2 шт.)
	public static final int YOU_HAVE_EARNED_S1 = 54; // Присвоено: $s1.
	public static final int YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA = 55; // Нельзя поднять: $s1 аден.
	public static final int YOU_HAVE_FAILED_TO_PICK_UP_S1 = 56; // Нельзя поднять: $s1.
	public static final int YOU_HAVE_FAILED_TO_PICK_UP_S2_S1S = 57; // Нельзя поднять: $s1 ($s2 шт.)
	public static final int YOU_HAVE_FAILED_TO_EARN_S1_ADENA = 58; // Нельзя получить: $s1 аден.
	public static final int YOU_HAVE_FAILED_TO_EARN_S1 = 59; // Нельзя получить: $s1.
	public static final int YOU_HAVE_FAILED_TO_EARN_S2_S1S = 60; // Нельзя получить: $s1 ($s2 шт.)
	public static final int NOTHING_HAPPENED = 61; // Ничего не произошло.
	public static final int S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED = 62; // $s1: улучшение удалось.
	public static final int _S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED = 63; // +$s1 $s2: улучшение удалось.
	public static final int THE_ENCHANTMENT_HAS_FAILED_YOUR_S1_HAS_BEEN_CRYSTALLIZED = 64; // $s1: произошла кристаллизация.
	public static final int THE_ENCHANTMENT_HAS_FAILED_YOUR__S1_S2_HAS_BEEN_CRYSTALLIZED = 65; // +$s1 $s2: произошла кристаллизация.
	public static final int C1_IS_INVITING_YOU_TO_JOIN_A_PARTY_DO_YOU_ACCEPT = 66; // $c1 приглашает Вас в группу. Вы согласны?
	public static final int S1_HAS_INVITED_YOU_TO_THE_JOIN_THE_CLAN_S2_DO_YOU_WISH_TO_JOIN = 67; // $s1 приглашает Вас в клан $s2. Вы согласны?
	public static final int WOULD_YOU_LIKE_TO_WITHDRAW_FROM_THE_S1_CLAN_IF_YOU_LEAVE_YOU_WILL_HAVE_TO_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN = 68; // Выйти из клана $s1? После этого нельзя вступить в другой клан в течение 24 ч.
	public static final int WOULD_YOU_LIKE_TO_DISMISS_S1_FROM_THE_CLAN_IF_YOU_DO_SO_YOU_WILL_HAVE_TO_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER = 69; // Исключить из клана персонажа $s1? После этого нельзя принимать в клан других персонажей в течение 24 ч.
	public static final int DO_YOU_WISH_TO_DISPERSE_THE_CLAN_S1 = 70; // Распустить клан $s1?
	public static final int HOW_MANY_S1_S_DO_YOU_WANT_TO_DISCARD = 71; // $s1: сколько шт. выкинуть?
	public static final int HOW_MANY_S1_S_DO_YOU_WANT_TO_MOVE = 72; // $s1: сколько шт. переместить?
	public static final int HOW_MANY_S1_S_DO_YOU_WANT_TO_DESTROY = 73; // $s1: сколько шт. уничтожить?
	public static final int DO_YOU_WISH_TO_DESTROY_YOUR_S1 = 74; // $s1 - уничтожить?
	public static final int ID_DOES_NOT_EXIST = 75; // Логин не существует.
	public static final int INCORRECT_PASSWORD = 76; // Неправильный пароль.
	public static final int YOU_CANNOT_CREATE_ANOTHER_CHARACTER_PLEASE_DELETE_THE_EXISTING_CHARACTER_AND_TRY_AGAIN = 77; // Нельзя создать персонажа. Удалите уже существующего и повторите попытку.
	public static final int DO_YOU_WISH_TO_DELETE_S1 = 78; // Удалить персонажа $s1?
	public static final int THIS_NAME_ALREADY_EXISTS = 79; // Такое имя уже используется.
	public static final int YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH_PLEASE_TRY_AGAIN = 80; // Введите имя персонажа (максимум 16 символов).
	public static final int PLEASE_SELECT_YOUR_RACE = 81; // Выберите расу.
	public static final int PLEASE_SELECT_YOUR_OCCUPATION = 82; // Выберите профессию.
	public static final int PLEASE_SELECT_YOUR_GENDER = 83; // Выберите пол.
	public static final int YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE = 84; // Вы не можете атаковать в мирной зоне.
	public static final int YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE = 85; // Вы не можете атаковать цель в мирной зоне.
	public static final int PLEASE_ENTER_YOUR_ID = 86; // Введите логин.
	public static final int PLEASE_ENTER_YOUR_PASSWORD = 87; // Введите пароль.
	public static final int YOUR_PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_RESTART_YOUR_CLIENT_AND_RUN_A_FULL_CHECK = 88; // Другая версия протокола. Пожалуйста, завершите программу.
	public static final int YOUR_PROTOCOL_VERSION_IS_DIFFERENT_PLEASE_CONTINUE = 89; // Другая версия протокола. Пожалуйста, продолжайте.
	public static final int YOU_ARE_UNABLE_TO_CONNECT_TO_THE_SERVER = 90; // Невозможно соединиться с сервером.
	public static final int PLEASE_SELECT_YOUR_HAIRSTYLE = 91; // Выберите прическу.
	public static final int S1_HAS_WORN_OFF = 92; // Закончился эффект: $s1.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_SP_FOR_THIS = 93; // Недостаточно SP.
	public static final int COPYRIGHT_NCSOFT_CORPORATION_ALL_RIGHTS_RESERVED = 94; // 2008 Copyright NCsoft Corporation. All Rights Reserved.
	public static final int YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP = 95; // Получено: $s1 опыта и $s2 SP.
	public static final int YOUR_LEVEL_HAS_INCREASED = 96; // Уровень увеличен!
	public static final int THIS_ITEM_CANNOT_BE_MOVED = 97; // Невозможно переместить предмет.
	public static final int THIS_ITEM_CANNOT_BE_DISCARDED = 98; // Невозможно выбросить предмет.
	public static final int THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD = 99; // Невозможно передать/продать предмет.
	public static final int S1_REQUESTS_A_TRADE_DO_YOU_WANT_TO_TRADE = 100; // $c1 предлагает сделку. Принять?
	public static final int YOU_CANNOT_EXIT_WHILE_IN_COMBAT = 101; // Во время боя нельзя выйти из игры.
	public static final int YOU_CANNOT_RESTART_WHILE_IN_COMBAT = 102; // Во время боя нельзя перезапустить игру.
	public static final int THIS_ID_IS_CURRENTLY_LOGGED_IN = 103; // Игрок с данным именем уже в игре.
	public static final int YOU_MAY_NOT_EQUIP_ITEMS_WHILE_CASTING_OR_PERFORMING_A_SKILL = 104; // Нельзя надевать предметы во время использования умений.
	public static final int YOU_HAVE_INVITED_C1_TO_JOIN_YOUR_PARTY = 105; // $c1 приглашает Вас в группу.
	public static final int YOU_HAVE_JOINED_S1S_PARTY = 106; // Вы присоединились к группе.
	public static final int S1_HAS_JOINED_THE_PARTY = 107; // $c1 присоединяется к группе.
	public static final int S1_HAS_LEFT_THE_PARTY = 108; // $c1 выходит из группы.
	public static final int INVALID_TARGET = 109; // Неверная цель.
	public static final int S1_S2S_EFFECT_CAN_BE_FELT = 110; // Вы ощущаете эффект: $s1.
	public static final int YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED = 111; // Вы удачно блокировали удар.
	public static final int YOU_HAVE_RUN_OUT_OF_ARROWS = 112; // Стрелы закончились.
	public static final int S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS = 113; // $s1: нельзя сейчас использовать.
	public static final int YOU_HAVE_ENTERED_THE_SHADOW_OF_THE_MOTHER_TREE = 114; // Вы зашли в Тень Древа Жизни.
	public static final int YOU_HAVE_LEFT_THE_SHADOW_OF_THE_MOTHER_TREE = 115; // Вы вышли из Тени Древа Жизни.
	public static final int YOU_HAVE_ENTERED_A_PEACEFUL_ZONE = 116; // Вы зашли в мирную зону.
	public static final int YOU_HAVE_LEFT_THE_PEACEFUL_ZONE = 117; // Вы вышли из мирной зоны.
	public static final int YOU_HAVE_REQUESTED_A_TRADE_WITH_C1 = 118; // Вы предложили сделку персонажу $c1.
	public static final int C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE = 119; // $c1 отказывается от сделки.
	public static final int YOU_BEGIN_TRADING_WITH_C1 = 120; // $c1: обмен начался.
	public static final int C1_HAS_CONFIRMED_THE_TRADE = 121; // $c1 подтверждает сделку.
	public static final int YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED = 122; // Нельзя изменять предметы, если сделка подтверждена.
	public static final int YOUR_TRADE_IS_SUCCESSFUL = 123; // Обмен успешно завершен.
	public static final int C1_HAS_CANCELLED_THE_TRADE = 124; // $c1 отменяет сделку.
	public static final int DO_YOU_WISH_TO_EXIT_THE_GAME = 125; // Завершить игру?
	public static final int DO_YOU_WISH_TO_EXIT_TO_THE_CHARACTER_SELECT_SCREEN = 126; // Выйти в меню выбора персонажа?
	public static final int YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN = 127; // Связь с сервером прервалась. Пожалуйста, зайдите в игру позже.
	public static final int YOUR_CHARACTER_CREATION_HAS_FAILED = 128; // Не удалось создать персонажа.
	public static final int YOUR_INVENTORY_IS_FULL = 129; // Все ячейки в инвентаре заняты.
	public static final int YOUR_WAREHOUSE_IS_FULL = 130; // Все ячейки в хранилище заняты.
	public static final int S1_HAS_LOGGED_IN = 131; // $s1 заходит в игру.
	public static final int S1_HAS_BEEN_ADDED_TO_YOUR_FRIEND_LIST = 132; // $s1 добавляется в список друзей.
	public static final int S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIEND_LIST = 133; // $s1 удаляется из списка друзей.
	public static final int PLEASE_CHECK_YOUR_FRIEND_LIST_AGAIN = 134; // Пожалуйста, проверьте список друзей.
	public static final int S1_DID_NOT_REPLY_TO_YOUR_INVITATION_PARTY_INVITATION_HAS_BEEN_CANCELLED = 135; // $c1 не отвечает на Ваше приглашение.
	public static final int YOU_HAVE_NOT_REPLIED_TO_C1S_INVITATION_THE_OFFER_HAS_BEEN_CANCELLED = 136; // Вы не ответили на приглашение персонажа $c1.
	public static final int THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT = 137; // Больше нет предметов, привязанных к ярлыку.
	public static final int DESIGNATE_SHORTCUT = 138; // Не удалось назначить ярлык.
	public static final int C1_HAS_RESISTED_YOUR_S2 = 139; // Умение $s2 не подействовало на цель $c1.
	public static final int YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP = 140; // Недостаточно MP, действие умения прекращено.
	public static final int ONCE_THE_TRADE_IS_CONFIRMED_THE_ITEM_CANNOT_BE_MOVED_AGAIN = 141; // Нельзя изменять предметы, если сделка подтверждена.
	public static final int YOU_ARE_ALREADY_TRADING_WITH_SOMEONE = 142; // Вы уже обмениваетесь с другим персонажем.
	public static final int C1_IS_ALREADY_TRADING_WITH_ANOTHER_PERSON_PLEASE_TRY_AGAIN_LATER = 143; // $c1 обменивается с другим персонажем.
	public static final int THAT_IS_THE_INCORRECT_TARGET = 144; // Неправильная цель.
	public static final int THAT_PLAYER_IS_NOT_ONLINE = 145; // Этого персонажа нет в игре.
	public static final int CHATTING_IS_NOW_PERMITTED = 146; // Блокировка чата снята.
	public static final int CHATTING_IS_CURRENTLY_PROHIBITED = 147; // Чат заблокирован.
	public static final int YOU_CANNOT_USE_QUEST_ITEMS = 148; // Нельзя использовать квестовые предметы.
	public static final int YOU_CANNOT_PICK_UP_OR_USE_ITEMS_WHILE_TRADING = 149; // Во время сделки нельзя поднимать/использовать предметы.
	public static final int YOU_CANNOT_DISCARD_OR_DESTROY_AN_ITEM_WHILE_TRADING_AT_A_PRIVATE_STORE = 150; // Нельзя выбросить/уничтожить предметы во время сделки.
	public static final int THAT_IS_TOO_FAR_FROM_YOU_TO_DISCARD = 151; // Слишком далеко, нельзя выбросить предмет.
	public static final int YOU_HAVE_INVITED_WRONG_TARGET = 152; // Нельзя пригласить выбранную цель.
	public static final int S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER = 153; // $c1 занимается чем-то другим.
	public static final int ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS = 154; // Приглашать в группу может только ее лидер.
	public static final int PARTY_IS_FULL = 155; // Группа заполнена.
	public static final int DRAIN_WAS_ONLY_HALF_SUCCESSFUL = 156; // Поглощение удачно только на 50%.
	//@Deprecated
	public static final int YOU_RESISTED_S1S_DRAIN = 157; // заменено C1_RESISTED_C2S_DRAIN = 2267
	//@Deprecated
	public static final int ATTACK_FAILED = 158; // заменено C1S_ATTACK_FAILED = 2268
	//@Deprecated
	public static final int RESISTED_AGAINST_S1S_MAGIC = 159; // заменено C1_RESISTED_C2S_MAGIC = 2269
	public static final int S1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED = 160; // $c1 уже состоит в другой группе.
	public static final int THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE = 161; // Персонажа нет в игре.
	public static final int WAREHOUSE_IS_TOO_FAR = 162; // Хранилище находится слишком далеко.
	public static final int YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT = 163; // Нельзя уничтожить предмет, задано неверное количество.
	public static final int WAITING_FOR_ANOTHER_REPLY = 164; // Ожидание ответа.
	public static final int YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST = 165; // Нельзя добавить себя в список друзей.
	public static final int FRIEND_LIST_IS_NOT_READY_YET_PLEASE_REGISTER_AGAIN_LATER = 166; // Нельзя создать список друзей. Попробуйте позже.
	public static final int S1_IS_ALREADY_ON_YOUR_FRIEND_LIST = 167; // $c1 уже находится в списке друзей.
	public static final int S1_HAS_REQUESTED_TO_BECOME_FRIENDS = 168; // $c1 хочет добавить Вас в список друзей.
	public static final int ACCEPT_FRIENDSHIP_0_1__1_TO_ACCEPT_0_TO_DENY = 169; // Принять дружбу? 0/1 (1 - да, 0 - нет)
	public static final int THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME = 170; // Персонажа, которого Вы хотите добавить в друзья, нет в игре.
	public static final int S1_IS_NOT_ON_YOUR_FRIEND_LIST = 171; // $c1 не входит в Ваш список друзей.
	public static final int YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION = 172; // Недостаточно денег для оплаты хранилища.
	public static final int YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION_2 = 173; // Недостаточно денег для оплаты услуги.
	public static final int THE_PERSONS_INVENTORY_IS_FULL = 174; // У персонажа в инвентаре нет места.
	public static final int HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED = 175; // HP полностью восстановились, и умение деактивировалось.
	public static final int THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE = 176; // Игрок заблокировал принятие личных сообщений.
	public static final int MESSAGE_REFUSAL_MODE = 177; // Принятие сообщений заблокировано.
	public static final int MESSAGE_ACCEPTANCE_MODE = 178; // Принятие сообщений разблокировано.
	public static final int YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE = 179; // Нельзя выбросить здесь предмет.
	public static final int YOU_HAVE_S1_DAY_S_LEFT_UNTIL_DELETION_DO_YOU_WANT_TO_CANCEL_DELETION = 180; // Дней до удаления: $s1. Отменить удаление?
	public static final int CANNOT_SEE_TARGET = 181; // Цели не видно.
	public static final int DO_YOU_WANT_TO_QUIT_THE_CURRENT_QUEST = 182; // Отменить текущий квест?
	public static final int THERE_ARE_TOO_MANY_USERS_ON_THE_SERVER_PLEASE_TRY_AGAIN_LATER = 183; // На сервере слишком много игроков. Попробуйте зайти позже.
	public static final int PLEASE_TRY_AGAIN_LATER = 184; // Попробуйте зайти позже.
	public static final int SELECT_USER_TO_INVITE_TO_YOUR_PARTY = 185; // Выберите персонажа, которого хотите пригласить в группу.
	public static final int SELECT_USER_TO_INVITE_TO_YOUR_CLAN = 186; // Выберите персонажа, которого хотите пригласить в клан.
	public static final int SELECT_USER_TO_EXPEL = 187; // Выберите персонажа, которого хотите исключить.
	public static final int CREATE_CLAN_NAME = 188; // Введите название клана.
	public static final int CLAN_HAS_BEEN_CREATED = 189; // Клан создан.
	public static final int YOU_HAVE_FAILED_TO_CREATE_A_CLAN = 190; // Не удалось создать клан.
	public static final int CLAN_MEMBER_S1_HAS_BEEN_EXPELLED = 191; // $s1 исключается из клана.
	public static final int YOU_HAVE_FAILED_TO_EXPEL_S1_FROM_THE_CLAN = 192; // $s1: исключение не удалось.
	public static final int CLAN_HAS_DISPERSED = 193; // Клан расформирован.
	public static final int YOU_HAVE_FAILED_TO_DISPERSE_THE_CLAN = 194; // Не удалось расформировать клан.
	public static final int ENTERED_THE_CLAN = 195; // Вы вступили в клан.
	public static final int S1_REFUSED_TO_JOIN_THE_CLAN = 196; // $s1 отказывается вступить в клан.
	public static final int WITHDRAWN_FROM_THE_CLAN = 197; // Вы покинули клан.
	public static final int YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_S1_CLAN = 198; // Не удалось покинуть клан $s1.
	public static final int YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS = 199; // Вы исключены из клана и можете вступить в другой только через 24 ч.
	public static final int YOU_HAVE_WITHDRAWN_FROM_THE_PARTY = 200; // Вы вышли из группы.
	public static final int S1_WAS_EXPELLED_FROM_THE_PARTY = 201; // $c1 исключается из группы.
	public static final int YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY = 202; // Вас исключили из группы.
	public static final int THE_PARTY_HAS_DISPERSED = 203; // Группа расформирована.
	public static final int INCORRECT_NAME_PLEASE_TRY_AGAIN = 204; // Неправильное имя. Попробуйте еще раз.
	public static final int INCORRECT_CHARACTER_NAME_PLEASE_ASK_THE_GM = 205; // Неправильное имя персонажа. Попробуйте еще раз.
	public static final int ENTER_NAME_OF_CLAN_TO_DECLARE_WAR_ON = 206; // Введите название клана, которому хотите объявить войну.
	public static final int S2_OF_THE_S1_CLAN_REQUESTS_DECLARATION_OF_WAR_DO_YOU_ACCEPT = 207; // $s2 из клана $s1 объявляет Вам войну. Согласиться?
	public static final int PLEASE_INCLUDE_FILE_TYPE_WHEN_ENTERING_FILE_PATH = 208; // Укажите расширение файла и путь к нему.
	public static final int THE_SIZE_OF_THE_IMAGE_FILE_IS_DIFFERENT_PLEASE_ADJUST_TO_16_12 = 209; // Неверный размер рисунка. Пожалуйста, поменяйте размер на 16*12.
	public static final int CANNOT_FIND_FILE_PLEASE_ENTER_PRECISE_PATH = 210; // Файл не найден. Пожалуйста, введите заново точный путь к нему.
	public static final int CAN_ONLY_REGISTER_16_12_SIZED_BMP_FILES_OF_256_COLORS = 211; // Файл - формат bmp, 256 цветов, размер 16*12.
	public static final int YOU_ARE_NOT_A_CLAN_MEMBER = 212; // Вы не состоите в клане.
	public static final int NOT_WORKING_PLEASE_TRY_AGAIN_LATER = 213; // Недоступно. Пожалуйста, попробуйте позже.
	public static final int TITLE_HAS_CHANGED = 214; // Титул изменен.
	public static final int WAR_WITH_THE_S1_CLAN_HAS_BEGUN = 215; // Началась война с кланом $s1.
	public static final int WAR_WITH_THE_S1_CLAN_HAS_ENDED = 216; // Война с кланом $s1 закончилась.
	public static final int YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN = 217; // Вы выиграли войну с кланом $s1!
	public static final int YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN = 218; // Вы капитулировали в войне с кланом $s1.
	public static final int YOUR_CLAN_LEADER_HAS_DIEDYOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN = 219; // Глава клана убит. Вы проиграли войну с кланом $s1.
	public static final int YOU_HAVE_S1_MINUTES_LEFT_UNTIL_THE_CLAN_WAR_ENDS = 220; // До окончания войны: $s1 мин.
	public static final int THE_TIME_LIMIT_FOR_THE_CLAN_WAR_IS_UPWAR_WITH_THE_S1_CLAN_IS_OVER = 221; // Время войны истекло. Война закончилась.
	public static final int S1_HAS_JOINED_THE_CLAN = 222; // $s1 вступает в клан.
	public static final int S1_HAS_WITHDRAWN_FROM_THE_CLAN = 223; // $s1 выходит из клана.
	public static final int S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED = 224; // $s1 не отвечает. Приглашение на вступление в клан отменено.
	public static final int YOU_DIDNT_RESPOND_TO_S1S_INVITATION_JOINING_HAS_BEEN_CANCELLED = 225; // Вы не ответили персонажу $s1. Приглашение на вступление в клан отменено.
	public static final int THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED = 226; // Клан $s1 не ответил. Объявление войны отменено.
	public static final int CLAN_WAR_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLANS_WAR_PROCLAMATION = 227; // Вы не ответили клану $s1. Война не была объявлена.
	public static final int REQUEST_TO_END_WAR_HAS_BEEN_DENIED = 228; // Предложение завершить войну отклонено.
	public static final int YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN = 229; // Пока Вы не можете создать клан.
	public static final int YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN = 230; // Нельзя создать новый клан в течение 10 дней после расформирования предыдущего.
	public static final int AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER = 231; // После исключения персонажа из клана нужно подождать 24 ч, перед тем как принять другого.
	public static final int AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN = 232; // Нельзя вступить в новый клан в течение 24 ч после выхода из предыдущего.
	public static final int THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME = 233; // Клан переполнен.
	public static final int THE_TARGET_MUST_BE_A_CLAN_MEMBER = 234; // Цель должна состоять в клане.
	public static final int YOU_CANNOT_TRANSFER_YOUR_RIGHTS = 235; // Вы не можете распоряжаться правами.
	public static final int ONLY_THE_CLAN_LEADER_IS_ENABLED = 236; // Это может сделать только глава клана.
	public static final int CANNOT_FIND_CLAN_LEADER = 237; // Невозможно найти главу клана.
	public static final int NOT_JOINED_IN_ANY_CLAN = 238; // Не в клане.
	public static final int THE_CLAN_LEADER_CANNOT_WITHDRAW = 239; // Глава клана не может покинуть клан.
	public static final int CURRENTLY_INVOLVED_IN_CLAN_WAR = 240; // Сейчас идет война кланов.
	public static final int LEADER_OF_THE_S1_CLAN_IS_NOT_LOGGED_IN = 241; // Главы клана $s1 нет в игре.
	public static final int SELECT_TARGET = 242; // Выберите цель.
	public static final int CANNOT_PROCLAIM_WAR_ON_ALLIED_CLANS = 243; // Нельзя объявить войну союзному клану.
	public static final int UNQUALIFIED_TO_REQUEST_DECLARATION_OF_CLAN_WAR = 244; // У Вас нет прав на объявление войны.
	public static final int _5_DAYS_HAS_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR_DO_YOU_WANT_TO_CONTINUE = 245; // С момента отказа от войны не прошло 5 дней. Продолжить?
	public static final int THE_OTHER_CLAN_IS_CURRENTLY_AT_WAR = 246; // Этот клан уже воюет.
	public static final int YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_PROCLAIM_WAR_AGAIN = 247; // Вы уже воевали с кланом $s1. Перед повторным объявлением войны должно пройти 5 дней.
	public static final int YOU_CANNOT_PROCLAIM_WAR_THE_S1_CLAN_DOES_NOT_HAVE_ENOUGH_MEMBERS = 248; // В клане $s1 слишком мало персонажей. Невозможно объявить войну.
	public static final int DO_YOU_WISH_TO_SURRENDER_TO_THE_S1_CLAN = 249; // Капитулировать в войне с кланом $s1?
	public static final int YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN_YOU_ARE_LEAVING_THE_CLAN_WAR = 250; // Вы сдались клану $s1. Война закончена.
	public static final int YOU_CANNOT_PROCLAIM_WAR_YOU_ARE_AT_WAR_WITH_ANOTHER_CLAN = 251; // Вы уже в состоянии войны. Нельзя объявить войну другому клану.
	public static final int ENTER_THE_NAME_OF_CLAN_TO_SURRENDER_TO = 252; // Введите название клана, которому хотите сдаться.
	public static final int ENTER_THE_NAME_OF_CLAN_TO_REQUEST_END_OF_WAR = 253; // Введите название клана, которому предлагаете мир.
	public static final int CLAN_LEADER_CANNOT_SURRENDER_PERSONALLY = 254; // Глава клана не может сдаться.
	public static final int THE_S1_CLAN_HAS_REQUESTED_TO_END_WAR_DO_YOU_AGREE = 255; // Клан $s1 предлагает мир. Согласиться?
	public static final int ENTER_NAME = 256; // Введите титул.
	public static final int DO_YOU_PROPOSE_TO_THE_S1_CLAN_TO_END_THE_WAR = 257; // Предложить мир клану $s1?
	public static final int NOT_INVOLVED_IN_CLAN_WAR = 258; // Вы не участвуете в войне.
	public static final int SELECT_CLAN_MEMBERS_FROM_LIST = 259; // Выберите члена клана из списка.
	public static final int FAME_LEVEL_HAS_DECREASED_5_DAYS_HAVE_NOT_PASSED_SINCE_YOU_WERE_REFUSED_WAR = 260; // После окончания войны кланов не прошло 5 дней, репутация клана понизилась.
	public static final int CLAN_NAME_IS_INCORRECT = 261; // Неверное название клана.
	public static final int CLAN_NAMES_LENGTH_IS_INCORRECT = 262; // Неверная длина названия клана.
	public static final int DISPERSION_HAS_ALREADY_BEEN_REQUESTED = 263; // Вы уже подали заявку на расформирование клана.
	public static final int YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR = 264; // Нельзя расформировать клан во время войны.
	public static final int YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE = 265; // Нельзя расформировать клан во время осады.
	public static final int YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE = 266; // Нельзя расформировать клан, владеющий холлом/замком.
	public static final int NO_REQUESTS_FOR_DISPERSION = 267; // Нет заявки на расформирование клана.
	public static final int PLAYER_ALREADY_BELONGS_TO_A_CLAN = 268; // Персонаж уже состоит в клане.
	public static final int YOU_CANNOT_EXPEL_YOURSELF = 269; // Вы не можете исключить себя.
	public static final int YOU_HAVE_ALREADY_SURRENDERED = 270; // Вы уже капитулировали.
	public static final int TITLE_ENDOWMENT_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3 = 271; // Изменение титулов возможно при уровне клана 3 и выше.
	public static final int CLAN_CREST_REGISTRATION_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3 = 272; // Установка эмблемы доступна при уровне клана 3 и выше.
	public static final int PROCLAMATION_OF_CLAN_WAR_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3 = 273; // Объявление войны возможно при уровне клана 3 и выше.
	public static final int CLANS_SKILL_LEVEL_HAS_INCREASED = 274; // Уровень клана увеличен.
	public static final int CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL = 275; // Не удалось увеличить уровень умений клана.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS = 276; // Не хватает материалов, чтобы выучить умение.
	public static final int YOU_HAVE_EARNED_S1_2 = 277; // Выучено умение: $s1.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_SKILLS = 278; // Недостаточно SP для изучения умения.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_ADENA = 279; // Недостаточно аден.
	public static final int YOU_DO_NOT_HAVE_ANY_ITEMS_TO_SELL = 280; // Нет предметов на продажу.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_CUSTODY_FEES = 281; // Недостаточно денег для оплаты хранилища.
	public static final int YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE = 282; // В хранилище нет предметов.
	public static final int YOU_HAVE_ENTERED_A_COMBAT_ZONE = 283; // Вы зашли в боевую зону.
	public static final int YOU_HAVE_LEFT_A_COMBAT_ZONE = 284; // Вы вышли из боевой зоны.
	public static final int CLAN_S1_HAS_SUCCEEDED_IN_ENGRAVING_THE_RULER = 285; // Победил клан $s1!
	public static final int YOUR_BASE_IS_BEING_ATTACKED = 286; // Ваша база атакована.
	public static final int THE_OPPONENT_CLAN_HAS_BEGUN_TO_ENGRAVE_THE_RULER = 287; // Противники пытаются захватить Вашу базу.
	public static final int THE_CASTLE_GATE_HAS_BEEN_BROKEN_DOWN = 288; // Врата замка разрушены.
	public static final int SINCE_A_HEADQUARTERS_ALREADY_EXISTS_YOU_CANNOT_BUILD_ANOTHER_ONE = 289; // Нельзя поставить еще одну базу.
	public static final int YOU_CANNOT_SET_UP_A_BASE_HERE = 290; // Невозможно поставить базу здесь.
	public static final int CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE = 291; // Клан $s1 завоевал $s2!
	public static final int S1_HAS_ANNOUNCED_THE_CASTLE_SIEGE_TIME = 292; // $s1 объявляет время осады замка.
	public static final int THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED = 293; // Регистрация на осаду замка $s1 завершена.
	public static final int YOU_CANNOT_SUMMON_A_BASE_BECAUSE_YOU_ARE_NOT_IN_BATTLE = 294; // Ваш клан не участвует в осаде. Нельзя создать базу.
	public static final int S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED = 295; // Осада замка $s1 отменена из-за отсутствия заявок.
	public static final int YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL = 296; // Получено $s1 урона из-за падения с высоты.
	public static final int YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE = 297; // Получено $s1 урона от удушья.
	public static final int YOU_HAVE_DROPPED_S1 = 298; // Вы выронили: $s1.
	public static final int S1_HAS_OBTAINED_S3_S2 = 299; // $c1 получает: $s2 ($s3 шт.)
	public static final int S1_HAS_OBTAINED_S2 = 300; // $c1 получает: $s2.
	public static final int S2_S1_HAS_DISAPPEARED = 301; // Исчезло: $s1 ($s2 шт.)
	public static final int S1_HAS_DISAPPEARED = 302; // Исчезло: $s1.
	public static final int SELECT_ITEM_TO_ENCHANT = 303; // Выберите предмет для улучшения.
	public static final int CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME = 304; // Член клана $s1 зашел в игру.
	public static final int THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY = 305; // Персонаж отказался присоединиться к группе.
	public static final int YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER = 306; // Удаление персонажа не удалось.
	public static final int YOU_HAVE_FAILED_TO_TRADE_WITH_THE_WAREHOUSE = 307; // Нельзя торговать со Смотрителем Склада.
	public static final int FAILED_TO_JOIN_THE_CLAN = 308; // Персонаж отказался присоединиться к клану.
	public static final int SUCCEEDED_IN_EXPELLING_A_CLAN_MEMBER = 309; // Исключение члена клана прошло успешно.
	public static final int FAILED_TO_EXPEL_A_CLAN_MEMBER = 310; // Исключение члена клана не удалось.
	public static final int CLAN_WAR_HAS_BEEN_ACCEPTED = 311; // Предложение войны между кланами принято.
	public static final int CLAN_WAR_HAS_BEEN_REFUSED = 312; // Предложение войны между кланами отклонено.
	public static final int THE_CEASE_WAR_REQUEST_HAS_BEEN_ACCEPTED = 313; // Предложение о завершении войны принято.
	public static final int FAILED_TO_SURRENDER = 314; // Капитуляция не удалась.
	public static final int FAILED_TO_PERSONALLY_SURRENDER = 315; // Нельзя выйти из войны.
	public static final int FAILED_TO_WITHDRAW_FROM_THE_PARTY = 316; // Нельзя выйти из группы.
	public static final int FAILED_TO_EXPEL_A_PARTY_MEMBER = 317; // Нельзя исключить персонажа из группы.
	public static final int FAILED_TO_DISPERSE_THE_PARTY = 318; // Нельзя расформировать группу.
	public static final int YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR = 319; // Нельзя открыть дверь.
	public static final int YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR = 320; // Вам не удалось открыть дверь.
	public static final int IT_IS_NOT_LOCKED = 321; // Не заперто.
	public static final int PLEASE_DECIDE_ON_THE_SALES_PRICE = 322; // Укажите цену продажи.
	public static final int YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL = 323; // Ваша сила увеличена до $s1 уровня.
	public static final int YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_ = 324; // Ваша сила достигла максимального уровня.
	public static final int THE_CORPSE_HAS_ALREADY_DISAPPEARED = 325; // Труп уже исчез.
	public static final int SELECT_TARGET_FROM_LIST = 326; // Выберите цель из списка.
	public static final int YOU_CANNOT_EXCEED_80_CHARACTERS = 327; // Нельзя ввести больше 80 символов.
	public static final int PLEASE_INPUT_TITLE_USING_LESS_THAN_128_CHARACTERS = 328; // Максимальная длина заголовка - 128 символов.
	public static final int PLEASE_INPUT_CONTENTS_USING_LESS_THAN_3000_CHARACTERS = 329; // Максимальный объем текста - 3000 символов.
	public static final int A_ONE_LINE_RESPONSE_MAY_NOT_EXCEED_128_CHARACTERS = 330; // Максимальная длина ответа - 128 символов.
	public static final int YOU_HAVE_ACQUIRED_S1_SP = 331; // Получено: $s1 SP.
	public static final int DO_YOU_WANT_TO_BE_RESTORED = 332; // Хотите возродиться?
	public static final int YOU_HAVE_RECEIVED_S1_DAMAGE_BY_CORES_BARRIER = 333; // Получено $s1 урона от барьера Ядра.
	public static final int PLEASE_ENTER_STORE_MESSAGE = 334; // Введите сообщение.
	public static final int S1_IS_ABORTED = 335; // $s1: отменено.
	public static final int S1_IS_CRYSTALLIZED_DO_YOU_WANT_TO_CONTINUE = 336; // $s1: продолжить кристаллизацию?
	public static final int SOULSHOT_DOES_NOT_MATCH_WEAPON_GRADE = 337; // Заряд Души не соответствует рангу оружия.
	public static final int NOT_ENOUGH_SOULSHOTS = 338; // Недостаточно Зарядов Души.
	public static final int CANNOT_USE_SOULSHOTS = 339; // Нельзя использовать Заряд Души.
	public static final int PRIVATE_STORE_UNDER_WAY = 340; // Личная лавка открыта.
	public static final int NOT_ENOUGH_MATERIALS = 341; // Недостаточно материалов.
	public static final int POWER_OF_THE_SPIRITS_ENABLED = 342; // Ваше оружие наполнено силой.
	public static final int SWEEPER_FAILED_TARGET_NOT_SPOILED = 343; // Цель не оценена. Не удалось ничего присвоить.
	public static final int POWER_OF_THE_SPIRITS_DISABLED = 344; // Сила духов недоступна.
	public static final int CHAT_ENABLED = 345; // Чат включен.
	public static final int CHAT_DISABLED = 346; // Чат отключен.
	// 347 какого-то хрена не отображается в клиенте, 351 вместо него
	//@Deprecated
	public static final int INCORRECT_ITEM_COUNT_0 = 347; // Неверное количество предметов.
	public static final int INCORRECT_ITEM_PRICE = 348; // Неверная цена предмета.
	public static final int PRIVATE_STORE_ALREADY_CLOSED = 349; // Личная лавка закрыта.
	public static final int ITEM_OUT_OF_STOCK = 350; // Предмет продан.
	public static final int INCORRECT_ITEM_COUNT = 351; // Неверное количество предмета.
	public static final int INCORRECT_ITEM = 352; // Неверный предмет.
	public static final int CANNOT_PURCHASE = 353; // Нельзя купить.
	public static final int CANCEL_ENCHANT = 354; // Улучшение отменено.
	public static final int INAPPROPRIATE_ENCHANT_CONDITIONS = 355; // Нельзя улучшить в этих условиях.
	public static final int REJECT_RESURRECTION = 356; // Воскрешение отменено.
	public static final int ALREADY_SPOILED = 357; // Цель уже оценена.
	public static final int S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION = 358; // До окончания осады замка: $s1 ч.
	public static final int S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION = 359; // До окончания осады замка: $s1 мин.
	public static final int CASTLE_SIEGE_S1_SECOND_S_LEFT = 360; // До окончания осады замка: $s1 сек.
	public static final int OVER_HIT = 361; // Сверхудар!
	public static final int ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT = 362; // Получено дополнительно $s1 опыта за сверхудар.
	public static final int CHAT_AVAILABLE_TIME_S1_MINUTE = 363; // До разблокировки чата: $s1 мин.
	public static final int ENTER_USERS_NAME_TO_SEARCH = 364; // Введите имя персонажа для поиска.
	public static final int ARE_YOU_SURE = 365; // Вы уверены?
	public static final int SELECT_HAIR_COLOR = 366; // Выберите цвет волос.
	public static final int CANNOT_REMOVE_CLAN_CHARACTER = 367; // Нельзя удалить персонажа, состоящего в клане.
	public static final int EQUIPPED__S1_S2 = 368; // +$s1 $s2: надето.
	public static final int YOU_HAVE_OBTAINED__S1S2 = 369; // Получено: +$s1 $s2.
	//@Deprecated
	public static final int FAILED_TO_PICK_UP_S1 = 370; // Нельзя поднять: +$s1 $s2.
	public static final int ACQUIRED__S1_S2 = 371; // Получено: +$s1 $s2.
	//@Deprecated
	public static final int FAILED_TO_EARN_S1 = 372; // Нельзя получить: +$s1 $s2.
	public static final int DESTROY__S1_S2_DO_YOU_WISH_TO_CONTINUE = 373; // +$s1 $s2 - уничтожить?
	public static final int CRYSTALLIZE__S1_S2_DO_YOU_WISH_TO_CONTINUE = 374; // +$s1 $s2 - кристаллизировать?
	public static final int DROPPED__S1_S2 = 375; // Вы уронили: +$s1 $s2.
	public static final int S1_HAS_OBTAINED__S2S3 = 376; // $c1 получает: +$s2 $s3.
	public static final int _S1_S2_DISAPPEARED = 377; // +$s1 $s2 исчезает.
	public static final int S1_PURCHASED_S2 = 378; // $c1 покупает: $s2.
	public static final int S1_PURCHASED__S2_S3 = 379; // $c1 покупает: +$s2 $s3.
	public static final int S1_PURCHASED_S3_S2_S = 380; // $c1 покупает: $s2 ($s3 шт.).
	public static final int CANNOT_CONNECT_TO_PETITION_SERVER = 381; // Нельзя подключиться к серверу петиций.
	public static final int CURRENTLY_THERE_ARE_NO_USERS_THAT_HAVE_CHECKED_OUT_A_GM_ID = 382; // В данный момент на игре нет Игровых мастеров.
	public static final int REQUEST_CONFIRMED_TO_END_CONSULTATION_AT_PETITION_SERVER = 383; // Запрос о завершении консультации на сервере петиций подтвержден.
	public static final int THE_CLIENT_IS_NOT_LOGGED_ONTO_THE_GAME_SERVER = 384; // Клиента нет в игре.
	public static final int REQUEST_CONFIRMED_TO_BEGIN_CONSULTATION_AT_PETITION_SERVER = 385; // Запрос о начале консультации на сервер обращений подтвержден.
	public static final int PETITION_REQUESTS_MUST_BE_OVER_FIVE_CHARACTERS = 386; // Минимальный размер сообщения - 6 символов.
	public static final int ENDING_PETITION_CONSULTATION = 387; // Служба поддержки ответила на Вашу заявку. \\nПожалуйста, прокомментируйте качество обслуживания.
	public static final int NOT_UNDER_PETITION_CONSULTATION = 388; // На данный момент не ведется консультаций.
	public static final int PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1 = 389; // Ваша заявка принята. \\n - Номер заявки: $s1.
	public static final int ALREADY_APPLIED_FOR_PETITION = 390; // Вы не можете подать больше 1 заявки.
	public static final int RECEIPT_NO_S1_PETITION_CANCELLED = 391; // Заявка №$s1 отменена.
	public static final int UNDER_PETITION_ADVICE = 392; // На данный момент ведется консультация.
	public static final int FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER = 393; // Не удалось отменить заявку. Пожалуйста, попробуйте позже.
	public static final int PETITION_CONSULTATION_WITH_S1_UNDER_WAY = 394; // Беседа с игроком $c1 началась.
	public static final int ENDING_PETITION_CONSULTATION_WITH_S1 = 395; // Беседа с игроком $c1 закончилась.
	public static final int PLEASE_LOGIN_AFTER_CHANGING_YOUR_TEMPORARY_PASSWORD = 396; // Измените временный пароль на сайте и зайдите в игру.
	public static final int NOT_A_PAID_ACCOUNT = 397; // Аккаунт не оплачен.
	public static final int YOU_HAVE_NO_MORE_TIME_LEFT_ON_YOUR_ACCOUNT = 398; // Истекло оплаченное время игры на Вашем аккаунте.
	public static final int SYSTEM_ERROR = 399; // Ошибка системы.
	public static final int DISCARD_S1_DO_YOU_WISH_TO_CONTINUE = 400; // $s1 - выбросить?
	public static final int TOO_MANY_QUESTS_IN_PROGRESS = 401; // У Вас слишком много квестов.
	public static final int YOU_MAY_NOT_GET_ON_BOARD_WITHOUT_A_PASS = 402; // Безбилетный проезд запрещен.
	public static final int YOU_HAVE_EXCEEDED_YOUR_POCKET_MONEY_LIMIT = 403; // Превышен лимит аден.
	public static final int CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE = 404; // Уровень умения «Создать Предмет» слишком низок для регистрации рецепта.
	public static final int THE_TOTAL_PRICE_OF_THE_PRODUCT_IS_TOO_HIGH = 405; // Общая стоимость слишком высока.
	public static final int PETITION_APPLICATION_ACCEPTED = 406; // Заявка принята.
	public static final int PETITION_UNDER_PROCESS = 407; // Петиция находится в стадии проверки.
	public static final int SET_PERIOD = 408; // Установка осады
	public static final int SET_TIME_S1_S2_S3 = 409; // Выбор времени: $s1 ч $s2 мин $s3 сек.
	public static final int REGISTRATION_PERIOD = 410; // Регистрация осады
	public static final int REGISTRATION_TIME_S1_S2_S3 = 411; // Время регистрации: $s1 ч $s2 мин $s3 сек.
	public static final int BATTLE_BEGINS_IN_S1_S2_S4 = 412; // Начало осады: $s1 ч $s2 мин $s4 сек.
	public static final int BATTLE_ENDS_IN_S1_S2_S5 = 413; // Окончание осады: $s1 ч $s2 мин $s5 сек.
	public static final int STANDBY = 414; // Ожидание осады
	public static final int UNDER_SIEGE = 415; // В процессе осады
	public static final int CANNOT_BE_EXCHANGED = 416; // Нельзя обменять.
	public static final int S1__HAS_BEEN_DISARMED = 417; // Снято: $s1.
	public static final int THERE_IS_A_SIGNIFICANT_DIFFERENCE_BETWEEN_THE_ITEMS_PRICE_AND_ITS_STANDARD_PRICE_PLEASE_CHECK_AGAIN = 418; // Цена предмета слишком высока по сравнению со стандартной. Пожалуйста, измените цену.
	public static final int S1_MINUTE_S_OF_DESIGNATED_USAGE_TIME_LEFT = 419; // До окончания использования: $s1 мин.
	public static final int TIME_EXPIRED = 420; // Время истекло.
	public static final int ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT = 421; // Этот аккаунт уже в игре.
	public static final int YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT = 422; // Превышен лимит веса.
	public static final int THE_SCROLL_OF_ENCHANT_HAS_BEEN_CANCELED = 423; // Вы отменили процесс улучшения.
	public static final int DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL = 424; // Несоответствие условий свитка улучшения.
	public static final int YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW = 425; // Уровень умения «Создать Предмет» слишком низок для регистрации рецепта.
	public static final int YOUR_ACCOUNT_HAS_BEEN_REPORTED_FOR_INTENTIONALLY_NOT_PAYING_THE_CYBER_CAFE_FEES = 426; // От администрации компьютерного клуба поступила жалоба, что Ваша учетная запись была замечена на компьютере с неоплаченным временем.
	public static final int PLEASE_CONTACT_US = 427; // Пожалуйста, свяжитесь со службой поддержки.
	public static final int IN_ACCORDANCE_WITH_COMPANY_POLICY_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_SUSPICION_OF_ILLEGAL = 428; // В соответствии с политикой компании, Ваш аккаунт был заблокирован за подозрение в нелегальной деятельности/присвоении чужих данных. Если Вы не считаете себя виновным, посетите наш сайт и обратитесь в службу поддержки для подачи апелляции.
	public static final int IN_ACCORDANCE_WITH_COMPANY_POLICY_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_FALSELY_REPORTING_A = 429; // В соответствии с политикой компании, Ваш аккаунт был заблокирован за ложный доклад о присвоении аккаунта. Такие доклады могут навредить честным игрокам. За дополнительной информацией обратитесь в службу поддержки на сайте.
	public static final int __DOESNT_NEED_TO_TRANSLATE = 430; // (перевод не нужен)
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_DUE_TO_VIOLATING_THE_EULA_ROC_AND_OR_USER_AGREEMENT_CHAPTER_4 = 431; // Ваш аккаунт был заблокирован за нарушение Пользовательского Соглашения. В случае несоблюдения условий соглашения со стороны пользователя компания имеет право заблокировать его учетную запись. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_7_DAYS_RETROACTIVE_TO_THE_DAY_OF_DISCLOSURE_UNDER_CHAPTER_3 = 432; // Ваш аккаунт был заблокирован на 7 дней (со дня оглашения) в соответствии с Пользовательским Соглашением за проведение денежных операций (или их попытку) с игровыми предметами, персонажами (аккаунтами) или игровой валютой. Через 7 дней Ваш аккаунт автоматически разблокируется. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE = 433; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за обмен (или попытку обмена) игровых предметов/персонажей на наличные деньги или предметы из другой игры. За дополнительной информацией обратитесь в службу поддержки на сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_1 = 434; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за непристойное поведение или мошенничество. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_2 = 435; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за непристойное поведение. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_3 = 436; // Ваш аккаунт был заблокирован в соответствии Пользовательским Соглашением за злоупотребление игровой системой и использование ошибок игры с целью получения выгоды. Использование ошибок нарушает игровой баланс. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_4 = 437; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за использование нелегального программного обеспечения. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_5 = 438; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за попытку выдать себя за представителя службы поддержки или штатного сотрудника компании. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int IN_ACCORDANCE_WITH_THE_COMPANYS_USER_AGREEMENT_AND_OPERATIONAL_POLICY_THIS_ACCOUNT_HAS_BEEN = 439; // В соответствии с Пользовательским Соглашением и политикой компании этот аккаунт заблокирован по просьбе владельца. Если у Вас есть вопросы по поводу аккаунта, обратитесь в службу поддержки на нашем сайте.
	public static final int BECAUSE_YOU_ARE_REGISTERED_AS_A_MINOR_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_AT_THE_REQUEST_OF_YOUR = 440; // Вы зарегистрированы как несовершеннолетний, и Ваш аккаунт был заблокирован по просьбе ваших родителей или опекунов. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int PER_OUR_COMPANYS_USER_AGREEMENT_THE_USE_OF_THIS_ACCOUNT_HAS_BEEN_SUSPENDED_IF_YOU_HAVE_ANY = 441; // В соответствии с Пользовательским Соглашением данный аккаунт был заблокирован. Если у Вас есть вопросы касательно этого аккаунта, обратитесь в службу поддержки на нашем сайте.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_2_SECTION_7_OF_THE_LINEAGE_II_SERVICE_USE = 442; // Ваш аккаунт был заблокирован в соответствии с Пользовательским Соглашением за присвоение платы другого аккаунта. За дополнительной информацией обратитесь в службу поддержки на нашем сайте.
	public static final int THE_IDENTITY_OF_THIS_ACCOUNT_HAS_NOT_BEEN_VEEN_VERIFIED_THEREFORE_LINEAGE_II_SERVICE_FOR_THIS = 443; // Подлинность Вашего аккаунта не была подтверждена, потому его действие сейчас приостановлено. Для подтверждения подлинности, пожалуйста, вышлите нам данные аккаунта, Ваши личные данные, копию документа, удостоверяющего Вашу личность, и контактную информацию. За дополнительными сведениями обратитесь в службу поддержки на нашем сайте.
	public static final int SINCE_WE_HAVE_RECEIVED_A_WITHDRAWAL_REQUEST_FROM_THE_HOLDER_OF_THIS_ACCOUNT_ACCESS_TO_ALL = 444; // Получен запрос на блокировку аккаунта от его владельца, аккаунт автоматически блокируется.
	public static final int REFERENCE_NUMBER_REGARDING_MEMBERSHIP_WITHDRAWAL_REQUEST__S1 = 445; // (Подать заявку на членство? Номер квитанции: $s1)
	public static final int FOR_MORE_INFORMATION_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_PLAYNC_WEBSITE_HTTP___WWWPLAYNCCOM = 446; // Дополнительную информацию можно получить в службе поддержки на нашем сайте.
	public static final int SYSMSG_ID447 = 447; // .
	public static final int SYSTEM_ERROR_PLEASE_LOG_IN_AGAIN_LATER = 448; // Ошибка системы. Пожалуйста, зайдите в игру позже.
	public static final int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = 449; // Вы ввели неверный логин или пароль.
	public static final int CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_LOG_IN_AGAIN_LATER = 450; // Подтвердите информацию и зайдите в игру.
	public static final int THE_PASSWORD_YOU_HAVE_ENTERED_IS_INCORRECT = 451; // Вы ввели неверный логин или пароль.
	public static final int PLEASE_CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_TRY_LOGGING_IN_AGAIN = 452; // Подтвердите информацию и зайдите в игру.
	public static final int YOUR_ACCOUNT_INFORMATION_IS_INCORRECT = 453; // Неверная информация.
	public static final int FOR_MORE_DETAILS_PLEASE_CONTACT_OUR_CUSTOMER_SERVICE_CENTER_AT_HTTP__SUPPORTPLAYNCCOM = 454; // Для получения дополнительной информации обратитесь в службу поддержки на нашем сайте.
	public static final int THE_ACCOUNT_IS_ALREADY_IN_USE_ACCESS_DENIED = 455; // Такой аккаунт уже используется. Войти невозможно.
	public static final int LINEAGE_II_GAME_SERVICES_MAY_BE_USED_BY_INDIVIDUALS_15_YEARS_OF_AGE_OR_OLDER_EXCEPT_FOR_PVP_SERVERS = 456; // В Lineage II могут играть пользователи, которым не менее 15 лет, на PvP серверах – не менее 18 лет.
	public static final int SERVER_UNDER_MAINTENANCE_PLEASE_TRY_AGAIN_LATER = 457; // В данный момент на сервере проходят профилактические работы. Попробуйте зайти позже.
	public static final int YOUR_USAGE_TERM_HAS_EXPIRED = 458; // Время игры закончилось.
	public static final int PLEASE_VISIT_THE_OFFICIAL_LINEAGE_II_WEBSITE_AT_HTTP__WWWLINEAGE2COM = 459; // Чтобы продолжить игру, приобретите LineageII
	public static final int TO_REACTIVATE_YOUR_ACCOUNT = 460; // в нашем сайте или в магазине.
	public static final int ACCESS_FAILED = 461; // Не удалось соединиться с сервером.
	//@Deprecated
	public static final int PLEASE_TRY_AGAIN_LATER_1 = 462; // Пожалуйста, повторите попытку позже.
	public static final int SYSMSG_ID463 = 463; // .
	public static final int FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY = 464; // Это может делать только глава альянса.
	public static final int YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS = 465; // Вы не состоите в альянсе.
	public static final int YOU_HAVE_EXCEEDED_THE_LIMIT = 466; // Нельзя принять новый клан.
	public static final int YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN = 467; // После исключения клана из альянса Вы можете принять новый клан только через 24 часа.
	public static final int A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION = 468; // Исключенный из альянса или покинувший его клан не может вступить в новый в течение 24 ч.
	public static final int YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_AT_BATTLE_WITH = 469; // Нельзя создать альянс с кланом, с которым Вы ведете войну.
	public static final int ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE = 470; // Только глава клана может вывести клан из альянса.
	public static final int ALLIANCE_LEADERS_CANNOT_WITHDRAW = 471; // Глава альянса не может покинуть альянс.
	public static final int YOU_CANNOT_EXPEL_YOURSELF_FROM_THE_CLAN = 472; // Вы не можете исключить себя из клана.
	public static final int DIFFERENT_ALLIANCE = 473; // Клан состоит в другом альянсе.
	public static final int THE_FOLLOWING_CLAN_DOES_NOT_EXIST = 474; // Клана не существует.
	//@Deprecated
	public static final int DIFFERENT_ALLIANCE_1 = 475; // Клан состоит в другом альянсе.
	public static final int INCORRECT_IMAGE_SIZE_PLEASE_ADJUST_TO_8X12 = 476; // Неверный размер рисунка. Пожалуйста, поменяйте размер на 8*12.
	public static final int NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED = 477; // Нет ответа. Приглашение на вступление в альянс отменено.
	public static final int NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED = 478; // Нет ответа. Ваше вступление в альянс отменено.
	public static final int S1_HAS_JOINED_AS_A_FRIEND = 479; // $s1 добавляется в список друзей.
	public static final int PLEASE_CHECK_YOUR_FRIENDS_LIST = 480; // Проверьте список друзей.
	public static final int S1__HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST = 481; // $s1 удаляется из списка друзей.
	//@Deprecated
	public static final int YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST_1 = 482; // Нельзя добавить себя в список друзей.
	public static final int FRIEND_LIST_IS_NOT_READY_YET_PLEASE_TRY_AGAIN_LATER = 483; // Нельзя создать список друзей. Попробуйте позже.
	public static final int ALREADY_REGISTERED_ON_THE_FRIENDS_LIST = 484; // Уже в списке друзей.
	public static final int NO_NEW_FRIEND_INVITATIONS_FROM_OTHER_USERS = 485; // Нельзя принять новых друзей.
	public static final int THE_FOLLOWING_USER_IS_NOT_IN_YOUR_FRIENDS_LIST = 486; // Нет в списке Ваших друзей.
	public static final int _FRIENDS_LIST_ = 487; // ======<FRIENDS_LIST>======
	public static final int S1_CURRENTLY_ONLINE = 488; // $s1 (В сети)
	public static final int S1_CURRENTLY_OFFLINE = 489; // $s1 (Не в сети)
	public static final int __EQUALS__ = 490; // ========================
	public static final int _ALLIANCE_INFORMATION_ = 491; // =======<ALLIANCE_INFORMATION>=======
	public static final int ALLIANCE_NAME_S1 = 492; // Название альянса : $s1
	public static final int CONNECTION_S1_TOTAL_S2 = 493; // В сети: $s1/Всего: $s2
	public static final int ALLIANCE_LEADER_S2_OF_S1 = 494; // Глава альянса: $s2, клан $s1
	public static final int AFFILIATED_CLANS_TOTAL_S1_CLAN_S = 495; // Кланов в альянсе: $s1
	public static final int _CLAN_INFORMATION_ = 496; // =====<CLAN_INFORMATION>=====
	public static final int CLAN_NAME_S1 = 497; // Название клана: $s1
	public static final int CLAN_LEADER_S1 = 498; // Глава клана: $s1
	public static final int CLAN_LEVEL_S1 = 499; // Уровень клана: $s1
	public static final int __DASHES__ = 500; // ------------------------
	public static final int SYSMSG_ID501 = 501; // ========================
	public static final int YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE = 502; // Вы уже состоите в альянсе.
	public static final int S1_FRIEND_HAS_LOGGED_IN = 503; // Ваш друг $s1 вошел в игру.
	public static final int ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES = 504; // Только главы кланов могут создать альянс.
	public static final int YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_AFTER_DISSOLUTION = 505; // Нельзя создать альянс в течение 24 ч после роспуска предыдущего.
	public static final int INCORRECT_ALLIANCE_NAME = 506; // Неверное название альянса.
	public static final int INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME = 507; // Неверная длина названия альянса.
	public static final int THIS_ALLIANCE_NAME_ALREADY_EXISTS = 508; // Альянс с таким названием уже существует.
	public static final int CANNOT_ACCEPT_CLAN_ALLY_IS_REGISTERED_AS_AN_ENEMY_DURING_SIEGE_BATTLE = 509; // Заявка отклонена. Союзный клан зарегистрирован в осаде Вашего замка.
	public static final int YOU_HAVE_INVITED_SOMEONE_TO_YOUR_ALLIANCE = 510; // Вы пригласили в альянс.
	public static final int SELECT_USER_TO_INVITE = 511; // Выберите, кого хотите пригласить.
	public static final int DO_YOU_REALLY_WISH_TO_WITHDRAW_FROM_THE_ALLIANCE = 512; // Выйти из альянса? После этого нельзя вступить в другой в течение 24 ч.
	public static final int ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_EXPEL = 513; // Введите название клана, который хотите исключить из альянса.
	public static final int DO_YOU_REALLY_WISH_TO_DISSOLVE_THE_ALLIANCE = 514; // Распустить альянс? После этого нельзя создать другой в течение 24 ч.
	public static final int ENTER_FILE_NAME_FOR_THE_ALLIANCE_CREST = 515; // Введите название файла, который хотите зарегистрировать как эмблему альянса.
	public static final int S1_HAS_INVITED_YOU_AS_A_FRIEND = 516; // $s1 хочет добавить Вас в друзья.
	public static final int YOU_HAVE_ACCEPTED_THE_ALLIANCE = 517; // Вы вошли в состав альянса.
	public static final int YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE = 518; // Нельзя пригласить клан в альянс.
	public static final int YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE = 519; // Вы вышли из альянса.
	public static final int YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE = 520; // Нельзя выйти из альянса.
	public static final int YOU_HAVE_SUCCEEDED_IN_EXPELLING_A_CLAN = 521; // Вы исключили клан из альянса.
	public static final int YOU_HAVE_FAILED_TO_EXPEL_A_CLAN = 522; // Нельзя исключить клан из альянса.
	public static final int THE_ALLIANCE_HAS_BEEN_DISSOLVED = 523; // Альянс распущен.
	public static final int YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE = 524; // Нельзя распустить альянс.
	public static final int YOU_HAVE_SUCCEEDED_IN_INVITING_A_FRIEND = 525; // Вы добавили нового друга.
	public static final int YOU_HAVE_FAILED_TO_INVITE_A_FRIEND = 526; // Вы не смогли добавить нового друга.
	public static final int S2_THE_LEADER_OF_S1_HAS_REQUESTED_AN_ALLIANCE = 527; // $s2, лидер клана $s1, предлагает Вам альянс.
	public static final int FILE_NOT_FOUND = 528; // Невозможно найти файл.
	public static final int YOU_MAY_ONLY_REGISTER_8X12_BMP_FILES_WITH_256_COLORS = 529; // Файл: формат .bmp, 256 цветов, размер 8*12.
	public static final int SPIRITSHOT_DOES_NOT_MATCH_WEAPON_GRADE = 530; // Заряд Духа не соответствует рангу оружию.
	public static final int NOT_ENOUGH_SPIRITSHOTS = 531; // Недостаточно Зарядов Духа.
	public static final int CANNOT_USE_SPIRITSHOTS = 532; // Нельзя использовать Заряд Духа.
	public static final int POWER_OF_MANA_ENABLED = 533; // Магия наполняет Ваше оружие.
	public static final int POWER_OF_MANA_DISABLED = 534; // Действие Заряда Духа закончилось.
	public static final int NAME_PET = 535; // Введите имя питомца.
	public static final int HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_INVENTORY = 536; // Сколько аден переместить в инвентарь?
	public static final int HOW_MUCH_WILL_YOU_TRANSFER = 537; // Сколько переместить?
	public static final int SP_HAS_DECREASED_BY_S1 = 538; // SP снижены на $s1.
	public static final int EXPERIENCE_HAS_DECREASED_BY_S1 = 539; // Опыт снижен на $s1.
	public static final int CLAN_LEADERS_CANNOT_BE_DELETED_DISSOLVE_THE_CLAN_AND_TRY_AGAIN = 540; // Нельзя удалить лидера клана. Распустите клан и попробуйте снова.
	public static final int YOU_CANNOT_DELETE_A_CLAN_MEMBER_WITHDRAW_FROM_THE_CLAN_AND_TRY_AGAIN = 541; // Нельзя удалить члена клана. Выйдите из клана и попробуйте снова.
	public static final int NPC_SERVER_NOT_OPERATING_PETS_CANNOT_BE_SUMMONED = 542; // Сервер NPC отключен. Нельзя призвать питомца.
	public static final int YOU_ALREADY_HAVE_A_PET = 543; // У Вас уже есть питомец.
	public static final int ITEM_NOT_AVAILABLE_FOR_PETS = 544; // Питомец не может использовать этот предмет.
	public static final int DUE_TO_THE_VOLUME_LIMIT_OF_THE_PETS_INVENTORY_NO_MORE_ITEMS_CAN_BE_PLACED_THERE = 545; // Инвентарь питомца переполнен. Удалите что-нибудь и повторите попытку.
	public static final int EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT = 546; // Инвентарь питомца переполнен.
	public static final int SUMMON_A_PET = 547; // Призыв питомца…
	public static final int YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS = 548; // Максимальная длина имени питомца - 8 символов.
	public static final int TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER = 549; // Только клан 5-го уровня или выше может создать альянс.
	public static final int YOU_CANNOT_CREATE_AN_ALLIANCE_DURING_THE_TERM_OF_DISSOLUTION_POSTPONEMENT = 550; // Нельзя создать альянс сразу же после роспуска предыдущего.
	public static final int YOU_CANNOT_RAISE_YOUR_CLAN_LEVEL_DURING_THE_TERM_OF_DISPERSION_POSTPONEMENT = 551; // Нельзя повысить уровень клана после объявления о его роспуске.
	public static final int DURING_THE_GRACE_PERIOD_FOR_DISSOLVING_A_CLAN_REGISTRATION_OR_DELETION_OF_A_CLANS_CREST_IS_NOT_ALLOWED = 552; // Нельзя изменить/удалить эмблему клана во время роспуска клана.
	public static final int THE_OPPOSING_CLAN_HAS_APPLIED_FOR_DISPERSION = 553; // Выбранный клан находится в состоянии роспуска.
	public static final int YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE = 554; // Нельзя расформировать клан, состоящий в альянсе.
	public static final int YOU_CANNOT_MOVE_YOUR_ITEM_WEIGHT_IS_TOO_GREAT = 555; // Вы перегружены и не можете передвигаться.
	public static final int YOU_CANNOT_MOVE_IN_THIS_STATE = 556; // Вы не можете передвигаться в таком состоянии.
	public static final int THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED = 557; // Питомец призван и не может быть удален.
	public static final int THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_LET_GO = 558; // Питомец призван и не может быть отпущен.
	public static final int PURCHASED_S2_FROM_S1 = 559; // Вы купили у персонажа $c1: $s2.
	public static final int PURCHASED_S2_S3_FROM_S1 = 560; // Вы купили у персонажа $c1: +$s2 $s3.
	public static final int PURCHASED_S3_S2_S_FROM_S1_ = 561; // Вы купили у персонажа $c1: $s2 ($s3 шт.)
	public static final int CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW = 562; // Вы не можете разбить на кристаллы этот предмет. Уровень Вашего умения слишком низок.
	public static final int FAILED_TO_DISABLE_ATTACK_TARGET = 563; // Невозможно сбросить цель атаки.
	public static final int FAILED_TO_CHANGE_ATTACK_TARGET = 564; // Невозможно сменить цель атаки.
	public static final int NOT_ENOUGH_LUCK = 565; // У Вас не хватает удачи.
	public static final int CONFUSION_FAILED = 566; // Ваше заклинание не подействовало.
	public static final int FEAR_FAILED = 567; // Ваше заклинание страха не подействовало.
	public static final int CUBIC_SUMMONING_FAILED = 568; // Не удалось призвать Куб.
	public static final int CAUTION_THE_ITEM_PRICE_GREATLY_DIFFERS_FROM_THE_SHOPS_STANDARD_PRICE_DO_YOU_WISH_TO_CONTINUE = 569; // Внимание - цена предмета сильно отличается от цены в магазине. Продолжить?
	public static final int HOW_MANY__S1__S_DO_YOU_WISH_TO_PURCHASE = 570; // $s1: сколько купить?
	public static final int HOW_MANY__S1__S_DO_YOU_WANT_TO_PURCHASE = 571; // $s1: сколько удалить из списка покупок?
	public static final int DO_YOU_WISH_TO_JOIN_S1S_PARTY_ITEM_DISTRIBUTION_FINDERS_KEEPERS = 572; // Вступить в группу персонажа $c1? (распределение предметов: Нашедшему)
	public static final int DO_YOU_WISH_TO_JOIN_S1S_PARTY_ITEM_DISTRIBUTION_RANDOM = 573; // Вступить в группу персонажа $c1? (распределение предметов: Случайно)
	public static final int PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME = 574; // В данный момент призыв питомца и других существ невозможен.
	public static final int HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_PET = 575; // Сколько аден передать питомцу?
	public static final int HOW_MUCH_DO_YOU_WISH_TO_TRANSFER = 576; // Сколько предметов переместить?
	public static final int YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS = 577; // Нельзя призывать во время обмена или торговли.
	public static final int YOU_CANNOT_SUMMON_DURING_COMBAT = 578; // Нельзя призывать во время битвы.
	public static final int A_PET_CANNOT_BE_SENT_BACK_DURING_BATTLE = 579; // Нельзя отозвать питомца во время битвы.
	public static final int YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME = 580; // Нельзя призвать более одного питомца за раз.
	public static final int THERE_IS_A_SPACE_IN_THE_NAME = 581; // В имени есть пробел.
	public static final int INAPPROPRIATE_CHARACTER_NAME = 582; // Запрещенное имя персонажа.
	public static final int NAME_INCLUDES_FORBIDDEN_WORDS = 583; // В имени содержится запрещенное слово.
	public static final int ALREADY_IN_USE_BY_ANOTHER_PET = 584; // Питомец с таким именем уже существует.
	public static final int PLEASE_DECIDE_ON_THE_PRICE = 585; // Установите цену покупки.
	public static final int PET_ITEMS_CANNOT_BE_REGISTERED_AS_SHORTCUTS = 586; // Нельзя поместить предметы питомца в ячейки быстрого доступа.
	public static final int IRREGULAR_SYSTEM_SPEED = 587; // Скорость Вашей системы низка.
	public static final int PET_INVENTORY_IS_FULL = 588; // Инвентарь питомца полон.
	public static final int A_DEAD_PET_CANNOT_BE_SENT_BACK = 589; // Нельзя призвать мертвого питомца.
	public static final int CANNOT_GIVE_ITEMS_TO_A_DEAD_PET = 590; // Нельзя передать предмет мертвому питомцу.
	public static final int AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PETS_NAME = 591; // Имя питомца содержит запрещенный символ.
	public static final int DO_YOU_WISH_TO_DISMISS_YOUR_PET_DISMISSING_YOUR_PET_WILL_CAUSE_THE_PET_NECKLACE_TO_DISAPPEAR = 592; // Уничтожить питомца? При уничтожении пропадет ошейник питомца.
	public static final int YOUR_PET_HAS_LEFT_DUE_TO_UNBEARABLE_HUNGER = 593; // Питомец сбежал, так и не дождавшись от Вас еды.
	public static final int YOU_CANNOT_RESTORE_HUNGRY_PETS = 594; // Нельзя вернуть голодного питомца.
	public static final int YOUR_PET_IS_VERY_HUNGRY = 595; // Питомец очень голоден.
	public static final int YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY = 596; // Питомец немного поел, но все равно голоден.
	public static final int YOUR_PET_IS_VERY_HUNGRY_PLEASE_BE_CAREFUL = 597; // Питомец очень голоден, будьте осторожны.
	public static final int YOU_CANNOT_CHAT_WHILE_YOU_ARE_INVISIBLE = 598; // Нельзя пользоваться чатом в режиме невидимости.
	public static final int THE_GM_HAS_AN_IMPORTANT_NOTICE_CHAT_IS_TEMPORARILY_ABORTED = 599; // Внимание! Важное объявление службы поддержки! Некоторое время чат будет недоступен.
	public static final int YOU_CANNOT_EQUIP_A_PET_ITEM = 600; // Нельзя надеть предмет питомца.
	public static final int THERE_ARE_S1_PETITIONS_PENDING = 601; // Количество нерассмотренных петиций: $S1.
	public static final int THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER = 602; // Система петиций недоступна. Попробуйте воспользоваться ею позже.
	public static final int THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED = 603; // Нельзя выбросить или обменять данный предмет.
	public static final int YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION = 604; // Нельзя призвать питомца или других существ в этом месте.
	public static final int YOU_MAY_REGISTER_UP_TO_64_PEOPLE_ON_YOUR_LIST = 605; // В список можно внести до 64 человек.
	public static final int YOU_CANNOT_BE_REGISTERED_BECAUSE_THE_OTHER_PERSON_HAS_ALREADY_REGISTERED_64_PEOPLE_ON_HIS_HER_LIST = 606; // Список данного персонажа переполнен.
	public static final int YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1 = 607; // Вы не можете изучать умения. Возвращайтесь, когда достигнете $s1-го уровня.
	public static final int S1_HAS_OBTAINED_3_S2_S_BY_USING_SWEEPER = 608; // $c1 получает: $s2 ($s3 шт.) с помощью умения «Присвоить».
	public static final int S1_HAS_OBTAINED_S2_BY_USING_SWEEPER = 609; // $c1 получает: $s2 с помощью умения «Присвоить».
	public static final int YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP = 610; // Недостаточно HP. Умение отменено.
	public static final int YOU_HAVE_SUCCEEDED_IN_CONFUSING_THE_ENEMY = 611; // Вы успешно ввели противника в замешательство.
	public static final int THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED = 612; // Активировано умение «Spoil».
	public static final int _IGNORE_LIST_ = 613; // ======<IGNORE_LIST>======
	public static final int C1___C2 = 614; // $c1 : $c2
	public static final int S1 = 1983; // $s1
	public static final int YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST = 615; // Ошибка при добавлении в игнор-лист.
	public static final int YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST = 616; // Ошибка при удалении из игнор-листа.
	public static final int S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST = 617; // $s1 добавляется в игнор-лист.
	public static final int S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST = 618; // $s1 удаляется из игнор-листа.
	public static final int S1__HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST = 619; // $s1 добавляет Вас в игнор-лист.
	public static final int S1__HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST_1 = 620; // $s1 добавляет Вас в игнор-лист.
	public static final int THIS_SERVER_IS_RESERVED_FOR_PLAYERS_IN_KOREA__TO_USE_LINEAGE_II_GAME_SERVICES_PLEASE_CONNECT_TO_THE_SERVER_IN_YOUR_REGION = 621; // Вы заходите в игру с запрещенного IP.
	public static final int YOU_MAY_NOT_MAKE_A_DECLARATION_OF_WAR_DURING_AN_ALLIANCE_BATTLE = 622; // Нельзя послать сообщение об объявлении войны во время войны между кланами.
	public static final int YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED = 623; // Противник воюет с максимально допущенным количеством кланов.
	public static final int S1_CLAN_LEADER_IS_NOT_CURRENTLY_CONNECTED_TO_THE_GAME_SERVER = 624; // Лидер клана $s1 сейчас не в игре.
	public static final int YOUR_REQUEST_FOR_ALLIANCE_BATTLE_TRUCE_HAS_BEEN_DENIED = 625; // Поступил отказ от завершения войны между кланами.
	public static final int THE_S1_CLAN_DID_NOT_RESPOND__WAR_PROCLAMATION_HAS_BEEN_REFUSED = 626; // Клан $s1 не ответил на объявление войны.
	public static final int CLAN_BATTLE_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLANS_WAR_PROCLAMATION = 627; // Вы не ответили клану $s1 на сообщение об объявлении войны.
	public static final int YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN = 628; // Вы уже воевали с кланом $s1. Повторно объявить войну можно через 5 дней после окончания предыдущей.
	public static final int YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED_1 = 629; // Противник уже находится в состоянии войны с максимальным количеством кланов.
	public static final int WAR_WITH_THE_S1_CLAN_HAS_BEGUN_1 = 630; // Война с кланом $s1 началась.
	public static final int WAR_WITH_THE_S1_CLAN_IS_OVER = 631; // Война с кланом $s1 завершена.
	public static final int YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN_1 = 632; // Вы победили в войне с кланом $s1.
	public static final int YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN_1 = 633; // Вы проиграли в войне с кланом $s1.
	public static final int YOUR_ALLIANCE_LEADER_HAS_BEEN_SLAIN_YOU_HAVE_BEEN_DEFEATED_BY_THE_S1_CLAN = 634; // Лидер клана погиб. Вы проиграли клану $s1.
	public static final int THE_TIME_LIMIT_FOR_THE_CLAN_WAR_HAS_BEEN_EXCEEDED_WAR_WITH_THE_S1_CLAN_IS_OVER = 635; // Время войны кланов истекло. Война с кланом $s1 завершена.
	public static final int YOU_ARE_NOT_INVOLVED_IN_A_CLAN_WAR_1 = 636; // Вы не вовлечены в войну кланов.
	public static final int A_CLAN_ALLY_HAS_REGISTERED_ITSELF_TO_THE_OPPONENT = 637; // Клан, входящий в состав Вашего альянса, зарегистрировался Вашим оппонентом.
	public static final int YOU_HAVE_ALREADY_REQUESTED_A_SIEGE_BATTLE = 638; // Вы уже подали заявку на участие в осаде крепости.
	public static final int YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE = 639; // Вы уже подали заявку на участие в другой осаде крепости. Нельзя подать повторную заявку.
	public static final int YOU_HAVE_FAILED_TO_REFUSE_CASTLE_DEFENSE_AID = 640; // Вы не смогли отказаться от защиты крепости.
	public static final int YOU_HAVE_FAILED_TO_APPROVE_CASTLE_DEFENSE_AID = 641; // Вы не смогли подтвердить участие в защите крепости.
	public static final int YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST = 642; // Вы подали заявку на участие в атаке крепости. Необходимо отменить ее и повторить попытку.
	public static final int YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST = 643; // Вы подали заявку на участие в обороне крепости. Необходимо отменить ее и повторить попытку.
	public static final int YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE = 644; // Вы еще не подали заявку на участие в осаде крепости.
	public static final int ONLY_CLANS_WITH_LEVEL_4_AND_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE = 645; // Подать заявку на участие в осаде крепости могут только кланы, достигшие 5-го уровня.
	public static final int YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST = 646; // У Вас нет права вносить изменения в список защитников крепости.
	public static final int YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME = 647; // У Вас нет права выбрать время начала атаки на крепость.
	public static final int NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE = 648; // Заявки на участие в атаке крепости больше не принимаются.
	public static final int NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE = 649; // Заявки на участие в обороне крепости больше не принимаются.
	public static final int YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION = 650; // Нельзя призывать в данном месте.
	public static final int PLACE_S1_IN_THE_CURRENT_LOCATION_AND_DIRECTION_DO_YOU_WISH_TO_CONTINUE = 651; // Поместить предмет $s1 в данном месте?
	public static final int THE_TARGET_OF_THE_SUMMONED_MONSTER_IS_WRONG = 652; // Цель вызванного существа неверна.
	public static final int YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES = 653; // У Вас нет права устанавливать наемников.
	public static final int YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING = 654; // У Вас нет права изменить расположение наемников.
	public static final int MERCENARIES_CANNOT_BE_POSITIONED_HERE = 655; // Вы не можете установить наемника в этом месте.
	public static final int THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE = 656; // Нельзя установить этого наемника.
	public static final int POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT = 657; // Расстояние между наемниками слишком мало.
	public static final int THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING = 658; // Этот наемник не принадлежит Вашему замку, поэтому нельзя изменить его местоположение.
	public static final int THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED = 659; // Сейчас не время для регистрации на участие в осаде крепости, поэтому одобрить или отменить заявку невозможно.
	public static final int THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE = 660; // Сейчас не время для регистрации на участие в осаде крепости, поэтому подать или отменить заявку невозможно.
	public static final int IT_IS_A_CHARACTER_THAT_CANNOT_BE_SPOILED = 661; // Этот персонаж не может быть оценен.
	public static final int THE_OTHER_PLAYER_IS_REJECTING_FRIEND_INVITATIONS = 662; // Этот персонаж отказался быть Вашим другом.
	public static final int THE_SIEGE_TIME_HAS_BEEN_DECLARED_FOR_S_IT_IS_NOT_POSSIBLE_TO_CHANGE_THE_TIME_AFTER_A_SIEGE_TIME_HAS_BEEN_DECLARED_DO_YOU_WANT_TO_CONTINUE = 663; // Время начала осады: $s2. После этого внести какие-либо изменения будет невозможно. Продолжить?
	public static final int PLEASE_CHOOSE_A_PERSON_TO_RECEIVE = 664; // Выберите получателя.
	public static final int S2_OF_S1_ALLIANCE_IS_APPLYING_FOR_ALLIANCE_WAR_DO_YOU_WANT_TO_ACCEPT_THE_CHALLENGE = 665; // $s2, представитель альянса $s1, предлагает войну. Согласиться?
	public static final int A_REQUEST_FOR_CEASEFIRE_HAS_BEEN_RECEIVED_FROM_S1_ALLIANCE_DO_YOU_AGREE = 666; // Альянс $s1 просит о завершении войны. Вы согласны?
	public static final int YOU_ARE_REGISTERING_ON_THE_ATTACKING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE = 667; // Вас добавляют в список атакующих в осаде крепости $s1. Продолжить?
	public static final int YOU_ARE_REGISTERING_ON_THE_DEFENDING_SIDE_OF_THE_S1_SIEGE_DO_YOU_WANT_TO_CONTINUE = 668; // Вас добавляют в список защитников в осаде крепости $s1. Продолжить?
	public static final int YOU_ARE_CANCELING_YOUR_APPLICATION_TO_PARTICIPATE_IN_THE_S1_SIEGE_BATTLE_DO_YOU_WANT_TO_CONTINUE = 669; // Заявка на участие в осаде $s1 будет отменена. Продолжить?
	public static final int YOU_ARE_REFUSING_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT_TO_CONTINUE = 670; // Заявка на участие в защите крепости клана $s1 будет отклонена. Продолжить?
	public static final int YOU_ARE_AGREEING_TO_THE_REGISTRATION_OF_S1_CLAN_ON_THE_DEFENDING_SIDE_DO_YOU_WANT_TO_CONTINUE = 671; // Заявка на участие в защите крепости клана $s1 будет одобрена. Продолжить?
	public static final int S1_ADENA_DISAPPEARED = 672; // Исчезло: $s1 аден.
	public static final int YOU_ARE_MOVING_TO_ANOTHER_VILLAGE_DO_YOU_WANT_TO_CONTINUE = 682; // Перемещение в другую деревню. Продолжить?
	public static final int THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER = 683; // У Вас нет прав на получение этого предмета.
	public static final int YOU_CANNOT_POSITION_MERCENARIES_DURING_A_SIEGE = 684; // Во время осады крепости установить наемников невозможно.
	public static final int YOU_CANNOT_APPLY_FOR_CLAN_WAR_WITH_A_CLAN_THAT_BELONGS_TO_THE_SAME_ALLIANCE = 685; // Вы не можете объявить войну клану, входящему в Ваш альянс.
	public static final int YOU_HAVE_RECEIVED_S1_DAMAGE_FROM_THE_FIRE_OF_MAGIC = 686; // Вы получили $s1 урона от магии.
	public static final int YOU_CANNOT_MOVE_IN_A_FROZEN_STATE_PLEASE_WAIT_A_MOMENT = 687; // Вы не можете передвигаться в замерзшем состоянии. Подождите немного.
	public static final int THE_CLAN_THAT_OWNS_THE_CASTLE_IS_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE = 688; // Клан, владеющий замком, автоматически заносится в список защитников.
	public static final int A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE = 689; // Клан, владеющий замком, не может участвовать в других осадах.
	public static final int YOU_CANNOT_REGISTER_ON_THE_ATTACKING_SIDE_BECAUSE_YOU_ARE_PART_OF_AN_ALLIANCE_WITH_THE_CLAN_THAT_OWNS_THE_CASTLE = 690; // Так как Вы состоите в одном альянсе с кланом, владеющим крепостью, Вы не можете войти в состав осаждающих замок.
	public static final int S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE = 691; // Клан $s1 уже состоит в альянсе $s2.
	public static final int THE_OTHER_PARTY_IS_FROZEN_PLEASE_WAIT_A_MOMENT = 692; // Ваш соратник заморожен. Подождите немного.
	public static final int THE_PACKAGE_THAT_ARRIVED_IS_IN_ANOTHER_WAREHOUSE = 693; // Прибывшая посылка находится в другом хранилище.
	public static final int NO_PACKAGES_HAVE_ARRIVED = 694; // Посылок не было.
	public static final int YOU_CANNOT_SET_THE_NAME_OF_THE_PET = 695; // Вы не можете дать имя питомцу.
	public static final int YOUR_ACCOUNT_IS_RESTRICTED_FOR_NOT_PAYING_YOUR_PC_ROOM_USAGE_FEES = 696; // Ваш аккаунт был заблокирован за неуплату.
	public static final int THE_ITEM_ENCHANT_VALUE_IS_STRANGE = 697; // Число улучшений предмета неверно.
	public static final int THE_PRICE_IS_DIFFERENT_THAN_THE_SAME_ITEM_ON_THE_SALES_LIST = 698; // Цена предмета отличается от такого же в списке продаж.
	public static final int CURRENTLY_NOT_PURCHASING = 699; // В данный момент покупка не производится.
	public static final int THE_PURCHASE_IS_COMPLETE = 700; // Покупка завершена.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS = 701; // Недостаточно необходимых предметов.
	public static final int THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY = 702; // Сейчас в игре нет Игрового мастера.
	public static final int _GM_LIST_ = 703; // ======<GM_LIST>======
	public static final int GM_S1 = 704; // Игровой мастер: $c1
	public static final int YOU_CANNOT_EXCLUDE_YOURSELF = 705; // Вы не можете заблокировать себя.
	public static final int YOU_CAN_ONLY_REGISTER_UP_TO_64_NAMES_ON_YOUR_EXCLUDE_LIST = 706; // В список заблокированных пользователей можно добавить максимум 64 человека.
	public static final int YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE = 707; // Нельзя переместиться в осаждаемую деревню.
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CASTLE_WAREHOUSE = 708; // У Вас нет права пользоваться хранилищем замка.
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE = 709; // У Вас нет права пользоваться хранилищем клана.
	public static final int ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE = 710; // Хранилищем клана может пользоваться только клан, уровень которого превышает 1.
	public static final int THE_SIEGE_OF_S1_HAS_STARTED = 711; // $s1: осада началась.
	public static final int THE_SIEGE_OF_S1_HAS_FINISHED = 712; // $s1: осада завершена.
	public static final int S1_S2_S3_S4S5 = 713; // $s1/$s2/$s3 $s4:$s5
	public static final int A_TRAP_DEVICE_HAS_TRIPPED = 714; // Ловушка активирована.
	public static final int THE_TRAP_DEVICE_HAS_STOPPED = 715; // Действие ловушки было приостановлено.
	public static final int IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE = 716; // Воскрешение невозможно, если отсутствует база.
	public static final int THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE = 717; // Воскрешение невозможно, так как защитная башня была разрушена.
	public static final int THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE = 718; // Во время осады замка открыть или закрыть врата нельзя.
	public static final int YOU_FAILED_AT_ITEM_MIXING = 719; // У Вас не получилось усилить предмет.
	public static final int THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE = 720; // Цена покупки превышает Ваш капитал, поэтому начать торговлю невозможно.
	public static final int YOU_CANNOT_CREATE_AN_ALLIANCE_WHILE_PARTICIPATING_IN_A_SIEGE = 721; // Во время осады замка создать альянс нельзя.
	public static final int YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE = 722; // Один из кланов, входящих в альянс, участвует в осаде замка, поэтому распустить альянс невозможно.
	public static final int THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE = 723; // Данный клан участвует в осаде замка.
	public static final int YOU_CANNOT_LEAVE_WHILE_PARTICIPATING_IN_A_SIEGE_BATTLE = 724; // Нельзя уйти во время осады замка.
	public static final int YOU_CANNOT_BANISH_A_CLAN_FROM_AN_ALLIANCE_WHILE_THE_CLAN_IS_PARTICIPATING_IN_A_SIEGE = 725; // Нельзя изгнать из альянса клан, участвующий в осаде замка.
	public static final int THE_FROZEN_CONDITION_HAS_STARTED_PLEASE_WAIT_A_MOMENT = 726; // Состояние заморозки. Подождите немного.
	public static final int THE_FROZEN_CONDITION_WAS_REMOVED = 727; // Замороженное состояние закончилось.
	public static final int YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION = 728; // Нельзя подать заявку на роспуск в течение 7 дней после предыдущей.
	public static final int THAT_ITEM_CANNOT_BE_DISCARDED = 729; // Нельзя выбросить данный предмет.
	public static final int YOU_HAVE_SUBMITTED_S1_PETITIONS_YOU_MAY_SUBMIT_S2_MORE_PETITIONS_TODAY = 730; // Количество отправленных петиций: $s1. \\n - Количество доступных сегодня петиций: $s2.
	public static final int A_PETITION_HAS_BEEN_RECEIVED_BY_THE_GM_ON_BEHALF_OF_S1_IT_IS_PETITION_S2 = 731; // Получен ответ от службы поддержки на имя $c1. Петиция $s2.
	public static final int S1_HAS_RECEIVED_A_REQUEST_FOR_A_CONSULTATION_WITH_THE_GM = 732; // Игровой мастер просит о приватной беседе $c1.
	public static final int WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS = 733; // Сегодня Вы отправили максимальное количество петиций. Больше петиций отправить нельзя.
	public static final int YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_SOMEONE_ELSE_S1_ALREADY_SUBMITTED_A_PETITION = 734; // Не удалось подать петицию за кого-то другого. $c1 уже отправил петицию.
	public static final int YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_IS_S2 = 735; // Не удалось подать петицию за игрока $c1. Номер ошибки: $s2.
	public static final int THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITIONS_TODAY = 736; // Заявка отозвана. Вы можете обратиться сегодня еще $s1 раз.
	public static final int YOU_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1 = 737; // Вы отменили подачу петиции за игрока $c1.
	public static final int YOU_HAVE_NOT_SUBMITTED_A_PETITION = 738; // Вы не подали петицию.
	public static final int YOU_FAILED_AT_CANCELING_A_PETITION_ON_BEHALF_OF_S1_THE_ERROR_CODE_IS_S2 = 739; // Не удалось отменить подачу петиции за игрока $c1. Код ошибки: $s2.
	public static final int S1_PARTICIPATED_IN_A_PETITION_CHAT_AT_THE_REQUEST_OF_THE_GM = 740; // $c1 присоединился к чату петиций по просьбе службы поддержки.
	public static final int YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_A_PETITION_HAS_ALREADY_BEEN_SUBMITTED = 741; // Не удалось добавить игрока $c1 в чат петиций. Петиция уже подана
	public static final int YOU_FAILED_AT_ADDING_S1_TO_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2 = 742; // Не удалось добавить игрока $c1 в чат петиций. Код ошибки: $s2.
	public static final int S1_LEFT_THE_PETITION_CHAT = 743; // $c1 выходит из чата петиций.
	public static final int YOU_FAILED_AT_REMOVING_S1_FROM_THE_PETITION_CHAT_THE_ERROR_CODE_IS_S2 = 744; // Не удалось удалить игрока $s1 из чата петиций. Код ошибки: $s2.
	public static final int YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT = 745; // Вы не находитесь в чате петиций.
	public static final int IT_IS_NOT_CURRENTLY_A_PETITION = 746; // Это не петиция.
	public static final int IF_YOU_NEED_HELP_PLEASE_USE_11_INQUIRY_ON_THE_OFFICIAL_WEB_SITE = 747; // Если Вам необходима помощь, обратитесь в службу поддержки на нашем сайте.
	public static final int THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED = 748; // Дистанция слишком велика, поэтому заклинание отменено.
	public static final int THE_EFFECT_OF_S1_HAS_BEEN_REMOVED = 749; // $s1: эффект отменен.
	public static final int THERE_ARE_NO_OTHER_SKILLS_TO_LEARN = 750; // Сейчас Вы не можете изучать новые умения.
	public static final int AS_THERE_IS_A_CONFLICT_IN_THE_SIEGE_RELATIONSHIP_WITH_A_CLAN_IN_THE_ALLIANCE_YOU_CANNOT_INVITE_THAT_CLAN_TO_THE_ALLIANCE = 751; // Клан участвует в осаде замка на вражеской стороне. Нельзя пригласить в состав альянса.
	public static final int THAT_NAME_CANNOT_BE_USED = 752; // Данное имя использовать нельзя.
	public static final int YOU_CANNOT_POSITION_MERCENARIES_HERE = 753; // Установить наемника в этом месте нельзя.
	public static final int THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THIS_WEEKS_USAGE_TIME = 754; // Осталось $s1 ч $s2 мин на этой неделе.
	public static final int THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEKS_USAGE_TIME = 755; // Осталось $s2 мин на этой неделе.
	public static final int THIS_WEEKS_USAGE_TIME_HAS_FINISHED = 756; // Время на этой неделе закончилось.
	public static final int THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THE_FIXED_USE_TIME = 757; // Осталось: $s1 ч $s2 мин.
	public static final int THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEKS_PLAY_TIME = 758; // Вы можете играть $s1 ч $s2 мин до конца этой недели.
	public static final int THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEKS_PLAY_TIME_1 = 759; // Вы можете играть $s2 мин до конца этой недели.
	public static final int S1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_HE_SHE_LEFT_ANOTHER_CLAN = 760; // $c1 не может вступить в клан. Не прошло 24 ч с момента выхода из предыдущего.
	public static final int S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_IT_LEFT_ANOTHER_ALLIANCE = 761; // Клан $s1 вышел из альянса менее 24 ч назад и не может войти в состав альянса вновь.
	public static final int S1_ROLLED_S2_AND_S3S_EYE_CAME_OUT = 762; // $c1 бросает $s2. Выпадает: $s3.
	public static final int YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE = 763; // Расстояние до хранилища слишком велико. Отправить посылку невозможно.
	public static final int YOU_HAVE_BEEN_PLAYING_FOR_AN_EXTENDED_PERIOD_OF_TIME_PLEASE_CONSIDER_TAKING_A_BREAK = 764; // Вы играете уже довольно долго. Советуем Вам отдохнуть.
	public static final int GAMEGUARD_IS_ALREADY_RUNNING_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOOTING = 765; // Активирована программа GameGuard. Перезагрузитесь и повторите попытку.
	public static final int THERE_IS_A_GAMEGUARD_INITIALIZATION_ERROR_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_REBOOTING = 766; // Ошибка обновления программы GameGuard. Перезагрузите компьютер и повторите попытку.
	public static final int THE_GAMEGUARD_FILE_IS_DAMAGED__PLEASE_REINSTALL_GAMEGUARD = 767; // Поврежден файл программы GameGuard. Переустановите программу защиты.
	public static final int A_WINDOWS_SYSTEM_FILE_IS_DAMAGED_PLEASE_REINSTALL_INTERNET_EXPLORER = 768; // Поврежден системный файл Windows. Переустановите Internet Explorer.
	public static final int A_HACKING_TOOL_HAS_BEEN_DISCOVERED_PLEASE_TRY_PLAYING_AGAIN_AFTER_CLOSING_UNNECESSARY_PROGRAMS = 769; // Обнаружен взлом. Выключите ненужные программы и повторите попытку.
	public static final int THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_CHECK_YOUR_NETWORK_CONNECTION_STATUS_OR_FIREWALL = 770; // Обновление программы GameGuard отменено. Проверьте настройки сети и Firewall.
	public static final int THE_GAMEGUARD_UPDATE_WAS_CANCELED_PLEASE_TRY_RUNNING_IT_AGAIN_AFTER_DOING_A_VIRUS_SCAN_OR_CHANGING_THE_SETTINGS_IN_YOUR_PC_MANAGEMENT_PROGRAM = 771; // Обновление программы GameGuard отменено. Проверьте компьютер на наличие вируса или измените настройки управления программами и повторите попытку.
	public static final int THERE_WAS_A_PROBLEM_WHEN_RUNNING_GAMEGUARD = 772; // Обнаружена ошибка во время подключения GameGuard.
	public static final int THE_GAME_OR_GAMEGUARD_FILES_ARE_DAMAGED = 773; // Поврежден файл игры или GameGuard.
	public static final int SINCE_THIS_IS_A_PEACE_ZONE_PLAY_TIME_DOES_NOT_GET_EXPENDED_HERE = 774; // Время игры более не расходуется.
	public static final int FROM_HERE_ON_PLAY_TIME_WILL_BE_EXPENDED = 775; // С этого момента время игры будет расходоваться.
	public static final int YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION = 778; // Здесь нельзя выйти из игры.
	public static final int YOU_MAY_NOT_RESTART_IN_THIS_LOCATION = 779; // Здесь нельзя перезапустить игру.
	public static final int OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE = 780; // Режим зрителя возможен только во время осады.
	public static final int OBSERVERS_CANNOT_PARTICIPATE = 781; // Эта функция недоступна в режиме зрителя.
	public static final int YOU_MAY_NOT_OBSERVE_A_SUMMONED_CREATURE = 782; // Если Вы призвали питомца или другое существо, войти в режим зрителя невозможно.
	public static final int LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED = 783; // В данный момент продажа лотерейных билетов приостановлена.
	public static final int TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE = 784; // Продажа лотерейных билетов завершена.
	public static final int THE_RESULTS_OF_LOTTERY_NUMBER_S1_HAVE_NOT_YET_BEEN_PUBLISHED = 785; // Еще не получены результаты лотереи №$s1.
	public static final int INCORRECT_SYNTAX = 786; // Использовано неверное слово.
	public static final int THE_TRYOUTS_ARE_FINISHED = 787; // Отборочный тур завершен.
	public static final int THE_FINALS_ARE_FINISHED = 788; // Финал завершен.
	public static final int THE_TRYOUTS_HAVE_BEGUN = 789; // Отборочный тур начался.
	public static final int THE_FINALS_HAVE_BEGUN = 790; // Финал начался.
	public static final int THE_FINAL_MATCH_IS_ABOUT_TO_BEGIN_LINE_UP = 791; // Финал скоро начнется.
	public static final int THE_SIEGE_OF_THE_CLAN_HALL_IS_FINISHED = 792; // Битва за холл клана завершена.
	public static final int THE_SIEGE_OF_THE_CLAN_HALL_HAS_BEGUN = 793; // Битва за холл клана началась.
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT = 794; // У Вас нет прав, чтобы сделать это.
	public static final int ONLY_CLAN_LEADERS_ARE_AUTHORIZED_TO_SET_RIGHTS = 795; // Права распределяются лидером клана.
	public static final int YOUR_REMAINING_OBSERVATION_TIME_IS_S1_MINUTES = 796; // Осталось в режиме зрителя: $s1 мин.
	public static final int YOU_MAY_CREATE_UP_TO_48_MACROS = 797; // Можно создать 48 макросов.
	public static final int ITEM_REGISTRATION_IS_IRREVERSIBLE_DO_YOU_WISH_TO_CONTINUE = 798; // Регистрацию предмета нельзя отменить. Продолжить?
	public static final int THE_OBSERVATION_TIME_HAS_EXPIRED = 799; // Время режима зрителя истекло.
	public static final int YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER = 800; // Время подачи заявки на участие в битве за холл клана истекло, регистрация невозможна.
	public static final int REGISTRATION_FOR_THE_CLAN_HALL_SIEGE_IS_CLOSED = 801; // Заявки на участие в битве за холл клана больше не принимаются.
	public static final int PETITIONS_ARE_NOT_BEING_ACCEPTED_AT_THIS_TIME_YOU_MAY_SUBMIT_YOUR_PETITION_AFTER_S1_AM_PM = 802; // Невозможно открыть еще одно окно зрителя. Закройте уже открытое окно и повторите попытку.
	public static final int ENTER_THE_SPECIFICS_OF_YOUR_PETITION = 803; // Введите содержание петиции.
	public static final int SELECT_A_TYPE = 804; // Выберите тип.
	public static final int PETITIONS_ARE_NOT_BEING_ACCEPTED_AT_THIS_TIME_YOU_MAY_SUBMIT_YOUR_PETITION_AFTER_S1_AM_PM_1 = 805; // На данный момент петиция еще не была принята. Попробуйте повторить попытку через $s1 ч.
	public static final int IF_YOU_ARE_TRAPPED_TRY_TYPING__UNSTUCK = 806; // Если вы застрянете в текстурах, введите в строку ввода команду "/unstuck"
	public static final int THIS_TERRAIN_IS_UNNAVIGABLE_PREPARE_FOR_TRANSPORT_TO_THE_NEAREST_VILLAGE = 807; // Вы находитесь в зоне, где перемещение невозможно, мы переместим Вас в ближайший город.
	public static final int YOU_ARE_STUCK_YOU_MAY_SUBMIT_A_PETITION_BY_TYPING__GM = 808; // Вы застряли. Вам необходимо отправить петицию с помощью команды "/gm".
	public static final int YOU_ARE_STUCK_YOU_WILL_BE_TRANSPORTED_TO_THE_NEAREST_VILLAGE_IN_FIVE_MINUTES = 809; // Вы застряли. Через 5 мин Вы будете перемещены в ближайший город.
	public static final int INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS = 810; // Неверный макрос. Обратитесь к руководству по макросам.
	public static final int YOU_WILL_BE_MOVED_TO_S1_DO_YOU_WISH_TO_CONTINUE = 811; // Вы перемещаетесь в локацию ($s1). Продолжить?
	public static final int THE_SECRET_TRAP_HAS_INFLICTED_S1_DAMAGE_ON_YOU = 812; // Вы попали в ловушку и получили $s1 урона.
	public static final int YOU_HAVE_BEEN_POISONED_BY_A_SECRET_TRAP = 813; // Вы попались в ловушку и были отравлены.
	public static final int YOUR_SPEED_HAS_BEEN_DECREASED_BY_A_SECRET_TRAP = 814; // Вы попались в ловушку, и Ваша скорость была снижена.
	public static final int THE_TRYOUTS_ARE_ABOUT_TO_BEGIN_LINE_UP = 815; // Отборочный тур сейчас начнется. Приготовьтесь.
	public static final int TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE = 816; // Скоро начнется гонка монстров $s1. Купите билеты.
	public static final int WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE = 817; // Продаются билеты на участие в гонке монстров $s1.
	public static final int TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S = 818; // Продажа билетов на участие в гонке монстров $s1 завершена.
	public static final int TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED = 819; // Продажа билетов на участие в гонке монстров $s1 завершена. Вы можете просмотреть сумму выигрыша.
	public static final int THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES = 820; // Через $s1 мин начнется гонка монстров $s2.
	public static final int THE_S1TH_MONSTER_RACE_WILL_BEGIN_IN_30_SECONDS = 821; // Через 30 сек начнется гонка монстров $s2.
	public static final int THE_S1TH_MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS = 822; // Гонка монстров $s1 скоро начнется. Через 5 сек начнется отсчет.
	public static final int THE_RACE_WILL_BEGIN_IN_S1_SECONDS = 823; // До начала $s1 сек!
	public static final int THEYRE_OFF = 824; // Старт! Гонка началась!
	public static final int MONSTER_RACE_S1_IS_FINISHED = 825; // Гонка монстров $s1 завершена.
	public static final int FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2 = 826; // Победил монстр на дорожке $s1! II место - за дорожкой $s2.
	public static final int YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM = 827; // Нельзя заблокировать GM.
	public static final int ARE_YOU_SURE_YOU_WISH_TO_DELETE_THE_S1_MACRO = 828; // Макрос $s1 будет удален. Продолжить?
	public static final int S1_HAS_ROLLED_S2 = 834; // На брошенных персонажем $c1 кубиках выпало $s2.
	public static final int YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIMETRY_AGAIN_LATER = 835; // Сейчас бросить кубики нельзя. Попробуйте позже.
	public static final int THE_INVENTORY_IS_FULL_NO_FURTHER_QUEST_ITEMS_MAY_BE_DEPOSITED_AT_THIS_TIME = 836; // Вы не можете взять этот предмет, так как Ваш инвентарь переполнен.
	public static final int MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS = 837; // Описание макроса не должно превышать 32 символа.
	public static final int ENTER_THE_NAME_OF_THE_MACRO = 838; // Введите имя макроса.
	public static final int THAT_NAME_IS_ALREADY_ASSIGNED_TO_ANOTHER_MACRO = 839; // Макрос с таким именем уже существует.
	public static final int THAT_RECIPE_IS_ALREADY_REGISTERED = 840; // Такой рецепт уже существует.
	public static final int NO_FURTHER_RECIPES_MAY_BE_REGISTERED = 841; // Зарегистрировать рецепт больше нельзя.
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE = 842; // Уровень умения создания предмета слишком низок, чтобы зарегистрировать Ваш рецепт.
	public static final int THE_SIEGE_OF_S1_IS_FINISHED = 843; // Осада $s1 завершена.
	public static final int THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN = 844; // Осада $s1 началась.
	public static final int THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED = 845; // Время регистрации на участие в осаде $s1 истекло.
	public static final int THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST = 846; // Желающих участвовать нет, осада $s1 отменена.
	public static final int A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE = 847; // Клан, обладающий холлом, не может участвовать в битве за холл клана.
	public static final int S1_HAS_BEEN_DELETED = 848; // $s1 удален.
	public static final int S1_CANNOT_BE_FOUND = 849; // $s1 не найден.
	public static final int S1_ALREADY_EXISTS_1 = 850; // $s1 уже существует.
	public static final int S1_HAS_BEEN_ADDED = 851; // $s1 добавлен.
	public static final int THE_RECIPE_IS_INCORRECT = 852; // Содержание рецепта неверно.
	public static final int YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING = 853; // В режиме личной мастерской управлять книгой рецептов невозможно.
	public static final int YOU_LACK_S2_OF_S1 = 854; // Не хватает предмета $s1 ($s2 шт.)
	public static final int S1_CLAN_HAS_DEFEATED_S2 = 855; // Клан $s1 победил в битве за холл клана $s2.
	public static final int THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW = 856; // Битва за холл клана $s1 закончилась в ничью.
	public static final int S1_CLAN_HAS_WON_IN_THE_PRELIMINARY_MATCH_OF_S2 = 857; // Клан $s1 победил в отборочном туре $s2.
	public static final int THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW = 858; // Отборочный тур $s1 закончился в ничью.
	public static final int PLEASE_REGISTER_A_RECIPE = 859; // Зарегистрируйте рецепт.
	public static final int YOU_MAY_NOT_BUILD_YOUR_HEADQUARTERS_IN_CLOSE_PROXIMITY_TO_ANOTHER_HEADQUARTERS = 860; // Вы не можете установить штаб в этом месте, дистанция до другого штаба слишком мала.
	public static final int YOU_HAVE_EXCEEDED_THE_MAXIMUM_NUMBER_OF_MEMOS = 861; // Вы превысили максимальное количество памяток.
	public static final int ODDS_ARE_NOT_POSTED_UNTIL_TICKET_SALES_HAVE_CLOSED = 862; // Вы не можете просмотреть сумму приза, так как продажа билетов еще не завершена.
	public static final int YOU_FEEL_THE_ENERGY_OF_FIRE = 863; // Вы чувствуете силу Огня.
	public static final int YOU_FEEL_THE_ENERGY_OF_WATER = 864; // Вы чувствуете силу Воды.
	public static final int YOU_FEEL_THE_ENERGY_OF_WIND = 865; // Вы чувствуете силу Ветра.
	public static final int YOU_MAY_NO_LONGER_GATHER_ENERGY = 866; // Вы больше не можете получать силу.
	public static final int THE_ENERGY_IS_DEPLETED = 867; // Сила пропала.
	public static final int THE_ENERGY_OF_FIRE_HAS_BEEN_DELIVERED = 868; // Была получена сила Огня.
	public static final int THE_ENERGY_OF_WATER_HAS_BEEN_DELIVERED = 869; // Была получена сила Воды.
	public static final int THE_ENERGY_OF_WIND_HAS_BEEN_DELIVERED = 870; // Была получена сила Ветра.
	public static final int THE_SEED_HAS_BEEN_SOWN = 871; // Семя было посеяно.
	public static final int THIS_SEED_MAY_NOT_BE_SOWN_HERE = 872; // Это семя нельзя посеять здесь.
	public static final int THAT_CHARACTER_DOES_NOT_EXIST = 873; // Такого персонажа не существует.
	public static final int THE_CAPACITY_OF_THE_WAREHOUSE_HAS_BEEN_EXCEEDED = 874; // Хранилище выбранного персонажа переполнено.
	public static final int TRANSPORT_OF_CARGO_HAS_BEEN_CANCELED = 875; // Посылка отменена.
	public static final int CARGO_WAS_NOT_DELIVERED = 876; // Ошибка при отправке посылки.
	public static final int THE_SYMBOL_HAS_BEEN_ADDED = 877; // Символ добавлен.
	public static final int THE_SYMBOL_HAS_BEEN_DELETED = 878; // Символ удален.
	public static final int THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE = 879; // Сейчас проходит профилактика системы владений.
	public static final int THE_TRANSACTION_IS_COMPLETE = 880; // Передача завершена.
	public static final int THERE_IS_A_DISCREPANCY_ON_THE_INVOICE = 881; // Неверная информация о переданном предмете.
	public static final int SEED_QUANTITY_IS_INCORRECT = 882; // Неверное количество семян.
	public static final int SEED_INFORMATION_IS_INCORRECT = 883; // Неверная информация о семени.
	public static final int THE_MANOR_INFORMATION_HAS_BEEN_UPDATED = 884; // Информация о владении обновлена.
	public static final int THE_NUMBER_OF_CROPS_IS_INCORRECT = 885; // Количество урожая неверно.
	public static final int THE_CROPS_ARE_PRICED_INCORRECTLY = 886; // Неверная информация о цене урожая.
	public static final int THE_TYPE_IS_INCORRECT = 887; // Неверный тип.
	public static final int NO_CROPS_CAN_BE_PURCHASED_AT_THIS_TIME = 888; // Вы не можете приобрести урожай.
	public static final int THE_SEED_WAS_SUCCESSFULLY_SOWN = 889; // Вы успешно провели посев.
	public static final int THE_SEED_WAS_NOT_SOWN = 890; // Вы не смогли провести посев.
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_HARVEST = 891; // Вы не можете собрать урожай.
	public static final int THE_HARVEST_HAS_FAILED = 892; // Вы не смогли собрать урожай.
	public static final int THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN = 893; // Сбор урожая не мог быть проведен, так как не было посева.
	public static final int UP_TO_S1_RECIPES_CAN_BE_REGISTERED = 894; // Можно зарегистрировать рецептов: $s1.
	public static final int NO_RECIPES_HAVE_BEEN_REGISTERED = 895; // Зарегистрированных рецептов нет.
	public static final int QUEST_RECIPES_CAN_NOT_BE_REGISTERED = 896; // Вы не можете зарегистрировать квестовый рецепт.
	public static final int THE_FEE_TO_CREATE_THE_ITEM_IS_INCORRECT = 897; // Неверная комиссия за изготовление предмета.
	public static final int THE_SYMBOL_CANNOT_BE_DRAWN = 899; // Вы не можете нарисовать символ.
	public static final int NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL = 900; // У Вас нет ячейки для рисования символов.
	public static final int THE_SYMBOL_INFORMATION_CANNOT_BE_FOUND = 901; // Невозможно найти информацию о символах.
	public static final int THE_NUMBER_OF_ITEMS_IS_INCORRECT = 902; // Количество предметов неверно.
	public static final int YOU_MAY_NOT_SUBMIT_A_PETITION_WHILE_FROZEN_BE_PATIENT = 903; // Вы не можете послать петицию в замороженном состоянии. Подождите немного.
	public static final int ITEMS_CANNOT_BE_DISCARDED_WHILE_IN_PRIVATE_STORE_STATUS = 904; // В режиме личной торговой лавки выбросить предмет нельзя.
	public static final int THE_CURRENT_SCORE_FOR_THE_HUMAN_RACE_IS_S1 = 905; // Результат Людей на данный момент - $s1.
	public static final int THE_CURRENT_SCORE_FOR_THE_ELVEN_RACE_IS_S1 = 906; // Результат Эльфов на данный момент - $s1.
	public static final int THE_CURRENT_SCORE_FOR_THE_DARK_ELVEN_RACE_IS_S1 = 907; // Результат Темных Эльфов на данный момент - $s1.
	public static final int THE_CURRENT_SCORE_FOR_THE_ORC_RACE_IS_S1 = 908; // Результат Орков на данный момент - $s1.
	public static final int THE_CURRENT_SCORE_FOR_THE_DWARVEN_RACE_IS_S1 = 909; // Результат Гномов на данный момент - $s1.
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_TALKING_ISLAND_VILLAGE = 910; // Текущая локация: $s1, $s2, $s3 (возле Деревни Говорящего Острова)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_GLUDIN_VILLAGE = 911; // Текущая локация: $s1, $s2, $s3 (возле Глудина)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_GLUDIO_CASTLE_TOWN = 912; // Текущая локация: $s1, $s2, $s3 (возле Глудио)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_THE_NEUTRAL_ZONE = 913; // Текущая локация:: $s1, $s2, $s3 (возле Нейтральной Зоны)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_ELVEN_VILLAGE = 914; // Текущая локация: $s1, $s2, $s3 (возле Деревни Эльфов)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_DARK_ELVEN_VILLAGE = 915; // Текущая локация: $s1, $s2, $s3 (возле Деревни Темных Эльфов)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_DION_CASTLE_TOWN = 916; // Текущая локация: $s1, $s2, $s3 (возле Диона)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_FLORAN_VILLAGE = 917; // Текущая локация: $s1, $s2, $s3 (возле Флорана)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_GIRAN_CASTLE_TOWN = 918; // Текущая локация: $s1, $s2, $s3 (возле Гирана)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_GIRAN_HARBOR = 919; // Текущая локация: $s1, $s2, $s3 (возле Гавани Гирана)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_ORC_VILLAGE = 920; // Текущая локация: $s1, $s2, $s3 (возле Деревни Орков)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_DWARVEN_VILLAGE = 921; // Текущая локация: $s1, $s2, $s3 (возле Деревни Гномов)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_OREN = 922; // Текущая локация: $s1, $s2, $s3 (возле Орена)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_HUNTERS_VILLAGE = 923; // Текущая локация: $s1, $s2, $s3 (возле Деревни Охотников)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_ADEN_CASTLE_TOWN = 924; // Текущая локация: $s1, $s2, $s3 (возле Адена)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_THE_COLISEUM = 925; // Текущая локация: $s1, $s2, $s3 (возле Колизея)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_HEINE = 926; // Текущая локация: $s1, $s2, $s3 (возле Хейна)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_SCHUTTGART = 1714; // Текущее местоположение: $s1, $s2, $s3 (окрестности Шутгарта)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_KAMAEL_VILLAGE = 2189; // Текущая локация: $s1, $s2, $s3 (Возле Деревни Камаэль)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_PRIMEVAL_ISLE = 1924; // Текущая локация: $s1, $s2, $s3 (возле Первобытного Острова).
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_SOUTH_OF_WASTELANDS_CAP = 2190; // Текущая локация: $s1, $s2, $s3 (Возле Лагеря Пустоши)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_FANTASY_ISLE = 2259; // Текущая локация: $s1, $s2, $s3 (возле Острова Грез)
	public static final int THE_CURRENT_TIME_IS_S1S2_IN_THE_DAY = 927; // Текущее время: $s1 ч $s2 мин дня.
	public static final int THE_CURRENT_TIME_IS_S1S2_IN_THE_NIGHT = 928; // Текущее время: $s1 ч $s2 мин ночи.
	public static final int NO_COMPENSATION_WAS_GIVEN_FOR_THE_FARM_PRODUCTS = 929; // Подарка за урожай не будет.
	public static final int LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD = 930; // В данный момент лотерейные билеты не продаются.
	public static final int THE_WINNING_LOTTERY_TICKET_NUMBER_HAS_NOT_YET_BEEN_ANNOUNCED = 931; // Выигрышный лотерейный билет еще не объявлен.
	public static final int YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING = 932; // В режиме зрителя чатом пользоваться нельзя.
	public static final int THE_SEED_PRICING_GREATLY_DIFFERS_FROM_STANDARD_SEED_PRICES = 933; // Неверная информация о цене семян.
	public static final int IT_IS_A_DELETED_RECIPE = 934; // Удаленный рецепт.
	public static final int THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION = 935; // У Вас не хватает денег для активации владения.
	public static final int USE_S1 = 936; // Используется: $s1.
	public static final int CURRENTLY_PREPARING_FOR_PRIVATE_WORKSHOP = 937; // Идет подготовка личного рабочего места.
	public static final int THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE = 938; // Сервер общения находится на профилактике.
	public static final int YOU_CANNOT_EXCHANGE_WHILE_BLOCKING_EVERYTHING = 939; // Вы не можете произвести обмен в режиме полной блокировки.
	public static final int S1_IS_BLOCKING_EVERYTHING = 940; // $s1 находится в режиме полной блокировки.
	public static final int RESTART_AT_TALKING_ISLAND_VILLAGE = 941; // Возвращение в Деревню Говорящего Острова
	public static final int RESTART_AT_GLUDIN_VILLAGE = 942; // Возвращение в Глудин
	public static final int RESTART_AT_GLUDIN_CASTLE_TOWN = 943; // Возвращение в Глудио
	public static final int RESTART_AT_THE_NEUTRAL_ZONE = 944; // Возвращение в Нейтральную Зону
	public static final int RESTART_AT_ELVEN_VILLAGE = 945; // Возвращение в Эльфийскую Деревню
	public static final int RESTART_AT_DARK_ELVEN_VILLAGE = 946; // Возвращение в Деревню Темных Эльфов
	public static final int RESTART_AT_DION_CASTLE_TOWN = 947; // Возвращение в Дион
	public static final int RESTART_AT_FLORAN_VILLAGE = 948; // Возвращение во Флоран
	public static final int RESTART_AT_GIRAN_CASTLE_TOWN = 949; // Возвращение в Гиран
	public static final int RESTART_AT_GIRAN_HARBOR = 950; // Возвращение в Гавань Гирана
	public static final int RESTART_AT_ORC_VILLAGE = 951; // Возвращение в Деревню Орков
	public static final int RESTART_AT_DWARVEN_VILLAGE = 952; // Возвращение в Деревню Гномов
	public static final int RESTART_AT_THE_TOWN_OF_OREN = 953; // Возвращение в Орен
	public static final int RESTART_AT_HUNTERS_VILLAGE = 954; // Возвращение в Деревню Охотников
	public static final int RESTART_AT_ADEN_CASTLE_TOWN = 955; // Возвращение в Аден
	public static final int RESTART_AT_THE_COLISEUM = 956; // Возвращение в Колизей
	public static final int RESTART_AT_HEINE = 957; // Возвращение в Хейн
	public static final int ITEMS_CANNOT_BE_DISCARDED_OR_DESTROYED_WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP = 958; // Предмет нельзя выбросить или уничтожить в режиме личной торговой лавки или мастерской.
	public static final int S1_S2_MANUFACTURING_SUCCESS = 959; // $s1 (*$s2): изготовлено удачно.
	public static final int S1_MANUFACTURING_FAILURE = 960; // $s1: неудача.
	public static final int YOU_ARE_NOW_BLOCKING_EVERYTHING = 961; // Вы находитесь в режиме полной блокировки.
	public static final int YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING = 962; // Вы вышли из режима полной блокировки.
	public static final int PLEASE_DETERMINE_THE_MANUFACTURING_PRICE = 963; // Выберите цену изготовления.
	public static final int CHATTING_IS_PROHIBITED_FOR_ABOUT_ONE_MINUTE = 964; // Чат заблокирован на 1 мин.
	public static final int THE_CHATTING_PROHIBITION_HAS_BEEN_REMOVED = 965; // Блокировка чата снята.
	public static final int CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_BECOME_EVEN_LONGER = 966; // Блокировка чата. В случае попытки использования чата время блокировки будет продлено.
	public static final int DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_RANDOM_INCLUDING_SPOIL = 967; // Вы согласны вступить в группу с $c1? (распределение предметов: Случайно+Присвоить)
	public static final int DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_BY_TURN = 968; // Вы согласны вступить в группу с $c1? (распределение предметов: По очереди)
	public static final int DO_YOU_ACCEPT_THE_PARTY_INVITATION_FROM_S1_ITEM_DISTRIBUTION_BY_TURN_INCLUDING_SPOIL = 969; // Вы согласны вступить в группу с $c1? (распределение предметов: По очереди+Присвоить)
	public static final int S2S_MP_HAS_BEEN_DRAINED_BY_S1 = 970; // $s2 MP было поглощено персонажем $c1.
	public static final int PETITIONS_CANNOT_EXCEED_255_CHARACTERS = 971; // Обращение не должно превышать 255 символов.
	public static final int PETS_CANNOT_USE_THIS_ITEM = 972; // Питомец не может воспользоваться этим предметом.
	public static final int PLEASE_INPUT_NO_MORE_THAN_THE_NUMBER_YOU_HAVE = 973; // Введите количество, не превышающее то, что у Вас есть.
	public static final int THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL = 974; // Кристалл души поглотил душу.
	public static final int THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_A_SOUL = 975; // Кристалл души не смог поглотить душу.
	public static final int THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY = 976; // Кристалл души испорчен, так как не смог сдержать силу поглощенных им душ.
	public static final int THE_SOUL_CRYSTALS_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL = 977; // Кристалл души не смог поглотить душу, из-за того что кристаллы души вызвали резонанс.
	public static final int THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_A_SOUL = 978; // Кристалл души отказывается от поглощения души.
	public static final int ARRIVED_AT_TALKING_ISLAND_HARBOR = 979; // Корабль достиг Говорящего Острова.
	public static final int WILL_LEAVE_FOR_GLUDIN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES = 980; // Корабль отправится в гавань Глудина через 10 мин.
	public static final int WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_FIVE_MINUTES = 981; // Корабль отправится в гавань Глудина через 5 мин.
	public static final int WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_ONE_MINUTE = 982; // Корабль отправится в гавань Глудина через 1 мин.
	public static final int THOSE_WISHING_TO_RIDE_SHOULD_MAKE_HASTE_TO_GET_ON = 983; // Желающие уехать, поторопитесь.
	public static final int LEAVING_SOON_FOR_GLUDIN_HARBOR = 984; // Корабль скоро отправляется в гавань Глудина.
	public static final int LEAVING_FOR_GLUDIN_HARBOR = 985; // Корабль отправляется в гавань Глудина.
	public static final int ARRIVED_AT_GLUDIN_HARBOR = 986; // Корабль вошел в гавань Глудина.
	public static final int WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES = 987; // Корабль отправится на Говорящий Остров через 10 мин.
	public static final int WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_FIVE_MINUTES = 988; // Корабль отправится на Говорящий Остров через 5 мин.
	public static final int WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_ONE_MINUTE = 989; // Корабль отправится на Говорящий Остров через 1 мин.
	public static final int LEAVING_SOON_FOR_TALKING_ISLAND_HARBOR = 990; // Корабль скоро отбудет к Говорящему Острову.
	public static final int LEAVING_FOR_TALKING_ISLAND_HARBOR = 991; // Корабль отправляется к Говорящему Острову.
	public static final int ARRIVED_AT_GIRAN_HARBOR = 992; // Корабль прибыл в гавань Гирана.
	public static final int WILL_LEAVE_FOR_GIRAN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES = 993; // Корабль продолжит путь к гавани Гирана через 10 мин.
	public static final int WILL_LEAVE_FOR_GIRAN_HARBOR_IN_FIVE_MINUTES = 994; // Корабль отправляется к гавани Гирана через 5 мин.
	public static final int WILL_LEAVE_FOR_GIRAN_HARBOR_IN_ONE_MINUTE = 995; // Корабль отправляется к гавани Гирана через 1 мин.
	public static final int LEAVING_SOON_FOR_GIRAN_HARBOR = 996; // Корабль скоро отправляется к гавани Гирана.
	public static final int LEAVING_FOR_GIRAN_HARBOR = 997; // Корабль отправляется к гавани Гирана.
	public static final int THE_INNADRIL_PLEASURE_BOAT_HAS_ARRIVED_IT_WILL_ANCHOR_FOR_TEN_MINUTES = 998; // Прибыл корабль из Иннадрила. Стоянка 10 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_FIVE_MINUTES = 999; // Корабль в Иннадрил отбывает через 5 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_ONE_MINUTE = 1000; // Корабль в Иннадрил отбывает через 1 мин.
	public static final int INNADRIL_PLEASURE_BOAT_IS_LEAVING_SOON = 1001; // Корабль в Иннадрил скоро отбывает.
	public static final int INNADRIL_PLEASURE_BOAT_IS_LEAVING = 1002; // Корабль в Иннадрил отправляется.
	public static final int CANNOT_PROCESS_A_MONSTER_RACE_TICKET = 1003; // Обработать билет на гонку монстров невозможно.
	public static final int THE_PRELIMINARY_MATCH_REGISTRATION_OF_S1_HAS_FINISHED = 1007; // Регистрация на участие в отборочном туре $s1 завершена.
	public static final int A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED = 1008; // Оседлать голодного питомца невозможно.
	public static final int A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD = 1009; // Вы не можете оседлать питомца, если мертвы.
	public static final int A_DEAD_PET_CANNOT_BE_RIDDEN = 1010; // Вы не можете оседлать мертвого питомца.
	public static final int A_STRIDER_IN_BATTLE_CANNOT_BE_RIDDEN = 1011; // Воюющий питомец не может быть оседлан.
	public static final int A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE = 1012; // Оседлать питомца во время битвы нельзя.
	public static final int A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING = 1013; // Вы можете оседлать питомца, только если стоите.
	public static final int THE_PET_ACQUIRED_EXPERIENCE_POINTS_OF_S1 = 1014; // Ваш питомец получил $s1 опыта.
	public static final int THE_PET_GAVE_DAMAGE_OF_S1 = 1015; // Ваш питомец нанес $s1 урона.
	public static final int THE_PET_RECEIVED_DAMAGE_OF_S2_CAUSED_BY_S1 = 1016; // $c1 наносит питомцу $s2 урона.
	public static final int PETS_CRITICAL_HIT = 1017; // Критический удар питомца!
	public static final int THE_PET_USES_S1 = 1018; // Ваш питомец использует: $s1.
	public static final int YOUR_PET_USES_S1 = 1019; // Ваш питомец использует: $s1.
	public static final int THE_PET_GAVE_S1 = 1020; // Ваш питомец подобрал: $s1.
	public static final int THE_PET_GAVE_S2_S1_S = 1021; // Ваш питомец подобрал предмет: $s1 ($s2 шт.).
	public static final int THE_PET_GAVE__S1_S2 = 1022; // Ваш питомец подобрал: +$s1 $s2.
	public static final int THE_PET_GAVE_S1_ADENA = 1023; // Ваш питомец подобрал: $s1 аден.
	public static final int THE_PET_PUT_ON_S1 = 1024; // Ваш питомец надел: $s1.
	public static final int THE_PET_TOOK_OFF_S1 = 1025; // Ваш питомец снял: $s1.
	public static final int THE_SUMMONED_MONSTER_GAVE_DAMAGE_OF_S1 = 1026; // Ваш слуга наносит $s1 урона.
	public static final int THE_SUMMONED_MONSTER_RECEIVED_DAMAGE_OF_S2_CAUSED_BY_S1 = 1027; // $c1 наносит Вашему слуге $s2 урона.
	public static final int SUMMONED_MONSTERS_CRITICAL_HIT = 1028; // Критический удар вызванного монстра!
	public static final int A_SUMMONED_MONSTER_USES_S1 = 1029; // Ваш слуга использует: $s1.
	public static final int _PARTY_INFORMATION_ = 1030; // <Информация о группе.>
	public static final int LOOTING_METHOD_FINDERS_KEEPERS = 1031; // Подбор предметов: Нашедшему
	public static final int LOOTING_METHOD_RANDOM = 1032; // Подбор предметов: Случайно
	public static final int LOOTING_METHOD_RANDOM_INCLUDING_SPOIL = 1033; // Подбор предметов: Случайно+Присвоить
	public static final int LOOTING_METHOD_BY_TURN = 1034; // Подбор предметов: По очереди
	public static final int LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL = 1035; // Подбор предметов: По очереди+Присвоить
	public static final int YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED = 1036; // Вы превысили максимально возможное количество.
	public static final int S1_MANUFACTURED_S2 = 1037; // $c1 создает: $s2.
	public static final int S1_MANUFACTURED_S3_S2_S = 1038; // $c1 создает: $s2 ($s3 шт.).
	public static final int ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE = 1039; // Предметы, находящиеся в хранилище клана, может забрать только глава клана. Продолжить?
	public static final int PACKAGES_SENT_CAN_ONLY_BE_RETRIEVED_AT_THIS_WAREHOUSE_DO_YOU_WANT_TO_CONTINUE = 1040; // Посылку можно забрать с любого склада. Продолжить?
	public static final int THE_NEXT_SEED_PURCHASE_PRICE_IS_S1_ADENA = 1041; // Цена семян на завтра: $s1 аден.
	public static final int THE_NEXT_FARM_GOODS_PURCHASE_PRICE_IS_S1_ADENA = 1042; // Цена плодов на завтра: $s1 аден.
	public static final int AT_THE_CURRENT_TIME_THE__UNSTUCK_COMMAND_CANNOT_BE_USED_PLEASE_SEND_IN_A_PETITION = 1043; // Сейчас нельзя использовать команду "/unstuck". Обратитесь в службу поддержки.
	public static final int MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD = 1044; // Во время продажи билетов узнать сумму ставок нельзя.
	public static final int NOT_CURRENTLY_PREPARING_FOR_A_MONSTER_RACE = 1045; // Подготовка гонки монстров не проводится.
	public static final int MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE = 1046; // Продажа завершена, покупка билетов на гонку монстров невозможна.
	public static final int WE_DID_NOT_SUCCEED_IN_PRODUCING_S1_ITEM = 1047; // Изготовить предмет $s1 не получилось.
	public static final int WHISPERING_IS_NOT_POSSIBLE_IN_STATE_OF_OVERALL_BLOCKING = 1048; // Послать личное сообщение в режиме полной блокировки невозможно.
	public static final int IT_IS_NOT_POSSIBLE_TO_MAKE_INVITATIONS_FOR_ORGANIZING_PARTIES_IN_STATE_OF_OVERALL_BLOCKING = 1049; // Пригласить в группу в режиме полной блокировки невозможно.
	public static final int THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER = 1050; // В клане нет функции сообщества. Клановые сообщества доступны только для кланов 2-го уровня и выше.
	public static final int PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW = 1051; // Вы не сдали деньги за использование холла клана. Просим Вас положить необходимую сумму в хранилище клана до $s1 завтрашнего дня.
	public static final int THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED = 1052; // Вы опоздали с арендой холла клана на неделю, вследствие чего право на владение холлом отменено.
	public static final int IT_IS_IMPOSSIBLE_TO_BE_RESSURECTED_IN_BATTLEFIELDS_WHERE_SIEGE_WARS_ARE_IN_PROCESS = 1053; // Во время проведения осад Вы не можете пользоваться функцией воскрешения.
	public static final int YOU_HAVE_ENTERED_A_LAND_WITH_MYSTERIOUS_POWERS = 1054; // Вы попали в мистическое место.
	public static final int YOU_HAVE_LEFT_THE_LAND_WHICH_HAS_MYSTERIOUS_POWERS = 1055; // Вы ушли из мистического места.
	public static final int YOU_HAVE_EXCEEDED_THE_CASTLES_STORAGE_LIMIT_OF_ADENA = 1056; // Вы превысили лимит аден, которые можно положить в хранилище замка.
	public static final int THIS_COMMAND_CAN_ONLY_BE_USED_IN_THE_RELAX_SERVER = 1057; // Данная команда используется только на сервере для отдыха.
	public static final int THE_SALES_AMOUNT_OF_SEEDS_IS_S1_ADENA = 1058; // Цена семени: $s1 аден.
	public static final int THE_REMAINING_PURCHASING_AMOUNT_IS_S1_ADENA = 1059; // На закупку у Вас осталось: $s1 аден.
	public static final int THE_REMAINDER_AFTER_SELLING_THE_SEEDS_IS_S1 = 1060; // После продажи семян у Вас осталось: $s1 аден.
	public static final int THE_RECIPE_CANNOT_BE_REGISTERED__YOU_DO_NOT_HAVE_THE_ABILITY_TO_CREATE_ITEMS = 1061; // У Вас нет умения изготавливать предметы, зарегистрировать рецепт не получится.
	public static final int WRITING_SOMETHING_NEW_IS_POSSIBLE_AFTER_LEVEL_10 = 1062; // Написать сообщение можно, только достигнув 10-го уровня.
	public static final int PETITION_SERVICE_IS_NOT_AVAILABEL_FOR_S1_TO_S2_IN_CASE_OF_BEING_TRAPPED_IN_TERRITORY_WHERE_YOU_ARE_UNABLE_TO_MOVE_PLEASE_USE_THE__UNSTUCK_COMMAND = 1063; // В данный момент заявку отправить нельзя. В случае, если Вы застряли в текстурах, воспользуйтесь командой "/unstuck".
	public static final int EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED = 1064; // Вы сняли снаряжение +$s1 $s2.
	public static final int WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM = 1065; // В личной торговой лавке или мастерской обмен или удаление предмета невозможно.
	public static final int S1_HPS_HAVE_BEEN_RESTORED = 1066; // Восстановлено $s1 НР.
	public static final int XS2S_HP_HAS_BEEN_RESTORED_BY_S1 = 1067; // $c1 восстанавливает Вам $s2 НР.
	public static final int S1_MPS_HAVE_BEEN_RESTORED = 1068; // Восстановлено $s1 MP.
	public static final int XS2S_MP_HAS_BEEN_RESTORED_BY_S1 = 1069; // $c1 восстанавливает Вам $s2 MP.
	public static final int XYOU_DO_NOT_HAVE_XREADX_PERMISSION = 1070; // Вам не доступна функция "прочитать".
	public static final int XYOU_DO_NOT_HAVE_XWRITEX_PERMISSION = 1071; // Вам не доступна функция "написать".
	public static final int YOU_HAVE_OBTAINED_A_TICKET_FOR_THE_MONSTER_RACE_S1__SINGLE = 1072; // Вы получили одноразовый билет на гонку монстров $s1.
	public static final int YOU_HAVE_OBTAINED_A_TICKET_FOR_THE_MONSTER_RACE_S1__DOUBLE = 1073; // Вы получили многоразовый билет на гонку монстров $s1.
	public static final int YOU_DO_NOT_MEET_THE_AGE_REQUIREMENT_TO_PURCHASE_A_MONSTER_RACE_TICKET = 1074; // Вы не можете приобрести билет на гонки монстров из-за ограничения по возрасту.
	public static final int THE_GAME_CANNOT_BE_TERMINATED = 1076; // Невозможно выйти из игры.
	public static final int A_GAMEGUARD_EXECUTION_ERROR_HAS_OCCURRED_PLEASE_SEND_THE_ERL_FILE_S_LOCATED_IN_THE_GAMEGUARD_FOLDER_TO_GAME = 1077; // Ошибка при активации GameGuard. Обратитесь в службу поддержки на нашем сайте.
	public static final int WHEN_A_USERS_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD = 1078; // При многократном повторе одинаковых фраз включится блокировка чата. Будьте осторожны при многократном использовании одной и той же фразы.
	public static final int THE_TARGET_IS_CURRENTLY_BANNED_FROM_CHATTING = 1079; // У собеседника активировалась блокировка чата.
	public static final int DO_YOU_WISH_TO_USE_THE_FACELIFTING_POTION_X_TYPE_A_IT_IS_PERMANENT = 1080; // Вы хотите воспользоваться зельем изменения внешности - тип А? Эффект от этого зелья останется навсегда.
	public static final int DO_YOU_WISH_TO_USE_THE_DYE_POTION_X_TYPE_A_IT_IS_PERMANENT = 1081; // Вы хотите воспользоваться зельем изменения цвета волос - тип А? Эффект от этого зелья останется навсегда.
	public static final int DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_A_IT_IS_PERMANENT = 1082; // Вы хотите воспользоваться зельем изменения прически - тип А? Эффект от этого зелья останется навсегда.
	public static final int THE_FACELIFTING_POTION__TYPE_A_IS_BEING_USED = 1083; // Применено зелье изменения внешности - тип А.
	public static final int THE_DYE_POTION__TYPE_A_IS_BEING_USED = 1084; // Применено зелье изменения цвета волос - тип А.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_A_IS_BEING_USED = 1085; // Применено зелье изменения прически - тип А.
	public static final int YOUR_FACIAL_APPEARANCE_HAS_BEEN_CHANGED = 1086; // Ваше лицо изменилось.
	public static final int YOUR_HAIR_COLOR_HAS_BEEN_CHANGED = 1087; // Цвет волос изменен.
	public static final int YOUR_HAIR_STYLE_HAS_BEEN_CHANGED = 1088; // Прическа изменена.
	public static final int S1_HAS_OBTAINED_A_FIRST_ANNIVERSARY_COMMEMORATIVE_ITEM = 1089; // Персонаж $c1 получил подарок по случаю I годовщины в игре.
	public static final int DO_YOU_WISH_TO_USE_THE_FACELIFTING_POTION_X_TYPE_B_IT_IS_PERMANENT = 1090; // Вы хотите воспользоваться зельем изменения внешности - тип В? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_FACELIFTING_POTION_X_TYPE_C_IT_IS_PERMANENT = 1091; // Вы хотите воспользоваться зельем изменения внешности - тип С? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_DYE_POTION_X_TYPE_B_IT_IS_PERMANENT = 1092; // Вы хотите воспользоваться зельем изменения цвета волос - тип В? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_DYE_POTION_X_TYPE_C_IT_IS_PERMANENT = 1093; // Вы хотите воспользоваться зельем изменения цвета волос - тип С? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_DYE_POTION_X_TYPE_D_IT_IS_PERMANENT = 1094; // Вы хотите воспользоваться зельем изменения цвета волос - тип D? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_B_IT_IS_PERMANENT = 1095; // Вы хотите воспользоваться зельем изменения прически - тип B? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_C_IT_IS_PERMANENT = 1096; // Вы хотите воспользоваться зельем изменения прически - тип C? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_D_IT_IS_PERMANENT = 1097; // Вы хотите воспользоваться зельем изменения прически - тип D? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_E_IT_IS_PERMANENT = 1098; // Вы хотите воспользоваться зельем изменения прически - тип E? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_F_IT_IS_PERMANENT = 1099; // Вы хотите воспользоваться зельем изменения прически - тип F? Эффект от этого зелья останется навсегда.
	public static final int XDO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION_X_TYPE_G_IT_IS_PERMANENT = 1100; // Вы хотите воспользоваться зельем изменения прически - тип G? Эффект от этого зелья останется навсегда.
	public static final int THE_FACELIFTING_POTION__TYPE_B_IS_BEING_USED = 1101; // Применено зелье изменения внешности - тип B.
	public static final int THE_FACELIFTING_POTION__TYPE_C_IS_BEING_USED = 1102; // Применено зелье изменения внешности - тип C.
	public static final int THE_DYE_POTION__TYPE_B_IS_BEING_USED = 1103; // Применено зелье изменения цвета волос - тип B.
	public static final int THE_DYE_POTION__TYPE_C_IS_BEING_USED = 1104; // Применено зелье изменения цвета волос - тип C.
	public static final int THE_DYE_POTION__TYPE_D_IS_BEING_USED = 1105; // Применено зелье изменения цвета волос - тип D.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_B_IS_BEING_USED = 1106; // Применено зелье изменения прически - тип B.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_C_IS_BEING_USED = 1107; // Применено зелье изменения прически - тип C.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_D_IS_BEING_USED = 1108; // Применено зелье изменения прически - тип D.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_E_IS_BEING_USED = 1109; // Применено зелье изменения прически - тип E.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_F_IS_BEING_USED = 1110; // Применено зелье изменения прически - тип F.
	public static final int THE_HAIR_STYLE_CHANGE_POTION__TYPE_G_IS_BEING_USED = 1111; // Применено зелье изменения прически - тип G.
	public static final int THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS = 1112; // Джекпот лотереи $s1 составил $s2 аден. Количество победителей: $s3 чел.
	public static final int THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING = 1113; // Джекпот лотереи $s1 составил $s2 аден. Первое место не занял никто. Данный джекпот будет разыгран в следующей лотерее.
	public static final int YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLANS_DISSOLUTION = 1114; // Во время роспуска клана зарегистрироваться в осаде замка нельзя.
	public static final int INDIVIDUALS_MAY_NOT_SURRENDER_DURING_COMBAT = 1115; // Во время индивидуальной битвы сдаться нельзя.
	public static final int ONE_CANNOT_LEAVE_ONES_CLAN_DURING_COMBAT = 1116; // Во время битвы уйти из клана нельзя.
	public static final int A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT = 1117; // Во время битвы изгнать члена клана нельзя.
	public static final int PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY = 1118; // Чтобы продолжить квест, разгрузите инвентарь (менее 80%).
	public static final int QUEST_WAS_AUTOMATICALLY_CANCELED_WHEN_YOU_ATTEMPTED_TO_SETTLE_THE_ACCOUNTS_OF_YOUR_QUEST_WHILE_YOUR_INVENTORY_EXCEEDED_80_PERCENT_OF_CAPACITY = 1119; // Из-за неверной попытки выполнение квеста отменено.
	public static final int YOU_ARE_STILL_IN_THE_CLAN = 1120; // Ошибка при попытке покинуть клан.
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_VOTE = 1121; // У Вас нет права участвовать в выборах.
	public static final int THERE_IS_NO_CANDIDATE = 1122; // Нет кандидатов.
	public static final int WEIGHT_AND_VOLUME_LIMIT_HAS_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE = 1123; // Нельзя использовать умение из-за перегруза.
	public static final int A_RECIPE_BOOK_MAY_NOT_BE_USED_WHILE_USING_A_SKILL = 1124; // Нельзя использовать книгу рецептов одновременно с умением.
	public static final int AN_ITEM_MAY_NOT_BE_CREATED_WHILE_ENGAGED_IN_TRADING = 1125; // В режиме обмена функция изготовления недоступна.
	public static final int YOU_MAY_NOT_ENTER_A_NEGATIVE_NUMBER = 1126; // Нельзя ввести отрицательное число.
	public static final int THE_REWARD_MUST_BE_LESS_THAN_10_TIMES_THE_STANDARD_PRICE = 1127; // Нельзя установить награду, в 10 раз превышающую обычную цену.
	public static final int A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL = 1128; // Во время использования умения функции торговли и изготовления недоступны.
	public static final int THIS_IS_NOT_ALLOWED_WHILE_USING_A_FERRY = 1129; // Данная функция недоступна во время плавания.
	public static final int YOU_HAVE_GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR = 1130; // Вы нанесли $s1 урона врагу и $s2 - его слуге.
	public static final int IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT = 1131; // Сейчас уже полночь, и можно ощутить эффект $s1.
	public static final int IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR = 1132; // Настало утро, и эффект $s1 пропал.
	public static final int SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT = 1133; // У Вас мало HP. Вы ощущаете эффект умения $s1.
	public static final int SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR = 1134; // Количество HP увеличилось. Действие умения $s1 прекращается.
	public static final int WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP = 1135; // Во время битвы функциями торговли или изготовления воспользоваться нельзя.
	public static final int SINCE_THERE_WAS_AN_ACCOUNT_THAT_USED_THIS_IP_AND_ATTEMPTED_TO_LOG_IN_ILLEGALLY_THIS_ACCOUNT_IS_NOT_ALLOWED_TO_CONNECT_TO_THE_GAME_SERVER_FOR_S1_MINUTES_PLEASE_USE_ANOTHER_GAME_SERVER = 1136; // Была попытка нелегального входа с этого IP, поэтому подключиться к серверу нельзя. Попробуйте войти на другой сервер.
	public static final int S1_HARVESTED_S3_S2_S = 1137; // $c1 получает: $s2 $s3 штук.
	public static final int S1_HARVESTED_S2_S = 1138; // $c1 получает: $s2.
	public static final int THE_WEIGHT_AND_VOLUME_LIMIT_OF_INVENTORY_MUST_NOT_BE_EXCEEDED = 1139; // Нельзя превысить лимит веса.
	public static final int WOULD_YOU_LIKE_TO_OPEN_THE_GATE = 1140; // Вы хотите открыть врата?
	public static final int WOULD_YOU_LIKE_TO_CLOSE_THE_GATE = 1141; // Вы хотите закрыть врата?
	public static final int SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN = 1142; // $s1 уже рядом с Вами, Вы не можете призвать его снова.
	public static final int SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_WILL_DISAPPEAR = 1143; // Слуга исчезнет, так как у Вас не хватает предметов для его содержания.
	public static final int CURRENTLY_YOU_DONT_HAVE_ANYBODY_TO_CHAT_WITH_IN_THE_GAME = 1144; // Ни одного из Ваших друзей нет в игре.
	public static final int S2_HAS_BEEN_CREATED_FOR_S1_AFTER_THE_PAYMENT_OF_S3_ADENA_IS_RECEIVED = 1145; // Изготовлено для персонажа $c1: $s2 за $s3 аден.
	public static final int S1_CREATED_S2_AFTER_RECEIVING_S3_ADENA = 1146; // $c1 создает для Вас предмет $s2 за $s3 аден.
	public static final int S2_S3_HAVE_BEEN_CREATED_FOR_S1_AT_THE_PRICE_OF_S4_ADENA = 1147; // Изготовлено для персонажа $c1: $s2 ($s3 шт.) за $s4 аден.
	public static final int S1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA = 1148; // $c1 создает для Вас предмет $s2 ($s3 шт.) за $s4 аден.
	public static final int THE_ATTEMPT_TO_CREATE_S2_FOR_S1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED = 1149; // Не удалось изготовить для персонажа $c1: $s2 за $s3 аден.
	public static final int S1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA = 1150; // $c1 не может изготовить для Вас предмет $s2 за $s3 аден.
	public static final int S2_IS_SOLD_TO_S1_AT_THE_PRICE_OF_S3_ADENA = 1151; // $c1 покупает у Вас предмет $s2 за $s3 аден.
	public static final int S2_S3_HAVE_BEEN_SOLD_TO_S1_FOR_S4_ADENA = 1152; // $c1 покупает у Вас предмет $s2 ($s3 шт.) за $s4 аден.
	public static final int S2_HAS_BEEN_PURCHASED_FROM_S1_AT_THE_PRICE_OF_S3_ADENA = 1153; // Куплено у персонажа $c1: $s2 за $s3 аден.
	public static final int S3_S2_HAS_BEEN_PURCHASED_FROM_S1_FOR_S4_ADENA = 1154; // Куплено у персонажа $c1: $s2 ($s3 шт.) за $s4 аден.
	public static final int _S2S3_HAS_BEEN_SOLD_TO_S1_AT_THE_PRICE_OF_S4_ADENA = 1155; // Продано персонажу $c1: +$s2 $s3 за $s4 аден.
	public static final int _S2S3_HAS_BEEN_PURCHASED_FROM_S1_AT_THE_PRICE_OF_S4_ADENA = 1156; // Куплено у персонажа $c1: +$s2 $s3 за $s4 аден.
	public static final int TRYING_ON_STATE_LASTS_FOR_ONLY_5_SECONDS_WHEN_A_CHARACTERS_STATE_CHANGES_IT_CAN_BE_CANCELLED = 1157; // Примерка снаряжения длится 10 секунд. В случае изменения Вашего состояния режим примерки может быть отменен.
	public static final int YOU_CANNOT_GET_DOWN_FROM_A_PLACE_THAT_IS_TOO_HIGH = 1158; // Вы не можете спешиться с такой высоты.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_ARRIVE_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES = 1159; // Корабль с Говорящего Острова прибудет в гавань Глудина через 10 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES = 1160; // Корабль с Говорящего Острова прибудет в гавань Глудина через 5 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE = 1161; // Корабль с Говорящего Острова прибудет в гавань Глудина через 1 мин.
	public static final int THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_15_MINUTES = 1162; // Корабль из гавани Гирана прибудет на Говорящий Остров через 15 мин.
	public static final int THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_10_MINUTES = 1163; // Корабль из гавани Гирана прибудет на Говорящий Остров через 10 мин.
	public static final int THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_5_MINUTES = 1164; // Корабль из гавани Гирана прибудет на Говорящий Остров через 5 мин.
	public static final int THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_1_MINUTE = 1165; // Корабль из гавани Гирана прибудет на Говорящий Остров через 1 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_20_MINUTES = 1166; // Корабль с Говорящего Острова прибудет в гавань Гирана через 20 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_15_MINUTES = 1167; // Корабль с Говорящего Острова прибудет в гавань Гирана через 15 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_10_MINUTES = 1168; // Корабль с Говорящего Острова прибудет в гавань Гирана через 10 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_5_MINUTES = 1169; // Корабль с Говорящего Острова прибудет в гавань Гирана через 5 мин.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_1_MINUTE = 1170; // Корабль с Говорящего Острова прибудет в гавань Гирана через 1 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_20_MINUTES = 1171; // Наш корабль прибудет в пункт назначения через 20 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_15_MINUTES = 1172; // Наш корабль прибудет в пункт назначения через 15 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_10_MINUTES = 1173; // Наш корабль прибудет в пункт назначения через 10 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_5_MINUTES = 1174; // Наш корабль прибудет в пункт назначения через 5 мин.
	public static final int THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_1_MINUTE = 1175; // Наш корабль прибудет в пункт назначения через 1 мин.
	public static final int THIS_IS_A_QUEST_EVENT_PERIOD = 1176; // Продолжается проведение ивента.
	public static final int THIS_IS_THE_SEAL_VALIDATION_PERIOD = 1177; // Продолжается период проверки Печати.
	public static final int THIS_SEAL_PERMITS_THE_GROUP_THAT_HOLDS_IT_TO_EXCLUSIVELY_ENTER_THE_DUNGEON_OPENED_BY_THE_SEAL_OF_AVARICE_DURING_THE_SEAL_VALIDATION_PERIOD__IT_ALSO_PERMITS_TRADING_WITH_THE_MERCHANT_OF_MAMMON_WHO_APPEARS_IN_SPECIAL_DUNGEONS_AND_PERMITS_MEETINGS_WITH_ANAKIM_OR_LILITH_IN_THE_DISCIPLES_NECROPOLIS = 1178; // Эта Печать дает группе, владеющей ею, эксклюзивный вход в подземелье, открывающееся Печатью Алчности в период действия Печатей. Также она позволяет вести дела с Торговцами Маммона, которые находятся в особых подземельях. Также она позволяет встретиться с Анакимом или Лилит в Некрополе Апостолов.
	public static final int THIS_SEAL_PERMITS_THE_GROUP_THAT_HOLDS_IT_TO_ENTER_THE_DUNGEON_OPENED_BY_THE_SEAL_OF_GNOSIS_USE_THE_TELEPORTATION_SERVICE_OFFERED_BY_THE_PRIEST_IN_THE_VILLAGE_AND_DO_BUSINESS_WITH_THE_MERCHANT_OF_MAMMON_THE_ORATOR_OF_REVELATIONS_APPEARS_AND_CASTS_GOOD_MAGIC_ON_THE_WINNERS_AND_THE_PREACHER_OF_DOOM_APPEARS_AND_CASTS_BAD_MAGIC_ON_THE_LOSERS = 1179; // Эта Печать позволяет группе, владеющей ею, проникнуть в подземелье, открывающееся Печатью Познания, пользоваться услугами телепортации, которые предоставляет жрец в деревне, и вести дела с Кузнецами Маммона. Появляется Оратор Откровений и заклинает положительными эффектами победителей. Появляется Проповедник Судьбы и заклинает отрицательными эффектами проигравших.
	public static final int DURING_THE_SEAL_VALIDATION_PERIOD_THE_COSTS_OF_CASTLE_DEFENSE_MERCENARIES_AND_RENOVATIONS_BASIC_P_DEF_OF_CASTLE_GATES_AND_CASTLE_WALLS_AND_MAXIMUM_TAX_RATES_WILL_ALL_CHANGE_TO_FAVOR_THE_GROUP_OF_FIGHTERS_THAT_POSSESSES_THIS_SEAL = 1180; // В период действия Печатей максимум CP группы увеличивается. Кроме того, группа, владеющая Печатью, будет получать скидки при улучшении защитных наемников замка, врат замка и стен замка. Улучшается базовая Ф. Защ. стен и ворот замка и установленный лимит налогов. Использование осадных орудий также будет ограничено. Если Мятежники Заката получат право на обладание этой Печатью в осаде замка, то только клан, владеющий замком, сможет встать на его защиту.
	public static final int DO_YOU_REALLY_WISH_TO_CHANGE_THE_TITLE = 1181; // Вы хотите изменить заголовок?
	public static final int DO_YOU_REALLY_WISH_TO_DELETE_THE_CLAN_CREST = 1182; // Вы хотите удалить эмблему клана?
	public static final int THIS_IS_THE_INITIAL_PERIOD = 1183; // Идет подготовка.
	public static final int THIS_IS_A_PERIOD_OF_CALCULATIING_STATISTICS_IN_THE_SERVER = 1184; // Идет сбор информации о сервере.
	public static final int DAYS_LEFT_UNTIL_DELETION = 1185; // дней до удаления.
	public static final int TO_CREATE_A_NEW_ACCOUNT_PLEASE_VISIT_THE_PLAYNC_WEBSITE_HTTP___WWWPLAYNCCOM_US_SUPPORT = 1186; // Новый аккаунт можно создать на нашем сайте.
	public static final int IF_YOU_HAVE_LOST_YOUR_ACCOUNT_INFORMATION_PLEASE_VISIT_THE_OFFICIAL_LINEAGE_II_SUPPORT_WEBSITE_AT_HTTP__SUPPORTPLAYNCCOM = 1187; // Если Вы забыли свой логин или пароль, обратитесь в службу поддержки на сайте.
	public static final int THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT_IT_WILL_BE_DISSOLVED_WHEN_THE_CASTLE_LORD_IS_REPLACED = 1189; // Создан временный альянс на период осады замка. При смене хозяина замка альянс будет распущен.
	public static final int THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED = 1190; // Временный альянс, созданный на время осады замка, распущен.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_10_MINUTES = 1191; // Корабль из гавани Глудина до Говорящего Острова отправляется через 10 мин.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_5_MINUTES = 1192; // Корабль из гавани Глудина до Говорящего Острова отправляется через 5 мин.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_1_MINUTE = 1193; // Корабль из гавани Глудина до Говорящего Острова отправляется через 1 мин.
	public static final int A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS = 1194; // Наемников можно расставлять с начала периода действия Печати до начала осады замка.
	public static final int THIS_MERCENARY_CANNOT_BE_ASSIGNED_TO_A_POSITION_BY_USING_THE_SEAL_OF_STRIFE = 1195; // Во время действия Печати Раздора данного наемника разместить нельзя.
	public static final int YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY = 1196; // Вы увеличили свою силу до максимального предела.
	public static final int SUMMONING_A_SERVITOR_COSTS_S2_S1 = 1197; // В качестве платы за вызов взимается $s1 в количестве $s2 шт.
	public static final int THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED = 1198; // Ваш предмет успешно кристаллизован.
	public static final int _CLAN_WAR_TARGET_ = 1199; // =======<CLAN_WAR_TARGET>=======
	public static final int S1_S2_ALLIANCE = 1200; // = $s1 (Альянс $s2)
	public static final int PLEASE_SELECT_THE_QUEST_YOU_WISH_TO_QUIT = 1201; // Выберите квест, от которого хотите отказаться.
	public static final int S1_NO_ALLIANCE_EXISTS = 1202; // = $s1 (Альянс $s2)
	public static final int THERE_IS_NO_CLAN_WAR_IN_PROGRESS = 1203; // В данный момент вы не воюете с другими кланами.
	public static final int THE_SCREENSHOT_HAS_BEEN_SAVED_S1_S2XS3 = 1204; // Скриншот сохранен. ($s1 $s2x$s3)
	public static final int MAILBOX_IS_FULL100_MESSAGE_MAXIMUM = 1205; // Ваш почтовый ящик переполнен. Вы можете хранить в нем не более 100 сообщений.
	public static final int MEMO_BOX_IS_FULL_100_MEMO_MAXIMUM = 1206; // Ваш дневник полон. Вы можете хранить в нем не более 100 записей.
	public static final int PLEASE_MAKE_AN_ENTRY_IN_THE_FIELD = 1207; // Введите содержание.
	public static final int S1_DIED_AND_DROPPED_S3_S2 = 1208; // Персонаж $c1 погиб и потерял $s2 в количестве $s3 шт.
	public static final int CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL = 1209; // Поздравляем с успешным рейдом!
	public static final int SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_VISIT_A_PRIEST_OF_DAWN_OR_DUSK_TO_PARTICIPATE_IN_THE_EVENT = 1210; // Семь Печатей: Квестовый ивент начался. Вы можете участвовать в нем, пообщавшись со Жрецом Рассвета или Жрицей Заката.
	public static final int SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_ENDED_THE_NEXT_QUEST_EVENT_WILL_START_IN_ONE_WEEK = 1211; // Семь Печатей: Квестовый ивент завершен. Следующий пройдет через неделю.
	public static final int SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_AVARICE = 1212; // Семь Печатей: Лорды Рассвета завоевали Печать Алчности.
	public static final int SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS = 1213; // Семь Печатей: Лорды Рассвета завоевали Печать Познания.
	public static final int SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_STRIFE = 1214; // Семь Печатей: Лорды Рассвета завоевали Печать Раздора.
	public static final int SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_AVARICE = 1215; // Семь Печатей: Мятежники Заката завоевали Печать Алчности.
	public static final int SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS = 1216; // Семь Печатей: Мятежники Заката завоевали Печать Познания.
	public static final int SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_STRIFE = 1217; // Семь Печатей: Мятежники Заката завоевали Печать Раздора.
	public static final int SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_BEGUN = 1218; // Семь Печатей: Печать активирована.
	public static final int SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_ENDED = 1219; // Семь Печатей: Время действия Печати закончилось.
	public static final int ARE_YOU_SURE_YOU_WISH_TO_SUMMON_IT = 1220; // Вы хотите вызвать это существо?
	public static final int DO_YOU_REALLY_WISH_TO_RETURN_IT = 1221; // Вы хотите вернуть это?
	public static final int CURRENT_LOCATION_S1_S2_S3_GM_CONSULTATION_SERVICE = 1222; // Текущее местоположение: $s1, $s2, $s3 (служба поддержки)
	public static final int WE_DEPART_FOR_TALKING_ISLAND_IN_FIVE_MINUTES = 1223; // Корабль до Говорящего Острова отправляется через 5 мин.
	public static final int WE_DEPART_FOR_TALKING_ISLAND_IN_ONE_MINUTE = 1224; // Корабль до Говорящего Острова отправляется через 1 мин.
	public static final int ALL_ABOARD_FOR_TALKING_ISLAND = 1225; // Корабль до Говорящего Острова отправляется! Все на борт!
	public static final int WE_ARE_NOW_LEAVING_FOR_TALKING_ISLAND = 1226; // Мы отправляемся к Говорящему Острову.
	public static final int YOU_HAVE_S1_UNREAD_MESSAGES = 1227; // Новые сообщения: $c1.
	public static final int S1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_S1_ = 1228; // $c1 блокирует Вас. Вы не можете послать сообщение этому персонажу.
	public static final int NO_MORE_MESSAGES_MAY_BE_SENT_AT_THIS_TIME_EACH_ACCOUNT_IS_ALLOWED_10_MESSAGES_PER_DAY = 1229; // Вы не можете послать письмо. В день можно послать не более 10 писем.
	public static final int YOU_ARE_LIMITED_TO_FIVE_RECIPIENTS_AT_A_TIME = 1230; // Послать письмо можно максимум 5 персонажам.
	public static final int YOUVE_SENT_MAIL = 1231; // Письмо отправлено.
	public static final int THE_MESSAGE_WAS_NOT_SENT = 1232; // Ошибка при отправке письма.
	public static final int YOUVE_GOT_MAIL = 1233; // Письмо доставлено.
	public static final int THE_MAIL_HAS_BEEN_STORED_IN_YOUR_TEMPORARY_MAILBOX = 1234; // Письмо сохранено в черновиках.
	public static final int DO_YOU_WISH_TO_DELETE_ALL_YOUR_FRIENDS = 1235; // Вы хотите удалить всех друзей?
	public static final int PLEASE_ENTER_SECURITY_CARD_NUMBER = 1236; // Введите номер карты безопасности.
	public static final int PLEASE_ENTER_THE_CARD_NUMBER_FOR_NUMBER_S1 = 1237; // Введите номер карты для $s1.
	public static final int YOUR_TEMPORARY_MAILBOX_IS_FULL_NO_MORE_MAIL_CAN_BE_STORED_10_MESSAGE_LIMIT = 1238; // Ваш черновик полон. Вы можете хранить в черновиках не более 10 сообщений.
	public static final int LOADING_OF_THE_KEYBOARD_SECURITY_MODULE_HAS_FAILED_PLEASE_EXIT_THE_GAME_AND_RELOAD = 1239; // Ошибка при загрузке модуля безопасности ввода через клавиатуру. Для повторной попытки перезайдите в игру.
	public static final int SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_WON = 1240; // Семь Печатей: Победили Мятежники Заката.
	public static final int SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_WON = 1241; // Семь Печатей: Победили Лорды Рассвета.
	public static final int USERS_WHO_HAVE_NOT_VERIFIED_THEIR_AGE_CANNOT_LOG_IN_BETWEEN_1000_PM_AND_600_AM = 1242; // Пользователи, не подтвердившие достижение 18 лет, не могут войти в игру с 22:00 до 6:00.
	public static final int THE_SECURITY_CARD_NUMBER_IS_INVALID = 1243; // Номер карты недействителен.
	public static final int USERS_WHO_HAVE_NOT_VERIFIED_THEIR_AGE_CANNOT_LOG_IN_BETWEEN_1000_PM_AND_600_AM_LOGGING_OFF = 1244; // Подключение для пользователей, не подтвердивших достижение 18 лет, невозможно с 22:00 до 6:00. Игра будет отключена.
	public static final int YOU_WILL_BE_LOGGED_OUT_IN_S1_MINUTES = 1245; // До выхода из игры: $s1 мин.
	public static final int S1_DIED_AND_HAS_DROPPED_S2_ADENA = 1246; // $c1 погибает и теряет $s2 аден.
	public static final int THE_CORPSE_IS_TOO_OLD_THE_SKILL_CANNOT_BE_USED = 1247; // Монстр погиб слишком давно, поэтому Вы не можете воспользоваться данным умением.
	public static final int YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED = 1248; // У Вас закончился корм, поэтому езда верхом будет прекращена.
	public static final int YOU_MAY_ONLY_RIDE_A_WYVERN_WHILE_YOURE_RIDING_A_STRIDER = 1249; // Вы можете оседлать виверну только во время верховой езды на драконе.
	public static final int DO_YOU_REALLY_WANT_TO_SURRENDER_IF_YOU_SURRENDER_DURING_AN_ALLIANCE_WAR_YOUR_EXP_WILL_DROP_AS_MUCH_AS_WHEN_YOUR_CHARACTER_DIES_ONCE = 1250; // Вы хотите сдаться? При капитуляции альянса будет снято количество опыта, равное одной смерти.
	public static final int ARE_YOU_SURE_YOU_WANT_TO_DISMISS_THE_ALLIANCE_IF_YOU_USE_THE__ALLYDISMISS_COMMAND_YOU_WILL_NOT_BE_ABLE_TO_ACCEPT_ANOTHER_CLAN_TO_YOUR_ALLIANCE_FOR_ONE_DAY = 1251; // Вы хотите распустить альянс? После роспуска альянса в течение 1 дня Вы не сможете создать альянс с другим кланом.
	public static final int ARE_YOU_SURE_YOU_WANT_TO_SURRENDER_EXP_PENALTY_WILL_BE_THE_SAME_AS_DEATH = 1252; // Вы хотите сдаться? При капитуляции будет снято количество опыта, равное одной смерти.
	public static final int ARE_YOU_SURE_YOU_WANT_TO_SURRENDER_EXP_PENALTY_WILL_BE_THE_SAME_AS_DEATH_AND_YOU_WILL_NOT_BE_ALLOWED_TO_PARTICIPATE_IN_CLAN_WAR = 1253; // Вы хотите сдаться? При капитуляции будет снято количество опыта, равное одной смерти, и Вы не сможете участвовать в войне кланов.
	public static final int THANK_YOU_FOR_SUBMITTING_FEEDBACK = 1254; // Спасибо за Ваш ответ.
	public static final int GM_CONSULTATION_HAS_BEGUN = 1255; // Началось консультация со службой поддержки.
	public static final int PLEASE_WRITE_THE_NAME_AFTER_THE_COMMAND = 1256; // Напишите имя после команды.
	public static final int THE_SPECIAL_SKILL_OF_A_SERVITOR_OR_PET_CANNOT_BE_REGISTERED_AS_A_MACRO = 1257; // Особые умения питомцев и вызванных существ не могут быть сохранены как макросы.
	public static final int S1_HAS_BEEN_CRYSTALLIZED = 1258; // $s1: кристаллизация прошла удачно.
	public static final int _ALLIANCE_TARGET_ = 1259; // =======<ALLIANCE_TARGET>=======
	public static final int SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT = 1260; // Семь Печатей: Идет подготовка к новому кругу.
	public static final int SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_SPEAK_WITH_A_PRIEST_OF_DAWN_OR_DUSK_PRIESTESS_IF_YOU_WISH_TO_PARTICIPATE_IN_THE_EVENT = 1261; // Семь Печатей: Ивент начался. Поговорите со Жрецом Рассвета или Жрицей Заката.
	public static final int SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED_RESULTS_ARE_BEING_TALLIED = 1262; // Семь Печатей: Ивент завершен. Собираются данные статистики.
	public static final int SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD_A_NEW_QUEST_EVENT_PERIOD_BEGINS_NEXT_MONDAY = 1263; // Семь Печатей: Период действия Печати. Следующий ивент будет проведен в следующий понедельник.
	public static final int THIS_SOUL_STONE_CANNOT_CURRENTLY_ABSORB_SOULS_ABSORPTION_HAS_FAILED = 1264; // Этот кристалл души не смог поглотить душу.
	public static final int YOU_CANT_ABSORB_SOULS_WITHOUT_A_SOUL_STONE = 1265; // Вы не можете поглотить душу, если нет кристалла души.
	public static final int THE_EXCHANGE_HAS_ENDED = 1266; // Обмен завершен.
	public static final int YOUR_CONTRIBUTION_SCORE_IS_INCREASED_BY_S1 = 1267; // Размер вклада увеличен на $s1.
	public static final int DO_YOU_WISH_TO_ADD_S1_CLASS_AS_YOUR_SUB_CLASS = 1268; // $s1: принять как подкласс?
	public static final int THE_NEW_SUB_CLASS_HAS_BEEN_ADDED = 1269; // Вы добавили новый подкласс.
	public static final int THE_TRANSFER_OF_SUB_CLASS_HAS_BEEN_COMPLETED = 1270; // Вы сменили подкласс.
	public static final int DO_YOU_WISH_TO_PARTICIPATE_UNTIL_THE_NEXT_SEAL_VALIDATION_PERIOD_YOU_ARE_A_MEMBER_OF_THE_LORDS_OF_DAWN = 1271; // Вы хотите участвовать? До следующего периода действия Печати Вы будете состоять в рядах Лордов Рассвета.
	public static final int DO_YOU_WISH_TO_PARTICIPATE_UNTIL_THE_NEXT_SEAL_VALIDATION_PERIOD_YOU_ARE_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK = 1272; // Вы хотите участвовать? До следующего действия Печати Вы будете состоять в рядах Мятежников Заката.
	public static final int YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN = 1273; // Вы будете участвовать в Семи Печатях на стороне Лордов Рассвета.
	public static final int YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK = 1274; // Вы будете участвовать в Семи Печатях на стороне Мятежников Заката.
	public static final int YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD = 1275; // Вы выбрали Печать Алчности.
	public static final int YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD = 1276; // Вы выбрали Печать Познания.
	public static final int YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD = 1277; // Вы выбрали Печать Раздора.
	public static final int THE_NPC_SERVER_IS_NOT_OPERATING = 1278; // Сервер NPC остановлен.
	public static final int CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT_YOU_MAY_NOT_CONTINUE = 1279; // Был превышен лимит вклада.
	public static final int MAGIC_CRITICAL_HIT = 1280; // Критический удар магией!
	public static final int YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS = 1281; // Вы успешно защитились щитом.
	public static final int YOUR_KARMA_HAS_BEEN_CHANGED_TO_S1 = 1282; // Ваша карма изменена на $s1.
	public static final int THE_MINIMUM_FRAME_OPTION_HAS_BEEN_ACTIVATED = 1283; // Была активирована настройка минимальной детализации.
	public static final int THE_MINIMUM_FRAME_OPTION_HAS_BEEN_DEACTIVATED = 1284; // Настройка минимальной детализации отключена.
	public static final int NO_INVENTORY_EXISTS_YOU_CANNOT_PURCHASE_AN_ITEM = 1285; // Нет инвентаря, поэтому нельзя приобрести предмет.
	public static final int UNTIL_NEXT_MONDAY_AT_120_AM = 1286; // (До 18:00 следующего понедельника)
	public static final int UNTIL_TODAY_AT_120_AM = 1287; // (До 18:00)
	public static final int IF_TRENDS_CONTINUE_S1_WILL_WIN_AND_THE_SEAL_WILL_BELONG_TO = 1288; // Если битва за Печати завершится сейчас, победит $s1, и Печатью завладеют:
	public static final int SINCE_THE_SEAL_WAS_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_10_PERCENT_OR_MORE_PEOPLE_HAVE_VOTED = 1289; // Так как Вы обладали Печатью и проголосовало более 10% людей
	public static final int ALTHOUGH_THE_SEAL_WAS_NOT_OWNED_SINCE_35_PERCENT_OR_MORE_PEOPLE_HAVE_VOTED = 1290; // Хотя Вы не обладали Печатью в прошлый раз, но проголосовало более 35% людей
	public static final int ALTHOUGH_THE_SEAL_WAS_OWNED_DURING_THE_PREVIOUS_PERIOD_BECAUSE_LESS_THAN_10_PERCENT_OF_PEOPLE_HAVE_VOTED = 1291; // Так как Вы обладали Печатью и проголосовало менее 10% людей
	public static final int SINCE_THE_SEAL_WAS_NOT_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_SINCE_LESS_THAN_35_PERCENT_OF_PEOPLE_HAVE_VOTED = 1292; // Хотя Вы не обладали печатью в прошлый раз, но проголосовало менее 35% людей
	public static final int IF_CURRENT_TRENDS_CONTINUE_IT_WILL_END_IN_A_TIE = 1293; // Если битва за Печати завершится сейчас, будет ничья.
	public static final int SINCE_THE_COMPETITION_HAS_ENDED_IN_A_TIE_THE_SEAL_WILL_NOT_BE_AWARDED = 1294; // Соревнование закончилось в ничью, поэтому Печать не получит никто.
	public static final int SUB_CLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE = 1295; // Во время использования умения создать или изменить подкласс нельзя.
	public static final int A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA = 1296; // В этом месте открыть личную торговую лавку нельзя.
	public static final int A_PRIVATE_WORKSHOP_MAY_NOT_BE_OPENED_IN_THIS_AREA = 1297; // В этом месте открыть личную мастерскую нельзя.
	public static final int EXITING_THE_MONSTER_RACE_TRACK = 1298; // Подтвердите желание уйти с ипподрома монстров.
	public static final int S1S_CASTING_HAS_BEEN_INTERRUPTED = 1299; // Чтение заклинания $c1 было прервано.
	public static final int TRYING_ON_MODE_CANCELED = 1300; // Режим примерки отменен.
	public static final int CAN_BE_USED_ONLY_BY_THE_LORDS_OF_DAWN = 1301; // Вы можете воспользоваться этим только после присоединения к Лордам Рассвета.
	public static final int CAN_BE_USED_ONLY_BY_THE_REVOLUTIONARIES_OF_DUSK = 1302; // Вы можете воспользоваться этим только после присоединения к Мятежникам Заката.
	public static final int USED_ONLY_DURING_A_QUEST_EVENT_PERIOD = 1303; // Вы можете воспользоваться этим только во время квестового ивента.
	public static final int DUE_TO_THE_INFLUENCE_OF_THE_SEAL_OF_STRIFE_ALL_DEFENSIVE_REGISTRATION_HAS_BEEN_CANCELED_EXCEPT_BY_ALLIANCES_OF_CASTLE_OWNING_CLANS = 1304; // Под влиянием Печати Раздора была отменена регистрация кланов-защитников Печати.
	public static final int YOU_MAY_GIVE_SOMEONE_ELSE_A_SEAL_STONE_FOR_SAFEKEEPING_ONLY_DURING_A_QUEST_EVENT_PERIOD = 1305; // Камни Печатей могут быть переданы только во время квестового ивента.
	public static final int TRYING_ON_MODE_HAS_ENDED = 1306; // Примерка завершена.
	public static final int ACCOUNTS_MAY_ONLY_BE_SETTLED_DURING_THE_SEAL_VALIDATION_PERIOD = 1307; // Вы не можете управлять своим аккаунтом во время действия Печати.
	public static final int CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS = 1308; // Поздравляем! Вы сменили класс.
	public static final int THIS_OPTION_REQUIRES_THAT_THE_LATEST_VERSION_OF_MSN_MESSENGER_CLIENT_BE_INSTALLED_ON_YOUR_COMPUTER = 1309; // Для использования данной функции вам необходимо установить последнюю версию службы сообщений MSN.
	public static final int FOR_FULL_FUNCTIONALITY_THE_LATEST_VERSION_OF_MSN_MESSENGER_CLIENT_MUST_BE_INSTALLED_ON_THE_USERS_COMPUTER = 1310; // Чтобы воспользоваться функциями службы сообщений MSN в игре, необходимо установить последнюю версию MSN Messenger.
	public static final int PREVIOUS_VERSIONS_OF_MSN_MESSENGER_ONLY_PROVIDE_THE_BASIC_FEATURES_TO_CHAT_IN_THE_GAME_ADD_DELETE_CONTACTS_AND_OTHER_OPTIONS_ARENT_AVAILABLE = 1311; // Если Вы используете ранние версии службы сообщений MSN, то у Вас будет возможность беседовать, но добавить/удалить собеседника Вы не сможете.
	public static final int THE_LATEST_VERSION_OF_MSN_MESSENGER_MAY_BE_OBTAINED_FROM_THE_MSN_WEB_SITE_ = 1312; // Для установки последней версии службы сообщений MSN посетите сайт http://messenger.msn.com.
	public static final int S1_TO_BETTER_SERVE_OUR_CUSTOMERS_ALL_CHAT_HISTORIES_ARE_STORED_AND_MAINTAINED_BY_NCSOFT_IF_YOU_DO_NOT_AGREE_TO_HAVE_YOUR_CHAT_RECORDS_STORED_CLOSE_THE_CHAT_WINDOW_NOW_FOR_MORE_INFORMATION_REGARDING_THIS_ISSUE_PLEASE_VISIT_OUR_HOME_PAGE_AT_WWWNCSOFTCOM = 1313; // $s1, для улучшения предоставляемых услуг NCSoft сохраняет записи Ваших разговоров с персонажем $s2. Если Вы хотите отменить сохранение диалогов, закройте окно чата. Для получения более подробной информации посетите сайт нашей компании.
	public static final int PLEASE_ENTER_THE_PASSPORT_ID_OF_THE_PERSON_YOU_WISH_TO_ADD_TO_YOUR_CONTACT_LIST = 1314; // Введите имя человека, которого хотите добавить в друзья.
	public static final int DELETING_A_CONTACT_WILL_REMOVE_THAT_CONTACT_FROM_MSN_MESSENGER_AS_WELL_THE_CONTACT_CAN_STILL_CHECK_YOUR_ONLINE_STATUS_AND_WILL_NOT_BE_BLOCKED_FROM_SENDING_YOU_A_MESSAGE = 1315; // Если Вы удалите данного собеседника, он будет также удален из списка контактов в службе сообщений MSN. Но это не заблокирует его доступ к информации о Вашем статусе, и он сможет посылать Вам сообщения.
	public static final int THE_CONTACT_WILL_BE_DELETED_AND_BLOCKED_FROM_YOUR_CONTACT_LIST = 1316; // Этот контакт будет удален и заблокирован.
	public static final int WOULD_YOU_LIKE_TO_DELETE_THIS_CONTACT = 1317; // Вы действительно хотите удалить данный контакт?
	public static final int PLEASE_SELECT_THE_CONTACT_YOU_WANT_TO_BLOCK_OR_UNBLOCK = 1318; // Выберите друга, которого хотите заблокировать/разблокировать.
	public static final int PLEASE_SELECT_THE_NAME_OF_THE_CONTACT_YOU_WISH_TO_CHANGE_TO_ANOTHER_GROUP = 1319; // Выберите друга, группу которого хотите изменить.
	public static final int AFTER_SELECTING_THE_GROUP_YOU_WISH_TO_MOVE_YOUR_CONTACT_TO_PRESS_THE_OK_BUTTON = 1320; // Выбрав группу, в которую хотите поместить контакт, нажмите кнопку "ОК".
	public static final int ENTER_THE_NAME_OF_THE_GROUP_YOU_WISH_TO_ADD = 1321; // Введите название группы, которую хотите добавить.
	public static final int SELECT_THE_GROUP_AND_ENTER_THE_NEW_NAME = 1322; // Выбрав группу, которую хотите изменить, введите новое название.
	public static final int SELECT_THE_GROUP_YOU_WISH_TO_DELETE_AND_CLICK_THE_OK_BUTTON = 1323; // Выберите группу, которую хотите удалить, и нажмите кнопку "ОК".
	public static final int SIGNING_IN = 1324; // Вход в систему…
	public static final int YOUVE_LOGGED_INTO_ANOTHER_COMPUTER_AND_BEEN_LOGGED_OUT_OF_THE_NET_MESSENGER_SERVICE_ON_THIS_COMPUTER = 1325; // Вы вошли на другом компьютере, поэтому произошел выход из .NET Messenger Service.
	public static final int S1_ = 1326; // $s1:
	public static final int THE_FOLLOWING_MESSAGE_COULD_NOT_BE_DELIVERED = 1327; // Это сообщение не было доставлено:
	public static final int MEMBERS_OF_THE_REVOLUTIONARIES_OF_DUSK_WILL_NOT_BE_RESURRECTED = 1328; // Мятежники Заката не могут возродиться.
	public static final int YOU_ARE_CURRENTLY_BANNED_FROM_ACTIVITIES_RELATED_TO_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP = 1329; // Вы не можете воспользоваться функциями личной торговой лавки и мастерской.
	public static final int NO_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_MAY_BE_OPENED_FOR_S1_MINUTES = 1330; // Личная торговая и мастерская заблокированы на $s1 мин.
	public static final int ACTIVITIES_RELATED_TO_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP_ARE_NOW_PERMITTED = 1331; // Блокировка на пользование личной торговой лавкой и мастерской снята.
	public static final int ITEMS_MAY_NOT_BE_USED_AFTER_YOUR_CHARACTER_OR_PET_DIES = 1332; // Вы не можете использовать предмет, если Вы мертвы.
	public static final int REPLAY_FILE_ISNT_ACCESSIBLE_VERIFY_THAT_REPLAYINI_FILE_EXISTS = 1333; // Невозможно считать файл Replay. Проверьте файл Replay.ini.
	public static final int THE_NEW_CAMERA_DATA_HAS_BEEN_STORED = 1334; // Сохранена новая информация с камеры.
	public static final int THE_ATTEMPT_TO_STORE_THE_NEW_CAMERA_DATA_HAS_FAILED = 1335; // Ошибка при сохранении информации с камеры.
	public static final int THE_REPLAY_FILE_HAS_BEEN_CORRUPTED_PLEASE_CHECK_THE_S1S2_FILE = 1336; // Файл повтора, $s1.$s2, был поврежден. Пожалуйста, проверьте его.
	public static final int REPLAY_MODE_WILL_BE_TERMINATED_DO_YOU_WISH_TO_CONTINUE = 1337; // Повтор будет завершен. Продолжить?
	public static final int YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_TRANSFERRED_AT_ONE_TIME = 1338; // Вы превысили количество, которое можно переместить за один раз.
	public static final int ONCE_A_MACRO_IS_ASSIGNED_TO_A_SHORTCUT_IT_CANNOT_BE_RUN_AS_A_MACRO_AGAIN = 1339; // Если макрос используется как клавиша ярлыков, воспользоваться им снова нельзя.
	public static final int THIS_SERVER_CANNOT_BE_ACCESSED_BY_THE_COUPON_YOU_ARE_USING = 1340; // Если купон используется, подключиться к этому серверу нельзя.
	public static final int THE_NAME_OR_E_MAIL_ADDRESS_YOU_ENTERED_IS_INCORRECT = 1341; // Неверное имя или электронный адрес.
	public static final int YOU_ARE_ALREADY_LOGGED_IN = 1342; // Вы уже в игре.
	public static final int THE_PASSWORD_OR_E_MAIL_ADDRESS_YOU_ENTERED_IS_INCORRECT__YOUR_ATTEMPT_TO_LOG_INTO_NET_MESSENGER_SERVICE_HAS_FAILED = 1343; // Вы ввели неверный пароль или электронный адрес, поэтому подключиться к .NET Messenger Service нельзя.
	public static final int THE_SERVICE_YOU_REQUESTED_COULD_NOT_BE_LOCATED_AND_THEREFORE_YOUR_ATTEMPT_TO_LOG_INTO_THE_NET_MESSENGER_SERVICE_HAS_FAILED_PLEASE_VERIFY_THAT_YOU_ARE_CURRENTLY_CONNECTED_TO_THE_INTERNET = 1344; // Вы не смогли войти в систему .NET Messenger Service. Проверьте соединение с интернетом.
	public static final int AFTER_SELECTING_A_CONTACT_NAME_CLICK_ON_THE_OK_BUTTON = 1345; // Выберите собеседника и нажмите "ОК".
	public static final int YOU_ARE_CURRENTLY_ENTERING_A_CHAT_MESSAGE = 1346; // Вы вводите в чат сообщение.
	public static final int THE_LINEAGE_II_MESSENGER_COULD_NOT_CARRY_OUT_THE_TASK_YOU_REQUESTED = 1347; // Lineage II Messenger не может выполнить запрос.
	public static final int S1_HAS_ENTERED_THE_CHAT_ROOM = 1348; // $s1 входит в чат.
	public static final int S1_HAS_LEFT_THE_CHAT_ROOM = 1349; // $s1 выходит из чата.
	public static final int THE_STATUS_WILL_BE_CHANGED_TO_INDICATE__OFF_LINE__ALL_THE_CHAT_WINDOWS_CURRENTLY_OPENED_WILL_BE = 1350; // Вы изменили статус на "Отключен". Все окна будут закрыты.
	public static final int AFTER_SELECTING_THE_CONTACT_YOU_WANT_TO_DELETE_CLICK_THE_DELETE_BUTTON = 1351; // Выберите контакт и нажмите кнопку "Удалить".
	public static final int YOU_HAVE_BEEN_ADDED_TO_THE_CONTACT_LIST_OF_S1_S2 = 1352; // $s1 ($s2) добавляет Вас в список контактов.
	public static final int YOU_CAN_SET_THE_OPTION_TO_SHOW_YOUR_STATUS_AS_ALWAYS_BEING_OFF_LINE_TO_ALL_OF_YOUR_CONTACTS = 1353; // Вы можете сменить статус на "Отключен" для всех контактов в списке.
	public static final int YOU_ARE_NOT_ALLOWED_TO_CHAT_WITH_YOUR_CONTACT_WHILE_YOU_ARE_BLOCKED_FROM_CHATTING = 1354; // Вы не можете общаться во время блокировки чата.
	public static final int THE_CONTACT_YOU_CHOSE_TO_CHAT_WITH_IS_CURRENTLY_BLOCKED_FROM_CHATTING = 1355; // У этого контакта заблокирована функция чата.
	public static final int THE_CONTACT_YOU_CHOSE_TO_CHAT_WITH_IS_NOT_CURRENTLY_LOGGED_IN = 1356; // Выбранный контакт отключен.
	public static final int YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED = 1357; // Этот контакт заблокировал возможность чата с ним.
	public static final int YOU_ARE_BEING_LOGGED_OUT = 1358; // Выход из системы…
	public static final int S1_HAS_LOGGED_IN_1 = 1359; // $s1 входит в игру.
	public static final int YOU_HAVE_RECEIVED_A_MESSAGE_FROM_S1 = 1360; // $s1: получено сообщение.
	public static final int DUE_TO_A_SYSTEM_ERROR_YOU_HAVE_BEEN_LOGGED_OUT_OF_THE_NET_MESSENGER_SERVICE = 1361; // Из-за системной ошибки произошел выход из .NET Messenger Service.
	public static final int PLEASE_SELECT_THE_CONTACT_YOU_WISH_TO_DELETE__IF_YOU_WOULD_LIKE_TO_DELETE_A_GROUP_CLICK_THE_BUTTON_NEXT_TO_MY_STATUS_AND_THEN_USE_THE_OPTIONS_MENU = 1362; // Выберите контакт, который хотите удалить. Если Вы хотите удалить группу, нажмите на кнопку рядом с Вашим статусом и воспользуйтесь меню.
	public static final int YOUR_REQUEST_TO_PARTICIPATE_IN_THE_ALLIANCE_WAR_HAS_BEEN_DENIED = 1363; // Пришел отказ на участие в войне альянсов.
	public static final int THE_REQUEST_FOR_AN_ALLIANCE_WAR_HAS_BEEN_REJECTED = 1364; // Вы отказались участвовать в войне альянсов.
	public static final int S2_OF_S1_CLAN_HAS_SURRENDERED_AS_AN_INDIVIDUAL = 1365; // $s2 из клана $s1 сдается.
	public static final int YOU_CAN_DELETE_A_GROUP_ONLY_WHEN_YOU_DO_NOT_HAVE_ANY_CONTACT_IN_THAT_GROUP__IN_ORDER_TO_DELETE_A_GROUP_FIRST_TRANSFER_YOUR_CONTACT_S_IN_THAT_GROUP_TO_ANOTHER_GROUP = 1366; // Группу можно удалить только при отсутствии в ней контактов. Перед удалением группы переместите имеющиеся в ней контакты в другую.
	public static final int ONLY_MEMBERS_OF_THE_GROUP_ARE_ALLOWED_TO_ADD_RECORDS = 1367; // Только члены группы могут добавить записи.
	public static final int THOSE_ITEMS_MAY_NOT_BE_TRIED_ON_SIMULTANEOUSLY = 1368; // Вы не можете примерить эти предметы одновременно.
	public static final int YOUVE_EXCEEDED_THE_MAXIMUM = 1369; // Вы превысили максимально возможную сумму.
	public static final int YOU_CANNOT_SEND_MAIL_TO_A_GM_SUCH_AS_S1 = 1370; // $c1 - Игровой мастер. Нельзя отправить ему письмо.
	public static final int IT_HAS_BEEN_DETERMINED_THAT_YOURE_NOT_ENGAGED_IN_NORMAL_GAMEPLAY_AND_A_RESTRICTION_HAS_BEEN_IMPOSED_UPON_YOU_YOU_MAY_NOT_MOVE_FOR_S1_MINUTES = 1371; // Вы подозреваетесь в совершении нелегальных действий. В течение $s1 мин передвижение невозможно.
	public static final int YOUR_PUNISHMENT_WILL_CONTINUE_FOR_S1_MINUTES = 1372; // Вы не можете передвигаться. Ограничение продлится еще $s1 мин.
	public static final int S1_HAS_PICKED_UP_S2_THAT_WAS_DROPPED_BY_A_RAID_BOSS = 1373; // $s1 подбирает предмет $s2, выпавший из Босса рейда.
	public static final int S1_HAS_PICKED_UP_S3_S2_S_THAT_WAS_DROPPED_BY_A_RAID_BOSS = 1374; // $s1 подбирает предмет $s2 ($s3 шт.), выпавший из Босса рейда.
	public static final int S1_HAS_PICKED_UP__S2_ADENA_THAT_WAS_DROPPED_BY_A_RAID_BOSS = 1375; // $s1 подбирает $s2 аден, выпавшие из Босса рейда.
	public static final int S1_HAS_PICKED_UP_S2_THAT_WAS_DROPPED_BY_ANOTHER_CHARACTER = 1376; // $c1 подбирает предмет $s2, выпавший из другого персонажа.
	public static final int S1_HAS_PICKED_UP_S3_S2_S_THAT_WAS_DROPPED_BY_ANOTHER_CHARACTER = 1377; // $c1 подбирает предмет $s2 ($s3шт.), выпавший из другого персонажа.
	public static final int S1_HAS_PICKED_UP__S3S2_THAT_WAS_DROPPED_BY_ANOTHER_CHARACTER = 1378; // $c1 подбирает предмет +$s3 $s2, выпавший из другого персонажа.
	public static final int S1_HAS_OBTAINED_S2_ADENA = 1379; // $c1 получает $s2 аден.
	public static final int YOU_CANT_SUMMON_A_S1_WHILE_ON_THE_BATTLEGROUND = 1380; // Вы не можете вызвать персонажа $s1 на поле боя.
	public static final int THE_PARTY_LEADER_HAS_OBTAINED_S2_OF_S1 = 1381; // Лидер группы получает: $s1 ($s2 шт.)
	public static final int ARE_YOU_SURE_YOU_WANT_TO_CHOOSE_THIS_WEAPON_TO_FULFILL_THE_QUEST_YOU_MUST_BRING_THE_CHOSEN_WEAPON = 1382; // Вы хотите выбрать это оружие? Чтобы завершить квест, вы должны принести выбранное оружие.
	public static final int ARE_YOU_SURE_YOU_WANT_TO_EXCHANGE = 1383; // Вы хотите произвести обмен?
	public static final int S1_HAS_BECOME_A_PARTY_LEADER = 1384; // $c1 теперь лидер группы.
	public static final int YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_AT_THIS_LOCATION = 1385; // Вы не можете спешиться в этом месте.
	public static final int HOLD_STATE_HAS_BEEN_LIFTED = 1386; // Ограничение на передвижение снято.
	public static final int PLEASE_SELECT_THE_ITEM_YOU_WOULD_LIKE_TO_TRY_ON = 1387; // Выберите предмет, который хотите примерить.
	public static final int A_PARTY_ROOM_HAS_BEEN_CREATED = 1388; // Создана комната группы.
	public static final int THE_PARTY_ROOMS_INFORMATION_HAS_BEEN_REVISED = 1389; // Изменена информация о комнате группы.
	public static final int YOU_ARE_NOT_ALLOWED_TO_ENTER_THE_PARTY_ROOM = 1390; // Вы не можете войти в комнату группы.
	public static final int YOU_HAVE_EXITED_FROM_THE_PARTY_ROOM = 1391; // Вы вышли из комнаты группы.
	public static final int S1_HAS_LEFT_THE_PARTY_ROOM = 1392; // $c1 выходит из комнаты группы.
	public static final int YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM = 1393; // Вы были изгнаны из комнаты группы.
	public static final int S1_HAS_BEEN_OUSTED_FROM_THE_PARTY_ROOM = 1394; // $c1 изгоняется из комнаты группы.
	public static final int THE_PARTY_ROOM_HAS_BEEN_DISBANDED = 1395; // Комната группы была закрыта.
	public static final int THE_LIST_OF_PARTY_ROOMS_CAN_BE_VIEWED_BY_A_PERSON_WHO_HAS_NOT_JOINED_A_PARTY_OR_WHO_IS_A_PARTY_LEADER = 1396; // Вы не входите в группу, или доступом к списку комнаты группы обладает только ее лидер.
	public static final int THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED = 1397; // Глава комнаты группы был сменен.
	public static final int WE_ARE_RECRUITING_PARTY_MEMBERS = 1398; // Мы ищем членов группы.
	public static final int ONLY_A_PARTY_LEADER_CAN_TRANSFER_ONES_RIGHTS_TO_ANOTHER_PLAYER = 1399; // Только лидер группы может передать право быть им другому персонажу.
	public static final int PLEASE_SELECT_THE_PERSON_YOU_WISH_TO_MAKE_THE_PARTY_LEADER = 1400; // Выберите члена группы, которому хотите передать звание лидера группы.
	public static final int YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF = 1401; // Вы не можете передать звание лидера группы самому себе.
	public static final int YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER = 1402; // Вы можете передать звание лидера группы только члену этой группы.
	public static final int YOU_HAVE_FAILED_TO_TRANSFER_THE_PARTY_LEADER_RIGHTS = 1403; // Ошибка при передаче звания лидера группы.
	public static final int THE_OWNER_OF_THE_PRIVATE_MANUFACTURING_STORE_HAS_CHANGED_THE_PRICE_FOR_CREATING_THIS_ITEM__PLEASE_CHECK_THE_NEW_PRICE_BEFORE_TRYING_AGAIN = 1404; // Хозяин мастерской изменил цену изготовления. Проверьте цену и повторите попытку.
	public static final int S1_CPS_WILL_BE_RESTORED = 1405; // $s1: CP восстановлены.
	public static final int S1_WILL_RESTORE_S2S_CP = 1406; // $s2: CP восстановлены ($c1).
	public static final int YOU_ARE_USING_A_COMPUTER_THAT_DOES_NOT_ALLOW_YOU_TO_LOG_IN_WITH_TWO_ACCOUNTS_AT_THE_SAME_TIME = 1407; // Вы пользуетесь компьютером, двойной вход с которого запрещен.
	public static final int YOUR_PREPAID_REMAINING_USAGE_TIME_IS_S1_HOURS_AND_S2_MINUTES__YOU_HAVE_S3_PAID_RESERVATIONS_LEFT = 1408; // Предоплаченное время: $s1ч $s2мин. Осталось оплаченных чеков: $s3.
	public static final int YOUR_PREPAID_USAGE_TIME_HAS_EXPIRED_YOUR_NEW_PREPAID_RESERVATION_WILL_BE_USED_THE_REMAINING_USAGE_TIME_IS_S1_HOURS_AND_S2_MINUTES = 1409; // Предоплаченное время закончилось, и начинается использование предоплаченных чеков. Оставшееся время: $s1 ч $s2 мин.
	public static final int YOUR_PREPAID_USAGE_TIME_HAS_EXPIRED_YOU_DO_NOT_HAVE_ANY_MORE_PREPAID_RESERVATIONS_LEFT = 1410; // Предоплаченное время закончилось. Предоплаченных чеков не осталось.
	public static final int THE_NUMBER_OF_YOUR_PREPAID_RESERVATIONS_HAS_CHANGED = 1411; // Изменено количество предоплаченных чеков.
	public static final int YOUR_PREPAID_USAGE_TIME_HAS_S1_MINUTES_LEFT = 1412; // Осталось предоплаченного времени: $s1 мин.
	public static final int SINCE_YOU_DO_NOT_MEET_THE_REQUIREMENTS_YOU_ARE_NOT_ALLOWED_TO_ENTER_THE_PARTY_ROOM = 1413; // Вы не соответствуете требованиям для входа в комнату группы.
	public static final int THE_WIDTH_AND_LENGTH_SHOULD_BE_100_OR_MORE_GRIDS_AND_LESS_THAN_5000_GRIDS_RESPECTIVELY = 1414; // Ширина и длина должна быть более 100, но менее 5000.
	public static final int THE_COMMAND_FILE_IS_NOT_SET = 1415; // Командный файл не был настроен.
	public static final int THE_PARTY_REPRESENTATIVE_OF_TEAM_1_HAS_NOT_BEEN_SELECTED = 1416; // Представитель 1-й команды в группе не выбран.
	public static final int THE_PARTY_REPRESENTATIVE_OF_TEAM_2_HAS_NOT_BEEN_SELECTED = 1417; // Представитель 2-й команды в группе не выбран.
	public static final int THE_NAME_OF_TEAM_1_HAS_NOT_YET_BEEN_CHOSEN = 1418; // Название 1-й команды не выбрано.
	public static final int THE_NAME_OF_TEAM_2_HAS_NOT_YET_BEEN_CHOSEN = 1419; // Название 2-й команды не выбрано.
	public static final int THE_NAME_OF_TEAM_1_AND_THE_NAME_OF_TEAM_2_ARE_IDENTICAL = 1420; // Названия 1-й и 2-й команд одинаковы.
	public static final int THE_RACE_SETUP_FILE_HAS_NOT_BEEN_DESIGNATED = 1421; // Не выбран файл настройки соревнования.
	public static final int RACE_SETUP_FILE_ERROR__BUFFCNT_IS_NOT_SPECIFIED = 1422; // Ошибка в файле настройки соревнования - не выбран BuffCnt.
	public static final int RACE_SETUP_FILE_ERROR__BUFFIDS1_IS_NOT_SPECIFIED = 1423; // Ошибка в файле настройки соревнования - не выбран BuffID$s1.
	public static final int RACE_SETUP_FILE_ERROR__BUFFLVS1_IS_NOT_SPECIFIED = 1424; // Ошибка в файле настройки соревнования - не выбран BuffLv$s1.
	public static final int RACE_SETUP_FILE_ERROR__DEFAULTALLOW_IS_NOT_SPECIFIED = 1425; // Ошибка в файле настройки соревнования - не выбран DefaultAllow.
	public static final int RACE_SETUP_FILE_ERROR__EXPSKILLCNT_IS_NOT_SPECIFIED = 1426; // Ошибка в файле настройки соревнования - не выбран ExpSkillCnt.
	public static final int RACE_SETUP_FILE_ERROR__EXPSKILLIDS1_IS_NOT_SPECIFIED = 1427; // Ошибка в файле настройки соревнования - не выбран ExpSkillID$s1.
	public static final int RACE_SETUP_FILE_ERROR__EXPITEMCNT_IS_NOT_SPECIFIED = 1428; // Ошибка в файле настройки соревнования - не выбран ExpItemCnt.
	public static final int RACE_SETUP_FILE_ERROR__EXPITEMIDS1_IS_NOT_SPECIFIED = 1429; // Ошибка в файле настройки соревнования - не выбран ExpItemId$s1.
	public static final int RACE_SETUP_FILE_ERROR__TELEPORTDELAY_IS_NOT_SPECIFIED = 1430; // Ошибка в файле настройки соревнования - не выбран TeleportDelay.
	public static final int THE_RACE_WILL_BE_STOPPED_TEMPORARILY = 1431; // Соревнования будут временно остановлены.
	public static final int YOUR_OPPONENT_IS_CURRENTLY_IN_A_PETRIFIED_STATE = 1432; // Ваш противник окаменел.
	public static final int THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED = 1433; // $s1 будет использоваться автоматически
	public static final int THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED = 1434; // $s1: автоматическое использование отменено.
	public static final int DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_HAS_BEEN_CANCELLED = 1435; // $s1: нехватка предмета. Автоматическое использование отменено.
	public static final int DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_CANNOT_BE_ACTIVATED = 1436; // $s1: нехватка предмета. Автоматическое использование невозможно.
	public static final int PLAYERS_ARE_NO_LONGER_ALLOWED_TO_PLACE_DICE_DICE_CANNOT_BE_PURCHASED_FROM_A_VILLAGE_STORE_ANY_MORE_HOWEVER_YOU_CAN_STILL_SELL_THEM_TO_A_STORE_IN_A_VILLAGE = 1437; // Вы больше не можете играть в кости, а также приобретать их в магазине деревни. Но продажа в магазин возможна.

	public static final int THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT = 1438; // Отсутствует умение, позволяющее улучшение.
	public static final int ITEMS_REQUIRED_FOR_SKILL_ENCHANT_ARE_INSUFFICIENT = 1439; // У Вас нет предметов, необходимых для улучшения умения.
	public static final int SUCCEEDED_IN_ENCHANTING_SKILL_S1 = 1440; // Умение $s1 было улучшено.
	public static final int FAILED_IN_ENCHANTING_SKILL_S1 = 1441; // Ошибка при улучшении умения. Улучшаемое умение будет обновлено.
	public static final int SP_REQUIRED_FOR_SKILL_ENCHANT_IS_INSUFFICIENT = 1443; // Недостаточно SP для улучшения умения.
	public static final int EXP_REQUIRED_FOR_SKILL_ENCHANT_IS_INSUFFICIENT = 1444; // Недостаточно опыта для улучшения умения.
	public static final int YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_UNTRAIN_THE_ENCHANT_SKILL = 2068; // У вас нет предметов, необходимых для улучшения умения.

	public static final int Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_has_been_decreased_by_1 = 2069; // Разучивание умения улучшения успешно завершено. Текущий уровень умения $s1 был понижен на 1.
	public static final int Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_became_0_and_enchant_skill_will_be_initialized = 2070; // Сброс улучшений умения завершен. $s1: текущий уровень - 0, умение возвращено в исходное состояние.
	public static final int You_do_not_have_all_of_the_items_needed_to_enchant_skill_route_change = 2071; // У Вас нет необходимых предметов, чтобы изменить путь улучшения умения.
	public static final int Enchant_skill_route_change_was_successful_Lv_of_enchant_skill_S1_has_been_decreased_by_S2 = 2072; // Путь улучшения умения был изменен. Уровень умения $s1 был понижен на $s2.
	public static final int Enchant_skill_route_change_was_successful_Lv_of_enchant_skill_S1_will_remain = 2073; // Путь улучшения умения был изменен. Уровень умения $s1 был сохранен.
	public static final int Skill_enchant_failed_Current_level_of_enchant_skill_S1_will_remain_unchanged = 2074; // Улучшение не удалось. Уровень умения $s1 остался без изменений.

	public static final int REMAINING_TIME_S1_SECOND = 1442; // Осталось: $s1 сек.
	public static final int YOUR_PREVIOUS_SUB_CLASS_WILL_BE_DELETED_AND_YOUR_NEW_SUB_CLASS_WILL_START_AT_LEVEL_40__DO_YOU_WISH_TO_PROCEED = 1445; // Предыдущий подкласс будет заменен новым по достижении 40-го уровня. Продолжить?
	public static final int THE_FERRY_FROM_S1_TO_S2_HAS_BEEN_DELAYED = 1446; // Корабль, отбывающий из порта $s1 в порт $s2, задерживается.
	public static final int OTHER_SKILLS_ARE_NOT_AVAILABLE_WHILE_FISHING = 1447; // Вы не можете воспользоваться другим умением во время рыбалки.
	public static final int ONLY_FISHING_SKILLS_ARE_AVAILABLE = 1448; // Вы можете воспользоваться только умением рыбалки.
	public static final int SUCCEEDED_IN_GETTING_A_BITE = 1449; // Клюет!
	public static final int TIME_IS_UP_SO_THAT_FISH_GOT_AWAY = 1450; // Время прошло, и Вы упустили рыбу.
	public static final int THE_FISH_GOT_AWAY = 1451; // Вы упустили рыбу.
	public static final int BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY = 1452; // Вы упустили рыбу и потеряли наживку.
	public static final int FISHING_POLES_ARE_NOT_INSTALLED = 1453; // У Вас нет удочки.
	public static final int BAITS_ARE_NOT_PUT_ON_A_HOOK = 1454; // Наживка не прикреплена к крючку.
	public static final int YOU_CANT_FISH_IN_WATER = 1455; // Вы не можете рыбачить, находясь в воде.
	public static final int YOU_CANT_FISH_WHILE_YOU_ARE_ON_BOARD = 1456; // Вы не можете рыбачить, находясь на корабле.
	public static final int YOU_CANT_FISH_HERE = 1457; // Здесь рыбачить нельзя.
	public static final int CANCELS_FISHING = 1458; // Рыбалка отменена.
	public static final int NOT_ENOUGH_BAIT = 1459; // У Вас не хватает наживки.
	public static final int ENDS_FISHING = 1460; // Рыбалка завершена.
	public static final int STARTS_FISHING = 1461; // Рыбалка началась.
	public static final int PUMPING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING = 1462; // Подтягивать можно только во время рыбалки.
	public static final int REELING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING = 1463; // Подсекать можно только во время рыбалки.
	public static final int FISH_HAS_RESISTED = 1464; // Рыба сорвалась с крючка, и вы не смогли ее вытащить.
	public static final int PUMPING_IS_SUCCESSFUL_DAMAGE_S1 = 1465; // Вы успешно подтянули рыбу и нанесли ей урон $s1 HP
	public static final int PUMPING_FAILED_DAMAGE_S1 = 1466; // Вы не смогли подтянуть рыбу, и она восстановила $s1 HP
	public static final int REELING_IS_SUCCESSFUL_DAMAGE_S1 = 1467; // Вы успешно подсекли рыбу и нанесли ей урон $s1 HP
	public static final int REELING_FAILED_DAMAGE_S1 = 1468; // Вы не смогли подсечь рыбу, и она восстановила $s1 HP
	public static final int SUCCEEDED_IN_FISHING = 1469; // Вы что-то поймали.
	public static final int YOU_CANNOT_DO_THAT_WHILE_FISHING = 1470; // Вы не можете сделать это во время рыбалки.
	public static final int YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING = 1471; // Вы не можете сделать это во время рыбалки.
	public static final int YOU_CANT_MAKE_AN_ATTACK_WITH_A_FISHING_POLE = 1472; // Вы не можете атаковать во время рыбалки.
	public static final int S1_IS_NOT_SUFFICIENT = 1473; // Недостаточно $s1.
	public static final int S1_IS_NOT_AVAILABLE = 1474; // Нельзя использовать: $s1.
	public static final int PET_HAS_DROPPED_S1 = 1475; // Питомец потерял: $s1.
	public static final int PET_HAS_DROPPED__S1S2 = 1476; // Питомец потерял: +$s1 $s2.
	public static final int PET_HAS_DROPPED_S2_OF_S1 = 1477; // Питомец потерял: $s1 ($s2 шт.)
	public static final int YOU_CAN_REGISTER_ONLY_256_COLOR_BMP_FILES_WITH_A_SIZE_OF_64X64 = 1478; // Файл - формат bmp, 256 цветов, размер 64*64.
	public static final int THIS_FISHING_SHOT_IS_NOT_FIT_FOR_THE_FISHING_POLE_CRYSTAL = 1479; // Неверный ранг зарядов души для удочки.
	public static final int DO_YOU_WANT_TO_CANCEL_YOUR_APPLICATION_FOR_JOINING_THE_GRAND_OLYMPIAD = 1480; // Отказаться от участия в Олимпиаде?
	public static final int YOU_HAVE_BEEN_SELECTED_FOR_NO_CLASS_GAME_DO_YOU_WANT_TO_JOIN = 1481; // Вы выбрали игру с отсутствием ограничений по классам. Продолжить?
	public static final int YOU_HAVE_BEEN_SELECTED_FOR_CLASSIFIED_GAME_DO_YOU_WANT_TO_JOIN = 1482; // Вы выбрали игру с ограничением по классам. Продолжить?
	public static final int DO_YOU_WANT_TO_BECOME_A_HERO_NOW = 1483; // Вы готовы стать героем?
	public static final int DO_YOU_WANT_TO_USE_THE_HEROES_WEAPON_THAT_YOU_CHOSE = 1484; // Воспользоваться выбранным оружием Героя? Доступно всем расам, кроме Камаэлей.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_TO_GLUDIN_HARBOR_HAS_BEEN_DELAYED = 1485; // Корабль, отправляющийся с Говорящего Острова в гавань Глудин, задерживается.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_TO_TALKING_ISLAND_HAS_BEEN_DELAYED = 1486; // Корабль, отправляющийся с гавани Глудин на Говорящий Остров, задерживается.
	public static final int THE_FERRY_FROM_GIRAN_HARBOR_TO_TALKING_ISLAND_HAS_BEEN_DELAYED = 1487; // Корабль, отправляющийся из гавани Глудин на Говорящий Остров, задерживается.
	public static final int THE_FERRY_FROM_TALKING_ISLAND_TO_GIRAN_HARBOR_HAS_BEEN_DELAYED = 1488; // Корабль, отправляющийся с Говорящего Острова в гавань Глудин, задерживается.
	public static final int INNADRIL_CRUISE_SERVICE_HAS_BEEN_DELAYED = 1489; // Перемещение корабля в Иннадрил задерживается.
	public static final int TRADED_S2_OF_CROP_S1 = 1490; // Продано плодов: $s1 ($s2 шт.)
	public static final int FAILED_IN_TRADING_S2_OF_CROP_S1 = 1491; // Не удалось продать плодов: $s1 ($s2 шт.)
	public static final int YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S = 1492; // Вы переместитесь на Олимпийский стадион через $s1 сек.
	public static final int THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME = 1493; // Противник вышел из игры, отказавшись тем самым от участия в соревновании.
	public static final int THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME = 1494; // Противник не соответствовал условиям участия в соревновании, поэтому соревнование отменено.
	public static final int THE_GAME_WILL_START_IN_S1_SECOND_S = 1495; // Соревнования начнутся через $s1 сек.
	public static final int STARTS_THE_GAME = 1496; // Соревнования начались.
	public static final int S1_HAS_WON_THE_GAME = 1497; // Победитель соревнований - $c1.
	public static final int THE_GAME_ENDED_IN_A_TIE = 1498; // Ничья.
	public static final int YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S = 1499; // Через $s1 сек Вы переместитесь в город.
	public static final int YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER = 1500; // Вы не может участвовать в Олимпиаде персонажем с активированным подклассом.
	public static final int ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD = 1501; // В олимпиаде могут участвовать только дворяне.
	public static final int YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT = 1502; // Вы уже состоите в списке ожидающих соревнования.
	public static final int YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES = 1503; // Вы зарегистрировались на участие в классовых соревнованиях.
	public static final int YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES = 1504; // Вы зарегистрировались на участие во внеклассовых соревнованиях.
	public static final int YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME = 1505; // Вы удалили свою заявку из списка ожидающих соревнований.
	public static final int YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME = 1506; // Вы не состоите в списке ожидающих соревнования.
	public static final int THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT = 1507; // Невозможно надеть этот предмет на олимпиаде.
	public static final int THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT = 1508; // Невозможно использовать этот предмет на олимпиаде.
	public static final int THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT = 1509; // Невозможно использовать это умение на олимпиаде.
	public static final int S1_IS_MAKING_AN_ATTEMPT_AT_RESURRECTION_WITH_$S2_EXPERIENCE_POINTS_DO_YOU_WANT_TO_CONTINUE_WITH_THIS_RESURRECTION = 1510; // $c1 пробует восстановить утерянный опыт персонажа $s2. Вы согласны?
	public static final int WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER = 1511; // Во время воскрешения питомец не может помочь хозяину.
	public static final int WHILE_A_PETS_MASTER_IS_ATTEMPTING_TO_RESURRECT_THE_PET_CANNOT_BE_RESURRECTED_AT_THE_SAME_TIME = 1512; // Во время воскрешения хозяин не может помочь питомцу.
	public static final int BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED = 1513; // Предложение на воскрешение уже пришло.
	public static final int SINCE_THE_PET_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_ITS_MASTER_HAS_BEEN_CANCELLED = 1514; // Нельзя воскресить хозяина, так как в данный момент воскрешается питомец.
	public static final int SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED = 1515; // Нельзя воскресить питомца, так как в данный момент воскрешается хозяин.
	public static final int THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING = 1516; // На цель невозможно использовать семя.
	public static final int FAILED_IN_BLESSED_ENCHANT_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0 = 1517; // Благословенное улучшение не удалось. Заточка предмета стала 0.
	public static final int YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM = 1518; // Невозможно надеть этот предмет из-за того, что условия не соблюдены.
	public static final int THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS = 1519; // Питомец умер. Если не возродить питомца в течение 24 ч, то труп и все предметы исчезнут.
	public static final int SERVITOR_PASSED_AWAY = 1520; // Слуга умер.
	public static final int SERVITOR_DISAPPEASR_BECAUSE_THE_SUMMONING_TIME_IS_OVER = 1521; // Время использования слуги прошло. Вам нужно призвать еще одного.
	public static final int THE_CORPSE_DISAPPEARED_BECAUSE_MUCH_TIME_PASSED_AFTER_PET_DIED = 1522; // По прошествии долгого времени труп питомца исчезает.
	public static final int BECAUSE_PET_OR_SERVITOR_MAY_BE_DROWNED_WHILE_THE_BOAT_MOVES_PLEASE_RELEASE_THE_SUMMON_BEFORE_DEPARTURE = 1523; // Во время движения корабля питомец или слуга может упасть за борт и умереть. Пожалуйста, уберите ваших питомцев.
	public static final int PET_OF_S1_GAINED_S2 = 1524; // Питомец персонажа $c1 получил: $s2.
	public static final int PET_OF_S1_GAINED_S3_OF_S2 = 1525; // Питомец персонажа $c1 получил: $s2 ($s3 шт.)
	public static final int PET_OF_S1_GAINED__S2S3 = 1526; // Питомец персонажа $c1 получил: +$s2 $s3.
	public static final int PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY = 1527; // Ваш питомец проголодался и съел: $s1.
	public static final int A_FORCIBLE_PETITION_FROM_GM_HAS_BEEN_RECEIVED = 1528; // Вы послали Игровому мастеру заявку на беседу.
	public static final int S1_HAS_INVITED_YOU_TO_THE_COMMAND_CHANNEL_DO_YOU_WANT_TO_JOIN = 1529; // $c1 приглашает Вас в канал команды. Согласиться?
	public static final int SELECT_A_TARGET_OR_ENTER_THE_NAME = 1530; // Выберите цель или введите имя.
	public static final int ENTER_THE_NAME_OF_CLAN_AGAINST_WHICH_YOU_WANT_TO_MAKE_AN_ATTACK = 1531; // Введите название клана, с которым вы хотите начать войну.
	public static final int ENTER_THE_NAME_OF_CLAN_AGAINST_WHICH_YOU_WANT_TO_STOP_THE_WAR = 1532; // Введите название клана, с которым вы хотите прекратить войну.
	public static final int ATTENTION_S1_PICKED_UP_S2 = 1533; // $c1 поднимает: $s2.
	public static final int ATTENTION_S1_PICKED_UP__S2_S3 = 1534; // $c1 поднимает: +$s2 $s3.
	public static final int ATTENTION_S1_PET_PICKED_UP_S2 = 1535; // Питомец персонажа $c1 поднял: $s2.
	public static final int ATTENTION_S1_PET_PICKED_UP__S2_S3 = 1536; // Питомец персонажа $c1 поднял: +$s2 $s3.
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_RUNE_VILLAGE = 1537; // Текущее местоположение: $s1, $s2, $s3 (окрестности Руны)
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_GODDARD_CASTLE_TOWN = 1538; // Текущее местоположение: $s1, $s2, $s3 (окрестности Годдарда)
	public static final int CARGO_HAS_ARRIVED_AT_TALKING_ISLAND_VILLAGE = 1539; // Груз доставлен в Деревню Говорящего Острова.
	public static final int CARGO_HAS_ARRIVED_AT_DARK_ELVEN_VILLAGE = 1540; // Груз доставлен в Деревню Темных Эльфов.
	public static final int CARGO_HAS_ARRIVED_AT_ELVEN_VILLAGE = 1541; // Груз доставлен в Деревню Эльфов.
	public static final int CARGO_HAS_ARRIVED_AT_ORC_VILLAGE = 1542; // Груз доставлен в Деревню Орков.
	public static final int CARGO_HAS_ARRIVED_AT_DWARVEN_VILLAGE = 1543; // Груз доставлен в Деревню Гномов.
	public static final int CARGO_HAS_ARRIVED_AT_ADEN_CASTLE_TOWN = 1544; // Груз доставлен в Аден.
	public static final int CARGO_HAS_ARRIVED_AT_OREN_CASTLE_TOWN = 1545; // Груз доставлен в Орен.
	public static final int CARGO_HAS_ARRIVED_AT_HUNTERS_VILLAGE = 1546; // Груз доставлен в Деревню Охотников.
	public static final int CARGO_HAS_ARRIVED_AT_DION_CASTLE_TOWN = 1547; // Груз доставлен в Дион.
	public static final int CARGO_HAS_ARRIVED_AT_FLORAN_VILLAGE = 1548; // Груз доставлен в Флоран.
	public static final int CARGO_HAS_ARRIVED_AT_GLUDIN_VILLAGE = 1549; // Груз доставлен в Глудин.
	public static final int CARGO_HAS_ARRIVED_AT_GLUDIO_CASTLE_TOWN = 1550; // Груз доставлен в Глудио.
	public static final int CARGO_HAS_ARRIVED_AT_GIRAN_CASTLE_TOWN = 1551; // Груз доставлен в Гиран.
	public static final int CARGO_HAS_ARRIVED_AT_HEINE = 1552; // Груз доставлен в Хейн.
	public static final int CARGO_HAS_ARRIVED_AT_RUNE_VILLAGE = 1553; // Груз доставлен в Руну.
	public static final int CARGO_HAS_ARRIVED_AT_GODDARD_CASTLE_TOWN = 1554; // Груз доставлен в Годдард.
	public static final int DO_YOU_WANT_TO_CANCEL_CHARACTER_DELETION = 1555; // Вы хотите отменить удаление персонажа?
	public static final int NOTICE_HAS_BEEN_SAVED = 1556; // Ваше клановое приветствие было сохранено.
	public static final int SEED_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1557; // Цена семени: $s1 - $s2.
	public static final int THE_QUANTITY_OF_SEED_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1558; // Количество семян: $s1 - $s2.
	public static final int CROP_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2 = 1559; // Цена плодов: $s1 - $s2.
	public static final int THE_QUANTITY_OF_CROP_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2_ = 1560; // Количество плодов: $s1 - $s2.
	public static final int S1_CLAN_HAS_DECLARED_CLAN_WAR = 1561; // Клан $s1 объявил войну.
	public static final int CLAN_WAR_HAS_BEEN_DECLARED_AGAINST_S1_CLAN_IF_YOU_ARE_KILLED_DURING_THE_CLAN_WAR_BY_MEMBERS_OF_THE_OPPOSING_CLAN_THE_EXPERIENCE_PENALTY_WILL_BE_REDUCED_TO_1_4_OF_NORMAL = 1562; // Вы объявили войну клану $s1. С этого момента при смерти от противников из другого клана снимаемый опыт составит 1/4.
	public static final int S1_CLAN_CANT_MAKE_A_DECLARATION_OF_CLAN_WAR_SINCE_IT_HASNT_REACHED_THE_CLAN_LEVEL_OR_DOESNT_HAVE_ENOUGH_CLAN_MEMBERS = 1563; // У клана $s1 неподходящий уровень или слишком малое количество человек в клане. Объявить ему войну нельзя.
	public static final int A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER = 1564; // Вы можете объявлять войну только в том случае, если уровень вашего клана больше 3 и в игре находятся более 15 человек.
	public static final int THE_DECLARATION_OF_WAR_CANT_BE_MADE_BECAUSE_THE_CLAN_DOES_NOT_EXIST_OR_ACT_FOR_A_LONG_PERIOD = 1565; // Невозможно объявить войну этому клану. Этого клана не существует или он долгое время не принимает участия в игре.
	public static final int S1_CLAN_HAS_STOPPED_THE_WAR = 1566; // Клан $s1 принял решение остановить войну.
	public static final int THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED = 1567; // Война с кланом $s1 была остановлена.
	public static final int THE_TARGET_FOR_DECLARATION_IS_WRONG = 1568; // Неверная цель заявки.
	public static final int A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE = 1569; // Невозможно объявить войну клану, состоящему с вами в альянсе.
	public static final int A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME = 1570; // Невозможно объявить войну более чем 30 кланам одновременно.
	public static final int _ATTACK_LIST_ = 1571; // =======<ATTACK_LIST>=======
	public static final int _UNDER_ATTACK_LIST_ = 1572; // ======<UNDER_ATTACK_LIST>======
	public static final int THERE_IS_NO_ATTACK_CLAN = 1573; // Нет кланов, которым Вы объявили войну.
	public static final int THERE_IS_NO_UNDER_ATTACK_CLAN = 1574; // Нет кланов, которые объявили войну Вам.
	public static final int COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN = 1575; // Канал команды может быть создан только лидером группы, который также является главой клана уровнем не менее 5.
	public static final int PET_USES_THE_POWER_OF_SPIRIT = 1576; // Питомец использует силу духов.
	public static final int SERVITOR_USES_THE_POWER_OF_SPIRIT = 1577; // Слуга использует силу духов.
	public static final int ITEMS_ARE_NOT_AVAILABLE_FOR_A_PRIVATE_STORE_OR_PRIVATE_MANUFACTURE = 1578; // В личной торговой лавке и мастерской нельзя надевать предметы.
	public static final int S1_PET_GAINED_S2_ADENA = 1579; // Питомец персонажа $c1 получил $s2 аден.
	public static final int THE_COMMAND_CHANNEL_HAS_BEEN_FORMED = 1580; // Канал команды создан.
	public static final int THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED = 1581; // Канал команды расформирован.
	public static final int YOU_HAVE_PARTICIPATED_IN_THE_COMMAND_CHANNEL = 1582; // Вы присоединились к каналу команды.
	public static final int YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL = 1583; // Вас исключили из канала команды.
	public static final int S1_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL = 1584; // Группа персонажа $c1 была исключена из канала команды.
	public static final int THE_COMMAND_CHANNEL_HAS_BEEN_DEACTIVATED = 1585; // Канал команды расформирован.
	public static final int YOU_HAVE_QUIT_THE_COMMAND_CHANNEL = 1586; // Вы покинули канал команды.
	public static final int S1_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL = 1587; // Группа персонажа $c1 покинула канал команды.
	public static final int THE_COMMAND_CHANNEL_IS_ACTIVATED_ONLY_IF_AT_LEAST_FIVE_PARTIES_PARTICIPATE_IN = 1588; // Канал команды активируется только в том случае, если в нем находится более 5 групп.
	public static final int COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_S1 = 1589; // Полномочия по каналу команды перешли к персонажу $c1.
	public static final int _COMMAND_CHANNEL_INFO_TOTAL_PARTIES_S1_ = 1590; // ===<COMMAND_CHANNEL_INFO(TOTAL_PARTIES_S1)>===
	public static final int NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL = 1591; // Нет персонажей, приглашенных в канал команды.
	public static final int YOU_CANT_OPEN_COMMAND_CHANNELS_ANY_MORE = 1592; // Вы больше не можете создавать канал команды.
	public static final int YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL = 1593; // У Вас нет прав принимать персонажей в канал команды.
	public static final int S1_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL = 1594; // Группа персонажа $c1 уже состоит в канале команды.
	public static final int S1_HAS_SUCCEEDED = 1595; // Вы успешно использовали умение $1s.
	public static final int HIT_BY_S1 = 1596; // На Вас воздействовало умение $s1.
	public static final int S1_HAS_FAILED = 1597; // Вам не удалось использовать умение $s1.
	public static final int WHEN_PET_OR_SERVITOR_IS_DEAD_SOULSHOTS_OR_SPIRITSHOTS_FOR_PET_OR_SERVITOR_ARE_NOT_AVAILABLE = 1598; // Если питомец/слуга умер, нельзя использовать Заряды Души и Духа.
	public static final int WATCHING_IS_IMPOSSIBLE_DURING_COMBAT = 1599; // Нельзя перейти в режим зрителя во время боя.
	public static final int TOMORROWS_ITEMS_WILL_ALL_BE_SET_TO_0__DO_YOU_WISH_TO_CONTINUE = 1600; // Завтра все цены будут изменены на 0. Продолжить?
	public static final int TOMORROWS_ITEMS_WILL_ALL_BE_SET_TO_THE_SAME_VALUE_AS_TODAYS_ITEMS__DO_YOU_WISH_TO_CONTINUE = 1601; // Завтра все цены будут равны сегодняшним. Продолжить?
	public static final int ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL = 1602; // Общаться в канале команды могут только лидеры групп.
	public static final int ONLY_CHANNEL_OPENER_CAN_GIVE_ALL_COMMAND = 1603; // Все команды может использовать только создатель канала.
	public static final int WHILE_DRESSED_IN_FORMAL_WEAR_YOU_CANT_USE_ITEMS_THAT_REQUIRE_ALL_SKILLS_AND_CASTING_OPERATIONS = 1604; // В свадебных нарядах нельзя использовать предметы, которые задействуют все умения и магию.
	public static final int _HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR = 1605; // *Здесь можно купить только семена владения $s1.
	public static final int YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS = 1606; // Поздравляем! Вы завершили квест на третью смену профессии.
	public static final int S1_ADENA_HAS_BEEN_PAID_FOR_PURCHASING_FEES = 1607; // Налог аден с покупки: $s1.
	public static final int YOU_CANT_BUY_ANOTHER_CASTLE_SINCE_ADENA_IS_NOT_SUFFICIENT = 1608; // Не хватает аден, чтобы купить другой замок.
	public static final int THE_DECLARATION_OF_WAR_HAS_BEEN_ALREADY_MADE_TO_THE_CLAN = 1609; // Этот клан уже находится в состоянии войны.
	public static final int FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN = 1610; // Вы не можете объявить войну своему же клану.
	public static final int PARTY_LEADER_S1 = 1611; // Лидер группы: $c1
	public static final int _WAR_LIST_ = 1612; // =====<WAR_LIST>=====
	public static final int THERE_IS_NO_CLAN_LISTED_ON_WAR_LIST = 1613; // Нет кланов, состоящих в списке войн.
	public static final int YOU_ARE_PARTICIPATING_IN_THE_CHANNEL_WHICH_HAS_BEEN_ALREADY_OPENED = 1614; // Вы присоединены к каналу.
	public static final int THE_NUMBER_OF_REMAINING_PARTIES_IS_S1_UNTIL_A_CHANNEL_IS_ACTIVATED = 1615; // Осталось групп до активации канала: $s1.
	public static final int THE_COMMAND_CHANNEL_HAS_BEEN_ACTIVATED = 1616; // Канал команды активирован.
	public static final int YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL = 1617; // У вас нет прав пользования каналом команды.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_TO_GLUDIN_HARBOR_HAS_BEEN_DELAYED = 1618; // Корабль, отправляющийся из гавани Руны в гавань Глудина, задерживается.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_TO_RUNE_HARBOR_HAS_BEEN_DELAYED = 1619; // Корабль, отправляющийся из гавани Глудина в гавань Руны, задерживается.
	public static final int ARRIVED_AT_RUNE_HARBOR = 1620; // Корабль прибыл в гавань Руны.
	public static final int DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_FIVE_MINUTES = 1621; // Отплытие в Руну состоится через 5 минут.
	public static final int DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_ONE_MINUTE = 1622; // Отплытие в Руну состоится через 1 минуту.
	public static final int MAKE_HASTE__WE_WILL_BE_DEPARTING_FOR_GLUDIN_HARBOR_SHORTLY = 1623; // Корабль из Руны в Глудин скоро отплывает.
	public static final int WE_ARE_NOW_DEPARTING_FOR_GLUDIN_HARBOR__HOLD_ON_AND_ENJOY_THE_RIDE = 1624; // Корабль из Руны в Глудин отплывает.
	public static final int WILL_LEAVE_FOR_RUNE_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES = 1625; // Через 10 минут корабль отплывает в гавань Руны.
	public static final int WILL_LEAVE_FOR_RUNE_HARBOR_IN_FIVE_MINUTES = 1626; // Отплытие в гавань Руны состоится через 5 минут.
	public static final int WILL_LEAVE_FOR_RUNE_HARBOR_IN_ONE_MINUTE = 1627; // Отплытие в гавань Руны состоится через 1 минуту.
	public static final int LEAVING_SOON_FOR_RUNE_HARBOR = 1628; // Корабль из гавани Руны в гавань Глудина скоро отплывает.
	public static final int LEAVING_FOR_RUNE_HARBOR = 1629; // Корабль из гавани Руны в гавань Глудина отплывает.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_15_MINUTES = 1630; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 15 минут.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES = 1631; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 10 минут.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES = 1632; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 5 минут.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE = 1633; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 1 минуту.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_15_MINUTES = 1634; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 15 минут.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_10_MINUTES = 1635; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 10 минут.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_5_MINUTES = 1636; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 5 минут.
	public static final int THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_1_MINUTE = 1637; // Корабль, отплывший из гавани Руны, прибудет в гавань Глудина через 1 минуту.
	public static final int YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE = 1638; // Нельзя рыбачить во режимах личной торговой лавки, мастерской и книги рецептов.
	public static final int OLYMPIAD_PERIOD_S1_HAS_STARTED = 1639; // $s1 период Олимпиады начался.
	public static final int OLYMPIAD_PERIOD_S1_HAS_ENDED = 1640; // $s1 период Олимпиады закончился.
	public static final int THE_OLYMPIAD_GAME_HAS_STARTED = 1641; // Олимпиада началась.
	public static final int THE_OLYMPIAD_GAME_HAS_ENDED = 1642; // Олимпиада окончена.
	public static final int CURRENT_LOCATION_S1_S2_S3_DIMENSION_GAP = 1643; // Текущее местоположение: $s1, $s2, $s3 (Разлом Между Мирами)
	public static final int NONE_1644 = 1644; // Вы играете: $s1 ч $s2 мин. Необходимо отдохнуть: $s3 ч $s4 мин.
	public static final int NONE_1645 = 1645; // Если Вы находитесь в игре больше 3 ч, Вас ожидает наказание, поэтому выйдите из игры и отдохните немного.
	public static final int NONE_1646 = 1646; // Если Вы в игре больше 3 ч, получаемый опыт и шанс выпадения предметов сокращаются вдвое, поэтому выйдите из игры и отдохните немного.
	public static final int NONE_1647 = 1647; // Если Вы в игре больше 5 ч, то перестаете получать опыт и предметы, поэтому выйдите из игры и отдохните немного.
	public static final int NONE_1648 = 1648; // При нахождении в мирной зоне время все равно идет.
	public static final int PLAY_TIME_IS_NOW_ACCUMULATING = 1649; // Идет подсчет игрового времени.
	public static final int DUE_TO_A_LARGE_NUMBER_OF_USERS_CURRENTLY_ACCESSING_OUR_SERVER_YOUR_LOGIN_ATTEMPT_HAS_FAILED_PLEASE_WAIT_A_LITTLE_WHILE_AND_ATTEMPT_TO_LOG_IN_AGAIN = 1650; // Сервер переполнен. Пожалуйста, попробуйте зайти позже.
	public static final int THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS = 1651; // На данный момент Олимпиада не ведется.
	public static final int THE_VIDEO_RECORDING_OF_THE_REPLAY_WILL_NOW_BEGIN = 1652; // Вы начали запись игры.
	public static final int THE_REPLAY_FILE_HAS_BEEN_STORED_SUCCESSFULLY_S1 = 1653; // Записанный файл успешно сохранен ($s1).
	public static final int THE_ATTEMPT_TO_RECORD_THE_REPLAY_FILE_HAS_FAILED = 1654; // Не удалось записать игру.
	public static final int YOU_HAVE_CAUGHT_A_MONSTER = 1655; // Вы поймали монстра!
	public static final int YOU_HAVE_SUCCESSFULLY_TRADED_THE_ITEM_WITH_THE_NPC = 1656; // Обмен с NPC успешно завершен.
	public static final int C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES = 1657; // $c1 - получено баллов Олимпиады: $s2.
	public static final int C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES = 1658; // $c1 - потеряно баллов Олимпиады: $s2.
	public static final int CURRENT_LOCATION_S1_S2_S3_CEMETERY_OF_THE_EMPIRE = 1659; // Текущее местоположение: $s1, $s2, $s3 (Кладбище Империи)
	public static final int THE_CHANNEL_WAS_OPENED_BY_S1 = 1660; // Создатель канала: $c1.
	public static final int S1_HAS_OBTAINED_S3_S2S = 1661; // $c1 получает $s2 ($s3 шт.)
	public static final int IF_YOU_FISH_IN_ONE_SPOT_FOR_A_LONG_TIME_THE_SUCCESS_RATE_OF_A_FISH_TAKING_THE_BAIT_BECOMES_LOWER__PLEASE_MOVE_TO_ANOTHER_PLACE_AND_CONTINUE_YOUR_FISHING_THERE = 1662; // Шанс поймать рыбу уменьшается, если оставаться на одном месте. Попробуйте порыбачить в другом месте.
	public static final int THE_CLANS_EMBLEM_WAS_SUCCESSFULLY_REGISTERED__ONLY_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_A_CASTLE_CAN_GET_THEIR_EMBLEM_DISPLAYED_ON_CLAN_RELATED_ITEMS = 1663; // Эмблема клана была успешно зарегистрирована.
	public static final int BECAUSE_THE_FISH_IS_RESISTING_THE_FLOAT_IS_BOBBING_UP_AND_DOWN_A_LOT = 1664; // Рыба сопротивляется, и поплавок сильно дергается.
	public static final int SINCE_THE_FISH_IS_EXHAUSTED_THE_FLOAT_IS_MOVING_ONLY_SLIGHTLY = 1665; // Рыба устала и почти не сопротивляется.
	public static final int YOU_HAVE_OBTAINED__S1_S2 = 1666; // Вы получили +$s1 $s2.
	public static final int LETHAL_STRIKE = 1667; // Смертельный удар!
	public static final int YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL = 1668; // Ваш смертельный удар прошел успешно!
	public static final int THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT = 1669; // Изменение предмета не удалось.
	public static final int SINCE_THE_SKILL_LEVEL_OF_REELING_PUMPING_IS_HIGHER_THAN_THE_LEVEL_OF_YOUR_FISHING_MASTERY_A_PENALTY_OF_S1_WILL_BE_APPLIED = 1670; // Умение «Подсечь» («Подтянуть») на 3 или более уровня выше умения рыбной ловли.
	public static final int YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_ = 1671; // Ваше подсекание прошло успешно! (штраф: $ s1)
	public static final int YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_ = 1672; // Ваше подтягивание прошло успешно! (штраф: $ s1)
	public static final int THE_CURRENT_FOR_THIS_OLYMPIAD_IS_S1_WINS_S2_DEFEATS_S3_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS = 1673; // Ваш результат в Олимпиаде: матчей - $s1, побед - $s2, поражений - $s3. Получено баллов Олимпиады - $s4.
	public static final int THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE = 1674; // Эту команду может использовать только дворянин.
	public static final int A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM = 1675; // Владение не может быть установлено c 6:00 до 20:00.
	public static final int SINCE_A_SERVITOR_OR_A_PET_DOES_NOT_EXIST_AUTOMATIC_USE_IS_NOT_APPLICABLE = 1676; // У Вас нет слуги или питомца. Автоматические функции недоступны.
	public static final int A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE = 1677; // Невозможно отменить войну из-за того, что в данный момент члены клана вовлечены в битву.
	public static final int YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_TO_S1_CLAN = 1678; // Вы не объявляли войну клану $s1.
	public static final int ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND = 1679; // Все команды может использовать только создатель канала.
	public static final int S1_HAS_DECLINED_THE_CHANNEL_INVITATION = 1680; // $c1 отклоняет приглашение в канал.
	public static final int SINCE_S1_DID_NOT_RESPOND_YOUR_CHANNEL_INVITATION_HAS_FAILED = 1681; // $c1 не отвечает. Приглашение в канал не удалось.
	public static final int ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND = 1682; // Исключить из канала может только создатель канала.
	public static final int ONLY_A_PARTY_LEADER_CAN_CHOOSE_THE_OPTION_TO_LEAVE_A_CHANNEL = 1683; // Покинуть канал может только лидер группы.
	public static final int WHILE_A_CLAN_IS_BEING_DISSOLVED_IT_IS_IMPOSSIBLE_TO_DECLARE_A_CLAN_WAR_AGAINST_IT = 1684; // Нельзя объявлять войну, если ваш клан находится в стадии расформирования.
	public static final int IF_YOUR_PK_COUNT_IS_1_OR_MORE_YOU_ARE_NOT_ALLOWED_TO_WEAR_THIS_ITEM = 1685; // Невозможно надеть этот предмет, если PK > 0.
	public static final int THE_CASTLE_WALL_HAS_SUSTAINED_DAMAGE = 1686; // Стены замка разрушены.
	public static final int THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE = 1687; // В этой зоне нельзя летать на виверне. Если вы останетесь на этой территории, вам необходимо спешиться с виверны.
	public static final int YOU_CANNOT_PRACTICE_ENCHANTING_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_MANUFACTURING_WORKSHOP = 1688; // Нельзя улучшать предметы во время торговли.
	public static final int YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS = 1689; // $c1 уже состоит в списке ожидающих соревнования между представителями одной профессии.
	public static final int YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME = 1690; // $c1 уже состоит в списке ожидающих внеклассовые соревнования.
	public static final int SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD = 1691; // $c1 не может участвовать в Олимпиаде, так как ячейки инвентаря заполнены на 80%.
	public static final int SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD = 1692; // $c1 не может участвовать в Олимпиаде, так как класс изменен на подкласс.
	public static final int WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME = 1693; // Вы не можете наблюдать за Олимпиадой, когда вы состоите в списке участников.
	public static final int ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR = 1694; // Во время войны это может использовать только глава клана и только если он является дворянином.
	public static final int IT_CAN_BE_USED_ONLY_WHILE_A_SIEGE_WAR_IS_TAKING_PLACE = 1695; // Вы можете использовать это только при осаде замка.
	public static final int IF_THE_ACCUMULATED_ONLINE_ACCESS_TIME_IS_S1_OR_MORE_A_PENALTY_WILL_BE_IMPOSED__PLEASE_TERMINATE_YOUR_SESSION_AND_TAKE_A_BREAK = 1696; // none
	public static final int SINCE_YOUR_CUMULATIVE_ACCESS_TIME_HAS_EXCEEDED_S1_YOUR_EXP_AND_ITEM_DROP_RATE_WERE_REDUCED_BY_HALF_PLEASE_TERMINATE_YOUR_SESSION_AND_TAKE_A_BREAK = 1697; // none
	public static final int SINCE_YOUR_CUMULATIVE_ACCESS_TIME_HAS_EXCEEDED_S1_YOU_NO_LONGER_HAVE_EXP_OR_ITEM_DROP_PRIVILEGE__PLEASE_TERMINATE_YOUR_SESSION_AND_TAKE_A_BREAK = 1698; // none
	public static final int YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE = 1699; // Вы не можете исключить персонажа из группы.
	public static final int YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PET_SERVITOR = 1700; // Не хватает зарядов духа для питомца/слуги.
	public static final int YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PET_SERVITOR = 1701; // Не хватает зарядов души для питомца/слуги.
	public static final int THE_USER_WHO_CONDUCTED_A_SEARCH_A_MOMENT_AGO_HAS_BEEN_CONFIRMED_TO_BE_A_BOT_USER = 1702; // Персонаж $s1 проверен и признан ботом.
	public static final int THE_USER_WHO_CONDUCTED_A_SEARCH_A_MOMENT_AGO_HAS_BEEN_CONFIRMED_TO_BE_A_NONBOT_USER = 1703; // Персонаж $s1 проверен - бот не выявлен.
	public static final int PLEASE_CLOSE_THE_SETUP_WINDOW_FOR_A_PRIVATE_MANUFACTURING_STORE_OR_THE_SETUP_WINDOW_FOR_A_PRIVATE_STORE_AND_TRY_AGAIN = 1704; // Пожалуйста, закройте окно настроек частной торговой лавки/мастерской и повторите попытку.
	// Pc Bang Points
	public static final int PC_BANG_POINTS_ACQUISITION_PERIOD_PONTS_ACQUISITION_PERIOD_LEFT_S1_HOUR = 1705; // Период приобретения очков PC Bang. Он продлится $s1 ч.
	public static final int PC_BANG_POINTS_USE_PERIOD_POINTS_USE_PERIOD_LEFT_S1_HOUR = 1706; // Период использования очков PC Bang. Он продлится $s1 ч.
	public static final int YOU_ACQUIRED_S1_PC_BANG_POINT = 1707; // Получено очков PC Bang: $s1.
	public static final int DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT = 1708; // Очки удваиваются! Получено очков PC Bang: $s1.
	public static final int YOU_ARE_USING_S1_POINT = 1709; // Используется очков: $s1.
	public static final int YOU_ARE_SHORT_OF_ACCUMULATED_POINTS = 1710; // У Вас недостаточно очков.
	public static final int PC_BANG_POINTS_USE_PERIOD_HAS_EXPIRED = 1711; // Период использования очков PC Bang истек.
	public static final int THE_PC_BANG_POINTS_ACCUMULATION_PERIOD_HAS_EXPIRED = 1712; // Период приобретения очков PC Bang истек.
	public static final int THE_MATCH_MAY_BE_DELAYED_DUE_TO_NOT_ENOUGH_COMBATANTS = 1713; // Из-за нехватки противников соревнование может начаться позднее.
	public static final int THIS_IS_A_PEACEFUL_ZONE__N__PVP_IS_NOT_ALLOWED_IN_THIS_AREA = 1715; // Мирная Зона \\n- PvP запрещено.
	public static final int ALTERED_ZONE = 1716; // Измененная Зона
	public static final int SIEGE_WAR_ZONE___N__A_SIEGE_IS_CURRENTLY_IN_PROGRESS_IN_THIS_AREA____N_IF_A_CHARACTER_DIES_IN = 1717; // Зона Осады Замка \\n- Сейчас проходит осада замка. \\n- Использование умения воскрешения может быть ограничено.
	public static final int GENERAL_FIELD = 1718; // Обычная территория
	public static final int SEVEN_SIGNS_ZONE___N__ALTHOUGH_A_CHARACTER_S_LEVEL_MAY_INCREASE_WHILE_IN_THIS_AREA_HP_AND_MP___N = 1719; // Зона Семи Печатей\\n- Возможно поднять уровень, но HP и MP\\n не восстанавливаются.
	public static final int ___ = 1720; // ---
	public static final int COMBAT_ZONE = 1721; // Боевая Зона
	public static final int PLEASE_ENTER_THE_NAME_OF_THE_ITEM_YOU_WISH_TO_SEARCH_FOR = 1722; // Введите название предмета, который вы хотите найти.
	public static final int PLEASE_TAKE_A_MOMENT_TO_PROVIDE_FEEDBACK_ABOUT_THE_PETITION_SERVICE = 1723; // Пожалуйста, прокомментируйте качество обслуживания службы петиций.
	public static final int A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DE_ACTIVATED = 1724; // Если слуга вовлечен в битву, то его нельзя отозвать.
	public static final int YOU_HAVE_EARNED_S1_RAID_POINTS = 1725; // Получено рейдовых очков: $s1.
	public static final int S1_HAS_DISAPPEARED_BECAUSE_ITS_TIME_PERIOD_HAS_EXPIRED = 1726; // $s1 исчезает из-за окончания срока действия.
	public static final int C1_HAS_INVITED_YOU_TO_A_PARTY_ROOM_DO_YOU_ACCEPT = 1727; // $c1 приглашает Вас в комнату группы. Принять?
	public static final int THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION = 1728; // Нет ответа. Ваше приглашение в канал поиска группы было отменено.
	public static final int YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING = 1729; // Вы не можете присоединиться к каналу команды во время телепортации.
	public static final int TO_ESTABLISH_A_CLAN_ACADEMY_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER = 1730; // Для создания академии клана нужно, чтобы уровень клана был не меньше 5.
	public static final int ONLY_THE_CLAN_LEADER_CAN_CREATE_A_CLAN_ACADEMY = 1731; // Академию клана может создать только глава клана.
	public static final int TO_CREATE_A_CLAN_ACADEMY_A_BLOOD_MARK_IS_NEEDED = 1732; // Для создания академии нужен предмет Метка Крови.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_CREATE_A_CLAN_ACADEMY = 1733; // Не хватает аден для создания академии клана.
	public static final int TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER = 1734; // Для вступления в академию клана персонажи должны быть менее 40 уровня и не иметь второй профессии.
	public static final int S1_DOES_NOT_MEET_THE_REQUIREMENTS_TO_JOIN_A_CLAN_ACADEMY = 1735; // $s1 не соответствует условиям вступления в Академию клана.
	public static final int THE_CLAN_ACADEMY_HAS_REACHED_ITS_MAXIMUM_ENROLLMENT = 1736; // Максимально допустимое кол-во персонажей в академии достигнуто. Больше нельзя принять персонажей в академию.
	public static final int YOUR_CLAN_HAS_NOT_ESTABLISHED_A_CLAN_ACADEMY_BUT_IS_ELIGIBLE_TO_DO_SO = 1737; // В Вашем клане нет академии клана. Вы можете создать академию.
	public static final int YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY = 1738; // В Вашем клане уже имеется академия клана.
	public static final int WOULD_YOU_LIKE_TO_CREATE_A_CLAN_ACADEMY = 1739; // Вы желаете создать академию клана?
	public static final int PLEASE_ENTER_THE_NAME_OF_THE_CLAN_ACADEMY = 1740; // Введите название академии клана.
	public static final int CONGRATULATIONS_THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED = 1741; // Поздравляем! Академия клана $s1 создана.
	public static final int A_MESSAGE_INVITING_S1_TO_JOIN_THE_CLAN_ACADEMY_IS_BEING_SENT = 1742; // $s1: приглашение на вступление в академию клана отправлено.
	public static final int TO_OPEN_A_CLAN_ACADEMY_THE_LEADER_OF_A_LEVEL_5_CLAN_OR_ABOVE_MUST_PAY_XX_PROOFS_OF_BLOOD_OR_A_CERTAIN_AMOUNT_OF_ADENA = 1743; // Для создания академии клана нужно, чтобы уровень клана был не меньше 5 и глава клана должен заплатить XX Меток Крови или определенную сумму аден.
	public static final int THERE_WAS_NO_RESPONSE_TO_YOUR_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_SO_THE_INVITATION_HAS_BEEN_RESCINDED = 1744; // Нет ответа. Ваше приглашение на вступление в академию клана было отменено.
	public static final int THE_RECIPIENT_OF_YOUR_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_HAS_DECLINED = 1745; // Ваше приглашение на вступлению в академию было отклонено.
	public static final int YOU_HAVE_ALREADY_JOINED_A_CLAN_ACADEMY = 1746; // Вы уже состоите в академии клана.
	public static final int S1_HAS_SENT_YOU_AN_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_BELONGING_TO_THE_S2_CLAN_DO_YOU_ACCEPT = 1747; // $s1 присылает Вам приглашение на вступление в Академию клана $s2. Принять?
	public static final int CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS = 1748; // $s1 из Академии клана успешно получил 2-ю профессию. Клан получил очки репутации ($s2).
	public static final int CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES = 1749; // Поздравляем! Вы закончили академию и будете выпущены из клана. Выпускники академии могут сразу же вступить в клан.
	public static final int C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_THE_OWNER_OF_S2_CANNOT_PARTICIPATE_IN_THE = 1750; // $c1 не может участвовать в соревнованиях, так как обладатель территорией $s2 не может участвовать в Олимпиаде.
	public static final int THE_GRAND_MASTER_HAS_GIVEN_YOU_A_COMMEMORATIVE_ITEM = 1751; // Великий Мастер подарил Вам памятный предмет.
	public static final int SINCE_THE_CLAN_HAS_RECEIVED_A_GRADUATE_OF_THE_CLAN_ACADEMY_IT_HAS_EARNED_S1_POINTS_TOWARD_ITS_REPUTATION_SCORE = 1752; // Приняв выпускника академии в клан, Вы получаете $s1 очков репутации клана.
	public static final int THE_CLAN_LEADER_HAS_DECREED_THAT_THAT_PARTICULAR_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY = 1753; // Невозможно уполномочить этими правами ученика академии, так как глава клана ограничил передачу этих прав.
	public static final int THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER = 1754; // Невозможно уполномочить этими правами ученика академии.
	public static final int S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1 = 1755; // $s1 берет в ученики персонажа $s2.
	public static final int S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_IN = 1756; // Ученик академии $c1 зашел в игру.
	public static final int S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT = 1757; // Ученик академии $c1 вышел из игры.
	public static final int S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_IN = 1758; // Наставник академии $c1 зашел в игру.
	public static final int S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT = 1759; // Наставник академии $c1 вышел из игры.
	public static final int CLAN_MEMBER_S1S_TITLE_HAS_BEEN_CHANGED_TO_S2 = 1760; // $c1: титул изменен на $s2.
	public static final int CLAN_MEMBER_S1S_PRIVILEGE_LEVEL_HAS_BEEN_CHANGED_TO_S2 = 1761; // $c1: полномочия изменены на $s2.
	public static final int YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE = 1762; // У Вас нет прав исключать учеников.
	public static final int S2_CLAN_MEMBER_S1S_APPRENTICE_HAS_BEEN_REMOVED = 1763; // $s2, ученик персонажа $c1, удаляется.
	public static final int THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY = 1764; // Этот предмет может надеть только ученик академии.
	public static final int AS_A_GRADUATE_OF_THE_CLAN_ACADEMY_YOU_CAN_NO_LONGER_WEAR_THIS_ITEM = 1765; // Выпускники академии не могут надевать этот предмет.
	public static final int AN_APPLICATION_TO_JOIN_THE_CLAN_HAS_BEEN_SENT_TO_S1_IN_S2 = 1766; // Запрос на вступление в клан был отправлен персонажу $c1 в $s2.
	public static final int AN_APPLICATION_TO_JOIN_THE_CLAN_ACADEMY_HAS_BEEN_SENT_TO_S1 = 1767; // $c1: отправлен запрос на вступление в Академию клана.
	public static final int C1_HAS_INVITED_YOU_TO_JOIN_THE_CLAN_ACADEMY_OF_S2_CLAN_WOULD_YOU_LIKE_TO_JOIN = 1768; // $c1 приглашает Вас вступить в Академию клана $s2. Принять?
	public static final int C1_HAS_SENT_YOU_AN_INVITATION_TO_JOIN_THE_S3_ORDER_OF_KNIGHTS_UNDER_THE_S2_CLAN_WOULD_YOU_LIKE = 1769; // $c1 из клана $s2 приглашает Вас вступить в Орден Рыцарей $s3.
	public static final int THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_BELOW_0_THE_CLAN_MAY_FACE_CERTAIN_PENALTIES_AS_A_RESULT = 1770; // При снижении очков репутации ниже 0 на клан могут быть наложены некоторые санкции.
	public static final int NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS = 1771; // Очки репутации клана можно копить только с 5 уровня клана.
	public static final int SINCE_YOUR_CLAN_WAS_DEFEATED_IN_A_SIEGE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE_AND_GIVEN_TO_THE_OPPOSING_CLAN = 1772; // Ваш клан проиграл осаду замка. Потеряно очков клана: $s1.
	public static final int SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE = 1773; // Ваш клан выиграл осаду замка. Получено очков клана: $s1.
	public static final int YOUR_CLAN_NEWLY_ACQUIRED_CONTESTED_CLAN_HALL_HAS_ADDED_S1_POINTS_TO_YOUR_CLAN_REPUTATION_SCORE = 1774; // Ваш клан заново завоевал холл клана. Получено очков клана: $s1.
	public static final int CLAN_MEMBER_S1_WAS_AN_ACTIVE_MEMBER_OF_THE_HIGHEST_RANKED_PARTY_IN_THE_FESTIVAL_OF_DARKNESS_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE = 1775; // Член клана $c1был участником группы высшего ранга Фестиваля Тьмы. Получено очков клана: $s2.
	public static final int CLAN_MEMBER_S1_WAS_NAMED_A_HERO_2S_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE = 1776; // Член клана получил статус героя. Получено очков клана: $2s.
	public static final int YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE = 1777; // Вы успешно выполнили клановый квест. Получено очков клана: $s1.
	public static final int AN_OPPOSING_CLAN_HAS_CAPTURED_YOUR_CLAN_CONTESTED_CLAN_HALL_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE = 1778; // Враждебный клан захватил ваш спорный холл. Потеряно очков клана: $s1.
	public static final int AFTER_LOSING_THE_CONTESTED_CLAN_HALL_300_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE = 1779; // После потери спорного холла клана 300 очков будет вычтено из репутации вашего клана.
	public static final int YOUR_CLAN_HAS_CAPTURED_YOUR_OPPONENT_CONTESTED_CLAN_HALL_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENT_CLAN_REPUTATION_SCORE = 1780; // Ваш клан захватил спорный холл враждебного клана. Потеряно очков клана противника: $s1.
	public static final int YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE = 1781; // Получено очков клана: $1s.
	public static final int YOUR_CLAN_MEMBER_S1_WAS_KILLED_S2_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENT_CLAN_REPUTATION_SCORE = 1782; // Член Вашего клана $c1 был убит. Потеряно очков клана и добавлено противнику: $s2.
	public static final int FOR_KILLING_AN_OPPOSING_CLAN_MEMBER_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENTS_CLAN_REPUTATION_SCORE = 1783; // За убийство члена враждебного клана из клановой репутации противника вычтено очков клана: $s1.
	public static final int YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS = 1784; // Ваш клан не смог защитить замок. Потеряно очков клана и добавлено противнику: $s1.
	public static final int THE_CLAN_YOU_BELONG_TO_HAS_BEEN_INITIALIZED_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE = 1785; // Ваш клан был возвращен в исходное состояние. Потеряно очков клана: $s1.
	public static final int YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE = 1786; // Ваш клан не смог защитить замок. Потеряно очков клана: $s1.
	public static final int S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_REPUTATION_SCORE = 1787; // Потеряно очков клана: $1s.
	public static final int THE_CLAN_SKILL_S1_HAS_BEEN_ADDED = 1788; // Добавлено клановое умение: $s1.
	public static final int SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED = 1789; // С момента падения клановой репутации до 0 и ниже ваши клановые умения будут заблокированы.
	public static final int THE_CONDITIONS_NECESSARY_TO_INCREASE_THE_CLAN_LEVEL_HAVE_NOT_BEEN_MET = 1790; // Условия, необходимые для поднятия кланового уровня, не соблюдены.
	public static final int THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET = 1791; // Условия, необходимые для создания боевого подразделения, не соблюдены.
	public static final int PLEASE_ASSIGN_A_MANAGER_FOR_YOUR_NEW_ORDER_OF_KNIGHTS = 1792; // Пожалуйста, назначьте магистра для вашего нового Рыцарского Ордена.
	public static final int S1_HAS_BEEN_SELECTED_AS_THE_CAPTAIN_OF_S2 = 1793; // $c1 теперь капитан $s2.
	public static final int THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED = 1794; // Сформирован отряд Рыцарей $s1.
	public static final int THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED = 1795; // Сформирован отряд Королевской Стражи $s1.
	public static final int FOR_KOREA_ONLY = 1796; // Ваш аккаунт временно заблокирован за воровство аккаунтов или другую вредоносную деятельность. За дополнительной информацией обращайтесь в Центр Поддержки.
	public static final int C1_HAS_BEEN_PROMOTED_TO_S2 = 1797; // $c1 повышается до $s2.
	public static final int CLAN_LORD_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1 = 1798; // $c1 получает полномочия лидера клана.
	public static final int CURRENTLY_UNDER_INVESTIGATION_PLEASE_WAIT = 1799; // Идет поиск ботов. Пожалуйста, повторите попытку позже.
	public static final int THE_USER_NAME_S1_HAS_A_HISTORY_OF_USING_THIRD_PARTY_PROGRAMS = 1800; // $c1 обвиняется в использовании бота.
	public static final int THE_ATTEMPT_TO_SELL_HAS_FAILED = 1801; // Попытка продать не удалась.
	public static final int THE_ATTEMPT_TO_TRADE_HAS_FAILED = 1802; // Попытка торговли не удалась.
	public static final int YOU_CANNOT_REGISTER_FOR_A_MATCH = 1803; // Запрос участия в игре может быть сделан не ранее, чем через 10 мин после окончания игры.
	public static final int THIS_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_7_DAYS = 1804; // Ваш аккаунт заблокирован на 7 дней, так как он был замечен в нелегальных денежных сделках. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int THIS_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_30_DAYS_1 = 1805; // Ваш аккаунт заблокирован на 30 дней, так как он был повторно замечен в нелегальных денежных сделках. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int THIS_ACCOUNT_HAS_BEEN_PERMANENTLY_BANNED_1 = 1806; // Ваш аккаунт бессрочно заблокирован, так как он был в третий раз замечен в нелегальных денежных сделках. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int THIS_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_30_DAYS_2 = 1807; // Ваш аккаунт заблокирован на 30 дней, так как вы замешаны в нелегальных денежных операциях. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int THIS_ACCOUNT_HAS_BEEN_PERMANENTLY_BANNED_2 = 1808; // Ваш аккаунт бессрочно заблокирован, так как вы замешаны в нелегальных денежных операциях. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int ACCOUNT_OWNER_MUST_BE_VERIFIED_IN_ORDER_TO_USE_THIS_ACCOUNT_AGAIN = 1809; // Ваш аккаунт, вероятно, проверяется. За информацией о процедуре проверки аккаунтов обращайтесь в Центр Поддержки.
	public static final int THE_REFUSE_INVITATION_STATE_HAS_BEEN_ACTIVATED = 1810; // Статус отказа от приглашений активирован.
	public static final int THE_REFUSE_INVITATION_STATE_HAS_BEEN_REMOVED = 1811; // Статус отказа от приглашений дезактивирован.
	public static final int SINCE_THE_REFUSE_INVITATION_STATE_IS_CURRENTLY_ACTIVATED_NO_INVITATION_CAN_BE_MADE = 1812; // С момента активации статуса отказа от приглашений не могут быть сделаны приглашения.
	public static final int THERE_IS_S1_HOUR_AND_S2_MINUTE_LEFT_OF_THE_FIXED_USAGE_TIME = 1813; // $s1: осталось $s2 ч времени использования.
	public static final int S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1 = 1814; // $s1: осталось $s2 мин времени использования.
	public static final int S2_WAS_DROPPED_IN_THE_S1_REGION = 1815; // $s2 появился в районе $s1.
	public static final int THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION = 1816; // Обладатель предмета $s2 появился в районе $s1.
	public static final int S2_OWNER_HAS_LOGGED_INTO_THE_S1_REGION = 1817; // Обладатель предмета $s2 находится в районе $s1.
	public static final int S1_HAS_DISAPPEARED_CW = 1818; // $s1 исчез.
	public static final int AN_EVIL_IS_PULSATING_FROM_S2_IN_S1 = 1819; // Зло пульсирует из $s2 в $s1.
	public static final int S1_IS_CURRENTLY_ASLEEP = 1820; // $s1 в настоящее время спит.
	public static final int S2_S_EVIL_PRESENCE_IS_FELT_IN_S1 = 1821; // Аура зла $s2 чувствуется в $s1.
	public static final int S1_HAS_BEEN_SEALED = 1822; // $s1 запечатан.
	public static final int THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED = 1823; // Период регистрации войны за холл клана закончен.
	public static final int YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR__PLEASE_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALL_S = 1824; // Вы зарегистрировались для войны за холл клана. Пожалуйста, проследуйте на левый край арены холла клана и приготовьтесь.
	public static final int YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR_PLEASE_TRY_AGAIN = 1825; // Ваша попытка зарегистрироваться для войны за холл клана не удалась. Пожалуйста, попытайтесь снова.
	public static final int IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN = 1826; // Игра начнется через $s1 мин. Все игроки должны поспешить и отойти на левый край арены холла клана.
	public static final int IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW = 1827; // Игра начнется через $s1 мин. Игроки, пожалуйста, войдите на арену.
	public static final int IN_S1_SECONDS_THE_GAME_WILL_BEGIN = 1828; // Игра начнется через $s1 сек.
	public static final int THE_COMMAND_CHANNEL_IS_FULL = 1829; // Канал Команды заполнен.
	public static final int C1_IS_NOT_ALLOWED_TO_USE_THE_PARTY_ROOM_INVITE_COMMAND_PLEASE_UPDATE_THE_WAITING_LIST = 1830; // $c1 не может использовать команду приглашения в комнату группы. Пожалуйста, обновите лист ожидания.
	public static final int C1_DOES_NOT_MEET_THE_CONDITIONS_OF_THE_PARTY_ROOM_PLEASE_UPDATE_THE_WAITING_LIST = 1831; // $c1 не соответствует условиям комнаты группы. Пожалуйста, обновите лист ожидания.
	public static final int ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM = 1832; // Только хозяин комнаты может приглашать остальных в комнату группы.
	public static final int ALL_OF_S1_WILL_BE_DROPPED_WOULD_YOU_LIKE_TO_CONTINUE = 1833; // Все $s1 будут сброшены. Продолжить?
	public static final int THE_PARTY_ROOM_IS_FULL_NO_MORE_CHARACTERS_CAN_BE_INVITED_IN = 1834; // Комната группы заполнена. Больше персонажей пригласить нельзя.
	public static final int S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME = 1835; // $s на данный момент заполнена и не может принять дополнительных членов.
	public static final int YOU_CANNOT_JOIN_A_CLAN_ACADEMY_BECAUSE_YOU_HAVE_SUCCESSFULLY_COMPLETED_YOUR_2ND_CLASS_TRANSFER = 1836; // Вы не можете присоединиться к Академии Клана, так как уже выполнили квест на вторую смену профессии.
	public static final int C1_HAS_SENT_YOU_AN_INVITATION_TO_JOIN_THE_S3_ROYAL_GUARD_UNDER_THE_S2_CLAN_WOULD_YOU_LIKE_TO = 1837; // $c1 приглашает Вас вступить в отряд Королевской Стражи $s3 клана $s2. Принять?
	public static final int _1_THE_COUPON_CAN_BE_USED_ONCE_PER_CHARACTER = 1838; // Купон можно использовать только 1 раз.
	public static final int _2_A_USED_SERIAL_NUMBER_MAY_NOT_BE_USED_AGAIN = 1839; // Нельзя использовать активированный серийный номер.
	public static final int _3_IF_YOU_ENTER_THE_INCORRECT_SERIAL_NUMBER_MORE_THAN_5_TIMES__N___YOU_MAY_USE_IT_AGAIN_AFTER_A = 1840; // Если Вы ввели неверный серийный номер более 5-ти раз, \\n то сможете ввести его снова только спустя некоторое время.
	public static final int THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED__NOT_ENOUGH_CLANS_HAVE_REGISTERED = 1841; // Эта война за холл клана была отменена из-за недостаточного количества зарегистрированных кланов.
	public static final int S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT = 1842; // $c1 хочет призвать Вас из $s2. Вы согласны?
	public static final int S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED = 1843; // $c1 в бою.
	public static final int S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED = 1844; // $c1 сейчас мертв и не может быть призван.
	public static final int HERO_WEAPONS_CANNOT_BE_DESTROYED = 1845; // Оружие Героя не может быть уничтожено.
	public static final int YOU_ARE_TOO_FAR_AWAY_FROM_THE_FENRIR_TO_MOUNT_IT = 1846; // Вы слишком далеко от питомца, чтобы оседлать его.
	public static final int YOU_CAUGHT_A_FISH_S1_IN_LENGTH = 1847; // Вы поймали рыбу длиной $s1.
	public static final int BECAUSE_OF_THE_SIZE_OF_FISH_CAUGHT_YOU_WILL_BE_REGISTERED_IN_THE_RANKING = 1848; // Длина пойманной вами рыбы впечатляет. Вы будете занесены в рейтинг.
	public static final int ALL_OF_S1_WILL_BE_DISCARDED_WOULD_YOU_LIKE_TO_CONTINUE = 1849; // Все $s1 будут отклонены. Продолжить?
	public static final int THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED = 1850; // Капитан Рыцарского Ордена не может быть назначен.
	public static final int THE_CAPTAIN_OF_THE_ROYAL_GUARD_CANNOT_BE_APPOINTED = 1851; // Капитан Королевской стражи не может быть назначен.
	public static final int THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION_SCORE = 1852; // Попытка получить умение не удалась из-за недостаточного количества Очков Репутации Клана.
	public static final int QUANTITY_ITEMS_OF_THE_SAME_TYPE_CANNOT_BE_EXCHANGED_AT_THE_SAME_TIME = 1853; // Количественные предметы одного типа не могут быть обменены одновременно.
	public static final int THE_ITEM_WAS_CONVERTED_SUCCESSFULLY = 1854; // Предмет успешно преобразован.
	public static final int ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME = 1855; // Это имя уже используется боевым подразделением. Назначьте другое имя.
	public static final int SINCE_YOUR_OPPONENT_IS_NOW_THE_OWNER_OF_S1_THE_OLYMPIAD_HAS_BEEN_CANCELLED = 1856; // Так как ваш противник завладел $s1, Олимпиада отменена.
	public static final int SINCE_YOU_NOW_OWN_S1_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD = 1857; // $c1 завоевал $s2 и потерял возможность участия в Олимпиаде.
	public static final int YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_WHILE_DEAD = 1858; // $c1 погиб и не может принять участие в Олимпиаде.
	public static final int YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_MOVED_AT_ONE_TIME = 1859; // Вы превысили максимальное количество предметов, которое может быть перемещено за один раз.
	public static final int THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW = 1860; // У вас слишком мало очков клана.
	public static final int THE_CLANS_CREST_HAS_BEEN_DELETED = 1861; // Эмблема клана удалена.
	public static final int THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER = 1862; // С того момента, как количество очков репутации стало 0 или выше, клановые умение активны.
	public static final int S1_PURCHASED_A_CLAN_ITEM_REDUCING_THE_CLAN_REPUTATION_BY_S2_POINTS = 1863; // $c1 приобрел клановый предмет, понижающий репутацию клана на $s2.
	public static final int THE_PET_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS = 1864; // Ваш питомец/слуга не реагирует и не будет подчиняться приказам.
	public static final int THE_PET_SERVITOR_IS_CURRENTLY_IN_A_STATE_OF_DISTRESS = 1865; // Ваш слуга/питомец угнетен.
	public static final int MP_WAS_REDUCED_BY_S1 = 1866; // MP были понижены на $s1.
	public static final int YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1 = 1867; // MP Вашего противника были понижены на $s1.
	public static final int YOU_CANNOT_EXCHANGE_AN_ITEM_WHILE_IT_IS_BEING_USED = 1868; // Вы не можете обменять предмет, пока он используется.
	public static final int S1_HAS_GRANTED_THE_CHANNELS_MASTER_PARTY_THE_PRIVILEGE_OF_ITEM_LOOTING = 1869; // $c1 наделяется полномочиями сбора предметов главной группой Командного Канала.
	public static final int A_COMMAND_CHANNEL_WITH_THE_ITEM_LOOTING_PRIVILEGE_ALREADY_EXISTS = 1870; // Канал Команды с правами сбора предметов уже существует.
	public static final int DO_YOU_WANT_TO_DISMISS_S1_FROM_THE_CLAN = 1871; // $c1 - исключить из клана?
	public static final int YOU_HAVE_S1_HOURS_AND_S2_MINUTES_LEFT = 1872; // Осталось: $s1ч $s2 мин.
	public static final int THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THE_FIXED_USE_TIME_FOR_THIS_PC_CARD = 1873; // Осталось фиксированного времени в игровом клубе: $s1ч $s2 мин.
	public static final int THERE_ARE_S1_MINUTES_LEFT_FOR_THIS_INDIVIDUAL_USER = 1874; // У этого пользователя осталось: $s1ч $s2 мин.
	public static final int THERE_ARE_S1_MINUTES_LEFT_IN_THE_FIXED_USE_TIME_FOR_THIS_PC_CARD = 1875; // Осталось фиксированного времени в игровом клубе: $s1 мин.
	public static final int DO_YOU_WANT_TO_LEAVE_S1_CLAN = 1876; // Покинуть клан $s1?
	public static final int THE_GAME_WILL_END_IN_S1_MINUTES = 1877; // Игра закончится через $s1 мин.
	public static final int THE_GAME_WILL_END_IN_S1_SECONDS = 1878; // Игра закончится через $s1 сек.
	public static final int IN_S1_MINUTES_YOU_WILL_BE_TELEPORTED_OUTSIDE_OF_THE_GAME_ARENA = 1879; // Через $s1 мин Вы будете выброшены из игровой арены.
	public static final int IN_S1_SECONDS_YOU_WILL_BE_TELEPORTED_OUTSIDE_OF_THE_GAME_ARENA = 1880; // Через $s1 сек Вы будете выброшены из игровой арены.
	public static final int THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS_PREPARE_YOURSELF = 1881; // Отборочный тур начнется через $s1 сек. Приготовьтесь.
	public static final int CHARACTERS_CANNOT_BE_CREATED_FROM_THIS_SERVER = 1882; // Персонажи не могут быть созданы с этого сервера.
	public static final int THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR = 1883; // Мне нечего предложить.
	public static final int ENTER_THE_PC_ROOM_COUPON_SERIAL_NUMBER = 1884; // Введите серийный номер купона:
	public static final int THIS_SERIAL_NUMBER_CANNOT_BE_ENTERED_PLEASE_TRY_AGAIN_IN_S1_MINUTES = 1885; // Серийный номер сейчас не может быть введен. Пожалуйста, повторите попытку через $s1 мин.
	public static final int THIS_SERIAL_NUMBER_HAS_ALREADY_BEEN_USED = 1886; // Этот серийный номер уже использован.
	public static final int INVALID_SERIAL_NUMBER_YOUR_ATTEMPT_TO_ENTER_THE_NUMBER_HAS_FAILED_S1_TIMES_YOU_WILL_BE_ALLOWED_TO_MAKE_S2_MORE_ATTEMPTS = 1887; // Неверный серийный номер. Ваша попытка ввести номер не удалась $s1 раз(-а). Вы можете попытаться еще $s2 раз(-а).
	public static final int INVALID_SERIAL_NUMBER_YOUR_ATTEMPT_TO_ENTER_THE_NUMBER_HAS_FAILED_5_TIMES_PLEASE_TRY_AGAIN_IN_4_HOURS = 1888; // Неверный серийный номер. Ваша попытка ввести номер не удалась 5 раз. Пожалуйста, повторите попытку через 4 часа.
	public static final int CONGRATULATIONS_YOU_HAVE_RECEIVED_S1 = 1889; // Поздравляем! Вы получили: $s1.
	public static final int SINCE_YOU_HAVE_ALREADY_USED_THIS_COUPON_YOU_MAY_NOT_USE_THIS_SERIAL_NUMBER = 1890; // Вы уже использовали этот купон и больше невозможно вводить этот номер.
	public static final int YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP = 1891; // Нельзя использовать предметы в личной торговой лавке или мастерской.
	public static final int THE_REPLAY_FILE_FOR_THE_PREVIOUS_VERSION_CANNOT_BE_PLAYED = 1892; // Файл Replay предыдущей версии не может быть проигран.
	public static final int THIS_FILE_CANNOT_BE_REPLAYED = 1893; // Это файл не может быть воспроизведен.
	public static final int A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT = 1894; // Подкласс не может быть создан или изменен, если вы превышаете лимит веса.
	public static final int S1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING = 1895; // $c1 находится в зоне блокировки призыва.
	public static final int S1_HAS_ALREADY_BEEN_SUMMONED = 1896; // Персонаж $c1 уже призван.
	public static final int S1_IS_REQUIRED_FOR_SUMMONING = 1897; // Призыв требует: $s1.
	public static final int S1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED = 1898; // Персонаж $c1 сейчас находится в личной торговой лавке и не может быть призван.
	public static final int YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING = 1899; // Ваша цель находится в зоне, где призыв блокирован.
	public static final int S1_HAS_ENTERED_THE_PARTY_ROOM = 1900; // $c1 входит в комнату группы.
	public static final int S1_HAS_INVITED_YOU_TO_ENTER_THE_PARTY_ROOM = 1901; // $c1 приглашает Вас в комнату группы.
	public static final int INCOMPATIBLE_ITEM_GRADE_THIS_ITEM_CANNOT_BE_USED = 1902; // Несовместимый класс предмета. Предмет не может быть использован.
	public static final int REQUESTED_NCOTP = 1903; // Те из Вас, кому требуется NCOTP, могут запустить его,\\n используя свой телефон, чтобы получить NCOTP
	public static final int A_SUB_CLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED = 1904; // Подкласс не может быть создан или изменен, если вызван слуга или питомец.
	public static final int S2_OF_S1_WILL_BE_REPLACED_WITH_S4_OF_S3 = 1905; // $s2 $s1 заменен на $c4 $s3.
	public static final int SELECT_THE_COMBAT_UNIT_YOU_WISH_TO_TRANSFER_TO = 1906; // Выберите боевой отряд\\n для трансфера.
	public static final int SELECT_THE_THE_CHARACTER_WHO_WILL_REPLACE_THE_CURRENT_CHARACTER = 1907; // Выберите персонажа, который \\n заменит текущего персонажа.
	public static final int S1_IS_IN_A_STATE_WHICH_PREVENTS_SUMMONING = 1908; // $c1 имеет статус, не допускающий его призыв.
	public static final int LIST_OF_CLAN_ACADEMY_GRADUATES_DURING_THE_PAST_WEEK = 1909; // == <Список выпускников клана за последнюю неделю> ==
	public static final int GRADUATES = 1910; // Выпускники: $c1.
	public static final int YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD = 1911; // Вы не можете призывать персонажей, в данное время участвующих в Великой Олимпиаде.
	public static final int ONLY_THOSE_REQUESTING_NCOTP_SHOULD_MAKE_AN_ENTRY_INTO_THIS_FIELD = 1912; // Только те, кто запрашивал NCOTP, могут войти сюда.
	public static final int THE_REMAINING_RECYCLE_TIME_FOR_S1_IS_S2_MINUTES = 1913; // Время повтора для $s1: $s2 мин.
	public static final int THE_REMAINING_RECYCLE_TIME_FOR_S1_IS_S2_SECONDS = 1914; // Время повтора для $s1: $s2 сек.
	public static final int THE_GAME_WILL_END_IN_S1_SECONDS_2 = 1915; // Игра закончится через $s1 сек.
	public static final int THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED = 1916; // Уровень штрафа за смерть: $s1.
	public static final int THE_DEATH_PENALTY_HAS_BEEN_LIFTED = 1917; // Штраф за смерть отменен.
	public static final int THE_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL = 1918; // Ваш питомец слишком высокого уровня, вы не можете его контролировать
	public static final int THE_GRAND_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED = 1919; // Регистрация на Великую Олимпиаду окончена.
	public static final int YOUR_ACCOUNT_IS_CURRENTLY_INACTIVE_BECAUSE_YOU_HAVE_NOT_LOGGED_INTO_THE_GAME_FOR_SOME_TIME_YOU = 1920; // Ваш аккаунт сейчас неактивен, поскольку вы долгое время не посещали игру. Можно активировать на нашем сайте.
	public static final int S2_HOURS_AND_S3_MINUTES_HAVE_PASSED_SINCE_S1_HAS_KILLED = 1921; // С момента убийства $s1 прошло $s2 ч $s3 мин.
	public static final int BECAUSE_S1_FAILED_TO_KILL_FOR_ONE_FULL_DAY_IT_HAS_EXPIRED = 1922; // Из-за того, что $s1 был
	public static final int COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED = 1923; // Придворный Маг: Портал создан!
	public static final int DUE_TO_THE_AFFECTS_OF_THE_SEAL_OF_STRIFE_IT_IS_NOT_POSSIBLE_TO_SUMMON_AT_THIS_TIME = 1925; // Из-за эффекта Печати Раздора призыв сейчас невозможен.
	public static final int C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL = 1932; // $c1 отказывается от дуэли.
	public static final int C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL = 1935; // $c1 отказывается от групповой дуэли с Вами.
	public static final int THIS_IS_NOT_A_SUITABLE_PLACE_TO_CHALLENGE_ANYONE_OR_PARTY_TO_A_DUEL = 1941; // Это не подходящее место для приглашения в группу или вызова на дуэль.
	public static final int THE_OPPOSING_PARTY_IS_CURRENTLY_NOT_IN_A_SUITABLE_LOCATION_FOR_A_DUEL = 1943; // Группа противника находится в не подходящей для дуэли локации .
	public static final int C1_HAS_CHALLENGED_YOU_TO_A_DUEL_WILL_YOU_ACCEPT = 1946; // $c1 вызывает Вас на дуэль. Принять?
	public static final int C1_S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL_WILL_YOU_ACCEPT = 1947; // Группа персонажа $c1 вызывает Вашу группу на дуэль. Принять?
	public static final int THE_DUEL_WILL_BEGIN_IN_S1_SECONDS_1 = 1948; // Дуэль начнется через $s1 сек.
	public static final int SINCE_C1_WAS_DISQUALIFIED_S2_HAS_WON = 1953; // Из-за дисквалификации персонажа $c1 дуэль выигрывает $s2.
	public static final int SINCE_C1_S_PARTY_WAS_DISQUALIFIED_S2_S_PARTY_HAS_WON = 1954; // Из-за дисквалификации группы персонажа $c1 дуэль выигрывает группа персонажа $s2.
	public static final int ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS = 1966; // Только лидер клана имеет право командовать.
	public static final int THE_GATE_IS_FIRMLY_LOCKED_PLEASE_TRY_AGAIN_LATER = 1967; // Врата заперты. Пожалуйста, повторите попытку позже.
	public static final int S1_S_OWNER = 1968; // Владелец $s1.
	public static final int AREA_WHERE_S1_APPEARS = 1969; // Место появления $s1.
	public static final int THE_LEVEL_OF_THE_HARDENER_IS_TOO_HIGH_TO_BE_USED = 1971; // Уровень закрепителя слишком высок.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_FROZEN = 1973; // Вы не можете зачаровать предметы, будучи замороженным.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES = 1975; // Вы не можете зачаровать предметы во время торговли.
	public static final int S1_S_DROP_AREA_S2 = 1985; // Зона выпадения предмета $s1
	public static final int S1_S_OWNER_S2 = 1986; // Владелец предмета $s1 ($s2)
	public static final int S1_1 = 1987; // $s1
	public static final int THE_FERRY_HAS_ARRIVED_AT_PRIMEVAL_ISLE = 1988; // Корабль прибыл на Первобытный Остров.
	public static final int THE_FERRY_WILL_LEAVE_FOR_RUNE_HARBOR_AFTER_ANCHORING_FOR_THREE_MINUTES = 1989; // Корабль покинет гавань Руны через 3 минуты после прибытия.
	public static final int THE_FERRY_IS_NOW_DEPARTING_PRIMEVAL_ISLE_FOR_RUNE_HARBOR = 1990; // Корабль от гавани Руны к Первобытному Острову отчаливает.
	public static final int THE_FERRY_WILL_LEAVE_FOR_PRIMEVAL_ISLE_AFTER_ANCHORING_FOR_THREE_MINUTES = 1991; // Корабль покинет гавань Руны через 3 минуты после прибытия.
	public static final int THE_FERRY_IS_NOW_DEPARTING_RUNE_HARBOR_FOR_PRIMEVAL_ISLE = 1992; // Корабль от Первобытного Острова к гавани Руны отчаливает.
	public static final int THE_FERRY_FROM_PRIMEVAL_ISLE_TO_RUNE_HARBOR_HAS_BEEN_DELAYED = 1993; // Корабль с Первобытного Острова к гавани Руны задерживается.
	public static final int THE_FERRY_FROM_RUNE_HARBOR_TO_PRIMEVAL_ISLE_HAS_BEEN_DELAYED = 1994; // Корабль из гавани Руны к Первобытному Острову задерживается.
	public static final int S1_CHANNEL_FILTERING_OPTION = 1995; // Фильтр канала: $s1.
	public static final int THE_ATTACK_HAS_BEEN_BLOCKED = 1996; // Атака была заблокирована.
	public static final int YOU_HAVE_AVOIDED_C1_S_ATTACK = 2000; // Вы увернулись от атаки цели $c1.
	public static final int TRAP_FAILED = 2002; // Захват не удался.
	public static final int YOU_OBTAINED_AN_ORDINARY_MATERIAL = 2003; // Вы получили обычный материал.
	public static final int YOU_OBTAINED_A_RARE_MATERIAL = 2004; // Вы получили редкий материал.
	public static final int YOU_OBTAINED_A_UNIQUE_MATERIAL = 2005; // Вы получили уникальный материал.
	public static final int YOU_OBTAINED_THE_ONLY_MATERIAL_OF_THIS_KIND = 2006; // Вы получили единственный в своем роде материал.
	public static final int PLEASE_ENTER_THE_RECIPIENT_S_NAME = 2007; // Пожалуйста, введите имя адресата.
	public static final int PLEASE_ENTER_THE_TEXT = 2008; // Пожалуйста, введите текст.
	public static final int YOU_CANNOT_EXCEED_1500_CHARACTERS = 2009; // Не больше 1500 символов.
	public static final int S2_S1 = 2010; // $s2 $s1
	public static final int THE_AUGMENTED_ITEM_CANNOT_BE_DISCARDED = 2011; // Зачарование не может быть отменено.
	public static final int S1_HAS_BEEN_ACTIVATED = 2012; // $s1 активирован.
	public static final int YOUR_SEED_OR_REMAINING_PURCHASE_AMOUNT_IS_INADEQUATE = 2013; // Тип семян или остаток суммы закупки не соответствует вашему запросу.
	public static final int YOU_CANNOT_PROCEED_BECAUSE_THE_MANOR_CANNOT_ACCEPT_ANY_MORE_CROPS__ALL_CROPS_HAVE_BEEN_RETURNED = 2014; // Неосуществимо. Владение не может принять больше ростков. Все ростки были возвращены и в резерве не осталось аден.
	public static final int A_SKILL_IS_READY_TO_BE_USED_AGAIN = 2015; // Умение готово к повторному использованию.
	public static final int A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_RE_USE_COUNTER_TIME_HAS_INCREASED = 2016; // Умение готово к повторному использованию, но время отката возросло.
	public static final int C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD = 2029; // $c1 не может участвовать в Олимпиаде во время телепортации.
	public static final int YOU_ARE_CURRENTLY_LOGGING_IN = 2030; // Вы входите в игру.
	public static final int PLEASE_WAIT_A_MOMENT = 2031; // Пожалуйста, подождите.
	public static final int IT_IS_NOT_THE_RIGHT_TIME_FOR_PURCHASING_THE_ITEM = 2032; // Неподходящее время для покупок.
	public static final int A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT = 2033; // Подкласс не может быть создан или изменен, поскольку Вы превысили ваш лимит инвентаря.
	public static final int THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED = 2034; // Предмет можно купить через $s1 ч $s2 мин.
	public static final int THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED = 2035; // Предмет можно купить через $s1 мин.
	public static final int UNABLE_TO_INVITE_BECAUSE_THE_PARTY_IS_LOCKED = 2036; // Нельзя пригласить, поскольку группа закрыта.
	public static final int UNABLE_TO_CREATE_CHARACTER_YOU_ARE_UNABLE_TO_CREATE_A_NEW_CHARACTER_ON_THE_SELECTED_SERVER_A = 2037; // Невозможно создать персонажа на выбранном сервере. Действует ограничение, не позволяющее пользователям создавать персонажей на различных серверах, где до этого не было создано персонажей. Пожалуйста, выберите другой сервер.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_ARENT_ALLOWED_TO_DROP = 2038; // Этот аккаунт не может сбрасывать предметы.
	public static final int THIS_ACCOUNT_CANOT_TRADE_ITEMS = 2039; // Этот аккаунт не может продавать предметы.
	public static final int CANNOT_TRADE_ITEMS_WITH_THE_TARGETED_USER = 2040; // Нельзя торговать с выбранным пользователем.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_ARENT_ALLOWED_TO = 2041; // Нельзя войти в личную торговую лавку.
	public static final int THIS_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_NON_PAYMENT_BASED_ON_THE_CELL_PHONE_PAYMENT_AGREEMENT__N = 2042; // Этот аккаунт был заблокирован за неуплату в соответствии с соглашением об оплате через телефон. \\n Пожалуйста, пришлите подтверждение оплаты и свяжитесь с центром обслуживания клиентов.
	public static final int YOU_HAVE_EXCEEDED_YOUR_INVENTORY_VOLUME_LIMIT_AND_MAY_NOT_TAKE_THIS_QUEST_ITEM_PLEASE_MAKE_ROOM = 2043; // Вы превысили лимит объема вашего инвентаря и не можете взять квестовый предмет. Освободите место в инвентаре и возвращайтесь.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_ARENT_ALLOWED_TO_SET = 2044; // Этот аккаунт не может установить личную мастерскую.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_ARENT_ALLOWED_TO_USE = 2045; // Этот аккаунт не может использовать личную мастерскую.
	public static final int THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES = 2046; // Этот аккаунт не может использовать личную торговую лавку.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_ARENT_ALLOWED_TO_ = 2047; // Этот аккаунт не может использовать клановое хранилище.
	public static final int THE_SHORTCUT_IN_USE_CONFLICTS_WITH_S1_DO_YOU_WISH_TO_RESET_THE_CONFLICTING_SHORTCUTS_AND_USE_THE = 2048; // Ярлык конфликтует с $s1. Хотите сбросить конфликтующий ярлык и использовать сохраненный?
	public static final int THE_SHORTCUT_WILL_BE_APPLIED_AND_SAVED_IN_THE_SERVER_WILL_YOU_CONTINUE = 2049; // Ярлык принят и сохранен на сервере. Продолжить?
	public static final int S1_CLAN_IS_TRYING_TO_DISPLAY_A_FLAG = 2050; // Клан $s1 пытается поднять флаг.
	public static final int YOU_MUST_ACCEPT_THE_USER_AGREEMENT_BEFORE_THIS_ACCOUNT_CAN_ACCESS_LINEAGE_II__N_PLEASE_TRY_AGAIN = 2051; // Чтобы получить доступ в Lineage II, Вы должны принять пользовательское соглашение. \\n Пожалуйста, попробуйте еще раз.
	public static final int A_GUARDIAN_S_CONSENT_IS_REQUIRED_BEFORE_THIS_ACCOUNT_CAN_BE_USED_TO_PLAY_LINEAGE_II__NPLEASE_TRY = 2052; // Аккаунт будет допущен к игре в Lineage II только после принятия соглашения о безопасности. \\n Повторите попытку после принятия соглашения.
	public static final int THIS_ACCOUNT_HAS_DECLINED_THE_USER_AGREEMENT_OR_IS_PENDING_A_WITHDRAWL_REQUEST___NPLEASE_TRY = 2053; // Этот аккаунт отклонил пользовательское соглашение или запросил отклонение.\\n Пожалуйста, повторите попытку после отклонения запроса.
	public static final int THIS_ACCOUNT_HAS_BEEN_SUSPENDED___NFOR_MORE_INFORMATION_PLEASE_CALL_THE_CUSTOMER_S_CENTER_TEL = 2054; // Действие этого аккаунта приостановлено. \\n За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES__NFOR_MORE_INFORMATION_PLEASE_VISIT_THE = 2055; // Ваш аккаунт отключен от всех игровых услуг. \\n За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_CONVERTED_TO_AN_INTEGRATED_ACCOUNT_AND_IS_UNABLE_TO_BE_ACCESSED___NPLEASE = 2056; // Ваш аккаунт был преобразован в объединенный аккаунт и не может выбран. \\n Пожалуйста, пройдите регистрацию объединенным аккаунтом.
	public static final int YOU_HAVE_BLOCKED_C1 = 2057; // Вы заблокировали $c1.
	public static final int THAT_ITEM_CANNOT_BE_TAKEN_OFF = 2065; // Предмет не может быть выброшен.
	public static final int THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS = 2066; // Вы не можете атаковать этим оружием.
	public static final int THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPON_S_SKILL = 2067; // Оружие не может использовать умения, кроме умений оружия.
	public static final int THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU = 2081; // Нет резерва.
	public static final int YOU_HAVE_EXCEEDED_THE_TOTAL_AMOUT_OF_ADENA_ALLOWED_IN_INVENTORY = 2082; // Вы превысили предельно допустимую сумму в инвентаре.
	public static final int SEARCH_ON_USER_C2_FOR_THIRD_PARTY_PROGRAM_USE_WILL_BE_COMPLETED_IN_S1_MINUTES = 2086; // Поиск пользователя $c2, использующего сторонние программы, завершится через $s1 мин.
	public static final int YOUR_ACCOUNT_CAN_ONLY_BE_USED_AFTER_CHANGING_YOUR_PASSWORD_AND_QUIZ___N_SERVICES_WILL_BE = 2091; // Ваш аккаунт может использоваться только после смены пароля и контрольного вопроса.
	public static final int YOU_CANNOT_BID_DUE_TO_A_PASSED_IN_PRICE = 2092; // Нельзя сделать эту ставку из-за того, что цена просрочена.
	public static final int THE_BID_AMOUNT_WAS_S1_ADENA_WOULD_YOU_LIKE_TO_RETRIEVE_THE_BID_AMOUNT = 2093; // Сумма ставки - $s1 аден. Желаете забрать Вашу ставку?
	public static final int ANOTHER_USER_IS_PURCHASING_PLEASE_TRY_AGAIN_LATER = 2094; // Покупает другой пользователь. Пожалуйста, повторите попытку позже.
	public static final int SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNTS_HAVE_LIMITED_CHATTING = 2095; // Этот аккаунт не может использовать крик.
	public static final int YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL = 2103; // Вы не можете войти, так как Вы не в текущем Канале Альянса.
	public static final int THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER = 2104; // Превышено максимальное количество временных зон. Вы не можете войти.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_10_DAYS_FOR_USE_OF_ILLEGAL_SOFTWARE_AND_MAY_BE_PERMANENTLY = 2108; // Ваш аккаунт заблокирован на 10 дней за использование нелицензионного программного обеспечения и может быть заблокирован бессрочно. За дополнительной информацией обратитесь в центр поддержки.
	public static final int THE_SERVER_HAS_BEEN_INTEGRATED_AND_YOUR_CHARACTER_S1_HAS_BEEN_OVERLAPPED_WITH_ANOTHER_NAME = 2109; // Сервер был объединен и имя вашего персонажа было перекрыто другим персонажем. Пожалуйста, введите новое имя вашего персонажа.
	public static final int THIS_CHARACTER_NAME_ALREADY_EXISTS_OR_IS_AN_INVALID_NAME_PLEASE_ENTER_A_NEW_NAME = 2110; // Имя персонажа уже существует либо это некорректное имя. Пожалуйста, введите другое имя.
	public static final int ENTER_A_SHORTCUT_TO_ASSIGN = 2111; // Назначьте ярлык.
	public static final int SUB_KEY_CAN_BE_CTRL_ALT_SHIFT_AND_YOU_MAY_ENTER_TWO_SUB_KEYS_AT_A_TIME___N_EXAMPLE__CTRL___ALT__ = 2112; // Вспомогательными клавишами могут быть CTRL, ALT, SHIFT. Вы можете нажимать две одновременно. Например, "CTRL + ALT + A"
	public static final int CTRL_ALT_SHIFT_KEYS_MAY_BE_USED_AS_SUB_KEY_IN_EXPANDED_SUB_KEY_MODE_AND_ONLY_ALT_MAY_BE_USED_AS = 2113; // Клавиши CTRL, ALT, SHIFT могут быть использованы в расширенном режиме вспомогательных клавиш, и только ALT может быть использован как вспомогательная клавиша в стандартном режиме.
	public static final int FORCED_ATTACK_AND_STAND_IN_PLACE_ATTACKS_ASSIGNED_PREVIOUSLY_TO_CTRL_AND_SHIFT_WILL_BE_CHANGED = 2114; // Силовая и останавливающая атака, прежде назначенная на Ctrl и Shift, при установке расширенного режима ярлыков будет переназначена на Alt + Q и Alt + E. Ctrl и Shift освободятся для установки других быстрых доступов. Продолжить?
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_ABUSING_A_BUG_RELATED_TO_THE_NCCOIN_FOR_MORE_INFORMATION = 2115; // Ваш аккаунт заблокирован за использование ошибки, относящейся к Nccoin. За дополнительной информацией обращайтесь в Центр Поддержки.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_ABUSING_A_FREE_NCCOIN_FOR_MORE_INFORMATION_PLEASE_VISIT_THE = 2116; // Ваша учетная запись бесплатно jigeupdoen монета будет ограничено, и использоваться для определения akyonghan истории. Для получения дополнительной информации, клиентов центра (тел. 1600-0020) на основе запроса, пожалуйста
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_USING_ANOTHER_PERSON_S_IDENTIFICATION_IF_YOU_WERE_NOT = 2117; // Ваша учетная запись заблокирована из-за кражи оплаты услуг не ограничивается государством. Для получения дополнительной информации, PlayNC сайте (www.plaync.co.kr) через клиент-центр расследование, пожалуйста.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_MISAPPROPRIATING_PAYMENT_UNDER_ANOTHER_PLAYER_S_ACCOUNT_FOR = 2118; // Ваш аккаунт заблокирован за присвоение платежей другого аккаунта. За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES_AFTER_BEING_DETECTED_WITH_DEALING_AN = 2119; // Ваш аккаунт отключен от всех игровых услуг за торговлю аккаунтами. \\n За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_10_DAYS_FOR_USING_ILLEGAL_SOFTWARE_YOUR_ACCOUNT_MAY_BE = 2120; // Ваш аккаунт заблокирован на 10 дней за использование нелегального программного обеспечения. Он может быть заблокирован бессрочно, если данное нарушение будет выявлено повторно. За дополнительной информацией обратитесь в центр поддержки.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES_FOR_USE_OF_ILLEGAL_SOFTWARE_FOR_MORE = 2121; // Ваш аккаунт отключен от всех игровых услуг за использование нелегального программного обеспечения. \\n За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES_FOR_USE_OF_ILLEGAL_SOFTWARE_FOR_MORE_ = 2122; // Ваш аккаунт отключен от всех игровых услуг за использование нелегального программного обеспечения. \\n За дополнительной информацией обращайтесь в Службу Поддержки Пользователей.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_AT_YOUR_OWN_REQUEST_FOR_MORE_INFORMATION_PLEASE_VISIT_THE = 2123; // Ваш аккаунт заблокирован по Вашему требованию. За дополнительной информацией обратитесь в Службу Поддержки Пользователей.
	public static final int THE_SERVER_HAS_BEEN_INTEGRATED_AND_YOUR_CLAN_NAME_S1_HAS_BEEN_OVERLAPPED_WITH_ANOTHER_NAME = 2124; // Сервер был объединен, и имя вашего клана, $s1, было занято другим кланом. Пожалуйста, введите новое имя клана.
	public static final int THE_NAME_ALREADY_EXISTS_OR_IS_AN_INVALID_NAME_PLEASE_ENTER_THE_CLAN_NAME_TO_BE_CHANGED = 2125; // Имя уже существует либо оно некорректно. Пожалуйста, введите другое имя.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_REGULARLY_POSTING_ILLEGAL_MESSAGES_FOR_MORE_INFORMATION = 2126; // Ваш аккаунт был заблокирован за систематические нелегальные сообщения. За дополнительной информацией обратитесь в Центр Поддержки.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_AFTER_BEING_DETECTED_WITH_AN_ILLEGAL_MESSAGE_FOR_MORE = 2127; // Ваш аккаунт был заблокирован после обнаружения нелегального сообщения. За дополнительной информацией обращайтесь в Центр Поддержки.
	public static final int YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES_FOR_USING_THE_GAME_FOR_COMMERCIAL = 2128; // Ваш аккаунт был отключен от всех игровых услуг за использование игры в коммерческих целях. За дополнительной информацией обратитесь в центр поддержки.
	public static final int THE_AUGMENTED_ITEM_CANNOT_BE_CONVERTED_PLEASE_CONVERT_AFTER_THE_AUGMENTATION_HAS_BEEN_REMOVED = 2129; // Улучшенный предмет не может быть преобразован. Пожалуйста, преобразуйте после того, как улучшение будет снято.
	public static final int YOU_CANNOT_CONVERT_THIS_ITEM = 2130; // Вы не можете преобразовать этот предмет.
	public static final int YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL = 2131; // Вы предложили самую высокую ставку и купили предмет. Он находится в вашей личной мастерской.
	public static final int YOU_HAVE_ENTERED_A_COMMON_SEVER = 2132; // Вы зашли на общий сервер.
	public static final int YOU_HAVE_ENTERED_AN_ADULTS_ONLY_SEVER = 2133; // Вы зашли на сервер только для взрослых.
	public static final int YOU_HAVE_ENTERED_A_SERVER_FOR_JUVENILES = 2134; // Вы зашли на подростковый сервер.
	public static final int BECAUSE_OF_YOUR_FATIGUE_LEVEL_THIS_IS_NOT_ALLOWED = 2135; // Вы слишком утомлены, действие недоступно.
	public static final int A_CLAN_NAME_CHANGE_APPLICATION_HAS_BEEN_SUBMITTED = 2136; // Заявка на смену имени клана подана.
	public static final int YOU_ARE_ABOUT_TO_BID_S1_ITEM_WITH_S2_ADENA_WILL_YOU_CONTINUE = 2137; // Вы намереваетесь предложить за предмет $s1 ставку в $s2 аден. Продолжить?
	public static final int PLEASE_ENTER_A_BID_PRICE = 2138; // Пожалуйста, введите свою ставку.
	public static final int C1_S_PET = 2139; // Питомец персонажа $c1.
	public static final int C1_S_SERVITOR = 2140; // Слуга персонажа $c1.
	public static final int YOU_SLIGHTLY_RESISTED_C1_S_MAGIC = 2141; // Вы частично отразили магию цели $c1.
	public static final int YOU_CANNOT_EXPEL_C1_BECAUSE_C1_IS_NOT_A_PARTY_MEMBER = 2142; // Вы не можете исключить персонажа $c1, так как он не состоит в группе.
	public static final int YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED = 2151; // У Вашего противника есть сопротивление к магии, урон был уменьшен.
	public static final int THE_ASSIGNED_SHORTCUT_WILL_BE_DELETED_AND_THE_INITIAL_SHORTCUT_SETTING_RESTORED_WILL_YOU = 2152; // Назначенные установки ярлыков будут удалены, а исходные восстановлены. Продолжить?
	public static final int YOU_ARE_CURRENTLY_LOGGED_INTO_10_OF_YOUR_ACCOUNTS_AND_CAN_NO_LONGER_ACCESS_YOUR_OTHER_ACCOUNTS = 2153; // Вы уже зарегистрировали 10 аккаунтов и больше не можете регистрировать аккаунты.
	public static final int THE_TARGET_IS_NOT_A_FLAGPOLE_SO_A_FLAG_CANNOT_BE_DISPLAYED = 2154; // Цель не является флагштоком и флаг не может быть поднят.
	public static final int A_FLAG_IS_ALREADY_BEING_DISPLAYED_ANOTHER_FLAG_CANNOT_BE_DISPLAYED = 2155; // Флаг уже был поднят. Другой флаг поднять нельзя.
	public static final int THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL = 2156; // Нет предметов, необходимых для использования этого навыка.
	public static final int BID_WILL_BE_ATTEMPTED_WITH_S1_ADENA = 2157; // Установлена цена: $s1 аден.
	public static final int FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE = 2158; // Силовая атака невозможна против временных союзников осаждающей стороны.
	public static final int THE_BARRACKS_HAVE_BEEN_SEIZED = 2164; // Казармы были захвачены.
	public static final int THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED = 2165; // Функции казарм восстановлены.
	public static final int ALL_BARRACKS_ARE_OCCUPIED = 2166; // Казармы были оккупированы.
	public static final int C1_HAS_ACQUIRED_THE_FLAG = 2168; // $c1 захватывает флаг.
	public static final int A_MALICIOUS_SKILL_CANNOT_BE_USED_WHEN_AN_OPPONENT_IS_IN_THE_PEACE_ZONE = 2170; // Вредоносное умение не может быть использовано, когда противник находится в мирной зоне.
	public static final int THIS_ITEM_CANNOT_BE_CRYSTALIZED = 2171; // Этот предмет не может быть кристаллизован.
	public static final int S1_S_ELEMENTAL_POWER_HAS_BEEN_REMOVED = 2176; // $s1: сила стихии деактивирована.
	public static final int _S1S2_S_ELEMENTAL_POWER_HAS_BEEN_REMOVED = 2177; // +$s1 $s2: сила стихии деактивирована.
	public static final int YOU_FAILED_TO_REMOVE_THE_ELEMENTAL_POWER = 2178; // Убрать силу стихии не удалось.
	public static final int YOU_HAVE_THE_HIGHEST_BID_SUBMITTED_IN_A_GIRAN_CASTLE_AUCTION = 2179; // Вы предложили самую высокую ставку на аукционе замка Гиран.
	public static final int YOU_HAVE_THE_HIGHEST_BID_SUBMITTED_IN_AN_ADEN_CASTLE_AUCTION = 2180; // Вы предложили самую высокую ставку на аукционе замка Аден.
	public static final int YOU_HAVE_HIGHEST_THE_BID_SUBMITTED_IN_A_RUNE_CASTLE_AUCTION = 2181; // Вы предложили самую высокую ставку на аукционе замка Руна.
	public static final int THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE = 2187; // Вы не можете атаковать цель.
	public static final int ANOTHER_ENCHANTMENT_IS_IN_PROGRESS_PLEASE_COMPLETE_PREVIOUS_TASK_AND_TRY_AGAIN = 2188; // Другое улучшение в процессе. Пожалуйста, завершите текущую задачу и попробуйте снова.
	public static final int TO_APPLY_SELECTED_OPTIONS_THE_GAME_NEEDS_TO_BE_RELOADED_IF_YOU_DON_T_APPLY_NOW_IT_WILL_BE = 2191; // Для применения выбранных настроек игра должна быть перезагружена. Если не применить сейчас, то настройки применятся при следующем входе в игру. Применить сейчас?
	public static final int YOU_HAVE_BID_ON_AN_ITEM_AUCTION = 2192; // Вы сделали ставку на аукционе предметов.
	public static final int NO_OWNED_CLAN = 2196; // Нет клана.
	public static final int OWNED_BY_CLAN_S1 = 2197; // Есть клан $s1.
	public static final int YOU_HAVE_THE_HIGHEST_BID_IN_AN_ITEM_AUCTION = 2198; // У вас самая высокая ставка на аукционе предметов.
	public static final int YOU_CANNOT_ENTER_THIS_INSTANCE_ZONE_WHILE_THE_NPC_SERVER_IS_UNAVAILABLE = 2199; // Вы не можете войти во временную зону, пока услуга NPC недоступна.
	public static final int THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_BECAUSE_THE_NPC_SERVER_IS_UNAVAILABLE_YOU_WILL_BE_FORCIBLY = 2200; // Эта временная зона закрыта, потому что сервер NPC не работает. Сейчас вы будете принудительно выброшены из подземелья.
	public static final int S1YEARS_S2MONTHS_S3DAYS = 2201; // $s1 г. $s2 мес. $s3 дн.
	public static final int S1HOURS_S2MINUTES_S3_SECONDS = 2202; // $s1 ч $s2 мин $s3 сек
	public static final int S1_M_S2_D = 2203; // $s1 мес. $s2 дн.
	public static final int S1HOURS = 2204; // $s1 ч
	public static final int YOU_HAVE_ENTERED_AN_AREA_WHERE_THE_MINI_MAP_CANNOT_BE_USED_THE_MINI_MAP_WILL_BE_CLOSED = 2205; // Вы вошли в зону, где миникарта не может быть использована. Миникарта будет закрыта.
	public static final int YOU_HAVE_ENTERED_AN_AREA_WHERE_THE_MINI_MAP_CAN_BE_USED = 2206; // Вы вошли в зону, где может быть использована миникарта.
	public static final int THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP_THE_MINI_MAP_WILL_NOT_BE_OPENED = 2207; // В этой зоне миникарта не используется и не открывается.
	public static final int YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS = 2208; // Ваш уровень не соответствует требованиям.
	public static final int THIS_IS_AN_AREA_WHERE_RADAR_CANNOT_BE_USED = 2209; // В этой зоне радар не используется.
	public static final int IT_WILL_RETURN_TO_AN_UNENCHANTED_CONDITION = 2210; // Вы вернулись в неулучшенное состояние.
	public static final int YOU_MUST_LEARN_A_GOOD_DEED_SKILL_BEFORE_YOU_CAN_ACQUIRE_NEW_SKILLS = 2211; // Вы должны выучить благотворное умение, прежде чем станут доступны другие умения.
	public static final int YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION = 2212; // Умение недоступно, вы не выполнили необходимый квест.
	public static final int A_NEW_CHARACTER_WILL_BE_CREATED_WITH_THE_CURRENT_SETTINGS_CONTINUE = 2214; // Новый персонаж будет создан с текущими настройками. Продолжить?
	public static final int S1P_DEF = 2215; // Физ. Защ.: $s1
	public static final int THE_CPU_DRIVER_IS_NOT_UP_TO_DATE_PLEASE_INSTALL_AN_UP_TO_DATE_CPU_DRIVER = 2216; // Не обновлен CPU драйвер. Пожалуйста, установите обновление.
	public static final int THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED_AND_THE_CLAN_S_REPUTATION_WILL_BE_INCREASED = 2217; // Баллиста была успешно уничтожена. Клановая репутация возросла.
	public static final int THIS_IS_A_MAIN_CLASS_SKILL_ONLY = 2218; // Это умение только для основного класса.
	public static final int THIS_LOWER_CLAN_SKILL_HAS_ALREADY_BEEN_ACQUIRED = 2219; // Низшие клановые умения уже доступны.
	public static final int THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED = 2220; // Предыдущее умение не было выучено.
	public static final int WILL_YOU_ACTIVATE_THE_SELECTED_FUNCTIONS = 2221; // Хотите активировать выбранные функции?
	public static final int IT_WILL_COST_150000_ADENA_TO_PLACE_SCOUTS_WILL_YOU_PLACE_THEM = 2222; // Разместить разведчиков стоит 150 000 аден. Разместить?
	public static final int IT_WILL_COST_200000_ADENA_FOR_A_FORTRESS_GATE_ENHANCEMENT_WILL_YOU_ENHANCE_IT = 2223; // Улучшение врат крепости будет стоить 200 000 аден. Улучшить?
	public static final int CROSSBOW_IS_PREPARING_TO_FIRE = 2224; // Арбалет готовится к стрельбе.
	public static final int THERE_ARE_NO_OTHER_SKILLS_TO_LEARN_PLEASE_COME_BACK_AFTER_S1ND_CLASS_CHANGE = 2225; // Нет других умений для изучения. Возвращайтесь после $s1-й смены класса.
	public static final int IT_IS_NOT_POSSIBLE_TO_REGISTER_FOR_THE_CASTLE_SIEGE_SIDE_OR_CASTLE_SIEGE_OF_A_HIGHER_CASTLE_IN = 2227; // Невозможно зарегистрироваться на осаду замка и осаду высшего замка в контракте.
	public static final int THE_SUPPLY_ITEMS_HAVE_NOT_NOT_BEEN_PROVIDED_BECAUSE_THE_HIGHER_CASTLE_IN_CONTRACT_DOESN_T_HAVE = 2231; // Предметы поддержки не допускаются, потому что у высшего замка в контракте недостаточно очков репутации.
	public static final int S1_WILL_BE_CRYSTALIZED_BEFORE_DESTRUCTION_WILL_YOU_CONTINUE = 2232; // $s1 будет кристаллизован перед разрушением. Продолжить?
	public static final int SIEGE_REGISTRATION_IS_NOT_POSSIBLE_DUE_TO_A_CONTRACT_WITH_A_HIGHER_CASTLE = 2233; // Регистрация на осаду невозможна из-за контракта с высшим замком.
	public static final int WILL_YOU_USE_THE_SELECTED_KAMAEL_RACE_ONLY_HERO_WEAPON = 2234; // Вы будете использовать выбранное Героическое Оружие, подходящее только расе Камаэль?
	public static final int THE_INSTANCE_ZONE_IN_USE_HAS_BEEN_DELETED_AND_CANNOT_BE_ACCESSED = 2235; // Временная зона была удалена и не может быть выбрана.
	public static final int S1_MINUTES_LEFT_FOR_WYVERN_RIDING = 2236; // До гонки на вивернах осталось: $s1 мин.
	public static final int S1_SECONDS_LEFT_FOR_WYVERN_RIDING = 2237; // До гонки на вивернах осталось: $s1 мин.
	public static final int YOU_HAVE_PARTICIPATED_IN_THE_SIEGE_OF_S1_THIS_SIEGE_WILL_CONTINUE_FOR_2_HOURS = 2238; // Вы участвуете в осаде $s1. Осада продлится 2 часа.
	public static final int THE_SIEGE_OF_S1_IN_WHICH_YOU_ARE_PARTICIPATING_HAS_FINISHED = 2239; // Осада $s1, в которой Вы принимали участие, закончена.
	public static final int YOU_CANNOT_REGISTER_FOR_THE_TEAM_BATTLE_CLAN_HALL_WAR_WHEN_YOUR_CLAN_LORD_IS_ON_THE_WAITING_LIST = 2240; // Вы не можете зарегистрироваться на командную битву за холл клана, когда глава вашего клана находится в листе ожидания на транзакцию.
	public static final int YOU_CANNOT_APPLY_FOR_A_CLAN_LORD_TRANSACTION_IF_YOUR_CLAN_HAS_REGISTERED_FOR_THE_TEAM_BATTLE = 2241; // Если ваш клан зарегистрирован на командную битву за холл клана, то транзакция главы клана недоступна.
	public static final int CLAN_MEMBERS_CANNOT_LEAVE_OR_BE_EXPELLED_WHEN_THEY_ARE_REGISTERED_FOR_THE_TEAM_BATTLE_CLAN_HALL = 2242; // Член клана не может покинуть клан, если клан заявлен на командную битву за холл клана.
	public static final int WHEN_A_CLAN_LORD_OCCUPYING_THE_BANDIT_STRONGHOLD_OR_WILD_BEAST_RESERVE_CLAN_HALL_IS_IN_DANGER = 2243; // Когда глава клана оккупирует Твердыню Разбойников или холл клана в Загоне Диких Зверей в опасности, предыдущий глава клана принимает участие в битве за холл клана.
	public static final int S1_MINUTES_REMAINING = 2244; // Осталось: $s1 мин.
	public static final int S1_SECONDS_REMAINING = 2245; // Осталось: $s1 сек.
	public static final int THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES = 2246; // Соревнование начнется через $s1 мин.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED = 2247; // Вы не можете оседлать питомца, находясь в измененной форме.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED = 2248; // Вы окаменели, и не можете оседлать питомца.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD = 2249; // Вы не можете оседлать питомца будучи мертвым.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING = 2250; // Вы не можете оседлать питомца во время рыбалки.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE = 2251; // Вы не можете оседлать питомца во время битвы.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL = 2252; // Вы не можете оседлать питомца во время дуэли.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING = 2253; // Вы не можете оседлать питомца, когда сидите.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SKILL_CASTING = 2254; // Вы не можете оседлать питомца во время выбора умений.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_CURSED_WEAPON_IS_EQUPPED = 2255; // Вы не можете оседлать питомца, если Вы экипированы проклятым оружием.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG = 2256; // Вы не можете оседлать питомца, если держите флаг.
	public static final int YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED = 2257; // Вы не можете оседлать питомца, если призван слуга или питомец.
	public static final int YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP = 2258; // Вы уже оседлали другого питомца.

	public static final int THE_PET_CAN_RUN_AWAY_IF_THE_HUNGER_GAUGE_IS_BELOW_10 = 2260; // Питомец может убежать, если его шкала голода опустится ниже 10%.
	public static final int THE_KEY_YOU_ASSIGNED_AS_A_SHORTCUT_KEY_IS_NOT_AVAILABLE_IN_THE_REGULAR_CHATTING_MODE = 2272; // Клавиша, назначенная вами как ярлык, недоступна в обычном режиме чата.
	public static final int THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUB_CLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE = 2273; // Это умение нельзя выучить подклассом. Вернитесь к основному классу и повторите попытку.
	public static final int YOU_ENTERED_AN_AREA_WHERE_YOU_CANNOT_THROW_AWAY_ITEMS = 2274; // Вы вошли в зону, где нельзя выбрасывать предметы.
	public static final int YOU_ARE_IN_AN_AREA_WHERE_YOU_CANNOT_CANCEL_PET_SUMMONING = 2275; // Вы в зоне, где нельзя отменить призыв питомца.
	public static final int PARTY_OF_S1 = 2277; // Группа персонажа $s1
	public static final int REMAINING_TIME_S1_S2 = 2278; // Осталось времени: $s1:$s2
	public static final int YOU_CAN_NO_LONGER_ADD_A_QUEST_TO_THE_QUEST_ALERTS = 2279; // Вы больше не можете добавлять квесты в журнал квестов.
	public static final int C1_HIT_YOU_FOR_S3_DAMAGE_AND_HIT_YOUR_SERVITOR_FOR_S4 = 2281; // $c1 наносит урон $s3 врагу $c2, также нанес урон $s4 объекту.
	public static final int LEAVE_FANTASY_ISLE = 2282; // Покинуть Остров Грез?
	public static final int YOU_HAVE_OBTAINED_ALL_THE_POINTS_YOU_CAN_GET_TODAY_IN_A_PLACE_OTHER_THAN_INTERNET_CAF = 2284; // Сегодня вы добыли все возможные очки в месте, отличном от интернет кафе.
	public static final int THIS_SKILL_CANNOT_REMOVE_THIS_TRAP = 2285; // Это умение не может обезвредить эту ловушку.
	public static final int YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_THE_BRACELET = 2286; // Вы не можете одеть предмет $s1, так как не носите браслеты.
	public static final int THERE_IS_NO_SPACE_TO_WEAR_S1 = 2287; // Нельзя одеть предмет $s1, нет свободного места.
	public static final int RESURRECTION_WILL_OCCUR_IN_S1_SECONDS = 2288; // Воскрешение произойдет через $s1 сек.
	public static final int THE_MATCH_BETWEEN_THE_PARTIES_IS_NOT_AVAILABLE_BECAUSE_ONE_OF_THE_PARTY_MEMBERS_IS_BEING = 2289; // Групповое соревнование невозможно, так как один из членов группы телепортирован.
	public static final int YOU_CANNOT_ASSIGN_SHORTCUT_KEYS_BEFORE_YOU_LOG_IN = 2290; // Вы не можете назначить ярлыки до захода в игру.
	public static final int YOU_CAN_OPERATE_THE_MACHINE_WHEN_YOU_PARTICIPATE_IN_THE_PARTY = 2291; // Вы не можете управлять этим механизмом, если состоите в группе.
	public static final int CURRENT_LOCATION__S1_S2_S3_INSIDE_THE_STEEL_CITADEL = 2293; // Текущая локация: $s1, $s2, $s3 (внутри Стальной Цитадели)
	public static final int THE_WIDTH_OF_THE_UPLOADED_BADGE_OR_INSIGNIA_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS = 2294; // Ширина загруженного значка или эмблемы не соответствует стандартам.
	public static final int THE_LENGTH_OF_THE_UPLOADED_BADGE_OR_INSIGNIA_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS = 2295; // Длина загруженного значка или эмблемы не соответствует стандартам.
	public static final int ROUND_S1 = 2297; // Раунд $s1
	public static final int THE_COLOR_OF_THE_BADGE_OR_INSIGNIA_THAT_YOU_WANT_TO_REGISTER_DOES_NOT_MEET_THE_STANDARD = 2298; // Цвет значка или эмблемы, которую Вы хотите зарегистрировать, не соответствует стандартам.
	public static final int THE_FILE_FORMAT_OF_THE_BADGE_OR_INSIGNIA_THAT_YOU_WANT_TO_REGISTER_DOES_NOT_MEET_THE_STANDARD = 2299; // Формат файла значка или эмблемы, которую вы хотите зарегистрировать, не соответствует требованиям.
	public static final int FAILED_TO_LOAD_KEYBOARD_SECURITY_MODULE_FOR_EFFECTIVE_GAMING_FUNCTIONALITY_WHEN_THE_GAME_IS_OVER = 2300; // Невозможно загрузить модуль защиты клавиатуры. Когда вы закончите игру, пожалуйста, проверьте все файлы в режиме автоматического обновления Lineage II.
	public static final int CURRENT_LOCATION__STEEL_CITADEL_RESISTANCE = 2301; // Текущая локация: внутри Стальной Цитадели.
	public static final int YOUR_VITAMIN_ITEM_HAS_ARRIVED_VISIT_THE_VITAMIN_MANAGER_IN_ANY_VILLAGE_TO_OBTAIN_IT = 2302; // Витамин доставлен. Чтобы получить его, найдите менеджера витаминов в деревне.
	public static final int RESURRECTION_IS_POSSIBLE_BECAUSE_OF_THE_COURAGE_CHARM_S_EFFECT_WOULD_YOU_LIKE_TO_RESURRECT_NOW = 2306; // Воскрешение возможно благодаря эффекту Отвага. Хотите воскреснуть?
	public static final int THE_TARGET_IS_RECEIVING_THE_COURAGE_CHARM_S_EFFECT = 2307; // Цель получает эффект Отвага.
	public static final int REMAINING_TIME__S1_DAYS = 2308; // Осталось времени: $s1 дн.
	public static final int REMAINING_TIME__S1_HOURS = 2309; // Осталось времени: $s1 ч.
	public static final int REMAINING_TIME__S1_MINUTES = 2310; // Осталось времени: $s1 мин.
	public static final int YOU_DO_NOT_HAVE_A_SERVITOR = 2311; // У Вас нет слуги.
	public static final int YOU_DO_NOT_HAVE_A_PET = 2312; // У Вас нет питомца.
	public static final int THE_VITAMIN_ITEM_HAS_ARRIVED = 2313; // Витамин доставлен.
	public static final int ONLY_AN_ENHANCED_SKILL_CAN_BE_CANCELLED = 2318; // Только улучшенное умение можно отменить.
	public static final int MASTERWORK_POSSIBLE = 2320; // Можно создать шедевр.
	public static final int CURRENT_LOCATION__INSIDE_KAMALOKA = 2321; // Текущая локация: внутри Камалоки
	public static final int CURRENT_LOCATION__INSIDE_NIA_KAMALOKA = 2322; // Текущая локация: внутри Земель Камалоки
	public static final int CURRENT_LOCATION__INSIDE_RIM_KAMALOKA = 2323; // Текущая локация: внутри Окрестностей Камалоки
	public static final int C1_YOU_CANNOT_ENTER_BECAUSE_YOU_HAVE_INSUFFICIENT_PC_CAFE_POINTS = 2324; // $c1, вы не можете войти, у вас недостаточно очков игрового клуба.
	public static final int ANOTHER_TELEPORT_IS_TAKING_PLACE_PLEASE_TRY_AGAIN_ONCE_THE_TELEPORT_IN_PROCESS_ENDS = 2325; // Происходит телепортация. Повторите попытку, когда процесс телепортации завершится.
	public static final int CLANS_OF_LEVEL_4_OR_ABOVE_CAN_REGISTER_FOR_HIDEAWAY_WARS_FOR_DEVASTATED_CASTLE_AND_FORTRESS_OF = 2328; // Кланы 4 уровня и выше могут участвовать в битве укреплений в Разоренном Замке и Крепости Неупокоенных.
	public static final int VITALITY_LEVEL_S1_S2 = 2329; // Уровень энергии $s1 $s2
	public static final int __EXPERIENCE_POINTS_BOOSTED_BY_S1 = 2330; // - Количество получаемого опыта составляет $s1% от обычного.
	public static final int RARE_S1 = 2331; // <Редкий> $s1
	public static final int SUPPLY_S1 = 2332; // <Ресурс> $s1
	public static final int YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT = 2333; // Награда не может быть получена, так как в инвентаре нет свободного места.
	public static final int SCORE_THAT_SHOWS_A_PLAYER_S_INDIVIDUAL_FAME_FAME_CAN_BE_OBTAINED_BY_PARTICIPATING_IN_A_TERRITORY = 2334; // Очки, показывающие личную репутацию. Могут быть добыты в осаде территории, замка, крепости, укрепления, в Подземном Колизее, на Фестивале Тьмы и Олимпиаде.
	public static final int THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND = 2335; // Витаминов больше не может быть найдено.
	public static final int IF_IT_S_A_DRAW_THE_PLAYER_WHO_FIRST_ENTERED_IS_FIRST = 2338; // При одинаковом результате первый игрок
	public static final int PLEASE_PLACE_THE_ITEM_TO_BE_ENCHANTED = 2339; // Перетащите предмет, который Вы хотите зачаровать.
	public static final int PLEASE_PLACE_THE_ITEM_FOR_RATE_INCREASE = 2340; // Перетащите предмет, шанс которого Вы хотите увеличить.
	public static final int THE_ENCHANT_WILL_BEGIN_ONCE_YOU_PRESS_THE_START_BUTTON_BELOW = 2341; // Нажмите кнопку "Начать", чтобы зачаровать предмет.
	public static final int SUCCESS_THE_ITEM_IS_NOW_A_S1 = 2342; // Поздравляем. Предмет успешно зачарован и стал $s1.
	public static final int FAILED_YOU_HAVE_OBTAINED_S2_OF_S1 = 2343; // Зачарование не удалось. Вы получили $s1 $s2ед.
	public static final int YOU_HAVE_BEEN_KILLED_BY_AN_ATTACK_FROM_C1 = 2344; // Вы были атакованы персонажем $c1 и погибли.
	public static final int YOU_HAVE_ATTACKED_AND_KILLED_C1 = 2345; // Вы убили персонажа $c1.
	public static final int YOUR_ACCOUNT_IS_TEMPORARILY_LIMITED_BECAUSE_YOUR_GAME_USE_GOAL_IS_PRESUMED_TO_BE_EMBEZZLEMENT_OF = 2346; // Выявлена попытка использования аккаунта с целью торговли игровыми предметами за реальные деньги. Для более подробной информации обратитесь в службу поддержки на сайте игры.
	public static final int S1_SECONDS_TO_GAME_END = 2347; // До завершения игры $s1 сек!
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE = 2348; // Во время боя установить флаг для последующего возврата к нему невозможно.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE = 2349; // Во время полномасштабных сражений - осад крепостей, замков, холлов клана - установить флаг для последующего возврата к нему невозможно.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL = 2350; // Во время дуэли установить флаг для последующего возврата к нему невозможно.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING = 2351; // Во время полета возврат к флагу недоступен.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH = 2352; // Во время Олимпиады возврат к флагу недоступен.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_FLINT_OR_PARALYZED_STATE = 2353; // В состоянии паралича или окаменения возврат к флагу недоступен.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD = 2354; // Если Ваш персонаж умер, Вы не можете вернуться к флагу.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA = 2355; // Вы находитесь в локации, на которой возврат к флагу недоступен.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER = 2356; // Вы не можете вернуться к флагу, находясь в воде.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE = 2357; // Вы не можете вернуться к флагу, находясь во временной зоне.
	public static final int YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION = 2358; // Вы не можете установить еще один флаг для возврата к нему.
	public static final int YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM = 2359; // Вы не можете вернуться к флагу без соответствующего предмета.
	public static final int MY_TELEPORTS_SPELLBK__S1 = 2360; // Свиток Возврата к Флагу: $s1ед.
	public static final int CURRENT_LOCATION__S1 = 2361; // Текущие координаты: $s1
	public static final int THE_SAVED_TELEPORT_LOCATION_WILL_BE_DELETED_DO_YOU_WISH_TO_CONTINUE = 2362; // Удаление сохраненного для телепорта места. Продолжить?
	public static final int YOUR_ACCOUNT_HAS_BEEN_CONFIRMED_AS_USING_ANOTHER_PERSON_S_NAME_ALL_GAME_SERVICES_HAVE_BEEN = 2363; // Ваш аккаунт заблокирован, так как он зарегистрирован под другим именем. За дополнительной информацией обращайтесь в службу поддержки.
	public static final int THE_ITEM_HAS_EXPIRED_AFTER_ITS_S1_PERIOD = 2364; // $s1: период использования истек. Предмет исчез.
	public static final int THE_DESIGNATED_ITEM_HAS_EXPIRED_AFTER_ITS_S1_PERIOD = 2365; // Период использования предмета истек, и он исчез.
	public static final int S1_S_BLESSING_HAS_RECOVERED_HP_BY_S2 = 2367; // HP увеличиваются на $s2 (благословение персонажа $s1).
	public static final int S1_S_BLESSING_HAS_RECOVERED_MP_BY_S2 = 2368; // MP увеличиваются на $s2 (благословение персонажа $s1).
	public static final int S1_S_BLESSING_HAS_FULLY_RECOVERED_HP_AND_MP = 2369; // HP и MP полностью восстановлены (благословение персонажа $s1).
	public static final int RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS = 2370; // Через $s1 сек Вы будете воскрешены в комнате ожидания.
	public static final int C1_WAS_REPORTED_AS_A_BOT = 2371; // Подана жалоба, что $c1 использует бота.
	public static final int THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_HUNTING_HELPER_PET_LEAVES = 2372; // Питомец скоро сбежит.
	public static final int THE_HUNTING_HELPER_PET_IS_NOW_LEAVING = 2373; // Питомец сбежал от Вас.
	public static final int END_MATCH = 2374; // Соревнование окончено!
	public static final int THE_HUNTING_HELPER_PET_CANNOT_BE_RETURNED_BECAUSE_THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_IT = 2375; // Нельзя вернуть питомца.
	public static final int YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE = 2376; // Нельзя получить Витамин во время обмена.
	public static final int YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEFIELD = 2377; // Нельзя подать жалобу на персонажа, находящегося в мирной зоне или на поле боя.
	public static final int YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED = 2378; // Нельзя подавать жалобу, если клан объявил войну.
	public static final int YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_EXP_AFTER_CONNECTING = 2379; // Нельзя подавать жалобу на персонажа, который еще не получил опыт после захода в игру.
	public static final int YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME___CHARACTER = 2380; // Нельзя отправить больше положенного количества жалоб с одного персонажа.
	public static final int YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME___ACCOUNT = 2381; // Нельзя отправить больше положенного количества жалоб с одного аккаунта.
	public static final int YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME___CLAN = 2382; // Нельзя отправить больше положенного количества жалоб с одного клана.
	public static final int YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME___IP = 2383; // Нельзя отправить больше положенного количества жалоб с одного IP.
	public static final int THIS_ITEM_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_THE_ENHANCEMENT_SPELLBOOK = 2384; // Этот предмет не подходит по правилам Свитка Зачарования.
	public static final int THIS_IS_AN_INCORRECT_SUPPORT_ENHANCEMENT_SPELLBOOK = 2385; // Неверный Свиток Дополнительного Зачарования.
	public static final int THIS_ITEM_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_THE_SUPPORT_ENHANCEMENT_SPELLBOOK = 2386; // Этот предмет не подходит по правилам Свитка Дополнительного Зачарования.
	public static final int REGISTRATION_OF_THE_SUPPORT_ENHANCEMENT_SPELLBOOK_HAS_FAILED = 2387; // Не удалось зарегистрировать Свиток Дополнительного Зачарования.
	public static final int THE_MAXIMUM_ACCUMULATION_ALLOWED_OF_PC_CAFE_POINTS_HAS_BEEN_EXCEEDED_YOU_CAN_NO_LONGER_ACQUIRE = 2389; // Количество очков Интернет-кафе превысило лимит. Больше невозможно получить очки.
	public static final int YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT = 2390; // Достигнут лимит ячеек для Флагов. Увеличить это количество невозможно.
	public static final int YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT = 2391; // Вы воскресли благодаря Благословенному Перу.
	public static final int THE_VITAMIN_ITEM_CANNOT_BE_LOCATED_BECAUSE_OF_A_TEMPORARY_CONNECTION_ERROR = 2392; // Из-за временной ошибки обнаружение Витаминов невозможно.
	public static final int YOU_HAVE_ACQUIRED_S1_PC_CAFE_POINTS = 2393; // Вы получили ежедневные очки за использование РС-клуба в размере $s1 очков.
	public static final int THAT_SKILL_CANNOT_BE_USED_BECAUSE_YOUR_PET_SERVITOR_LACKS_SUFFICIENT_MP = 2394; // У питомца/слуги не хватает МР для использование данного умения.
	public static final int THAT_SKILL_CANNOT_BE_USED_BECAUSE_YOUR_PET_SERVITOR_LACKS_SUFFICIENT_HP = 2395; // У питомца/слуги не хватает НР для использование данного умения.
	public static final int THAT_PET_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING = 2396; // Питомец/слуга еще не готов повторить умение.
	public static final int PLEASE_USE_A_MY_TELEPORT_SCROLL = 2397; // Воспользуйтесь Книгой Возврата к Флагу.
	public static final int YOU_HAVE_NO_OPEN_MY_TELEPORTS_SLOTS = 2398; // Данная ячейка не используется.
	public static final int S1_S_OWNERSHIP_EXPIRES_IN_S2_MINUTES = 2399; // Время действия $s1 завершится через $s2 мин.
	public static final int INSTANT_ZONE_CURRENTLY_IN_USE__S1 = 2400; // Текущая временная зона: $s1
	public static final int CLAN_LORD_C2_WHO_LEADS_CLAN_S1_HAS_BEEN_DECLARED_THE_LORD_OF_THE_S3_TERRITORY = 2401; // Глава клана $s1, $c2, был избран Правителем Земель $s3а.
	public static final int THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED = 2402; // Время подачи заявок на участие в Битве за Земли истекло.
	public static final int TERRITORY_WAR_BEGINS_IN_10_MINUTES = 2403; // До начала Битвы за Земли осталось 10мин!
	public static final int TERRITORY_WAR_BEGINS_IN_5_MINUTES = 2404; // До начала Битвы за Земли осталось 5мин!
	public static final int TERRITORY_WAR_BEGINS_IN_1_MINUTE = 2405; // До начала Битвы за Земли осталось 1мин!
	public static final int S1_S_TERRITORY_WAR_HAS_BEGUN = 2406; // Битва за Земли $s1 началась.
	public static final int S1_S_TERRITORY_WAR_HAS_ENDED = 2407; // Битва за Земли $s1 завершена.
	public static final int YOU_HAVE_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_TEAM_MATCH_EVENT = 2408; // Вы добавлены в список ожидания игры без ограничения по классам.
	public static final int THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED = 2409; // Количество ячеек для возврата к флагу увеличено.
	public static final int YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA = 2410; // Переместиться на выбранную местность с помощью возврата к флагу невозможно.
	public static final int C1_HAS_ISSUED_A_PARTY_INVITATION_WHICH_YOU_AUTOMATICALLY_REJECTED_TO_RECEIVE_PARTY_INVITATIONS = 2411; // Вы получили приглашение в ступить в группу от $c1, но у Вас установлена блокировка получения приглашений в группу. Чтобы получить приглашение, измените настройки.
	public static final int THE_BIRTHDAY_GIFT_HAS_BEEN_DELIVERED_VISIT_THE_VITAMIN_MANAGER_IN_ANY_VILLAGE_TO_OBTAIN_IT = 2412; // Вы получили подарок на День Рождения. Получить его можно у менеджера витаминов.
	public static final int YOU_ARE_REGISTERING_AS_A_RESERVE_ON_THE_AERIAL_CLEFT_RED_TEAM_S_BATTLEFIELD_DO_YOU_WISH_TO = 2413; // Регистрация на битву за Ущелье на стороне красной команды. Продолжить?
	public static final int YOU_ARE_REGISTERING_AS_A_RESERVE_ON_THE_AERIAL_CLEFT_BLUE_TEAM_S_BATTLEFIELD_DO_YOU_WISH_TO = 2414; // Регистрация на битву за Ущелье на стороне синей команды. Продолжить?
	public static final int YOU_HAVE_REGISTERED_AS_A_RESERVE_ON_THE_AERIAL_CLEFT_RED_TEAM_S_BATTLEFIELD_WHEN_IN_BATTLE_THE = 2415; // Вы вступили в красную команду. В течение битвы команда может быть изменена для поддержания баланса игры.
	public static final int YOU_HAVE_REGISTERED_AS_A_RESERVE_ON_THE_AERIAL_CLEFT_BLUE_TEAM_S_BATTLEFIELD_WHEN_IN_BATTLE_THE = 2416; // Вы вступили в синюю команду. В течение битвы команда может быть изменена для поддержания баланса игры.
	public static final int YOU_ARE_CANCELING_THE_AERIAL_CLEFT_BATTLEFIELD_REGISTRATION_DO_YOU_WISH_TO_CONTINUE = 2417; // Отмена участия в битве за Ущелье. Продолжить?
	public static final int THE_AERIAL_CLEFT_BATTLEFIELD_REGISTRATION_HAS_BEEN_CANCELED = 2418; // Вы отказались от участия в битве за Ущелье.
	public static final int THE_AERIAL_CLEFT_BATTLEFIELD_HAS_BEEN_ACTIVATED_FLIGHT_TRANSFORMATION_WILL_BE_POSSIBLE_IN = 2419; // Активировано поле битвы за Ущелье. Через 40 сек. станет возможным перевоплощение в летающее существо.
	public static final int THE_BATTLEFIELD_CLOSES_IN_1_MINUTE = 2420; // Битва завершится через 1мин.
	public static final int THE_BATTLEFIELD_CLOSES_IN_10_SECONDS = 2421; // Битва завершится через 10мин.
	public static final int EP_OR_ENERGY_POINTS_REFERS_TO_FUEL = 2422; // EP означает очки энергии - запас топлива.
	public static final int EP_CAN_BE_REFILLED_BY_USING_A_S1_WHILE_SAILING_ON_AN_AIRSHIP = 2423; // ЕР можно восстановить с помощью $s1, находясь внутри летающего объекта.
	public static final int THE_COLLECTION_HAS_FAILED = 2424; // Вы не смогли получить предмет.
	public static final int THE_AERIAL_CLEFT_BATTLEFIELD_HAS_BEEN_CLOSED = 2425; // Битва за Ущелье завершена.
	public static final int C1_HAS_BEEN_EXPELLED_FROM_THE_TEAM = 2426; // Персонаж $c1 покинул команду.
	public static final int THE_RED_TEAM_IS_VICTORIOUS = 2427; // Победила красная команда.
	public static final int THE_BLUE_TEAM_IS_VICTORIOUS = 2428; // Победила синяя команда.
	public static final int C1_HAS_BEEN_DESIGNATED_AS_THE_TARGET = 2429; // Персонаж $c1 выбран главной целью атаки.
	public static final int C1_HAS_FALLEN_THE_RED_TEAM_S_POINTS_HAVE_INCREASED = 2430; // Персонаж $c1 потерпел крушение. Красная команда получает очки.
	public static final int C2_HAS_FALLEN_THE_BLUE_TEAM_S_POINTS_HAVE_INCREASED = 2431; // Персонаж $c2 потерпел крушение. Синяя команда получает очки.
	public static final int THE_CENTRAL_STRONGHOLD_S_COMPRESSOR_HAS_BEEN_DESTROYED = 2432; // Срединный Испаритель разрушен.
	public static final int THE_FIRST_STRONGHOLD_S_COMPRESSOR_HAS_BEEN_DESTROYED = 2433; // 1-й Испаритель разрушен.
	public static final int THE_SECOND_STRONGHOLD_S_COMPRESSOR_HAS_BEEN_DESTROYED = 2434; // 2-й Испаритель разрушен.
	public static final int THE_THIRD_STRONGHOLD_S_COMPRESSOR_HAS_BEEN_DESTROYED = 2435; // 3-й Испаритель разрушен.
	public static final int THE_CENTRAL_STRONGHOLD_S_COMPRESSOR_IS_WORKING = 2436; // Срединный Испаритель запущен.
	public static final int THE_FIRST_STRONGHOLD_S_COMPRESSOR_IS_WORKING = 2437; // 1-й Испаритель запущен
	public static final int THE_SECOND_STRONGHOLD_S_COMPRESSOR_IS_WORKING = 2438; // 2-й Испаритель запущен
	public static final int THE_THIRD_STRONGHOLD_S_COMPRESSOR_IS_WORKING = 2439; // 3-й Испаритель запущен
	public static final int C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_MATCH_EVENT = 2440; // $c1 находится в списке ожидающих на участие в игре без ограничения по классам.
	public static final int ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH = 2441; // Подать заявку на командные соревнования может только Лидер Группы.
	public static final int THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM = 2442; // Вы не подходите под условия соревнований. Для участия в соревнованиях в группе должно состоять 3 человека.
	public static final int FLAMES_FILLED_WITH_THE_WRATH_OF_VALAKAS_ARE_ENGULFING_YOU = 2443; // Гнев Валакаса направлен на Вас.
	public static final int FLAMES_FILLED_WITH_THE_AUTHORITY_OF_VALAKAS_ARE_BINDING_YOUR_MIND = 2444; // Пламя Валакаса сокрушило Ваше сознание.
	public static final int THE_BATTLEFIELD_CHANNEL_HAS_BEEN_ACTIVATED = 2445; // Канал битвы активирован.
	public static final int THE_BATTLEFIELD_CHANNEL_HAS_BEEN_DEACTIVATED = 2446; // Канал битвы деактивирован.
	public static final int THE_CLOAK_EQUIP_HAS_BEEN_REMOVED_BECAUSE_THE_ARMOR_SET_EQUIP_HAS_BEEN_REMOVED = 2451; // Так как Вы сняли комплект доспехов, плащ также снят.
	public static final int THE_INVENTORY_IS_FULL_SO_IT_CANNOT_BE_EQUIPPED_OR_REMOVED_ON_THE_BELT = 2452; // Дополнительные ячейки инвентаря переполнены, поэтому снять/надеть пояс невозможно.
	public static final int THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_A_NECESSARY_ITEM_IS_NOT_EQUIPPED = 2453; // Вы не надели все необходимые предметы, поэтому плащ надеть нельзя.
	public static final int KRESNIK_CLASS_AIRSHIP = 2454; // Летающий корабль типа Крейсера
	public static final int THE_AIRSHIP_MUST_BE_SUMMONED_IN_ORDER_FOR_YOU_TO_BOARD = 2455; // Вы не вызвали Летающий Корабль, поэтому не можете воспользоваться им.
	public static final int IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLAN_S_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER = 2456; // Для того чтобы получить Летающий Корабль, клан должен быть выше 5-го уровня.
	public static final int AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE = 2457; // Вы не ввели разрешение на вызов Летающего Корабля, или Ваш клан не обладает им.
	public static final int THE_AIRSHIP_OWNED_BY_THE_CLAN_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER = 2458; // Летающий Корабль Вашего клана уже используется.
	public static final int THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED = 2459; // Вы уже получили разрешение на использование Летающего Корабля.
	public static final int THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS = 2460; // У вашего клана уже есть Летающий Корабль.
	public static final int THE_AIRSHIP_OWNED_BY_THE_CLAN_CAN_ONLY_BE_PURCHASED_BY_THE_CLAN_LORD = 2461; // Летающий Корабль клана может быть приобретен исключительно Главой Клана.
	public static final int THE_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DON_T_HAVE_ENOUGH_S1 = 2462; // Вы не можете вызвать Летающий Корабль, так как не хватает $s1.
	public static final int THE_AIRSHIP_S_FUEL_EP_WILL_SOON_RUN_OUT = 2463; // Топливо Летающего Корабля (EP) на исходе.
	public static final int THE_AIRSHIP_S_FUEL_EP_HAS_RUN_OUT_THE_AIRSHIP_S_SPEED_WILL_BE_GREATLY_DECREASED_IN_THIS = 2464; // Закончилось Топливо Летающего Корабля (EP). Скорость корабля будет минимальной.
	public static final int YOU_HAVE_SELECTED_A_NON_CLASS_LIMITED_TEAM_MATCH_DO_YOU_WISH_TO_PARTICIPATE = 2465; // Вы выбрали игру без ограничения по классам. Вы правда хотите принять в ней участие?
	public static final int A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS = 2466; // Питомец, вызванный в режиме помощника, не может использовать умения.
	public static final int DO_YOU_WISH_TO_BEGIN_THE_GAME_NOW = 2467; // Вы хотите начать игру прямо сейчас?
	public static final int YOU_HAVE_USED_A_REPORT_POINT_ON_C1_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT = 2468; // Вы использовали очки для того, чтобы сообщить о $c1. У Вас осталось $s2 очков.
	public static final int YOU_HAVE_USED_ALL_AVAILABLE_POINTS_POINTS_ARE_RESET_EVERYDAY_AT_NOON = 2469; // Вы использовали все имевшиеся очки. Очки будут восстановлены после полудня.
	public static final int THIS_CHARACTER_CANNOT_MAKE_A_REPORT_YOU_CANNOT_MAKE_A_REPORT_WHILE_LOCATED_INSIDE_A_PEACE_ZONE = 2470; // Сообщить о данном персонаже нельзя. Он находится в Мирной Зоне, на Поле Боя или участвует в Войне Кланов или Олимпиаде.
	public static final int THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR = 2471; // Сообщить о данном персонаже нельзя. О данном персонаже уже сообщил член Вашего клана, альянса, или сообщение было подано с Вашего IP.
	public static final int THIS_CHARACTER_CANNOT_MAKE_A_REPORT_BECAUSE_ANOTHER_CHARACTER_FROM_THIS_ACCOUNT_HAS_ALREADY_DONE = 2472; // Сообщить о данном персонаже нельзя. Уже получено сообщение о нем с другого персонажа Вашего аккаунта.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_CHATTING_WILL_BE_BLOCKED_FOR_10 = 2473; // Получено сообщение о том, что Вы используете нелегальную программу. Вы не сможете использовать чат в течение 10-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_60 = 2474; // Получено сообщение о том, что Вы используете нелегальную программу. Вы не сможете вступить в группу в течение 60-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_120 = 2475; // Получено сообщение о том, что Вы используете нелегальную программу. Вы не сможете вступить в группу в течение 120-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_180 = 2476; // Получено сообщение о том, что Вы используете нелегальную программу. Вы не сможете вступить в группу в течение 180-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_120 = 2477; // Получено сообщение о том, что Вы используете нелегальную программу. Вы будете ограничены в движениях в течение 120-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180 = 2478; // Получено сообщение о том, что Вы используете нелегальную программу. Вы будете ограничены в движениях в течение 180-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_1 = 2479; // Получено сообщение о том, что Вы используете нелегальную программу. Вы будете ограничены в движениях в течение 180-ти мин.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_MOVING_WILL_BE_BLOCKED_FOR_120_MINUTES = 2480; // Получено сообщение о том, что Вы используете нелегальную программу. Вы будете не сможете перемещаться в течение 120-ти мин.
	public static final int C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_HAS_BEEN_INVESTIGATED = 2481; // Персонаж $c1 сообщил о том, что Вы используете нелегальную программу, поэтому Вы не сможете получить награду.
	public static final int C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY = 2482; // Персонаж $c1 сообщил о том, что Вы используете нелегальную программу, поэтому Вы не можете вступить в группу.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CHATTING_IS_NOT_ALLOWED = 2483; // Получено сообщение о том, что Вы используете нелегальную программу, поэтому чат запрещен.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED = 2484; // Получено сообщение о том, что Вы используете нелегальную программу, поэтому Вы не сможете вступить в группу.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_ACTIVITIES_ARE_ONLY_ALLOWED_WITHIN = 2485; // Получено сообщение о том, что Вы используете нелегальную программу, поэтому Вы будете ограничены в движениях.
	public static final int YOU_HAVE_BEEN_BLOCKED_DUE_TO_VERIFICATION_THAT_YOU_ARE_USING_A_THIRD_PARTY_PROGRAM_SUBSEQUENT = 2486; // До сих пор Вы получали штрафы за использование нелегальных программ в соответствии с количеством имеющихся у Вас очков. Со следующего раза штраф будет предусматривать не только игровые штрафы, но и неудобства в использовании аккаунта.
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_CONNECTION_HAS_BEEN_ENDED_PLEASE = 2487; // Получено сообщение о том, что Вы используете нелегальную программу, поэтому игра завершена. Вы сможете снова войти в игру после идентификации личности.
	public static final int YOU_CANNOT_ENTER_AERIAL_CLEFT_BECAUSE_YOU_ARE_NOT_AT_THE_RIGHT_LEVEL_ENTRY_IS_POSSIBLE_ONLY = 2488; // Ваш уровень не соответствует требованиям. Чтобы попасть в Ущелье, необходимо достичь 75-го уровня.
	public static final int YOU_MUST_TARGET_A_CONTROL_DEVICE_IN_ORDER_TO_PERFORM_THIS_ACTION = 2489; // Вы не выбрали в качестве цели существо, которым можно управлять, поэтому данная функция недоступна.
	public static final int YOU_CANNOT_PERFORM_THIS_ACTION_BECAUSE_YOU_ARE_TOO_FAR_AWAY_FROM_THE_CONTROL_DEVICE = 2490; // Вы находитесь слишком далеко от существа, поэтому контроль над ним невозможен.
	public static final int YOUR_SHIP_CANNOT_TELEPORT_BECAUSE_IT_DOES_NOT_HAVE_ENOUGH_FUEL_FOR_THE_TRIP = 2491; // Не хватает топлива Летающего Корабля, чтобы телепортироваться.
	public static final int THE_AIRSHIP_HAS_BEEN_SUMMONED_IT_WILL_AUTOMATICALLY_DEPART_IN_S_MINUTES = 2492; // Вы вызвали Летающий Корабль. Отправление через %s.
	public static final int ENTER_CHAT_MODE_IS_AUTOMATICALLY_ENABLED_WHEN_YOU_ARE_IN_A_FLYING_TRANSFORMATION_STATE = 2493; // Перевоплотившись в летающее существо, Вы входите в режим внутреннего чата.
	public static final int ENTER_CHAT_MODE_IS_AUTOMATICALLY_ENABLED_WHEN_YOU_ARE_IN_AIRSHIP_CONTROL_STATE = 2494; // Начав управление Летающим Кораблем, Вы входите в режим внутреннего чата.
	public static final int W_GO_FORWARD_S_STOP_A_TURN_LEFT_D_TURN_RIGHT_E_INCREASE_ALTITUDE_AND_Q_DECREASE_ALTITUDE = 2495; // W(Вперед), S(Остановиться), A(Влево), D(Вправо), E(Набрать Высоту), Q(Снизиться).
	public static final int IF_YOU_CLICK_ON_A_SKILL_DESIGNATED_ON_YOUR_SHORTCUT_BAR_THAT_SLOT_IS_ACTIVATED_ONCE_ACTIVATED = 2496; // Нажав клавишу, соответствующую ярлыку, Вы активируете панель ярлыков, а повторно нажав на нее или на пробел, Вы активируете ярлык.
	public static final int TO_CLOSE_THE_CURRENTLY_OPEN_TIP_PLEASE_CANCEL_THE_CHECKED_BOX__SYSTEM_TUTORIAL__IN_OPTIONS = 2497; // Чтобы отключить подсказки, выключите отметку "Системное Обучение" в настройках игры.
	public static final int DURING_THE_AIRSHIP_CONTROL_STATE_YOU_CAN_ALSO_CHANGE_ALTITUDE_USING_THE_BUTTON_AT_THE_CENTER_OF = 2498; // Изменить высоту Летающего Корабля можно также с помощью иконок панели управления.
	public static final int YOU_CANNOT_COLLECT_BECAUSE_SOMEONE_ELSE_IS_ALREADY_COLLECTING = 2499; // Кто-то уже собирает руду, поэтому Вы не сможете собрать руду сейчас.
	public static final int THE_COLLECTION_HAS_SUCCEEDED = 2500; // У Вас получилось добыть руду.
	public static final int YOU_WILL_BE_MOVED_TO_THE_PREVIOUS_CHATTING_CHANNEL_TAB = 2501; // Вы будете перемещены в предыдущую таблицу канала чата.
	public static final int YOU_WILL_BE_MOVED_TO_THE_NEXT_CHATTING_CHANNEL_TAB = 2502; // Вы будете перемещены в следующую таблицу канала чата.
	public static final int THE_CURRENTLY_SELECTED_TARGET_WILL_BE_CANCELLED = 2503; // Выбранная цель будет отменена.
	public static final int FOCUS_WILL_BE_MOVED_TO_CHAT_WINDOW = 2504; // Акцент переместится на окно чата.
	public static final int OPENS_OR_CLOSES_THE_INVENTORY_WINDOW = 2505; // Открывает и закрывает окно инвентаря.
	public static final int TEMPORARILY_HIDES_ALL_OPEN_WINDOWS = 2506; // Временно прячет все открытые окна.
	public static final int CLOSES_ALL_OPEN_WINDOWS = 2507; // Закрывает все окна.
	public static final int OPENS_THE_GM_MANAGER_WINDOW = 2508; // Открывает окно управления GM.
	public static final int OPENS_THE_GM_PETITION_WINDOW = 2509; // Открывает окно петиций GM.
	public static final int THE_BUFF_IN_THE_PARTY_WINDOW_IS_TOGGLED_BUFF_FOR_ONE_INPUT_DEBUFF_FOR_TWO_INPUTS_A_SONG_AND = 2510; // Положительный эффект в окне группы включен. Положительный эффект для одного ввода, отрицательный эффект для двух, танец и пение для трех и выключение для четырех.
	public static final int ACTIVATES_OR_DEACTIVATES_MINIMUM_FRAME_FUNCTION = 2511; // Активирует и дезактивирует минимум блокировочных функций.
	public static final int RUNS_OR_CLOSES_THE_MSN_MESSENGER_WINDOW = 2512; // Открывает и закрывает окно сообщений MSN.

	public static final int ASSIGN_1ST_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2513; // Назначьте ярлык к 1-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_2ND_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2514; // Назначьте ярлык к 2-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_3RD_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2515; // Назначьте ярлык к 3-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_4TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2516; // Назначьте ярлык к 4-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_5TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2517; // Назначьте ярлык к 5-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_6TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2518; // Назначьте ярлык к 6-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_7TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2519; // Назначьте ярлык к 7-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_8TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2520; // Назначьте ярлык к 8-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_9TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2521; // Назначьте ярлык к 9-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_10TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2522; // Назначьте ярлык к 10-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_11TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2523; // Назначьте ярлык к 11-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_12TH_SLOT_SHORTCUT_IN_THE_SHORTCUT_BASE_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT_CANNOT_BE = 2524; // Назначьте ярлык к 12-му слоту в окне базовых ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_1ST_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2525; // Назначьте ярлык к 1-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_2ND_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2526; // Назначьте ярлык к 2-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_3RD_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2527; // Назначьте ярлык к 3-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_4TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2528; // Назначьте ярлык к 4-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_5TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2529; // Назначьте ярлык к 5-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_6TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2530; // Назначьте ярлык к 6-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_7TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2531; // Назначьте ярлык к 7-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_8TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2532; // Назначьте ярлык к 8-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_9TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2533; // Назначьте ярлык к 9-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_10TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2534; // Назначьте ярлык к 10-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_11TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2535; // Назначьте ярлык к 11-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_12TH_SLOT_SHORTCUT_IN_THE_1ST_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2536; // Назначьте ярлык к 12-му слоту в 1-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_1ST_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2537; // Назначьте ярлык к 1-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_2ND_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2538; // Назначьте ярлык к 2-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_3RD_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2539; // Назначьте ярлык к 3-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_4TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2540; // Назначьте ярлык к 4-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_5TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2541; // Назначьте ярлык к 5-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_6TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2542; // Назначьте ярлык к 6-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_7TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2543; // Назначьте ярлык к 7-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_8TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2544; // Назначьте ярлык к 8-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_9TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2545; // Назначьте ярлык к 9-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_10TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2546; // Назначьте ярлык к 10-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_11TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2547; // Назначьте ярлык к 11-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.
	public static final int ASSIGN_12TH_SLOT_SHORTCUT_IN_THE_2ND_SHORTCUT_EXPANDED_WINDOW_COMBINATION_OF_CTRL_AND_SHIFT = 2548; // Назначьте ярлык к 12-му слоту в 2-м окне расширенных ярлыков. Комбинация Ctrl+Shift не может быть назначена.

	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_1 = 2549; // Переместите лист ярлыков в окно базовых ярлыков на стр. 1.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_2 = 2550; // Переместите лист ярлыков в окно базовых ярлыков на стр. 2.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_3 = 2551; // Переместите лист ярлыков в окно базовых ярлыков на стр. 3.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_4 = 2552; // Переместите лист ярлыков в окно базовых ярлыков на стр. 4.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_5 = 2553; // Переместите лист ярлыков в окно базовых ярлыков на стр. 5.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_6 = 2554; // Переместите лист ярлыков в окно базовых ярлыков на стр. 6.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_7 = 2555; // Переместите лист ярлыков в окно базовых ярлыков на стр. 7.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_8 = 2556; // Переместите лист ярлыков в окно базовых ярлыков на стр. 8.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_9 = 2557; // Переместите лист ярлыков в окно базовых ярлыков на стр. 9.
	public static final int MOVE_THE_SHORTCUT_PAGE_IN_THE_SHORTCUT_BASE_WINDOW_TO_PAGE_10 = 2558; // Переместите лист ярлыков в окно базовых ярлыков на стр. 10.

	public static final int OPENS_AND_CLOSES_THE_ACTION_WINDOW_EXECUTING_CHARACTER_ACTIONS_AND_GAME_COMMANDS = 2559; // Открывает и закрывает окно действий, исполняющее действия персонажа и игровые команды.
	public static final int OPENS_AND_CLOSES_THE_GAME_BULLETIN_BOARD = 2560; // Открывает и закрывает доску объявлений.
	public static final int OPENS_AND_CLOSES_THE_CALCULATOR = 2561; // Открывает и закрывает калькулятор.
	public static final int HIDES_OR_SHOWS_THE_CHAT_WINDOW_THE_WINDOW_ALWAYS_SHOWS_BY_DEFAULT = 2562; // Прячет и показывает окно чата. По умолчанию оно всегда показывается.
	public static final int OPENS_AND_CLOSES_THE_CLAN_WINDOW_CONFIRMING_INFORMATION_OF_THE_INCLUDED_CLAN_AND_PERFORMS_THE = 2563; // Открывает и закрывает окно клана, предоставляющее информацию о клане и разнообразные установки касательно клана.
	public static final int OPENS_AND_CLOSES_THE_STATUS_WINDOW_SHOWING_THE_DETAILED_STATUS_OF_A_CHARACTER_THAT_YOU_CREATED = 2564; // Открывает и закрывает окно статуса, показывающее детали состояния персонажа.
	public static final int OPENS_AND_CLOSES_THE_HELP_WINDOW = 2565; // Открывает и закрывает окно помощи.
	public static final int OPENS_OR_CLOSES_THE_INVENTORY_WINDOW_ = 2566; // Открывает и закрывает окно инвентаря.
	public static final int OPENS_AND_CLOSES_THE_MACRO_WINDOW_FOR_MACRO_SETTINGS = 2567; // Открывает и закрывает окно настроек макросов.
	public static final int OPENS_AND_CLOSES_THE_SKILL_WINDOW_DISPLAYING_THE_LIST_OF_SKILLS_THAT_YOU_CAN_USE = 2568; // Открывает и закрывает окно умений, отображающее перечень доступных к использованию умений.
	public static final int HIDES_OR_SHOWS_THE_MENU_WINDOW_THE_WINDOW_SHOWS_BY_DEFAULT = 2569; // Прячет и показывает окно меню. По умолчанию показано.
	public static final int OPENS_AND_CLOSES_THE_MINI_MAP_SHOWING_DETAILED_INFORMATION_ABOUT_THE_GAME_WORLD = 2570; // Открывает и закрывает миникарту, показывающую детализированную информацию об игровом мире.
	public static final int OPENS_AND_CLOSES_THE_OPTION_WINDOW = 2571; // Открывает и закрывает окно опций.
	public static final int OPEN_AND_CLOSE_THE_PARTY_MATCHING_WINDOW_USEFUL_IN_ORGANIZING_A_PARTY_BY_HELPING_TO_EASILY_FIND = 2572; // Открывает и закрывает окно подбора группы, помогающее найти других персонажей, желающих сформировать группу.
	public static final int OPEN_AND_CLOSE_THE_QUEST_JOURNAL_DISPLAYING_THE_PROGRESS_OF_QUESTS = 2573; // Открывает и закрывает журнал квестов, отображающий прогресс в прохождении квестов.
	public static final int HIDES_OR_RE_OPENS_THE_RADAR_MAP_WHICH_ALWAYS_APPEARS_BY_DEFAULT = 2574; // Прячет и показывает радар. По умолчанию показывает.
	public static final int HIDE_OR_SHOW_THE_STATUS_WINDOW_THE_WINDOW_WILL_SHOW_BY_DEFAULT = 2575; // Прячет и показывает окно статуса. По умолчанию показывает.
	public static final int OPENS_AND_CLOSES_THE_SYSTEM_MENU_WINDOW_ENABLES_DETAILED_MENU_SELECTION = 2576; // Открывает и закрывает окно системных опций, разрешающее детальные настройки меню.
	public static final int DO_NOT_SHOW_DROP_ITEMS_DROPPED_IN_THE_WORLD_GAME_PERFORMANCE_SPEED_CAN_BE_ENHANCED_BY_USING_THIS = 2577; // Не показывать выпавшие предметы. Эта опция может улучшить скорость игры.
	public static final int A_KEY_TO_AUTOMATICALLY_SEND_WHISPERS_TO_A_TARGETED_CHARACTER = 2578; // Автоматически посылает приватное сообщение выбранному персонажу.
	public static final int TURNS_OFF_ALL_GAME_SOUNDS = 2579; // Выключает все игровые звуки.
	public static final int EXPANDS_EACH_SHORTCUT_WINDOW = 2580; // Увеличивает окна ярлыков.
	public static final int INITIALIZE_USER_INTERFACE_LOCATION_TO_A_DEFAULT_LOCATION = 2581; // Устанавливает позиции интерфейса по умолчанию.
	public static final int SPIN_MY_CHARACTER_OR_MOUNTABLE_TO_THE_LEFT = 2582; // Позволяет повернуться налево.
	public static final int SPIN_MY_CHARACTER_OR_MOUNTABLE_TO_THE_RIGHT = 2583; // Позволяет повернуться направо.
	public static final int SPIN_MY_CHARACTER_OR_MOUNTABLE_FORWARD = 2584; // Позволяет двигаться вперед.
	public static final int SPIN_MY_CHARACTER_OR_MOUNTABLE_TO_THE_REAR = 2585; // Позволяет двигаться назад.
	public static final int CONTINUE_MOVING_IN_THE_PRESENT_DIRECTION = 2586; // Позволяет двигаться вперед автоматически.
	public static final int REDUCE_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2587; // Уменьшает вид персонажа.
	public static final int ENLARGE_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2588; // Увеличивает вид персонажа.
	public static final int QUICKLY_SPIN_IN_ALL_DIRECTIONS_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2589; // Мгновенно разворачивает вид персонажа на 180%.
	public static final int OPENS_THE_GM_MANAGER_WINDOW_ = 2590; // Открывает окно управления GM.
	public static final int OPENS_THE_GM_PETITION_WINDOW_ = 2591; // Открывает окно петиций GM.
	public static final int QUICKLY_SWITCH_THE_CONTENT_OF_THE_EXPANDED_SHORTCUT_WINDOW = 2592; // Быстро преобразовывает содержимое раскрытого окна ярлыков. Эта функция сейчас недоступна.
	public static final int ADVANCE_BY_A_SET_DISTANCE_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2593; // Смещает вид персонажа вперед на определенную дистанцию.
	public static final int RETREAT_BY_A_SET_DISTANCE_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2594; // Смещает вид персонажа назад на определенную дистанцию.
	public static final int RESET_THE_VIEWING_POINT_OF_MY_CHARACTER_OR_MOUNTABLE = 2595; // Восстанавливает вид персонажа по умолчанию.
	public static final int NO_TRANSLATION_REQUIRED_2596 = 2596; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2597 = 2597; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2598 = 2598; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2599 = 2599; // Не переводить
	public static final int THE_MATCH_IS_BEING_PREPARED_PLEASE_TRY_AGAIN_LATER = 2701; // Идет подготовка к соревнованиям. Подождите немного.
	public static final int YOU_WERE_EXCLUDED_FROM_THE_MATCH_BECAUSE_THE_REGISTRATION_COUNT_WAS_NOT_CORRECT = 2702; // Количество игроков не соответствовало требованиям, поэтому Вы были исключены из команды.
	public static final int THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT = 2703; // Для поддержания баланса в игре состав команды был изменен.
	public static final int YOU_CANNOT_REGISTER_BECAUSE_CAPACITY_HAS_BEEN_EXCEEDED = 2704; // Количество игроков превысило лимит, поэтому Вы не сможете принять участие в соревновании.
	public static final int THE_MATCH_WAITING_TIME_WAS_EXTENDED_BY_1_MINUTE = 2705; // Время ожидания соревнований продлено на 1мин.
	public static final int YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2706; // Вы не соответствуете требованиям соревнований, поэтому не можете войти.
	public static final int YOU_CANNOT_MAKE_ANOTHER_REQUEST_FOR_10_SECONDS_AFTER_CANCELLING_A_MATCH_REGISTRATION = 2707; // В течение 10-ти сек. после отмены регистрации в соревнованиях Вы не сможете повторить попытку.
	public static final int YOU_CANNOT_REGISTER_WHILE_POSSESSING_A_CURSED_WEAPON = 2708; // Вы не можете зарегистрироваться, пока используете проклятое оружие.
	public static final int APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER = 2709; // Подавший заявку на участие в Олимпиаде, Битве за Подземный Зал Боев или Куб Кратеи не может зарегистрироваться.
	public static final int CURRENT_LOCATION__S1_S2_S3_NEAR_THE_KEUCEREUS_CLAN_ASSOCIATION_LOCATION = 2710; // Координаты: $s1, $s2, $s3 (Окрестности Базы Альянса Кецеруса)
	public static final int CURRENT_LOCATION__S1_S2_S3_INSIDE_THE_SEED_OF_INFINITY = 2711; // Координаты: $s1, $s2, $s3 (Внутри Семени Бессмертия)
	public static final int CURRENT_LOCATION__S1_S2_S3_OUTSIDE_THE_SEED_OF_INFINITY = 2712; // Координаты: $s1, $s2, $s3 (Внутри Семени Разрушения)
	public static final int ______________________________________________________ = 2713; // ------------------------------------------------------
	public static final int ______________________________________________________________________ = 2714; // ----------------------------------------------------------------------
	public static final int AIRSHIPS_CANNOT_BE_BOARDED_IN_THE_CURRENT_AREA = 2715; // В данной локации Вы не можете воспользоваться Летающим Кораблем.
	public static final int CURRENT_LOCATION__S1_S2_S3_INSIDE_AERIAL_CLEFT = 2716; // Координаты: $s1, $s2, $s3 (Внутри Ущелья)
	public static final int THE_AIRSHIP_WILL_LAND_AT_THE_WHARF_SHORTLY = 2717; // Летающий Корабль прибывает в Воздушную Гавань.
	public static final int THE_SKILL_CANNOT_BE_USED_BECAUSE_THE_TARGET_S_LOCATION_IS_TOO_HIGH_OR_LOW = 2718; // Цель находится слишком высоко или низко, поэтому Вы не можете воспользоваться этим умением.
	public static final int ONLY_NON_COMPRESSED_256_COLOR_BMP_BITMAP_FILES_CAN_BE_REGISTERED = 2719; // Вы можете использовать не сжатый bmp файл с цветовой гаммой 256.
	public static final int INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE = 2720; // Вход во временную зону $s1 закрыт. Время следующего входа можно проверить с помощью команды "/Временная Зона".
	public static final int BOARDING_OR_CANCELLATION_OF_BOARDING_ON_AIRSHIPS_IS_NOT_ALLOWED_IN_THE_CURRENT_AREA = 2721; // В данной локации Вы не можете взойти на Летающий Корабль/сойти с Летающего Корабля.
	public static final int ANOTHER_AIRSHIP_HAS_ALREADY_BEEN_SUMMONED_AT_THE_WHARF_PLEASE_TRY_AGAIN_LATER = 2722; // В Воздушной Гавани находится другой Летающий Корабль. Повторите попытку позже.
	public static final int THE_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1 = 2723; // Вы не можете вызвать Летающий Корабль, так как не хватает $s1.
	public static final int THE_AIRSHIP_CANNOT_BE_PURCHASED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1 = 2724; // Вы не можете купить Летающий Корабль, так как не хватает $s1.
	public static final int YOU_CANNOT_SUMMON_THE_AIRSHIP_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2725; // Вы не можете вызвать Летающий Корабль, так как не соответствуете требованиям.
	public static final int YOU_CANNOT_PURCHASE_THE_AIRSHIP_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2726; // Вы не можете купить Летающий Корабль, так как не соответствуете требованиям.
	public static final int YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2727; // Вы не можете сесть на Летающий Корабль, так как не соответствуете требованиям.
	public static final int THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED = 2728; // Это действие невозможно во время полета на Корабле.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_TRANSFORMED = 2729; // Во время перевоплощения управлять целью невозможно.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_YOU_ARE_PETRIFIED = 2730; // Вы не можете управлять целью в окаменелом состоянии.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHEN_YOU_ARE_DEAD = 2731; // Вы не можете управлять целью будучи мертвым.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_FISHING = 2732; // Вы не можете управлять целью во время рыбалки.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_BATTLE = 2733; // Вы не можете управлять целью во время битвы.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_DUEL = 2734; // Вы не можете управлять целью во время дуэли.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_IN_A_SITTING_POSITION = 2735; // Вы не можете управлять целью сидя.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_USING_A_SKILL = 2736; // Вы не можете управлять целью во время прочтения заклинания.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_A_CURSED_WEAPON_IS_EQUIPPED = 2737; // Вы не можете управлять целью, используя проклятое оружие.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_HOLDING_A_FLAG = 2738; // Вы не можете управлять целью, подняв флаг.
	public static final int YOU_CANNOT_CONTROL_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2739; // Вы не можете управлять целью, так как не выполнили все условия.
	public static final int THIS_ACTION_IS_PROHIBITED_WHILE_CONTROLLING = 2740; // Это действие недоступно во время управления целью.
	public static final int YOU_CAN_CONTROL_THE_AIRSHIP_BY_TARGETING_THE_AIRSHIP_S_CONTROL_KEY_AND_PRESSING_THE__CONTROL_ = 2741; // Управление Летающим Кораблем возможно, если выбрать в качестве цели Панель Управления и выбрать действие "Управлять"
	public static final int ANY_CHARACTER_RIDING_THE_AIRSHIP_CAN_CONTROL_IT = 2742; // Любой персонаж, находящийся на Летающем Корабле, может принять управление на себя.
	public static final int IF_YOU_RESTART_WHILE_ON_AN_AIRSHIP_YOU_WILL_RETURN_TO_THE_DEPARTURE_LOCATION = 2743; // Выполнив перезапуск игры, находясь в Летающем Корабле, Вы окажетесь в точке отправки Корабля.
	public static final int IF_YOU_PRESS_THE__CONTROL_CANCEL__ACTION_BUTTON_YOU_CAN_EXIT_THE_CONTROL_STATE_AT_ANY_TIME = 2744; // С помощью действия "Выйти" Вы можете отменить управление кораблем.
	public static final int THE__MOUNT_CANCEL__ACTION_BUTTON_ALLOWS_YOU_TO_DISMOUNT_BEFORE_THE_AIRSHIP_DEPARTS = 2745; // С помощью функции "Сойти" Вы можете сойти с Летающего Корабля.
	public static final int USE_THE__DEPART__ACTION_TO_MAKE_THE_AIRSHIP_DEPART = 2746; // С помощью действия "Взлет" Вы можете начать полет на Летающем Корабле.
	public static final int AIRSHIP_TELEPORT_IS_POSSIBLE_THROUGH_THE__DEPART__ACTION_AND_IN_THAT_CASE_FUEL_EP_IS_CONSUMED = 2747; // С помощью действия "Взлет" Вы можете переместиться, будучи в Летающем Корабле, но при этом затрачивается топливо(EP).
	public static final int YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_REPORT_OTHER_USERS = 2748; // Получено сообщение о том, что Вы пользуетесь нелегальной программой, поэтому Вы не можете сообщить о другом игроке.
	public static final int YOU_HAVE_REACHED_YOUR_CRYSTALLIZATION_LIMIT_AND_CANNOT_CRYSTALLIZE_ANY_MORE = 2749; // Вы превысили число возможных кристаллизаций.
	public static final int THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD = 2750; // Знамя $s1 потеряно! $c2 захватил Знамя.
	public static final int THE_CHARACTER_THAT_ACQUIRED_S1_WARD_HAS_BEEN_KILLED = 2751; // Персонаж, захвативший Знамя, $s1 погиб.
	public static final int THE_WAR_FOR_S1_HAS_BEEN_DECLARED = 2752; // Объявлена война за Земли $s1.
	public static final int A_POWERFUL_ATTACK_IS_PROHIBITED_WHEN_ALLIED_TROOPS_ARE_THE_TARGET = 2753; // Вы не можете атаковать соратника.
	public static final int PVP_MATCHES_SUCH_AS_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEI_S_CUBE_AND_HANDY_S_BLOCK = 2754; // Вы не можете участвовать одновременно в нескольких PVP соревнованиях (Олимпиада, Подземный Зал Боев, Ущелье, Куб Кратеи и Арена).
	public static final int C1_HAS_BEEN_DESIGNATED_AS_CAT = 2755; // $c1 стал CAT.
	public static final int ANOTHER_PLAYER_IS_PROBABLY_CONTROLLING_THE_TARGET = 2756; // Цель находится под контролем другого игрока.
	public static final int THE_TARGET_IS_MOVING_SO_YOU_HAVE_FAILED_TO_MOUNT = 2757; // Цель перемещается, поэтому оседлать ее не получилось.
	public static final int YOU_CANNOT_CONTROL_THE_TARGET_WHILE_A_PET_OR_SERVITOR_IS_SUMMONED = 2758; // Вы не можете управлять целью, если призвали Питомца или Слугу.
	public static final int WHEN_ACTIONS_ARE_PROHIBITED_YOU_CANNOT_MOUNT_A_MOUNTABLE = 2759; // Вы не можете оседлать Питомца, будучи ограничены в действиях.
	public static final int WHEN_ACTIONS_ARE_PROHIBITED_YOU_CANNOT_CONTROL_THE_TARGET = 2760; // Вы не можете управлять целью, будучи ограничены в действиях.
	public static final int YOU_MUST_TARGET_THE_ONE_YOU_WISH_TO_CONTROL = 2761; // Сначала Вы должны выбрать цель, которой хотите управлять.
	public static final int YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR = 2762; // Вы не можете управлять целью, так как она находится слишком далеко.
	public static final int YOU_CANNOT_ENTER_THE_BATTLEFIELD_WHILE_IN_A_PARTY_STATE = 2763; // Вы не можете попасть на Поле Битвы, состоя в группе.
	public static final int YOU_CANNOT_ENTER_BECAUSE_THE_CORRESPONDING_ALLIANCE_CHANNEL_S_MAXIMUM_NUMBER_OF_ENTRANTS_HAS = 2764; // Количество участников Союзного Канала достигло максимума, поэтому вход воспрещен.
	public static final int ONLY_THE_ALLIANCE_CHANNEL_LEADER_CAN_ATTEMPT_ENTRY = 2765; // Войти может только Глава Союзного Канала.
	public static final int SEED_OF_INFINITY_STAGE_1_ATTACK_IN_PROGRESS = 2766; // Идет Осада 1-го уровня Семени Бессмертия
	public static final int SEED_OF_INFINITY_STAGE_2_ATTACK_IN_PROGRESS = 2767; // Идет Осада 2-го уровня Семени Бессмертия
	public static final int SEED_OF_INFINITY_CONQUEST_COMPLETE = 2768; // Семя Бессмертия захвачено
	public static final int SEED_OF_INFINITY_STAGE_1_DEFENSE_IN_PROGRESS = 2769; // Идет Оборона 1-го уровня Семени Бессмертия
	public static final int SEED_OF_INFINITY_STAGE_2_DEFENSE_IN_PROGRESS = 2770; // Идет Оборона 2-го уровня Семени Бессмертия
	public static final int SEED_OF_DESTRUCTION_ATTACK_IN_PROGRESS = 2771; // Проходит Осада Семени Разрушения
	public static final int SEED_OF_DESTRUCTION_CONQUEST_COMPLETE = 2772; // Семя Разрушения захвачено
	public static final int SEED_OF_DESTRUCTION_DEFENSE_IN_PROGRESS = 2773; // Идет Оборона Семени Разрушения
	public static final int YOU_CAN_MAKE_ANOTHER_REPORT_IN_S1_MINUTES_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT = 2774; // Через $s1 Вы сможете подать петицию. У Вас осталось еще $s2 очков.
	public static final int THE_MATCH_CANNOT_TAKE_PLACE_BECAUSE_A_PARTY_MEMBER_IS_IN_THE_PROCESS_OF_BOARDING = 2775; // Член группы сейчас находится верхом на Питомце и поэтому Дуэль между Группами невозможна.
	public static final int THE_EFFECT_OF_TERRITORY_WARD_IS_DISAPPEARING = 2776; // Эффект Знамени пропадает.
	public static final int THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED_YOUR_CLAN_CAN_NOW_SUMMON_THE_AIRSHIP = 2777; // Вы успешно передали разрешение на вызов Летающего Корабля. Теперь Ваш клан может вызвать Летающий Корабль.
	public static final int YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD = 2778; // Со Знаменем телепорт невозможен.
	public static final int FURTHER_INCREASE_IN_ALTITUDE_IS_NOT_ALLOWED = 2779; // Вы достигли максимальной высоты.
	public static final int FURTHER_DECREASE_IN_ALTITUDE_IS_NOT_ALLOWED = 2780; // Вы находитесь на минимальной высоте.
	public static final int NUMBER_OF_UNITS__S1 = 2781; // $s1 ед.
	public static final int NUMBER_OF_PEOPLE__S1 = 2782; // $s1 чел.
	public static final int NO_ONE_IS_LEFT_FROM_THE_OPPOSING_TEAM_THUS_VICTORY_IS_YOURS = 2783; // Противник покинул Арену. Вы победили.
	public static final int THE_BATTLEFIELD_HAS_BEEN_CLOSED_THE_MATCH_HAS_ENDED_IN_A_TIE_BECAUSE_THE_MATCH_LASTED_FOR_S1 = 2784; // Противник покинул Арену. Время битвы составило $s1 мин. $s2 сек. не достигнув минимальных 15-ти мин., поэтому зачислена ничья.
	public static final int IT_S_A_LARGE_SCALED_AIRSHIP_FOR_TRANSPORTATIONS_AND_BATTLES_AND_CAN_BE_OWNED_BY_THE_UNIT_OF_CLAN = 2785; // Данный Летающий Корабль может принадлежать исключительно кланам, он отлично подходят для перевозок и битв.
	public static final int START_ACTION_IS_AVAILABLE_ONLY_WHEN_CONTROLLING_THE_AIRSHIP = 2786; // Взлет возможен только при управлении Летающим Кораблем.
	public static final int C1_HAS_DRAINED_YOU_OF_S2_HP = 2787; // $c1 поглотил у Вас $s2 HP.
	public static final int MERCENARY_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY = 2788; // Вы подали заявку на участие в Битве за Земли $s1 на стороне наемников.
	public static final int MERCENARY_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY = 2789; // Вы отменили заявку на участие в Битве за Земли $s1 на стороне наемников.
	public static final int CLAN_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY = 2790; // Вы подали заявку на участие в Битве за Земли $s1 на стороне клана.
	public static final int CLAN_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY = 2791; // Вы отменили заявку на участие в Битве за Земли $s1 на стороне клана.
	public static final int _50_CLAN_REPUTATION_POINTS_WILL_BE_AWARDED_DO_YOU_WISH_TO_CONTINUE = 2792; // Увеличение репутации клана на 50 за счет своей репутации. Продолжить?
	public static final int THE_MINIMUM_NUMBER_S1_OF_PEOPLE_TO_ENTER_INSTANT_ZONE_IS_NOT_MET_AND_ENTRY_IS_NOT_AVAILABLE = 2793; // Минимальное количество участников, необходимое для попадания во временную зону($s1 чел.) не достигнуто, поэтому вход в нее невозможен.
	public static final int THE_TERRITORY_WAR_CHANNEL_AND_FUNCTIONS_WILL_NOW_BE_DEACTIVATED = 2794; // Канал Битвы и его функции деактивированы.
	public static final int YOU_VE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE = 2795; // Вы уже подали заявку на участие в Битве за другие Земли.
	public static final int THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES = 2796; // Члены Клана, владеющего Землями, не могут принять участие в Битве за Земли в качестве наемников.
	public static final int IT_IS_NOT_A_TERRITORY_WAR_REGISTRATION_PERIOD_SO_A_REQUEST_CANNOT_BE_MADE_AT_THIS_TIME = 2797; // Период регистрации завершен. Вы не можете подать заявку на участие в Битве.
	public static final int THE_TERRITORY_WAR_WILL_END_IN_S1_HOURS = 2798; // До завершения Битвы за Земли осталось $s1 ч!
	public static final int THE_TERRITORY_WAR_WILL_END_IN_S1_MINUTES = 2799; // До завершения Битвы за Земли осталось $s1 мин!

	public static final int NO_TRANSLATION_REQUIRED_2800 = 2800; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2801 = 2801; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2802 = 2802; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2803 = 2803; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2804 = 2804; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2805 = 2805; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2806 = 2806; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2807 = 2807; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2808 = 2808; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2809 = 2809; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2810 = 2810; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2811 = 2811; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2812 = 2812; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2813 = 2813; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2814 = 2814; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2815 = 2815; // Не переводить

	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_1 = 2816; // Назначьте ярлык к 1-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_2 = 2817; // Назначьте ярлык к 2-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_3 = 2818; // Назначьте ярлык к 3-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_4 = 2819; // Назначьте ярлык к 4-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_5 = 2820; // Назначьте ярлык к 5-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_6 = 2821; // Назначьте ярлык к 6-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_7 = 2822; // Назначьте ярлык к 7-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_8 = 2823; // Назначьте ярлык к 8-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_9 = 2824; // Назначьте ярлык к 9-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_10 = 2825; // Назначьте ярлык к 10-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_11 = 2826; // Назначьте ярлык к 11-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_FLYING_TRANSFORMED_OBJECT_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_12 = 2827; // Назначьте ярлык к 12-му слоту в окне ярлыков летающего существа. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_1_SLOT_THE_CTRL = 2828; // Назначьте ярлык к 1-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_2_SLOT_THE_CTRL = 2829; // Назначьте ярлык к 2-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_3_SLOT_THE_CTRL = 2830; // Назначьте ярлык к 3-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_4_SLOT_THE_CTRL = 2831; // Назначьте ярлык к 4-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_5_SLOT_THE_CTRL = 2832; // Назначьте ярлык к 5-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_6_SLOT_THE_CTRL = 2833; // Назначьте ярлык к 6-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_7_SLOT_THE_CTRL = 2834; // Назначьте ярлык к 7-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_8_SLOT_THE_CTRL = 2835; // Назначьте ярлык к 8-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_9_SLOT_THE_CTRL = 2836; // Назначьте ярлык к 9-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_10_SLOT_THE_CTRL = 2837; // Назначьте ярлык к 10-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_11_SLOT_THE_CTRL = 2838; // Назначьте ярлык к 11-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.
	public static final int DESIGNATE_A_SHORTCUT_KEY_FOR_THE_MOUNTABLE_EXCLUSIVE_USE_SHORTCUT_WINDOW_S_NO_12_SLOT_THE_CTRL = 2839; // Назначьте ярлык к 12-му слоту в окне ярлыков ездового животного. Комбинация Ctrl+Shift не может быть назначена.

	public static final int EXECUTE_THE_DESIGNATED_SHORTCUT_S_ACTION_SKILL_MACRO = 2840; // Запускает дейстивя, умения и макросы, установленных ярлыков.
	public static final int RAISE_MY_CHARACTER_TO_THE_TOP = 2841; // Позволяет персонажу взлететь вверх.
	public static final int LOWER_MY_CHARACTER_TO_THE_BOTTOM = 2842; // Позволяет персонажу спуститься вниз.
	public static final int RAISE_THE_CONTROLLED_MOUNTABLE_TO_THE_TOP = 2843; // Позволяет взлететь вверх на ездовом животном.
	public static final int LOWER_THE_CONTROLLED_MOUNTABLE_TO_THE_BOTTOM = 2844; // Позволяет спуститься вниз на ездовом животном.
	public static final int AUTOMATICALLY_SEND_FORWARD_MY_CHARACTER_OR_MOUNTABLE = 2845; // Позволяет персонажу или ездовому животному двигаться вперед автоматически.
	public static final int NO_TRANSLATION_REQUIRED_2846 = 2846; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2847 = 2847; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2848 = 2848; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2849 = 2849; // Не переводить
	public static final int NO_TRANSLATION_REQUIRED_2850 = 2850; // Не переводить
	public static final int STOP_ALL_ACTIONS_OF_MY_CHARACTER = 2851; // Останавливает все передвижения Вашего персонажа.
	public static final int STOP_ALL_ACTIONS_OF_MY_CONTROLLED_MOUNTABLE = 2852; // Останавливает все передвижения ездового животного.
	public static final int IF_YOU_JOIN_THE_CLAN_ACADEMY_YOU_CAN_BECOME_A_CLAN_MEMBER_AND_LEARN_THE_GAME_SYSTEM_UNTIL_YOU = 2853; // Поступив в академию клана, Вы можете стать членом клана и изучать игровую систему до получения 40-го уровня. Если хотите получать от игры больше удовольствия, то мы рекомендуем Вам поступить в академию клана.
	public static final int IF_YOU_BECOME_LEVEL_40_THE_SECOND_CLASS_CHANGE_IS_AVAILABLE_IF_YOU_COMPLETE_THE_SECOND_CLASS = 2854; // По достижении 40-го уровня Вы получаете возможность второй смены профессии. После второй смены профессии возможности Вашего персонажа возрастут.
	public static final int YOU_CAN_SEE_THE_GAME_HELP = 2855; // You can see the game help.
	public static final int YOU_CAN_ASK_A_QUESTION_ABOUT_YOUR_GAME_PROGRESS_TO_A_GM = 2856; // You can ask a question about your game progress to a GM.
	public static final int YOU_CAN_SELECT_SEVERAL_OPTIONS_RELATED_TO_THE_GAME_INCLUDING_GRAPHIC_SETTINGS_AND_SOUND_SETTINGS = 2857; // You can select several options related to the game, including graphic settings and sound settings.
	public static final int YOU_ARE_RESTARTING_THE_GAME_AS_ANOTHER_CHARACTER = 2858; // You are restarting the game as another character.
	public static final int YOU_ARE_QUITTING_THE_GAME_CLIENT_AND_LOGGING_OUT_FROM_THE_SERVER = 2859; // You are quitting the game client and logging out from the server.
	public static final int THIS_DISPLAYS_A_LIST_OF_MY_CHARACTER_S_SKILL_AND_MAGIC_ABILITIES = 2860; // This displays a list of my character's skill and magic abilities.
	public static final int THIS_CONFIRMS_MY_CHARACTER_S_CLAN_INFORMATION_AND_MANAGES_THE_CLAN = 2861; // This confirms my character's clan information and manages the clan.
	public static final int THIS_DISPLAYS_ALL_THE_ACTIONS_THAT_MY_CHARACTER_CAN_TAKE = 2862; // This displays all the actions that my character can take.
	public static final int THIS_DISPLAYS_THE_LIST_OF_ALL_THE_QUESTS_THAT_MY_CHARACTER_IS_UNDERTAKING_THE_QUEST_PROGRESS = 2863; // This displays the list of all the quests that my character is undertaking. The quest progress status can be easily managed.
	public static final int THIS_DISPLAYS_MY_CHARACTER_S_DETAILED_STATUS_INFORMATION_I_CAN_EASILY_CONFIRM_WHEN_AN_ITEM_IS = 2864; // This displays my character's detailed status information. I can easily confirm when an item is equipped, when a buff is received, and how much stronger my character has become.
	public static final int THIS_OPENS_AN_INVENTORY_WINDOW_WHERE_I_CAN_CHECK_THE_LIST_OF_ALL_MY_CHARACTER_S_ITEMS = 2865; // This opens an inventory window where I can check the list of all my character's items.
	public static final int I_CAN_SEE_INFORMATION_ABOUT_MY_LOCATION_BY_OPENING_A_MINI_MAP_WINDOW_AND_I_CAN_CHECK_CURRENT = 2866; // I can see information about my location by opening a mini-map window, and I can check current information about the entire game world.
	public static final int CLICK_HERE_TO_SEE_A_GAME_SYSTEM_MENU_THAT_CONTAINS_VARIOUS_FUNCTIONS_OF_THE_GAME_YOU_CAN_CHECK = 2867; // Click here to see a game system menu that contains various functions of the game. You can check information about the game bulletin, macro, help, GM suppression button, game option button, game restart button and the game quit button.
	public static final int IF_YOU_CLICK_THE_CHAT_TAB_YOU_CAN_SELECT_AND_VIEW_THE_MESSAGES_OF_YOUR_DESIRED_GROUP_THE_TABS = 2868; // If you click the Chat tab, you can select and view the messages of your desired group. The tabs can be separated by using drag-and-drop.
	public static final int YOU_CAN_CLOSE_OR_OPEN_SPECIFIC_MESSAGES_FROM_THE_CHAT_SCREEN_YOU_CAN_CLOSE_OR_OPEN_THE_SYSTEM = 2869; // You can close or open specific messages from the chat screen. You can close or open the system message exclusive window.
	public static final int YOU_CAN_LOGIN_ONTO_MSN_MESSENGER_YOU_CAN_ALSO_CHAT_WITH_REGISTERED_FRIENDS_WITHIN_THE_GAME = 2870; // You can login onto MSN Messenger. You can also chat with registered friends within the game.
	public static final int YOU_CAN_USE_THE_PARTY_MATCHING_FUNCTION_THAT_ALLOWS_YOU_TO_EASILY_FORM_PARTIES_WITH_OTHER = 2871; // You can use the party matching function that allows you to easily form parties with other players.
	public static final int YOU_CAN_INSTALL_VARIOUS_RAID_OPTIONS_SUCH_AS_MONSTER_OR_PARTY_MEMBER_MARK_AND_RAID_FIX = 2872; // You can install various raid options such as monster or party member mark and raid fix.
	public static final int THIS_ENLARGES_THE_RAIDER_S_VIEW_AND_MARKS_A_MORE_DETAILED_TOPOGRAPHY = 2873; // This enlarges the raider's view and marks a more detailed topography.
	public static final int THIS_DIMINISHES_THE_RAIDER_S_VIEW_AND_MARKS_A_WIDER_TOPOGRAPHY = 2874; // This diminishes the raider's view and marks a wider topography.
	public static final int S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR = 2900; // До завершения Битвы за Земли осталось $s1 сек!
	public static final int YOU_CANNOT_FORCE_ATTACK_A_MEMBER_OF_THE_SAME_TERRITORY = 2901; // Цель с тех же земель, что и Вы, поэтому атака невозможна.
	public static final int YOU_VE_ACQUIRED_THE_WARD_MOVE_QUICKLY_TO_YOUR_FORCES__OUTPOST = 2902; // Вы смогли добыть Знамя. Скорее вернитесь в свой лагерь.
	public static final int TERRITORY_WAR_HAS_BEGUN = 2903; // Битва за Земли началась.
	public static final int TERRITORY_WAR_HAS_ENDED = 2904; // Битва за Земли завершена.
	public static final int ALTITUDE_CANNOT_BE_DECREASED_ANY_FURTHER = 2905; // Вы находитесь на минимальной высоте.
	public static final int ALTITUDE_CANNOT_BE_INCREASED_ANY_FURTHER = 2906; // Вы достигли максимальной высоты.
	public static final int YOU_HAVE_ENTERED_A_POTENTIALLY_HOSTILE_ENVIRONMENT_SO_THE_AIRSHIP_S_SPEED_HAS_BEEN_GREATLY = 2907; // Вы попали в скопление большого количества монстров. Скорость Летающего Корабля снизится.
	public static final int AS_YOU_ARE_LEAVING_THE_HOSTILE_ENVIRONMENT_THE_AIRSHIP_S_SPEED_HAS_BEEN_RETURNED_TO_NORMAL = 2908; // Вы прошли через скопление большого количества монстров. Скорость Летающего Корабля восстановлена.
	public static final int A_SERVITOR_OR_PET_CANNOT_BE_SUMMONED_WHILE_ON_AN_AIRSHIP = 2909; // Находясь верхом на Питомце, Вы не можете призвать Слугу.
	public static final int YOU_HAVE_ENTERED_AN_INCORRECT_COMMAND = 2910; // Неверная команда.
	public static final int YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST = 2911; // Вы предложили персонажу $c1 добавить Вас в список друзей.
	public static final int YOU_VE_INVITED_C1_TO_JOIN_YOUR_CLAN = 2912; // Вы предложили персонажу $c1 вступить в Ваш клан.
	public static final int CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2_S_TERRITORY_WARD = 2913; // Клан $s1 смог завоевать знамя $s2.
	public static final int THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES_TERRITORY_RELATED_FUNCTIONS_IE__BATTLEFIELD_CHANNEL = 2914; // До начала Битвы за Земли осталось 20мин! Активируется Канал Битвы, появляется возможность маскировки и перевоплощения.
	public static final int THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR = 2915; // Член клана, участвующий в Битве за Землю, не может покинуть клан.
	public static final int PARTICIPATING_IN_S1_TERRITORY_WAR = 2916; // Принимает участие в Битве за Земли $s1
	public static final int NOT_PARTICIPATING_IN_A_TERRITORY_WAR = 2917; // Не принимает участие в Битве за Земли
	public static final int ONLY_CHARACTERS_WHO_ARE_LEVEL_40_OR_ABOVE_WHO_HAVE_COMPLETED_THEIR_SECOND_CLASS_TRANSFER_CAN = 2918; // В Битве за Земли могут принять участие персонажи, достигшие 40-го уровня и сменившие 2-ю профессию.
	public static final int WHILE_DISGUISED_YOU_CANNOT_OPERATE_A_PRIVATE_OR_MANUFACTURE_STORE = 2919; // Во время маскировки Вы не можете открыть личную торговую лавку или мастерскую.
	public static final int NO_MORE_AIRSHIPS_CAN_BE_SUMMONED_AS_THE_MAXIMUM_AIRSHIP_LIMIT_HAS_BEEN_MET = 2920; // Вы превысили количество вызванных Летающих Кораблей и не можете вызвать еще один.
	public static final int YOU_CANNOT_BOARD_THE_AIRSHIP_BECAUSE_THE_MAXIMUM_NUMBER_FOR_OCCUPANTS_IS_MET = 2921; // Летающий Корабль переполнен, и Вы не можете в него попасть.
	public static final int BLOCK_CHECKER_WILL_END_IN_5_SECONDS = 2922; // Арена закроется через 5сек!!!!
	public static final int BLOCK_CHECKER_WILL_END_IN_4_SECONDS = 2923; // Арена закроется через 4сек!!!!
	public static final int YOU_CANNOT_ENTER_A_SEED_WHILE_IN_A_FLYING_TRANSFORMATION_STATE = 2924; // Вы не можете попасть внутрь Семени, перевоплотившись в Летающее Существо.
	public static final int BLOCK_CHECKER_WILL_END_IN_3_SECONDS = 2925; // Арена закроется через 3сек!!!!
	public static final int BLOCK_CHECKER_WILL_END_IN_2_SECONDS = 2926; // Арена закроется через 2сек!!!!
	public static final int BLOCK_CHECKER_WILL_END_IN_1_SECOND = 2927; // Арена закроется через 1сек!!!!
	public static final int THE_C1_TEAM_HAS_WON = 2928; // В соревнованиях победила команда $c1.
	public static final int YOUR_REQUEST_CANNOT_BE_PROCESSED_BECAUSE_THERE_S_NO_ENOUGH_AVAILABLE_MEMORY_ON_YOUR_GRAPHIC_CARD = 2929; // Память видеокарты максимально загружена, поэтому запуск невозможен. Смените разрешение и повторите попытку.
	public static final int A_GRAPHIC_CARD_INTERNAL_ERROR_HAS_OCCURRED_PLEASE_INSTALL_THE_LATEST_VERSION_OF_THE_GRAPHIC_CARD = 2930; // Произошла внутренняя ошибка видеокарты. Попробуйте установить последнюю версию драйверов для видеокарты.
	public static final int THE_SYSTEM_FILE_MAY_HAVE_BEEN_DAMAGED_AFTER_ENDING_THE_GAME_PLEASE_CHECK_THE_FILE_USING_THE = 2931; // Есть вероятность, что был поврежден системный файл. Завершите игу и совершите полную проверку файлов игры.
	public static final int S1_ADENA = 2932; // $s1 аден.
	public static final int THOMAS_D_TURKEY_HAS_APPEARED_PLEASE_SAVE_SANTA = 2933; // Появился Бешеный Индюк. Спасите Деда Мороза!
	public static final int YOU_HAVE_DEFEATED_THOMAS_D_TURKEY_AND_RESCUED_SANTA = 2934; // Вы выиграли поединок с Бешеным Индюком и спасли Деда Мороза!
	public static final int YOU_FAILED_TO_RESCUE_SANTA_AND_THOMAS_D_TURKEY_HAS_DISAPPEARED = 2935; // Вы не смогли спасти Деда Мороза, и Бешеный Индюк исчез.
	public static final int THE_DISGUISE_SCROLL_CANNOT_BE_USED_BECAUSE_IT_IS_MEANT_FOR_USE_IN_A_DIFFERENT_TERRITORY = 2936; // Свиток не подходит к Змелям, в которых Вы состоите, поэтому невозможно замаскироваться.
	public static final int A_TERRITORY_OWNING_CLAN_MEMBER_CANNOT_USE_A_DISGUISE_SCROLL = 2937; // Член клана, чей клан владеет Землями, не может замаскироваться.
	public static final int THE_DISGUISE_SCROLL_CANNOT_BE_USED_WHILE_YOU_ARE_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE = 2938; // Невозможно замаскироваться, когда Вы открыли личную торговую лавку или личную мастерскую
	public static final int A_DISGUISE_CANNOT_BE_USED_WHEN_YOU_ARE_IN_A_CHAOTIC_STATE = 2939; // Хаотические персонажы не могут маскироваться.
	public static final int YOU_CAN_INCREASE_THE_CHANCE_TO_ENCHANT_THE_ITEM_PRESS_THE_START_BUTTON_BELOW_TO_BEGIN = 2940; // Вы можете использовать предмет, повышающий шанс успешного зачарования. Зачарование начнется, если нажать на кнопку 'Начать'.
	public static final int THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_REQUIREMENTS_ARE_NOT_MET_IN_ORDER_TO_PARTICIPATE_IN = 2941; // Не выполнены требования, поэтому невозможно подать заявку. Чтобы принять участие в командном соревновании, необходимо, чтобы у всех членов команды очки Олимпиады были выше 1.
	public static final int THE_FIRST_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_HOURS_S2_MINUTES_S3_SECONDS__IF_YOU_RESUMMON_THE = 2942; // До получения первого подарка осталось: $s1 ч. $s2 мин. $s3 сек. (Если призвать Агатиона еще раз, время получения может занять на 10 минут больше.)
	public static final int THE_FIRST_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_MINUTES_S2_SECONDS_IF_YOU_RESUMMON_THE_AGATHION = 2943; // До получения первого подарка осталось: $s1 мин. $s2 сек. (Если призвать Агатиона еще раз, время получения может занять на 10 минут больше.)
	public static final int THE_FIRST_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_SECONDS__IF_YOU_RESUMMON_THE_AGATHION_AT_THE_GIFT = 2944; // До получения первого подарка осталось: $s1 сек. (Если призвать Агатиона еще раз, время получения может занять на 10 минут больше.)
	public static final int THE_SECOND_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_HOURS_S2_MINUTES_S3_SECONDS__IF_YOU_RESUMMON_THE = 2945; // До получения второго подарка осталось: $s1 ч. $s2 мин. $s3 сек. (Если призвать Агатиона еще раз, время получения может занять на 1 ч. 10 минут больше.)
	public static final int THE_SECOND_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_MINUTES_S2_SECONDS_IF_YOU_RESUMMON_THE_AGATHION = 2946; // До получения второго подарка осталось: $s1 мин. $s2 сек. (Если призвать Агатиона еще раз, время получения может занять на 1 ч. 10 минут больше.)
	public static final int THE_SECOND_GIFT_S_REMAINING_RESUPPLY_TIME_IS_S1_SECONDS_IF_YOU_RESUMMON_THE_AGATHION_AT_THE_GIFT = 2947; // До получения второго подарка осталось: $s1 сек. (Если призвать Агатиона еще раз, время получения может занять на 1 ч. 10 минут больше.)
	public static final int THE_TERRITORY_WAR_EXCLUSIVE_DISGUISE_AND_TRANSFORMATION_CAN_BE_USED_20_MINUTES_BEFORE_THE_START = 2955; // Маскировка и Перевоплощение, котороые используются во время Битвы за Земли, доступны для использования за 20 мин. до Битвы и 10 мин. после ее окончания.
	public static final int A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_WITNESS_THE_BATTLE = 2956; // Невозможно наблюдать за пользователем, который участвует в Олимпиаде.
	public static final int CHARACTERS_WITH_A_FEBRUARY_29_CREATION_DATE_WILL_RECEIVE_A_GIFT_ON_FEBRUARY_28 = 2957; // Персонажи, созданные 29-го февраля, смогут получить подарок 28-го февраля.
	public static final int AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED = 2958; // Вы уже призвали Агатиона.
	public static final int YOUR_ACCOUNT_HAS_BEEN_TEMPORARILY_RESTRICTED_BECAUSE_IT_IS_SUSPECTED_OF_BEING_USED_ABNORMALLY = 2959; // Ваш аккаунт был заблокирован за неправомерные действия внутри игры. Если вы не совершали таких действий, вам необходимо зайти на веб-сайт игры и пройти идентификацию личности. Более подробную информацию вы можете получить, обратившись в службу поддержки.
	public static final int THE_ITEM_S1_IS_REQUIRED = 2960; // Вам необходим предмет $s1%
	public static final int _2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED = 2961; // Вам необходим предмет $s1% в количестве $2 шт.
	public static final int THE_ITEM_HAS_BEEN_SUCCESSFULLY_PURCHASED = 6001; // Предмет успешно приобретен.
	public static final int THE_ITEM_HAS_FAILED_TO_BE_PURCHASED = 6002; // Ошибка при покупке предмета.
	public static final int THE_ITEM_YOU_SELECTED_CANNOT_BE_PURCHASED_UNFORTUNATELY_THE_SALE_PERIOD_ENDED = 6003; // Невозможно приобрести выбранный предмет.
	public static final int ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED = 6004; // Улучшение не удалось.
	public static final int GAME_POINTS_ARE_NOT_ENOUGH = 6005; // У Вас не хватает монет 4Game.
	public static final int THE_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED = 6006; // Вы не можете приобрести предмет, так как Ваш инвентарь переполнен.
	public static final int PRODUCT_PURCHASE_ERROR___THE_USER_STATE_IS_NOT_RIGHT = 6007; // Вы не можете сейчас приобрести предмет.
	public static final int PRODUCT_PURCHASE_ERROR___THE_PRODUCT_IS_NOT_RIGHT = 6008; // Невозможно приобрести данный предмет.
	public static final int PRODUCT_PURCHASE_ERROR___THE_ITEM_WITHIN_THE_PRODUCT_IS_NOT_RIGHT = 6009; // Вы не можете приобрести упаковку предметов.
	public static final int THE_MASTER_ACCOUNT_OF_YOUR_ACCOUNT_HAS_BEEN_RESTRICTED = 6010; // Ваш мастер-аккаунт был заблокирован.
	public static final int YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG = 6501; // Невозможно сохранить данное местоположение, так как отсутствует Флаг.
	public static final int MY_TELEPORT_FLAG__S1 = 6502; // Флаг: $s1 ед.
	public static final int THE_EVIL_THOMAS_D_TURKEY_HAS_APPEARED_PLEASE_SAVE_SANTA = 6503; // Появился Бешеный Индюк. Спасите Санта Клауса.
	public static final int YOU_WON_THE_BATTLE_AGAINST_THOMAS_D_TURKEY_SANTA_HAS_BEEN_RESCUED = 6504; // Вы победили Бешеного Индюка и спасли Санта Клауса.
	public static final int YOU_DID_NOT_RESCUE_SANTA_AND_THOMAS_D_TURKEY_HAS_DISAPPEARED = 6505; // Вы не успели спасти Санта Клауса, и Индюк исчез.
	public static final int ALTHOUGH_YOU_CAN_T_BE_CERTAIN_THE_AIR_SEEMS_LADEN_WITH_THE_SCENT_OF_FRESHLY_BAKED_BREAD = 6506; // Откуда-то исходит запах хлеба. Перед глазами все плывет.
	public static final int YOU_FEEL_REFRESHED_EVERYTHING_APPEARS_CLEAR = 6507; // Настроение улучшается. Становится лучше видно.

	// Recommedations
	public static final int YOU_CANNOT_RECOMMEND_YOURSELF = 829; // Вы не можете рекомендовать самого себя.
	public static final int YOU_HAVE_BEEN_RECOMMENDED_BY_C1 = 831; // $c1 дает Вам рекомендацию.
	public static final int YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT = 830; // $c1 получает от Вас рекомендацию. Осталось рекомендаций: $s2.
	public static final int THAT_CHARACTER_HAS_ALREADY_BEEN_RECOMMENDED = 832; // Вы уже давали рекомендацию этому персонажу.
	public static final int YOU_ARE_NOT_AUTHORIZED_TO_MAKE_FURTHER_RECOMMENDATIONS_AT_THIS_TIME_YOU_WILL_RECEIVE_MORE_RECOMMENDATION_CREDITS_EACH_DAY_AT_1_PM = 833; // Вы использовали все рекомендации. Количество рекомендаций обновляется каждый день в час дня.
	public static final int ONLY_CHARACTERS_OF_LEVEL_10_OR_ABOVE_ARE_AUTHORIZED_TO_MAKE_RECOMMENDATIONS = 898; // Вы можете давать рекомендации только по достижении 10-го уровня.
	public static final int YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION = 1188; // Цель не может больше принимать от Вас рекомендации.

	// Duelling
	public static final int THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL = 1926; // Ни один оппонент не принял ваш вызов на дуэль.
	public static final int S1_HAS_BEEN_CHALLENGED_TO_A_DUEL = 1927; // $c1 вызывается на дуэль.
	public static final int S1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL = 1928; // Группа персонажа $c1 была вызвана на дуэль.
	public static final int S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS = 1929; // $c1 принимает Ваш вызов на дуэль. Дуэль сейчас начнется.
	public static final int YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS = 1930; // Вы приняли вызов на дуэль, брошенный персонажем $c1. Дуэль сейчас начнется.
	public static final int S1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL = 1931; // $c1 отказывается от дуэли с Вами.
	public static final int YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS = 1933; // Вы приняли вызов на групповую дуэль, брошенный персонажем $c1. Дуэль сейчас начнется.
	public static final int S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS = 1934; // $c1 принимает вызов на групповую дуэль. Дуэль сейчас начнется.
	public static final int THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL = 1936; // Группа противника отвергла Ваш вызов на дуэль.
	public static final int SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY = 1937; // Персонаж, вызванный на дуэль, не состоит в группе и не может сражаться против Вашей группы.
	public static final int S1_HAS_CHALLENGED_YOU_TO_A_DUEL = 1938; // $c1 вызывает Вас на дуэль.
	public static final int S1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL = 1939; // Группа персонажа $c1 вызвала Вас на групповую дуэль.
	public static final int YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME = 1940; // Вы сейчас не можете вызывать на дуэль.
	public static final int THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL = 1942; // Группа противника не может сейчас принять ваш вызов на дуэль.
	public static final int IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE = 1944; // Сейчас вы будете перемещены на место, где произойдет дуэль.
	public static final int THE_DUEL_WILL_BEGIN_IN_S1_SECONDS = 1945; // Дуэль начнется через $s1 сек.
	public static final int LET_THE_DUEL_BEGIN = 1949; // Дуэль началась!
	public static final int S1_HAS_WON_THE_DUEL = 1950; // Победитель дуэли - $c1.
	public static final int S1S_PARTY_HAS_WON_THE_DUEL = 1951; // Победитель дуэли - группа персонажа $c1.
	public static final int THE_DUEL_HAS_ENDED_IN_A_TIE = 1952; // Дуэль закончилась в ничью.
	public static final int SINCE_S1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON = 1955; // Персонаж $c1 отменил дуэль. Победитель - $s2.
	public static final int SINCE_S1S_PARTY_WITHDREW_FROM_THE_DUEL_S1S_PARTY_HAS_WON = 1956; // Группа персонажа $c1 отменила дуэль. Победитель - группа персонажа $s2.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE = 2017; // $c1 не может вступить в дуэль, поскольку в данный момент находится в личной торговой лавке или мастерской.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_FISHING = 2018; // $c1 не может вступить в дуэль, поскольку в данный момент рыбачит.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1S_HP_OR_MP_IS_BELOW_50_PERCENT = 2019; // $c1 не может вступить в дуэль, поскольку HP или MP меньше 50%.
	public static final int S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA = 2020; // $c1 не может принять вызов на дуэль, поскольку находится в зоне, где дуэли запрещены.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_BATTLE = 2021; // $c1 не может вступить в дуэль, поскольку находится в бою.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_ALREADY_ENGAGED_IN_A_DUEL = 2022; // $c1 не может вступить в дуэль, поскольку уже участвует в дуэли.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_IN_A_CHAOTIC_STATE = 2023; // $c1 не может вступить в дуэль, поскольку находится в состоянии Хаоса.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD = 2024; // $c1 не может вступить в дуэль, поскольку участвует в Олимпиаде.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR = 2025; // $c1 не может вступить в дуэль, поскольку участвует в войне за холл клана.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_SIEGE_WAR = 2026; // $c1 не может вступить в дуэль, поскольку участвует в осаде.
	public static final int S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_RIDING_A_BOAT_WYVERN_OR_STRIDER = 2027; // $c1 не может вступить в дуэль, поскольку в данный момент находится в личной торговой лавке или мастерской.
	public static final int S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY = 2028; // $c1 не может принять вызов на дуэль, потому что находится слишком далеко.
	public static final int YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP = 2143; // Вы не можете добавить силу стихий, пока находитесь в личной торговой лавке или мастерской.
	public static final int PLEASE_SELECT_ITEM_TO_ADD_ELEMENTAL_POWER = 2144; // Пожалуйста, выберите предмет, к которому вы хотите добавить силу стихий.
	public static final int ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED = 2145; // Использование усилителя стихий было отменено.
	public static final int ELEMENTAL_POWER_ENCHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT = 2146; // Условия использования усилителя стихий не соблюдены.
	public static final int S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1 = 2147; // $s1: сила стихии $s2 активирована.
	public static final int S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO__S1S2 = 2148; // +$s1 $s2: сила стихии $s3 активирована.
	public static final int YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER = 2149; // Добавление силы стихий не удалось.
	public static final int ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED = 2150; // Другая сила стихии уже была добавлена. Эта сила стихии не может быть добавлена.
	public static final int A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA = 2388; // Здесь нельзя создать группу.

	public static final int A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE = 2167; // Вредоносное умение не может быть использовано в мирной зоне.
	public static final int C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED = 2174; // $c1 не может участвовать в дуэли, поскольку он изменил форму.
	public static final int PARTY_DUEL_CANNOT_BE_INITIATED_DUEL_TO_A_POLYMORPHED_PARTY_MEMBER = 2175; // Групповая дуэль не может состояться из-за измененной формы одного из членов группы.
	public static final int HALF_KILL = 2336; // Ваш враг наполовину мертв!
	public static final int CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL = 2337; // CP уничтожены смертельным умением, успешным наполовину.

	public static final int NOT_ENOUGH_BOLTS = 2226; // Недостаточно болтов.

	// ClanHall Auction messages
	public static final int ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION = 673; // Участвовать в аукционе холлов кланов могут только кланы 2-го уровня и выше.
	public static final int IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION = 674; // Не прошло 7 дней со дня отмены аукциона.
	public static final int THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION = 675; // Данный холл клана не будет выставлен на аукционе.
	public static final int SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME = 676; // Вы уже внесли свою ставку, поэтому участие в другом аукционе невозможно.
	public static final int YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_THAT_CAN_BE_BID = 677; // Ставка должна превышать минимально возможный размер платежа.
	public static final int YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1 = 678; // Вы сделали ставку в аукционе $s1.
	public static final int YOU_HAVE_CANCELED_YOUR_BID = 679; // Ставка отменена.
	public static final int YOU_CANNOT_PARTICIPATE_IN_AN_AUCTION = 680; // Вы не можете участвовать в аукционе.
	public static final int THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL = 681; // Ваш клан не владеет холлом.
	public static final int THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN = 776; // Холл, выставленный на аукционе, перешел в собственность клана $s1.
	public static final int THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED = 777; // Холл, выставленный на аукционе, не был продан и возвращается в список холлов, выставленных на продажу.
	public static final int YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION = 1004; // Вы добавлены в список участников в аукционе холлов кланов.
	public static final int THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE = 1005; // Не хватает аден в хранилище клана.
	public static final int YOU_HAVE_BID_IN_A_CLAN_HALL_AUCTION = 1006; // Вы сделали свою ставку в аукционе холлов кланов.
	public static final int THE_SECOND_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_ORIGINAL = 1075; // Сумма должна превысить предыдущую ставку.
	public static final int IT_IS_NOT_AN_AUCTION_PERIOD = 2075; // Аукцион сейчас не проводится.
	public static final int YOUR_BID_CANNOT_EXCEED_100_BILLION = 2076; // Ваша ставка не может превышать 2,1 миллиарда.
	public static final int YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID = 2077; // Ваша ставка должна быть выше текущей ставки.
	public static final int YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID = 2078; // Недостаточно аден для этой ставки.
	public static final int YOU_CURRENTLY_HAVE_THE_HIGHEST_BID = 2079; // У Вас самая высокая ставка.
	public static final int YOU_HAVE_BEEN_OUTBID = 2080; // Ваша ставка была перебита.
	public static final int THE_AUCTION_HAS_BEGUN = 2083; // Аукцион начался.
	public static final int BIDDER_EXISTS__THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES = 2159; // Новая ставка! Аукцион продлен на 5 минут.
	public static final int BIDDER_EXISTS__AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES = 2160; // Новая ставка! Аукцион продлен на 3 минуты.
	public static final int TRADE_S1_S2_S_AUCTION_HAS_ENDED = 2172; // +$s1 $s2: аукцион закончен.
	public static final int S1_S_AUCTION_HAS_ENDED = 2173; // $s1: аукцион закончен.

	// Combat messages
	public static final int C1S_IS_PERFORMING_A_COUNTERATTACK = 1997; // $c1 проводит контратаку.
	public static final int YOU_COUNTERED_C1S_ATTACK = 1998; // Вы контратаковали цель $c1.
	public static final int C1_DODGES_THE_ATTACK = 1999; // $c1 уворачивается от атаки.
	public static final int C1_HAS_GIVEN_C2_DAMAGE_OF_S3 = 2261; // $c1 наносит цели $c2 $s3 урона.
	public static final int C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2 = 2262; // $c1 получает от цели $c2 $s3 урона.
	public static final int C1_HAS_RECEIVED_DAMAGE_OF_S3_THROUGH_C2 = 2263; // $c1 получает $s3 урона ($c2).
	public static final int C1_HAS_EVADED_C2S_ATTACK = 2264; // $c1 уклоняется от атаки цели $c2.
	public static final int C1S_ATTACK_WENT_ASTRAY = 2265; // $c1 наносит удар мимо цели.
	public static final int C1_HAD_A_CRITICAL_HIT = 2266; // $c1 наносит критический удар!
	public static final int C1_RESISTED_C2S_DRAIN = 2267; // $c1 отражает Поглощение цели $c2.
	public static final int C1S_ATTACK_FAILED = 2268; // $c1: атака не удалась.
	public static final int C1_RESISTED_C2S_MAGIC = 2269; // $c1 отражает магию врага $c2.
	public static final int C1_HAS_RECEIVED_DAMAGE_FROM_S2_THROUGH_THE_FIRE_OF_MAGIC = 2270; // $c1 получает урон от магии врага $s2.
	public static final int C1_WEAKLY_RESISTED_C2S_MAGIC = 2271; // $c1 слабо сопротивляется магии врага $c2.
	public static final int DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_AGAINST_C2S_MAGIC = 2280; // $c1 сопротивляется магии врага $c2, урон уменьшен.

	public static final int THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME = 2303; // $s1: до повторного использования $s2 сек.
	public static final int THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME = 2304; // $s1: до повторного использования $s2 мин $s3 сек.
	public static final int THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME = 2305; // $s1: до повторного использования $s2 ч $s3 мин $s4 сек.

	// Augmentation
	public static final int SELECT_THE_ITEM_TO_BE_AUGMENTED = 1957; // Выберите предмет для зачарования.
	public static final int SELECT_THE_CATALYST_FOR_AUGMENTATION = 1958; // Выберите катализатор для зачарования.
	public static final int REQUIRES_S1_S2 = 1959; // Требуется: $s2 $s1.
	public static final int THIS_IS_NOT_A_SUITABLE_ITEM = 1960; // Это неподходящий предмет.
	public static final int GEMSTONE_QUANTITY_IS_INCORRECT = 1961; // Параметры самоцвета не подходят.
	public static final int THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED = 1962; // Предмет успешно зачарован!
	public static final int SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION = 1963; // Выберите предмет, с которого вы хотите снять зачарование.
	public static final int AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM = 1964; // Снять зачарование можно только с зачарованного предмета.
	public static final int AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1 = 1965; // Зачарование было успешно снято с предмета $s1.
	public static final int ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN = 1970; // Зачарованный предмет не может быть зачарован снова.

	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION = 1972; // Вы не можете зачаровать предметы, пока действует режим торговой лавки или мастерской.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD = 1974; // Вы не можете зачаровать предметы, будучи мертвым.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED = 1976; // Вы не можете зачаровать предметы, будучи парализованным.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING = 1977; // Вы не можете зачаровать предметы во время рыбалки.
	public static final int YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN = 1978; // Вы не можете зачаровать предметы сидя.

	public static final int PRESS_THE_AUGMENT_BUTTON_TO_BEGIN = 1984; // Для начала нажмите кнопку "Зачаровать".
	public static final int AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS = 2001; // Условия не соблюдены, зачарование не удалось.

	// Shadow items
	public static final int S1S_REMAINING_MANA_IS_NOW_10 = 1979; // $s1: осталось 10 маны.
	public static final int S1S_REMAINING_MANA_IS_NOW_5 = 1980; // $s1: осталось 5 маны.
	public static final int S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON = 1981; // $s1: осталась 1 мана. Предмет скоро исчезнет.
	public static final int S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED = 1982; // $s1: мана закончилась. Предмет исчез.

	// Limited-items
	public static final int THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED = 2366; // Вы удалили предмет с ограниченным временем использования.

	// Трансформация
	public static final int YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN = 2058; // Вы уже превратились и не можете сделать этого снова.
	public static final int THE_NEARBLY_AREA_IS_TOO_NARROW_FOR_YOU_TO_POLYMORPH_PLEASE_MOVE_TO_ANOTHER_AREA_AND_TRY_TO_POLYMORPH_AGAIN = 2059; // Окружающее пространство слишком тесно для вашего превращения. Пожалуйста, отойдите в другую зону и попробуйте превратиться снова.
	public static final int YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER = 2060; // Вы не сможете превратиться в воде.
	public static final int YOU_ARE_STILL_UNDER_TRANSFORM_PENALTY_AND_CANNOT_BE_POLYMORPHED = 2061; // Вы находитесь под действием штрафа превращения и не можете изменить форму.
	public static final int YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITOR_PET = 2062; // Вы не можете превратиться, когда призван слуга/питомец.
	public static final int YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET = 2063; // Вы не можете превратиться верхом на питомце.
	public static final int YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL = 2064; // Вы не можете превратиться, находясь под действием особого умения.
	public static final int YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT = 2182; // Вы не можете перевоплощаться на корабле.
	public static final int YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED = 2213; // Вы не можете взойти на борт корабля превращенным.
	public static final int CURRENT_POLYMORPH_FORM_CANNOT_BE_APPLIED_WITH_CORRESPONDING_EFFECTS = 2194; // Превращение невозможно с этими эффектами.
	public static final int SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON = 2085; // Крик и торговля в чате недоступны, пока у вас есть проклятое оружие.
	public static final int YOU_CANNOT_TRANSFORM_WHILE_SITTING = 2283; // Вы не можете превратиться сидя.

	public static final int THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED = 2161; // Недостаточно свободного пространства, умение не может быть использовано.

	// Абсорбация душ
	public static final int YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2 = 2162; // Ваше количество душ повысилось на $s1 и сейчас составляет $s2.
	public static final int SOUL_CANNOT_BE_INCREASED_ANY_MORE = 2163; // Количество душ больше не может быть повышено.
	public static final int SOUL_CANNOT_BE_ABSORBED_ANY_MORE = 2186; // Больше душ поглотить нельзя.
	public static final int THERE_IS_NOT_ENOUGHT_SOUL = 2195; // Недостаточно душ.

	public static final int AGATHION_SKILLS_CAN_BE_USED_ONLY_WHEN_AGATHION_IS_SUMMONED = 2292; // Умения Агатиона можно использовать только после того, как Агатион призван.

	public static final int YOU_HAVE_GAINED_VITALITY_POINTS = 2296; // Требуются очки энергии.
	public static final int YOUR_VITALITY_IS_AT_MAXIMUM = 2314; // Энергия полна.
	public static final int VITALITY_HAS_INCREASED = 2315; // Энергия увеличена.
	public static final int VITALITY_HAS_DECREASED = 2316; // Энергия уменьшена.
	public static final int VITALITY_IS_FULLY_EXHAUSTED = 2317; // Вся энергия израсходована.

	public static final int ACQUIRED_50_CLAN_FAME_POINTS = 2326; // Вы получили 50 очков клановой репутации.
	public static final int NOT_ENOUGH_FAME_POINTS = 2327; // У вас недостаточно очков репутации.
	public static final int YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE = 2319; // Вы получили $s1 очков репутации.

	public static final int C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED = 2096; // $c1 находится в локации, в которую нельзя войти, поэтому действие невозможно.
	public static final int C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED = 2097; // $c1 не соответствует требованиям уровня и не может войти.
	public static final int C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED = 2098; // $c1 не соответствует требованиям квеста и не может войти.
	public static final int C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED = 2099; // $c1 не соответствует требованиям предмета и не может войти.
	public static final int C1_MAY_NOT_RE_ENTER_YET = 2100; // Время повторного входа для персонажа $c1 еще не пришло, $c1 не может войти.
	public static final int YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER = 2101; // Вы не состоите в группе и не можете войти.
	public static final int YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT = 2102; // Вы не можете войти, так как группа превысила лимит.
	public static final int YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL = 2103; // Вы не можете войти, так как вы не в текущем Канале Команды.
	//2104	1	The maximum number of instance zones has been exceeded. You cannot enter
	public static final int YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON = 2105; // Вы уже вошли в другую временную зону, поэтому не можете войти в данную.
	public static final int THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES = 2106; // Подземелье закроется через $s1 мин. По истечении времени вы будете выброшены из подземелья.
	public static final int THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES = 2107; // Временная зона закроется через $s1 мин. По истечении времени вы будете выброшены из подземелья.
	public static final int ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER = 2185; // Только лидер группы может попробовать войти.
	public static final int ITS_TOO_FAR_FROM_THE_NPC_TO_WORK = 2193; // NPC слишком далеко.

	public static final int INSTANCE_ZONE_TIME_LIMIT = 2228; // Лимит времени зоны:
	public static final int THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT = 2229; // Все временные зоны доступны.
	public static final int S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOURS_S3_MINUTES = 2230; // $s1: зона будет доступна через $s2 ч $s3 мин.

	// TODO Расставить сообщения для фортов
	public static final int ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS = 2084; // Вражеские Заложники Крови ворвались в крепость
	public static final int A_FORTRESS_IS_UNDER_ATTACK = 2087; // Крепость атакована!
	public static final int S1_MINUTE_UNTIL_THE_FORTRESS_BATTLE_STARTS = 2088; // Битва за крепость начинается через $s1 мин.
	public static final int S1_SECOND_UNTIL_THE_FORTRESS_BATTLE_STARTS = 2089; // Битва за крепость начинается через $s1 сек.
	public static final int THE_FORTRESS_BATTLE_S1_HAS_BEGAN = 2090; // Битва за крепость началась.
	public static final int YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1_FORTRESS_BATTLE = 2169; // Ваш клан был заявлен на битву за крепость $s1.
	public static final int THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED = 2183; // Битва за крепость $s1 закончена.
	public static final int S1_CLAN_IS_VICTORIOUS_IN_THE_FORTRESS_BATLE_OF_S2 = 2184; // В битве за крепость $s2 победил клан $s1.
	public static final int THE_REBEL_ARMY_RECAPTURED_THE_FORTRESS = 2276; // Армия повстанцев отвоевала крепость обратно.

	public static final int FIVE_YEARS_HAVE_PASSED_SINCE_THIS_CHARACTERS_CREATION = 2447; // Со дня создания персонажа прошло 5 лет.
	public static final int YOUR_BIRTHDAY_GIFT_HAS_ARRIVED_YOU_CAN_OBTAIN_IT_FROM_THE_GATEKEEPER_IN_ANY_VILLAGE = 2448; // Доставлен подарок в честь дня создания персонажа. Вы можете получить его у Хранителя Портала любой из деревень.
	public static final int THERE_ARE_S1_DAYS_UNTIL_YOUR_CHARACTERS_BIRTHDAY_ON_THAT_DAY_YOU_CAN_OBTAIN_A_SPECIAL_GIFT_FROM_THE_GATEKEEPER_IN_ANY_VILLAGE = 2449; // До дня создания персонажа осталось $s1 дн. Вы можете получить подарок у Хранителя Портала любой из деревень.
	public static final int C1S_CHARACTER_BIRTHDAY_IS_S3S4S2 = 2450; // Дата создания персонажа $c1: $s2 г. $s3 м. $s4 ч.

	public static final int _IF_YOU_JOIN_THE_CLAN_ACADEMY_YOU_CAN_BECOME_A_CLAN_MEMBER_AND_LEARN_THE_GAME_SYSTEM_UNTIL_YOU = 2875; // Поступив в Академию клана, Вы можете стать его членом и изучать игровую систему до получения 40-го уровня. Если хотите получать от игры больше удовольствия, то настоятельно рекомендуем Вам поступить в Академию.
	public static final int _IF_YOU_BECOME_LEVEL_40_THE_SECOND_CLASS_CHANGE_IS_AVAILABLE_IF_YOU_COMPLETE_THE_SECOND_CLASS = 2876; // По достижении 40-го уровня Вы получаете возможность второй смены профессии. После выполнения этого действия возможности Вашего персонажа возрастут.
	public static final int THIS_ITEM_CANNOT_BE_USED_IN_THE_CURRENT_TRANSFORMATION_STATTE = 2962; // Данный предмет недоступен в режиме перевоплощения.
	public static final int THE_OPPONENT_HAS_NOT_EQUIPPED_S1_SO_S2_CANNOT_BE_USED = 2963; // Персонаж не надел $s1%, поэтому не может использовать $s2%.
	public static final int BEING_APPOINTED_AS_A_NOBLESSE_WILL_CANCEL_ALL_RELATED_QUESTS_DO_YOU_WISH_TO_CONTINUE = 2964; // Если Вы станете Дворянином, все выполняемые квесты будут удалены. Продолжить?
	public static final int YOU_CANNOT_PURCHASE_AND_RE_PURCHASE_THE_SAME_TYPE_OF_ITEM_AT_THE_SAME_TIME = 2965; // Первичная и повторная покупка предметов одинакового типа одновременно невозможна.
	public static final int IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM = 2966; // Безопасная сделка. Прикрепите предмет.
	public static final int YOU_ARE_ATTEMPTING_TO_SEND_MAIL_DO_YOU_WISH_TO_PROCEED = 2967; // Вы действительно хотите совершить отправку?
	public static final int THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED = 2968; // Вы превысили лимит почты (240 шт.), поэтому отправка невозможна.
	public static final int THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED = 2969; // С момента пересылки предыдущего письма не прошло одной минуты, поэтому отправка невозможна.
	public static final int YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION = 2970; // Отправка возможна только из мирных зон.
	public static final int YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE = 2971; // Во время обмена отправка невозможна.
	public static final int YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS = 2972; // Отправка невозможна при открытой торговой лавке или мастерской.
	public static final int YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT = 2973; // Во время улучшения предмета отправка невозможна.
	public static final int THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER = 2974; // Отправляемый предмет не подходит.
	public static final int YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA = 2975; // У Вас не хватает денег для отправки.
	public static final int YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION = 2976; // Получение возможно только в мирной зоне.
	public static final int YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE = 2977; // Во время обмена получение писем невозможно.
	public static final int YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS = 2978; // При открытой торговой лавке или мастерской получение невозможно.
	public static final int YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT = 2979; // Во время улучшения предмета получение невозможно.
	public static final int YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA = 2980; // У вас не хватает денег для получения.
	public static final int YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL = 2981; // Из-за ошибки инвентаря Вам не удалось получить посылку.
	public static final int YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION = 2982; // Отмена доступна только в мирной зоне.
	public static final int YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE = 2983; // Во время обмена отмена невозможна.
	public static final int YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS = 2984; // При открытой торговой лавке или мастерской отмена невозможна.
	public static final int YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT = 2985; // Во время улучшения предмета отмена невозможна.
	public static final int PLEASE_SET_THE_AMOUNT_OF_ADENA_TO_SEND = 2986; // Выберите количество отправляемых аден.
	public static final int PLEASE_SET_THE_AMOUNT_OF_ADENA_TO_RECEIVE = 2987; // Выберите количество получаемых аден.
	public static final int YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL = 2988; // Из-за ошибки в инвентаре отменить получение не удалось.
	public static final int VITAMIN_ITEM_S1_IS_BEING_USED = 2989; // Вы использовали Витамин $s1%.
	public static final int _2_UNITS_OF_VITAMIN_ITEM_S1_WAS_CONSUMED = 2990; // Истрачен Витамин $s1% в количестве $2 ед.
	public static final int TRUE_INPUT_MUST_BE_ENTERED_BY_SOMEONE_OVER_15_YEARS_OLD = 2991; // Петиция должна содержать не более 15 символов.
	public static final int PLEASE_CHOOSE_THE_2ND_STAGE_TYPE = 2992; // Выберите тип 2-го этапа.
	public static final int WHEN_AN_COMMAND_CHANNEL_LEADER_GOES_OUT_OF_THE_COMMAND_CHANNEL_MATCHING_ROOM_THE_CURRENTLY_OPEN = 2993; // При выходе Командира Альянса Комната Поиска Союза закроется. Хотите выйти?
	public static final int THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED = 2994; // Комната Поиска Союза была закрыта.
	public static final int THIS_COMMAND_CHANNEL_MATCHING_ROOM_IS_ALREADY_CANCELLED = 2995; // Эта комната уже закрыта.
	public static final int YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS = 2996; // Вы не подходите под условия Комнаты Поиска Союза.
	public static final int YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM = 2997; // Вы вышли из Комнаты Поиска Союза.
	public static final int YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM = 2998; // Вас выгнали из Комнаты Поиска Союза.
	public static final int THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN = 2999; // Член группы, входящей в Канал Союза, не может использовать окно поиска.
	public static final int THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED = 3000; // Создана Комната Поиска Союза.
	public static final int THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED = 3001; // Изменена информация Комнаты Поиска Союза.
	public static final int WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE = 3002; // Невозможно отправить письмо, если получатель не существует или данный персонаж удален.
	public static final int C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM = 3003; // $c1 вошел в Комнату Поиска Союза.
	public static final int I_M_SORRY_TO_GIVE_YOU_A_SATISFACTORY_RESPONSE__N__NIF_YOU_SEND_YOUR_COMMENTS_REGARDING_THE = 3004; // Извините, что не смогли дать удовлетворяющий Вас ответ.\\n\\nЕсли Вы скажете, что именно вызвало недовольство, мы сделаем все, чтобы это больше не повторилось.\\n\\nБудем рады Вашему сообщению.
	public static final int THIS_SKILL_CANNOT_BE_ENHANCED = 3005; // Это умение улучшить нельзя.
	public static final int NEWLY_USED_PC_CAFE_S1_POINTS_WERE_WITHDRAWN = 3006; // Неиспользованные очки РС, $s1, вернулись.
	public static final int SHYEED_S_ROAR_FILLED_WITH_WRATH_RINGS_THROUGHOUT_THE_STAKATO_NEST = 3007; // Гнездо Стакато наполняется плачем разгневанной Шиид.
	public static final int THE_MAIL_HAS_ARRIVED = 3008; // Доставлена посылка.
	public static final int MAIL_SUCCESSFULLY_SENT = 3009; // Вы успешно отправили посылку.
	public static final int MAIL_SUCCESSFULLY_RETURNED = 3010; // Посылка была успешно доставлена обратно.
	public static final int MAIL_SUCCESSFULLY_CANCELLED = 3011; // Вы успешно отменили отправку.
	public static final int MAIL_SUCCESSFULLY_RECEIVED = 3012; // Посылка успешно получена.
	public static final int C1_HAS_SUCCESSFULY_ENCHANTED_A__S2_S3 = 3013; // $c1 успешно совершил улучшение до +$s2$s3.
	public static final int DO_YOU_WISH_TO_ERASE_THE_SELECTED_MAIL = 3014; // Вы хотите удалить выбранные письма?
	public static final int PLEASE_SELECT_THE_MAIL_TO_BE_DELETED = 3015; // Выберите письма, которые хотите стереть.
	public static final int ITEM_SELECTION_IS_POSSIBLE_UP_TO_8 = 3016; // Можно прикрепить до 8 предметов за раз.
	public static final int YOU_CANNOT_USE_ANY_SKILL_ENHANCING_SYSTEM_UNDER_YOUR_STATUS_CHECK_OUT_THE_PC_S_CURRENT_STATUS = 3017; // Вы не можете использовать систему улучшения умения. Проверьте состояние вашего персонажа.
	public static final int YOU_CANNOT_USE_SKILL_ENHANCING_SYSTEM_FUNCTIONS_FOR_THE_SKILLS_CURRENTLY_NOT_ACQUIRED = 3018; // Вы не можете улучшить умение, которым не обладаете.
	public static final int YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF = 3019; // Вы не можете отправить посылку самому себе.
	public static final int WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL = 3020; // Вы не можете совершить отправку, если не введете сумму оплаты.
	public static final int STAND_BY_FOR_THE_GAME_TO_BEGIN = 3021; // Сейчас Вы ожидаете начала соревнований.
	public static final int THE_KASHA_S_EYE_GIVES_YOU_A_STRANGE_FEELING = 3022; // Чувствуется мощная энергия, источаемая Монстроглазом Кхаши.
	public static final int I_CAN_FEEL_THAT_THE_ENERGY_BEING_FLOWN_IN_THE_KASHA_S_EYE_IS_GETTING_STRONGER_RAPIDLY = 3023; // Монстроглаз Кхаши набирается сил с ужасающей скоростью.
	public static final int KASHA_S_EYE_PITCHES_AND_TOSSES_LIKE_IT_S_ABOUT_TO_EXPLODE = 3024; // Монстроглаз Кхаши вот-вот взорвется.
	public static final int S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL = 3025; // $s2 завершил оплату и вы получили $s1 Аден
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION = 3026; // На данном уровне вы не можете улучшить умение. Эта функция доступна игрокам, достигшим 76-го уровня.
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION = 3027; // Ваша профессия не позволяет использовать функцию улучшения умения. Она доступна после 3-й смены профессии.
	public static final int YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING = 3028; // Ваше состояние не позволяет улучшить умение. Эта функция доступна, если вы не перевоплощены, не сражаетесь и не сидите верхом на ездовом существе.
	public static final int S1_RETURNED_THE_MAIL = 3029; // $s1 отправил посылку.
	public static final int YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT = 3030; // Получатель уже открыл посылку, поэтому отменить отправку нельзя.
	public static final int BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS = 3031; // Воспользуйтесь святым мечом Эйнхасад, чтобы расправиться с приспешниками Лилим!
	public static final int IN_ORDER_TO_HELP_ANAKIM_ACTIVATE_THE_SEALING_DEVICE_OF_THE_EMPEROR_WHO_IS_POSSESED_BY_THE_EVIL = 3032; // Чтобы помочь Анакиму, активируйте Устройство Печати Императора, проклятое Лилит! Сила проклятия ужасающа, остерегайтесь ее!
	public static final int BY_USING_THE_INVISIBLE_SKILL_SNEAK_INTO_THE_DAWN_S_DOCUMENT_STORAGE = 3033; // Воспользуйтесь умением стража "Скрыться", чтобы попасть в Хранилище Документов Рассвета!
	public static final int THE_DOOR_IN_FRONT_OF_US_IS_THE_ENTRANCE_TO_THE_DAWN_S_DOCUMENT_STORAGE_APPROACH_TO_THE_CODE = 3034; // Прямо перед вами Библиотека Рассвета! Подойдите к Устройству Ввода Пароля!
	public static final int MY_POWER_S_WEAKENING_PLEASE_ACTIVATE_THE_SEALING_DEVICE_POSSESSED_BY_LILITH_S_MAGICAL_CURSE = 3035; // Наша сила иссякает. Активируйте проклятое Лилит Устройство Печати!
	public static final int YOU_SUCH_A_FOOL_THE_VICTORY_OVER_THIS_WAR_BELONGS_TO_SHILIEN = 3036; // Глупцы! Победа в этой войне будет за Шилен!
	public static final int MALE_GUARDS_CAN_DETECT_THE_CONCEALMENT_BUT_THE_FEMALE_GUARDS_CANNOT = 3037; // Стражники могут обнаружить скрывшихся игроков, а стражницы - нет.
	public static final int FEMALE_GUARDS_NOTICE_THE_DISGUISES_FROM_FAR_AWAY_BETTER_THAN_THE_MALE_GUARDS_DO_SO_BEWARE = 3038; // Стражницы обнаруживают перевоплощенных игроков на большей дистанции, чем стражники, так что остерегайтесь их.
	public static final int BY_USING_THE_HOLY_WATER_OF_EINHASAD_OPEN_THE_DOOR_POSSESSED_BY_THE_CURSE_OF_FLAMES = 3039; // Воспользуйтесь Святой Водой Эйнхасад, чтобы открыть Дверь Огненного Проклятия.
	public static final int BY_USING_THE_COURT_MAGICIAN_S_MAGIC_STAFF_OPEN_THE_DOOR_ON_WHICH_THE_MAGICIAN_S_BARRIER_IS = 3040; // Воспользуйтесь Скипетром Королевского Волшебника, чтобы открыть Грань Реальности Волшебника.
	public static final int AROUND_FIFTEEN_HUNDRED_YEARS_AGO_THE_LANDS_WERE_RIDDLED_WITH_HERETICS = 3041; // Полторы тысячи лет назад было время, когда последователи\\nбогини Шилен
	public static final int WORSHIPPERS_OF_SHILEN_THE_GODDESS_OF_DEATH = 3042; // называли себя Детьми Шилен. Их сила была чрезвычайно\\nвелика.
	public static final int BUT_A_MIRACLE_HAPPENED_AT_THE_ENTHRONEMENT_OF_SHUNAIMAN_THE_FIRST_EMPEROR = 3043; // В это смутное время проходила коронация первого императора\\nЭльморедена Шунаймана.
	public static final int ANAKIM_AN_ANGEL_OF_EINHASAD_CAME_DOWN_FROM_THE_SKIES = 3044; // Во время этой коронации произошло чудо. С неба спустилась
	public static final int SURROUNDED_BY_SACRED_FLAMES_AND_THREE_PAIRS_OF_WINGS = 3045; // шестикрылая посланница Эйнхасад, Анаким.
	public static final int THUS_EMPOWERED_THE_EMPEROR_LAUNCHED_A_WAR_AGAINST__SHILEN_S_PEOPLE_ = 3046; // Император обрел великую силу и начал святую войну против\\nДетей Шилен.
	public static final int THE_EMPEROR_S_ARMY_LED_BY_ANAKIM_ATTACKED__SHILEN_S_PEOPLE__RELENTLESSLY = 3047; // Хотя армия императора, преисполненная духом Анакима,\\nс легкостью победила Детей Шилен,
	public static final int BUT_IN_THE_END_SOME_SURVIVORS_MANAGED_TO_HIDE_IN_UNDERGROUND_CATACOMBS = 3048; // многие из них выжили и спрятались в катакомбах храма.
	public static final int A_NEW_LEADER_EMERGED_LILITH_WHO_SEEKED_TO_SUMMON_SHILEN_FROM_THE_AFTERLIFE = 3049; // Выжившие Дети Шилен обрели своего нового лидера, Лилит,\\nвозглавившую их с целью воскресить Шилен
	public static final int AND_TO_REBUILD_THE_LILIM_ARMY_WITHIN_THE_EIGHT_NECROPOLISES = 3050; // и создавшую свою армию в восьми некрополях.
	public static final int NOW_IN_THE_MIDST_OF_IMPENDING_WAR_THE_MERCHANT_OF_MAMMON_STRUCK_A_DEAL = 3051; // В это время Торговцы Маммона, ради денег готовые на все,
	public static final int HE_SUPPLIES_SHUNAIMAN_WITH_WAR_FUNDS_IN_EXCHANGE_FOR_PROTECTION = 3052; // заключили с императором секретный договор.
	public static final int AND_RIGHT_NOW_THE_DOCUMENT_WE_RE_LOOKING_FOR_IS_THAT_CONTRACT = 3053; // И именно этот договор мы и должны разыскать.
	public static final int FINALLY_YOU_RE_HERE_I_M_ANAKIM_I_NEED_YOUR_HELP = 3054; // Наконец-то вы пришли! Я ждала вас!
	public static final int IT_S_THE_SEAL_DEVICES_I_NEED_YOU_TO_DESTROY_THEM_WHILE_I_DISTRACT_LILITH = 3055; // Пока я буду сдерживать Лилит, активизируйте механизм\\nПечати!
	public static final int PLEASE_HURRY_I_DON_T_HAVE_MUCH_TIME_LEFT = 3056; // Я не смогу долго ее сдерживать. Торопитесь!
	public static final int FOR_EINHASAD = 3057; // За Эйнхасад!
	public static final int EMBRYO = 3058; // Эмб... рио…
	public static final int S1_DID_NOT_RECEIVE_IT_DURING_THE_WAITING_TIME_SO_IT_WAS_RETURNED_AUTOMATICALLY_TNTLS = 3059; // $s1 не получил посылку в установленное время, поэтому она возвращена.
	public static final int THE_SEALING_DEVICE_GLITTERS_AND_MOVES_ACTIVATION_COMPLETE_NORMALLY = 3060; // Устройство Печати запущено. Механизм в порядке!
	public static final int THERE_COMES_A_SOUND_OF_OPENING_THE_HEAVY_DOOR_FROM_SOMEWHERE = 3061; // До Вас доносится звук открывающихся дверей.
	public static final int DO_YOU_WANT_TO_PAY_S1_ADENA = 3062; // Заплатить $s1 аден?
	public static final int DO_YOU_REALLY_WANT_TO_FORWARD = 3063; // Вернуть посылку?
	public static final int THERE_IS_AN_UNREAD_MAIL = 3064; // У вас есть непрочтенное письмо.
	public static final int CURRENT_LOCATION__INSIDE_THE_CHAMBER_OF_DELUSION = 3065; // Координаты: Внутри Грани Реальности
	public static final int YOU_CANNOT_USE_THE_MAIL_FUNCTION_OUTSIDE_THE_PEACE_ZONE = 3066; // Функции почты можно использовать только в мирной зоне.
	public static final int S1_CANCELED_THE_SENT_MAIL = 3067; // $s1 отменил отправку посылки.
	public static final int THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME = 3068; // Время ожидания превышено, поэтому посылка отправлена обратно.
	public static final int DO_YOU_REALLY_WANT_TO_CANCEL_THE_TRANSACTION = 3069; // Отменить сделку?
	public static final int SKILL_NOT_AVAILABLE_TO_BE_ENHANCED_CHECK_SKILL_S_LV_AND_CURRENT_PC_STATUS = 3070; // Это умение улучшить нельзя. Проверьте уровень умения и состояние персонажа.
	public static final int DO_YOU_REALLY_WANT_TO_RESET_1000000010_MILLION_ADENA_WILL_BE_CONSUMED = 3071; // Перезапустить? Будет истрачено 10,000,000 аден.
	public static final int S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL = 3072; // $s1 получил предмет, прикрепленный к письму.
	public static final int YOU_HAVE_ACQUIRED_S2_S1 = 3073; // Вы получили $s1, $s2 ед.
	public static final int THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED = 3074; // Вы превысили установленную длину имени получателя.
	public static final int THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED = 3075; // Вы превысили установленную длину названия.
	public static final int _THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED = 3076; // Вы превысили разрешенный объем содержания.
	public static final int THE_MAIL_LIMIT_240_OF_THE_OPPONENT_S_CHARACTER_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED = 3077; // У получателя переполнен почтовый ящик (240 ед.), поэтому отправка невозможна.
	public static final int YOU_RE_MAKING_A_REQUEST_FOR_PAYMENT_DO_YOU_WANT_TO_PROCEED = 3078; // Оплата. Отправить деньги?
	public static final int THERE_ARE_ITEMS_IN_THE_PET_INVENTORY_SO_YOU_CANNOT_REGISTER_AS_AN_INDIVIDUAL_STORE_OR_DROP_ITEMS = 3079; // В инвентаре Питомца имеются предметы, поэтому обменять его, выставить на продажу в личной торговой лавке или прогнать нельзя. Сначала очистите инвентарь Питомца.
	public static final int YOU_CANNOT_RESET_THE_SKILL_LINK_BECAUSE_THERE_IS_NOT_ENOUGH_ADENA = 3080; // У вас не хватает аден, чтобы сбросить настройки ярлыков умений.
	public static final int YOU_CANNOT_RECEIVE_IT_BECAUSE_YOU_ARE_UNDER_THE_CONDITION_THAT_THE_OPPONENT_CANNOT_ACQUIRE_ANY = 3081; // Партнер не может получить деньги, поэтому купить данный предмет невозможно.
	public static final int YOU_CANNOT_SEND_MAILS_TO_ANY_CHARACTER_THAT_HAS_BLOCKED_YOU = 3082; // Отправить посылку персонажу, который вас заблокировал, нельзя.
	public static final int IN_THE_PROCESS_OF_WORKING_ON_THE_PREVIOUS_CLAN_DECLARATION_RETREAT_PLEASE_TRY_AGAIN_LATER = 3083; // Идет обработка информации о прошлой войне кланов. Повторите попытку позже.
	public static final int CURRENTLY_WE_ARE_IN_THE_PROCESS_OF_CHOOSING_A_HERO_PLEASE_TRY_AGAIN_LATER = 3084; // Идет обработка о выборе героя. Повторите попытку позже.
	public static final int YOU_CAN_SUMMON_THE_PET_YOU_ARE_TRYING_TO_SUMMON_NOW_ONLY_WHEN_YOU_OWN_AN_AGIT = 3085; // Вызвать этого Питомца можно только при наличии Холла Клана.
	public static final int WOULD_YOU_LIKE_TO_GIVE_S2_S1 = 3086; // Вы хотите передать игроку $s1 $s2?
	public static final int THIS_MAIL_IS_BEING_SENT_WITH_A_PAYMENT_REQUEST_WOULD_YOU_LIKE_TO_CONTINUE = 3087; // Это функция предназначена для перечисления денег. Вы согласны продолжить?
	public static final int YOU_HAVE_S1_HOURS_S2_MINUTES_AND_S3_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME_IF_AGATHION_IS = 3088; // До повторного предоставления Свидетельства Пространства осталось $s1час. $s2мин. $s3сек. (Если в данный период повторно призвать Агатиона, то можно продлить время на 10 мин.)
	public static final int YOU_HAVE_S1_MINUTES_AND_S2_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME_IF_AGATHION_IS_SUMMONED = 3089; // До повторного предоставления Свидетельства Пространства осталось $s1мин. $s2сек. (Если в данный период повторно призвать Агатиона, то можно продлить время на 10 мин.)
	public static final int YOU_HAVE_S1_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME_IF_AGATHION_IS_SUMMONED_WITHIN_THIS_TIME = 3090; // До повторного предоставления Свидетельства Пространства осталось $s1сек. (Если в данный период повторно призвать Агатиона, то можно продлить время на 10 мин.)
	public static final int YOU_CANNOT_DELETE_CHARACTERS_ON_THIS_SERVER_RIGHT_NOW = 3091; // В настоящее время на данном сервере удаление персонажа невозможно.
	public static final int TRANSACTION_COMPLETED = 3092; // Сделка завершена.
	public static final int YOU_ARE_PROTECTED_AGGRESSIVE_MONSTERS = 3108;
	public static final int YOU_ACQUIRED_S1_EXP_AND_S2_SP_AS_A_REWARD_YOU_RECEIVE_S3_MORE_EXP = 6011; // Вы получили $s1 очков опыта и $s2 SP. (В награду вы дополнительно получаете $s3 очков опыта.)
	public static final int A_BLESSING_THAT_INCREASES_EXP_BY_1_2 = 6012; // Количество очков опыта увеличивается на $s1 $s2
	public static final int IT_IS_NOT_A_BLESSING_PERIOD_WHEN_YOU_REACH_TODAY_S_TARGET_YOU_CAN_RECEIVE_S1 = 6013; // Сейчас Ивент не проводится. Вам необходимо достичь сегодняшнюю цель, и тогда вы получите награду: $s1.
	public static final int IT_IS_EVA_S_BLESSING_PERIOD_S1_WILL_BE_EFFECTIVE_UNTIL_S2 = 6014; // Доступно благословение Евы. Вы получаете $s1 до $s2.
	public static final int IT_IS_EVA_S_BLESSING_PERIOD_UNTIL_S1_JACK_SAGE_CAN_GIFT_YOU_WITH_S2 = 6015; // Доступно благословение Евы. До $s1 Стив выдаст $s2.
	public static final int PROGRESS__EVENT_STAGE_S1 = 6016; // Выполнение ($s1-й день Ивента)
	public static final int EVA_S_BLESSING_STAGE_S1_HAS_BEGUN = 6017; // Начинается $s1-й день ивента Благословение Евы.
	public static final int EVA_S_BLESSING_STAGE_S1_HAS_ENDED = 6018; // Заканчивается $s1-й день ивента Благословение Евы.
	public static final int YOU_CANNOT_BUY_THE_ITEM_ON_THIS_DAY_OF_THE_WEEK = 6019; // В этот день недели покупка предмета невозможна.
	public static final int YOU_CANNOT_BUY_THE_ITEM_AT_THIS_HOUR = 6020; // В данный период покупка предмета невозможна.
	public static final int S1_REACHED_S2_CONSECUTIVE_WINS_IN_JACK_GAME = 6021; // Персонаж $s1 победил $s2 раз в игре Джека.
	public static final int S1_RECEIVED_S4_S3_AS_REWARD_FOR_S2_CONSECUTIVE_WINS = 6022; // Персонаж $s1 получил предмет $s3 ($s4 шт.) в награду за то, что победил  $s2 раз.
	public static final int WORLD__S1_CONSECUTIVE_WINS_S2_PPL = 6023; // Мир: Побед - $s1 ($s2 чел.)
	public static final int MY_RECORD__S1_CONSECUTIVE_WINS = 6024; // Вы: побед: $s1
	public static final int WORLD__UNDER_4_CONSECUTIVE_WINS = 6025; // Мир: меньше 4 побед
	public static final int MY_RECORD__UNDER_4_CONSECUTIVE_WINS = 6026; // Вы: меньше 4 побед
	public static final int IT_S_HALLOWEEN_EVENT_PERIOD = 6027; // Период Ивента Хеллоуина.
	public static final int NO_RECORD_OVER_10_CONSECUTIVE_WINS = 6028; // Нет записей свыше 10 побед.
	public static final int C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS = 3164; // Персонаж $c1 не может совершать парных действий, запрос на Парное действие недоступен.

	public static final int YOU_HAVE_EXCEEDED_THE_CORRECT_CALCULATION_RANGE_PLEASE_ENTER_AGAIN = 3093; // Вы превысили лимит цены. Пожалуйста, введите цену еще раз.
	public static final int PLEASE_HELP_RAISE_REINDEER_FOR_SANTA_S_CHRISTMAS_DELIVERY = 6029; // Позаботьтесь о Рудольфе и вырастите его! Он нужен для доставки новогодних подарков.
	public static final int SANTA_HAS_STARTED_DELIVERING_THE_CHRISTMAS_GIFTS_TO_ADEN = 6030; // Седобородый Дед Мороз начинает развозить новогодние подарки по миру Аден.
	public static final int SANTA_HAS_COMPLETED_THE_DELIVERIES_SEE_YOU_IN_1_HOUR = 6031; // Седобородый Дед Мороз развез все подарки. Увидемся через 1 час.
	public static final int SANTA_IS_OUT_DELIVERING_THE_GIFTS_MERRY_CHRISTMAS = 6032; // Седобородый Дед Мороз развозит новогодние подарки. С Новым годом!
	public static final int ONLY_THE_TOP_S1_APPEAR_IN_THE_RANKING_AND_ONLY_THE_TOP_S2_ARE_RECORDED_IN_MY_BEST_RANKING = 6033; // В рейтинге отображается до $s1 чел., а самые первые $s2 чел. записываются в Мой высший рейтинг.
	public static final int S1_HAVE_HAS_BEEN_INITIALIZED = 6034; // $s1 обнулилось.
	public static final int WHEN_THERE_ARE_MANY_PLAYERS_WITH_THE_SAME_SCORE_THEY_APPEAR_IN_THE_ORDER_IN_WHICH_THEY_WERE = 6035; // Если одинаковое количество очков набрало несколько персонажей, то выше отобразится тот, кто набрал их раньше.
	public static final int BELOW_S1_POINTS = 6036; // Меньше $s1 очков

	public static final int YOU_CANCEL_FOR_COUPLE_ACTION = 3119; // Вы отказались от Парного действия.
	public static final int REQUEST_CANNOT_COMPLETED_BECAUSE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS = 3120; // Запрос невозможен. Месторасположение цели не соответствует условиям.
	public static final int COUPLE_ACTION_WAS_CANCELED = 3121; // Парное действие отменено.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE = 3123; // Запрос о Парном действии невозможен. $c1 занят личной лавкой или личной мастерской.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING = 3124; // Запрос о Парном действии невозможен. $c1 занят ловлей рыбы.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT = 3125; // Запрос о Парном действии невозможен. $c1 находится в бою.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IN_ANOTHER_COUPLE_ACTION = 3126; // Запрос о Парном действии невозможен. $c1 уже согласился на другое Парное действие.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_CURSED_WEAPON_EQUIPED = 3127; // Запрос о Парном действии невозможен. $c1 находится в состоянии Хаоса.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD = 3128; // Запрос о Парном действии невозможен. $c1 участвует в Олимпиаде.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_HIDEOUT_SIEGE = 3129; // Запрос о Парном действии невозможен. $c1 участвует в битве за холл клана.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE = 3130; // Запрос о Парном действии невозможен. $c1 участвует в осаде замка.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER = 3131; // Запрос о Парном действии невозможен. $c1 сейчас на корабле, верхом на питомце и т.п..
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING = 3132; // Запрос о Парном действии невозможен. $c1 в состоянии телепортации.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM = 3133; // Запрос о Парном действии невозможен. $c1 в состоянии перевоплощения.
	public static final int COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD = 3139; // Запрос о Парном действии невозможен. Персонаж $c1 мертв.
	public static final int YOU_ASK_FOR_COUPLE_ACTION_C1 = 3150; // Вы направили запрос на Парное действие игроку $c1.
	public static final int C1_ACCEPTED_COUPLE_ACTION = 3151; // $c1 согласился на Парное действие.
	public static final int REQUESTING_APPROVAL_CHANGE_PARTY_LOOT_S1 = 3135; // Requesting approval for changing party loot to "$s1".
	public static final int PARTY_LOOT_CHANGE_CANCELLED = 3137; // Party loot change was cancelled.
	public static final int PARTY_LOOT_CHANGED_S1 = 3138; // Party loot was changed to "$s1".
	public static final int YOU_OBTAINED_S1_RECOMMENDS = 3207; // Получено рекомендаций: "$s1:.
	public static final int CURRENT_LOCATION__S1_S2_S3_INSIDE_SEED_OF_ANNIHILATION = 3170; // Текущая локация: $s1, $s2, $s3 (Внутри Семени Уничтожения)
	public static final int YOU_HAVE_EARNED_S1_B_S2_EXP_AND_S3_B_S4_SP = 3259; // Получено значение опыта $s1 (Бонус: $s2) и SP $s3 (Бонус: $s4).

	public SystemMessage(SystemMsg msg)
	{
		_messageId = msg.getId();
	}

	public SystemMessage(int messageId)
	{
		_messageId = messageId;
	}

	public SystemMessage(String msg)
	{
		this(S1);
		addString(msg);
	}

	public static SystemMessage obtainItemsByMail(ItemInstance item)
	{
		return new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getItemId()).addNumber(item.getCount());
	}

	public SystemMessage addString(String text)
	{
		args.add(new Arg(TYPE_TEXT, StringUtils.defaultString(text)));
		return this;
	}

	public SystemMessage addNumber(int number)
	{
		args.add(new Arg(TYPE_NUMBER, number));
		return this;
	}

	public SystemMessage addNumber(long number)
	{
		args.add(new Arg(TYPE_LONG, number));
		return this;
	}

	public SystemMessage addNumber(byte number)
	{
		args.add(new Arg(TYPE_BYTE, number));
		return this;
	}

	/**
	 * Устанавливает имя если это playable или id если это npc
	 */
	public SystemMessage addName(Creature cha)
	{
		if(cha == null)
			return addString(StringUtils.EMPTY);

		if(cha.isDoor())
			return addDoorName(((DoorInstance) cha).getDoorId());

		if(cha.isNpc())
		{
			NpcInstance npc = (NpcInstance) cha;
			if(npc.getTemplate().displayId != 0 || !npc.getName().equals(npc.getTemplate().name))
				return addString(npc.getName());
			return addNpcName(npc.getNpcId());
		}

		return addString(cha.getName());
	}

	public SystemMessage addDoorName(int id)
	{
		args.add(new Arg(TYPE_DOOR_NAME, new Integer(id)));
		return this;
	}

	public SystemMessage addNpcName(int id)
	{
		args.add(new Arg(TYPE_NPC_NAME, new Integer(1000000 + id)));
		return this;
	}

	public SystemMessage addItemName(int id)
	{
		args.add(new Arg(TYPE_ITEM_NAME, id));
		return this;
	}

	public SystemMessage addZoneName(Location loc)
	{
		args.add(new Arg(TYPE_ZONE_NAME, loc));
		return this;
	}

	public SystemMessage addSkillName(int id, int level)
	{
		args.add(new Arg(TYPE_SKILL_NAME, new int[] { id, level }));
		return this;
	}

	/**
	 * Elemental name - 0(Fire) ...
	 * @param type
	 * @return
	 */
	public SystemMessage addElemntal(int type)
	{
		args.add(new Arg(TYPE_ELEMENT_NAME, type));
		return this;
	}

	public SystemMessage addClassName(int id)
	{
		args.add(new Arg(TYPE_CLASS_NAME, id));
		return this;
	}

	/**
	 * ID from sysstring-e.dat
	 * @param type
	 * @return
	 */
	public SystemMessage addSystemString(int type)
	{
		args.add(new Arg(TYPE_SYSTEM_STRING, type));
		return this;
	}

	/**
	 * Instance name from instantzonedata-e.dat
	 * @param type id of instance
	 * @return
	 */
	public SystemMessage addInstanceName(int type)
	{
		args.add(new Arg(TYPE_INSTANCE_NAME, type));
		return this;
	}

	/**
	 * Castlename-e.dat<br>
	 * 0-9 Castle names<br>
	 * 21-64 CH names<br>
	 * 81-89 Territory names<br>
	 * 101-121 Fortress names<br>
	 * @param number
	 * @return
	 */
	public SystemMessage addFortId(int number)
	{
		args.add(new Arg(TYPE_CASTLE_NAME, number));
		return this;
	}

	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.SystemMessagePacket;
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		writeH(_messageId);
		writeC(args.size());
		for(Arg e : args)
		{
			writeC(e.type);

			switch(e.type)
			{
				case TYPE_TEXT:
				case TYPE_PLAYER_NAME:
				{
					writeS((String) e.obj);
					break;
				}
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ITEM_NAME:
				case TYPE_CASTLE_NAME:
				case TYPE_ELEMENT_NAME:
				case TYPE_SYSTEM_STRING:
				case TYPE_INSTANCE_NAME:
				case TYPE_DOOR_NAME:
				case TYPE_CLASS_NAME:
				{
					writeD(((Number) e.obj).intValue());
					break;
				}
				case TYPE_SKILL_NAME:
				{
					int[] skill = (int[]) e.obj;
					writeD(skill[0]); // id
					writeH(skill[1]); // level
					break;
				}
				case TYPE_LONG:
				{
					writeQ((Long) e.obj);
					break;
				}
				case TYPE_ZONE_NAME:
				{
					Location coord = (Location) e.obj;
					writeD(coord.x);
					writeD(coord.y);
					writeD(coord.z);
					break;
				}
				case TYPE_UNKNOWN_8:
				{
					writeD(0x00); //?
					writeH(0x00); //?
					writeH(0x00); //?
					break;
				}
				case TYPE_BYTE:
				{
					writeC(((Number) e.obj).byteValue());
					break;
				}
			}
		}
	}

	private class Arg
	{
		public final int type;
		public final Object obj;

		private Arg(int _type, Object _obj)
		{
			type = _type;
			obj = _obj;
		}
	}
}