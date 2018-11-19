package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ManufactureItem;

public class RecipeShopSellListPacket extends L2GameServerPacket
{
	private int objId, curMp, maxMp;
	private long adena;
	private List<ManufactureItem> createList;

	public RecipeShopSellListPacket(Player buyer, Player manufacturer)
	{
		objId = manufacturer.getObjectId();
		curMp = (int) manufacturer.getCurrentMp();
		maxMp = manufacturer.getMaxMp();
		adena = buyer.getAdena();
		createList = manufacturer.getCreateList();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(objId);
		writeD(curMp);//Creator's MP
		writeD(maxMp);//Creator's MP
		writeQ(adena);
		writeD(createList.size());
		for(ManufactureItem mi : createList)
		{
			writeD(mi.getRecipeId());
			writeD(0x00); //unknown
			writeQ(mi.getCost());
		}
	}
}