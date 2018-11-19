package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.TradeItem;

public class PrivateStoreList extends L2GameServerPacket
{
	private int _sellerId;
	private long _adena;
	private final boolean _package;
	private List<TradeItem> _sellList;

	/**
	 * Список вещей в личном магазине продажи, показываемый покупателю
	 * @param buyer
	 * @param seller
	 */
	public PrivateStoreList(Player buyer, Player seller)
	{
		_sellerId = seller.getObjectId();
		_adena = buyer.getAdena();
		_package = seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
		_sellList = seller.getSellList();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_sellerId);
		writeD(_package ? 1 : 0);
		writeQ(_adena);
		writeD(0x00); //TODO: [Bonux] Количество свободных ячеек в инвентаре.
		writeD(_sellList.size());
		for(TradeItem si : _sellList)
		{
			writeItemInfo(si);
			writeQ(si.getOwnersPrice());
			writeQ(si.getStorePrice());
		}
	}
}