package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.model.MultiSellListContainer;
import org.l2j.gameserver.model.base.MultiSellEntry;
import org.l2j.gameserver.model.base.MultiSellIngredient;
import org.l2j.gameserver.templates.item.ItemTemplate;

public class MultiSellListPacket extends L2GameServerPacket
{
	private final int _page;
	private final int _finished;
	private final int _listId;
	private final int _type;
	private final List<MultiSellEntry> _list;

	public MultiSellListPacket(MultiSellListContainer list, int page, int finished)
	{
		_list = list.getEntries();
		_listId = list.getListId();
		_type = list.getType().ordinal(); // other values cause client stuck
		_page = page;
		_finished = finished;
	}

	@Override
	protected final void writeImpl()
	{
		writeByte(0x00);
		writeInt(_listId); // list id
		writeByte(0x00); // UNK
		writeInt(_page); // page
		writeInt(_finished); // finished
		writeInt(Config.MULTISELL_SIZE); // size of pages
		writeInt(_list.size()); //list length
		writeByte(0x00);
		writeByte(_type);  //Type (0x00 - Нормальный, 0xD0 - с шансом)
		writeInt(0x00);

		List<MultiSellIngredient> ingredients;
		for(MultiSellEntry ent : _list)
		{
			ingredients = fixIngredients(ent.getIngredients());

			writeInt(ent.getEntryId());
			writeByte(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
			writeShort(0x00); // unknown
			writeInt(0x00); // инкрустация
			writeInt(0x00); // инкрустация

			writeItemElements();
			int saCount = 0;
			writeByte(0x00);
			for(int i = 0; i < saCount; i++)
				writeInt(0x00);

			writeByte(0);
			for (int i = 0; i < saCount; i++)
				writeInt(0x00);

			writeShort(ent.getProduction().size());
			writeShort(ingredients.size());

			for(MultiSellIngredient prod : ent.getProduction())
			{
				int itemId = prod.getItemId();
				ItemTemplate template = itemId > 0 ? ItemHolder.getInstance().getTemplate(prod.getItemId()) : null;
				writeInt(itemId);
				writeLong(itemId > 0 ? template.getBodyPart() : 0);
				writeShort(itemId > 0 ? template.getType2() : 0);
				writeLong(prod.getItemCount());
				writeShort(prod.getItemEnchant());
				writeInt(prod.getChance());
				writeInt(0x00); // augment id
				writeInt(0x00); // mana
				writeItemElements(prod);
				writeByte(0x00);
				for(int i = 0; i < saCount; i++)
					writeInt(0x00);

				writeByte(0);
				for(int i = 0; i < saCount; i++)
					writeInt(0x00);
			}

			for(MultiSellIngredient i : ingredients)
			{
				int itemId = i.getItemId();
				final ItemTemplate item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
				writeInt(itemId); //ID
				writeShort(itemId > 0 ? item.getType2() : 0xFFFF);
				writeLong(i.getItemCount()); //Count
				writeShort(i.getItemEnchant()); //Enchant Level
				writeInt(0x00); // инкрустация
				writeInt(0x00); // инкрустация
				writeItemElements(i);
				writeByte(0x00);
				for(int s = 0; s < saCount; s++)
					writeInt(0x00);

				writeByte(0x00);
				for(int s = 0; s < saCount; s++)
					writeInt(0x00);
			}
		}
	}

	//FIXME временная затычка, пока NCSoft не починят в клиенте отображение мультиселов где кол-во больше Integer.MAX_VALUE
	private static List<MultiSellIngredient> fixIngredients(List<MultiSellIngredient> ingredients)
	{
		int needFix = 0;
		for(MultiSellIngredient ingredient : ingredients)
		{
			if(ingredient.getItemCount() > Integer.MAX_VALUE)
			{
				needFix++;
			}
		}

		if(needFix == 0)
		{
			return ingredients;
		}

		MultiSellIngredient temp;
		List<MultiSellIngredient> result = new ArrayList<MultiSellIngredient>(ingredients.size() + needFix);
		for(MultiSellIngredient ingredient : ingredients)
		{
			ingredient = ingredient.clone();
			while(ingredient.getItemCount() > Integer.MAX_VALUE)
			{
				temp = ingredient.clone();
				temp.setItemCount(2000000000);
				result.add(temp);
				ingredient.setItemCount(ingredient.getItemCount() - 2000000000);
			}
			if(ingredient.getItemCount() > 0)
			{
				result.add(ingredient);
			}
		}

		return result;
	}
}