package org.l2j.gameserver.network.l2.s2c;
 
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.ItemFunctions;

import java.nio.ByteBuffer;

public class ExResponseResetListPacket extends L2GameServerPacket
{
	private int _hairStyle;
	private int _hairColor;
	private int _face;
	private long _adena; 
	private long _coins;
 
	public ExResponseResetListPacket(Player player)
	{
		_hairStyle = player.getHairStyle();
		_hairColor = player.getHairColor();
		_face = player.getFace();
		_adena = player.getAdena();
		_coins = ItemFunctions.getItemCount(player, Config.BEAUTY_SHOP_COIN_ITEM_ID);
 
	}
 
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putLong(_adena);
		buffer.putLong(_coins);
		buffer.putInt(_hairStyle);
		buffer.putInt(_face);
		buffer.putInt(_hairColor);
	}
}