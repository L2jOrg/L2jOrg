package l2s.gameserver.model.base;

/**
 * @author VISTALL
 * @date 13:00/27.04.2011
 */
public enum RestartType
{
	TO_VILLAGE,
	TO_CLANHALL,
	TO_CASTLE,
	TO_FORTRESS,
	TO_FLAG,
	FIXED,
	AGATHION,
	ADVENTURES_SONG;

	public static final RestartType[] VALUES = values();
}