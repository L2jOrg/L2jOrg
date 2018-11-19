package l2s.gameserver.network.l2.s2c;

/**
 *
 * sample
 * <p>
 * 4b
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p>
 *
 * format
 * cdd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinPartyPacket extends L2GameServerPacket
{
	private String _requestorName;
	private int _itemDistribution;

	/**
	 * @param int objectId of the target
	 * @param int
	 */
	public AskJoinPartyPacket(String requestorName, int itemDistribution)
	{
		_requestorName = requestorName;
		_itemDistribution = itemDistribution;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_requestorName);
		writeD(_itemDistribution);
	}
}