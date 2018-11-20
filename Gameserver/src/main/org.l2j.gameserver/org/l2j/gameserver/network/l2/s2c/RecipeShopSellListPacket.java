package org.l2j.gameserver.network.l2.s2c;

import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ManufactureItem;

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
		writeInt(objId);
		writeInt(curMp);//Creator's MP
		writeInt(maxMp);//Creator's MP
		writeLong(adena);
		writeInt(createList.size());
		for(ManufactureItem mi : createList)
		{
			writeInt(mi.getRecipeId());
			writeInt(0x00); //unknown
			writeLong(mi.getCost());
		}
	}
}