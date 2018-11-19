package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * Format: (ch) dc
 * d: character object id
 * c: 1 if won 0 if failed
 */
public class ExFishingEndPacket extends L2GameServerPacket
{
	public static final int FAIL = 0;
	public static final int WIN = 1;
	public static final int CANCELED = 2;

	private final int _charId;
	private final int _type;

	public ExFishingEndPacket(Player character, int type)
	{
		_charId = character.getObjectId();
		_type = type;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_charId);
		writeC(_type);
	}
}