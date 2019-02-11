package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ManufactureItem;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(objId);
		buffer.putInt(curMp);//Creator's MP
		buffer.putInt(maxMp);//Creator's MP
		buffer.putLong(adena);
		buffer.putInt(createList.size());
		for(ManufactureItem mi : createList)
		{
			buffer.putInt(mi.getRecipeId());
			buffer.putInt(0x00); //unknown
			buffer.putLong(mi.getCost());
		}
	}
}