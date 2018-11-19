package l2s.gameserver.model.pledge;

/**
 * @author VISTALL
 * @date 23:16/04.05.2011
 */
public enum Privilege
{
	FREE,
	//-------- Системные привелигии ----------------------
	CL_JOIN_CLAN, // Пригласить в клан
	CL_GIVE_TITLE, // Изменить титул
	CL_VIEW_WAREHOUSE, // Просмотр склада
	CL_MANAGE_RANKS, // Изменять права
	CL_PLEDGE_WAR, // Обьявлять войны
	CL_DISMISS, // Выгнать
	CL_REGISTER_CREST, // Изменить значек
	CL_APPRENTICE, // Изменять спонсора/ученика
	CL_TROOPS_FAME, // Учить саб-скилы
	CL_SUMMON_AIRSHIP, // Сумонить лет. корабль
	//-------- КХ привелигии ----------------------
	CH_ENTER_EXIT, // Вход/Выход
	CH_USE_FUNCTIONS, // Использовать функции
	CH_AUCTION, // Аукцион - зайдествовано
	CH_DISMISS, // Выгнать чужаков из КХ
	CH_SET_FUNCTIONS, // Уставливать функции
	//-------- Замковые/Фортовые привелигии ----------------------
	CS_FS_ENTER_EXIT, // Вход/Выход
	CS_FS_MANOR_ADMIN, // Настройки манора
	CS_FS_SIEGE_WAR, // Настройки осады/Регистрация на осады
	CS_FS_USE_FUNCTIONS, // Использовать функции,
	CS_FS_DISMISS, // Выгнать чужаков из замка
	CS_FS_MANAGER_TAXES, // Изменять налог
	CS_FS_MERCENARIES, // Добавлять гвардов
	CS_FS_SET_FUNCTIONS; // Изменять функции

	public static final int ALL = 0xFFFFFF;
	public static final int NONE = 0;

	private final int _mask;

	Privilege()
	{
		_mask = ordinal() == 0 ? 0 : 1 << ordinal();
	}

	public int mask()
	{
		return _mask;
	}
}