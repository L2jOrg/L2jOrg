package l2s.gameserver.network.l2.s2c;
 
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.ItemFunctions;
 
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
	protected void writeImpl()
	{
		writeQ(_adena);
		writeQ(_coins);
		writeD(_hairStyle);
		writeD(_face);
		writeD(_hairColor);
	}
}