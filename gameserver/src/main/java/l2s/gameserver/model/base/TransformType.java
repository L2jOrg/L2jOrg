package l2s.gameserver.model.base;

/**
 * @author Bonux
 */
public enum TransformType
{
	COMBAT(true),
	NON_COMBAT(false),
	MODE_CHANGE(true),
	RIDING_MODE(false),
	FLYING(true),
	PURE_STAT(true),
	CURSED(true);

	public static final TransformType[] VALUES = values();

	private final boolean _canAttack;

	private TransformType(boolean canAttack)
	{
		_canAttack = canAttack;
	}

	public boolean isCanAttack()
	{
		return _canAttack;
	}
}