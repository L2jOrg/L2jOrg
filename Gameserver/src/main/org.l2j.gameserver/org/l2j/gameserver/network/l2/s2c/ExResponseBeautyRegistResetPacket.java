package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.ItemFunctions;
 
public class ExResponseBeautyRegistResetPacket extends L2GameServerPacket
{
	public static final int FAILURE = 0;
	public static final int SUCCESS = 1;

	public static final int CHANGE = 0;
	public static final int RESTORE = 1;

	private final int _type;
	private final int _result;
	private final int _hairStyle;
	private final int _hairColor;
	private final int _face;
	private final long _adena;
	private final long _coins;
 
	public ExResponseBeautyRegistResetPacket(Player player, int type, int result)
	{
		_type = type;
		_result = result;
		_hairStyle = player.getBeautyHairStyle() > 0 ? player.getBeautyHairStyle() : player.getHairStyle();
		_hairColor = player.getBeautyHairColor() > 0 ? player.getBeautyHairColor() : player.getHairColor();
		_face = player.getBeautyFace() > 0 ? player.getBeautyFace() : player.getFace();
		_adena = player.getAdena();
		_coins = ItemFunctions.getItemCount(player, Config.BEAUTY_SHOP_COIN_ITEM_ID);
	}
 
	@Override
	protected void writeImpl()
	{
		writeLong(_adena);
		writeLong(_coins);
		writeInt(_type);
		writeInt(_result);
		writeInt(_hairStyle);
		writeInt(_face);
		writeInt(_hairColor);
	}
}