package l2s.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ManufactureItem;
import l2s.gameserver.templates.item.RecipeTemplate;

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
		writeD(sellerId);
		writeD((int) Math.min(adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		writeD(isDwarven ? 0x00 : 0x01);
		writeD(recipes.size());
		int i = 1;
		for(RecipeTemplate recipe : recipes)
		{
			writeD(recipe.getId());
			writeD(i++);
		}
		writeD(createList.size());
		for(ManufactureItem mi : createList)
		{
			writeD(mi.getRecipeId());
			writeD(0x00); //??
			writeQ(mi.getCost());
		}
	}
}