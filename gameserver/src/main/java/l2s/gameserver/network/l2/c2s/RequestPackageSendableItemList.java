package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.PackageSendableListPacket;

/**
 * @author VISTALL
 * @date 20:35/16.05.2011
 */
public class RequestPackageSendableItemList extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl() throws Exception
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new PackageSendableListPacket(_objectId, player));
	}
}
