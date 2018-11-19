package l2s.gameserver.model.base;

/**
 * @author Bonux
**/
public enum HitCondBonusType
{
	AHEAD,
	SIDE,
	BACK,
	HIGH,
	LOW,
	DARK,
	RAIN;

	public static final HitCondBonusType[] VALUES = HitCondBonusType.values();
}