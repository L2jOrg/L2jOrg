package l2s.gameserver.model.base;

public enum ResidenceFunctionType
{
	NONE,
	RESTORE_HP,
	RESTORE_MP,
	RESTORE_CP,
	RESTORE_EXP,
	TELEPORT,
	BROADCAST,
	CURTAIN,
	HANGING,
	SUPPORT,
	OUTERFLAG,
	PLATFORM,
	ITEM_CREATE;

	public static final ResidenceFunctionType[] VALUES = values();
}