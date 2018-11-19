package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExShape_Shifting_Result extends L2GameServerPacket
{
	public static L2GameServerPacket FAIL = new ExShape_Shifting_Result(0x00, 0, 0, -1);
	public static int SUCCESS_RESULT = 0x01;;

	private final int _result;
	private final int _targetItemId;
	private final int _extractItemId;
	private final int _period;

	public ExShape_Shifting_Result(int result, int targetItemId, int extractItemId, int period)
	{
		_result = result;
		_targetItemId = targetItemId;
		_extractItemId = extractItemId;
		_period = period;
	}
	
	@Override
	protected void writeImpl()
	{
		writeD(_result); //Result
		writeD(_targetItemId);
		writeD(_extractItemId);
		writeD(_period);
	}
}