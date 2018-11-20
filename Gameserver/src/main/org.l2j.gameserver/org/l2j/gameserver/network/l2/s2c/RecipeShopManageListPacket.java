package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ManufactureItem;
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
	protected final void writeImpl()
	{
		writeInt(sellerId);
		writeInt((int) Math.min(adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		writeInt(isDwarven ? 0x00 : 0x01);
		writeInt(recipes.size());
		int i = 1;
		for(RecipeTemplate recipe : recipes)
		{
			writeInt(recipe.getId());
			writeInt(i++);
		}
		writeInt(createList.size());
		for(ManufactureItem mi : createList)
		{
			writeInt(mi.getRecipeId());
			writeInt(0x00); //??
			writeLong(mi.getCost());
		}
	}
}