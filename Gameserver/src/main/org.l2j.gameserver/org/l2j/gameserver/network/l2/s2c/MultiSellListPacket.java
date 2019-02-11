package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.model.MultiSellListContainer;
import org.l2j.gameserver.model.base.MultiSellEntry;
import org.l2j.gameserver.model.base.MultiSellIngredient;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)0x00);
		buffer.putInt(_listId); // list id
		buffer.put((byte)0x00); // UNK
		buffer.putInt(_page); // page
		buffer.putInt(_finished); // finished
		buffer.putInt(Config.MULTISELL_SIZE); // size of pages
		buffer.putInt(_list.size()); //list length
		buffer.put((byte)0x00);
		buffer.put((byte)_type);  //Type (0x00 - Нормальный, 0xD0 - с шансом)
		buffer.putInt(0x00);

		List<MultiSellIngredient> ingredients;
		for(MultiSellEntry ent : _list)
		{
			ingredients = fixIngredients(ent.getIngredients());

			buffer.putInt(ent.getEntryId());
			buffer.put((byte) (!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0)); // stackable?
			buffer.putShort((short) 0x00); // unknown
			buffer.putInt(0x00); // инкрустация
			buffer.putInt(0x00); // инкрустация

			writeItemElements(buffer);
			int saCount = 0;
			buffer.put((byte)0x00);
			for(int i = 0; i < saCount; i++)
				buffer.putInt(0x00);

			buffer.put((byte)0);
			for (int i = 0; i < saCount; i++)
				buffer.putInt(0x00);

			buffer.putShort((short) ent.getProduction().size());
			buffer.putShort((short) ingredients.size());

			for(MultiSellIngredient prod : ent.getProduction())
			{
				int itemId = prod.getItemId();
				ItemTemplate template = itemId > 0 ? ItemHolder.getInstance().getTemplate(prod.getItemId()) : null;
				buffer.putInt(itemId);
				buffer.putLong(itemId > 0 ? template.getBodyPart() : 0);
				buffer.putShort((short) (itemId > 0 ? template.getType2() : 0));
				buffer.putLong(prod.getItemCount());
				buffer.putShort((short) prod.getItemEnchant());
				buffer.putInt(prod.getChance());
				buffer.putInt(0x00); // augment id
				buffer.putInt(0x00); // mana
				writeItemElements(buffer, prod);
				buffer.put((byte)0x00);
				for(int i = 0; i < saCount; i++)
					buffer.putInt(0x00);

				buffer.put((byte)0);
				for(int i = 0; i < saCount; i++)
					buffer.putInt(0x00);
			}

			for(MultiSellIngredient i : ingredients)
			{
				int itemId = i.getItemId();
				final ItemTemplate item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
				buffer.putInt(itemId); //ID
				buffer.putShort((short) (itemId > 0 ? item.getType2() : 0xFFFF));
				buffer.putLong(i.getItemCount()); //Count
				buffer.putShort((short) i.getItemEnchant()); //Enchant Level
				buffer.putInt(0x00); // инкрустация
				buffer.putInt(0x00); // инкрустация
				writeItemElements(buffer, i);
				buffer.put((byte)0x00);
				for(int s = 0; s < saCount; s++)
					buffer.putInt(0x00);

				buffer.put((byte)0x00);
				for(int s = 0; s < saCount; s++)
					buffer.putInt(0x00);
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