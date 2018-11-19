package l2s.gameserver.network.l2.s2c.updatetype;

/**
 * @author Sdw
 */
public enum UserInfoType implements IUpdateTypeComponent
{
	RELATION(0x00, 4),
	BASIC_INFO(0x01, 16),
	BASE_STATS(0x02, 18),
	MAX_HPCPMP(0x03, 14),
	CURRENT_HPMPCP_EXP_SP(0x04, 38),
	ENCHANTLEVEL(0x05, 4),
	APPAREANCE(0x06, 15),
	STATUS(0x07, 6),
	
	STATS(0x08, 56),
	ELEMENTALS(0x09, 14),
	POSITION(0x0A, 18),
	SPEED(0x0B, 18),
	MULTIPLIER(0x0C, 18),
	COL_RADIUS_HEIGHT(0x0D, 18),
	ATK_ELEMENTAL(0x0E, 5),
	CLAN(0x0F, 32),
	
	SOCIAL(0x10, 22),
	VITA_FAME(0x11, 15),
	SLOTS(0x12, 9),
	MOVEMENTS(0x13, 4),
	COLOR(0x14, 10),
	INVENTORY_LIMIT(0x15, 9),
	UNK_3(0x16, 9);
	
	/** Int mask. */
	private final int _mask;
	private final int _blockLength;
	
	private UserInfoType(int mask, int blockLength)
	{
		_mask = mask;
		_blockLength = blockLength;
	}
	
	/**
	 * Gets the int mask.
	 * @return the int mask
	 */
	@Override
	public final int getMask()
	{
		return _mask;
	}
	
	public int getBlockLength()
	{
		return _blockLength;
	}
}