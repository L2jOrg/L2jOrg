package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExPut_Shape_Shifting_Extraction_Item_Result extends L2GameServerPacket
{
	public static L2GameServerPacket FAIL = new ExPut_Shape_Shifting_Extraction_Item_Result(0x00);
	public static L2GameServerPacket SUCCESS = new ExPut_Shape_Shifting_Extraction_Item_Result(0x01);

	private final int _result;

	public ExPut_Shape_Shifting_Extraction_Item_Result(int result)
	{
		_result = result;
	}
	
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_result); //Result
	}
}