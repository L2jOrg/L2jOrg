package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ManufactureItem;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.RecipeTemplate;

public class RecipeShopManageListPacket extends L2GameServerPacket
{
	private List<ManufactureItem> createList;
	private Collection<RecipeTemplate> recipes;
	private int sellerId;
	private long adena;
	private boolean isDwarven;

	public RecipeShopManageListPacket(Player seller, boolean isDwarvenCraft)
	{
		sellerId = seller.getObjectId();
		adena = seller.getAdena();
		isDwarven = isDwarvenCraft;
		if(isDwarven)
			recipes = seller.getDwarvenRecipeBook();
		else
			recipes = seller.getCommonRecipeBook();
		createList = seller.getCreateList();
		for(ManufactureItem mi : createList)
		{
			if(!seller.findRecipe(mi.getRecipeId()))
				createList.remove(mi);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(sellerId);
		buffer.putInt((int) Math.min(adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		buffer.putInt(isDwarven ? 0x00 : 0x01);
		buffer.putInt(recipes.size());
		int i = 1;
		for(RecipeTemplate recipe : recipes)
		{
			buffer.putInt(recipe.getId());
			buffer.putInt(i++);
		}
		buffer.putInt(createList.size());
		for(ManufactureItem mi : createList)
		{
			buffer.putInt(mi.getRecipeId());
			buffer.putInt(0x00); //??
			buffer.putLong(mi.getCost());
		}
	}
}