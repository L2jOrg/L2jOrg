package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.Config;
import l2s.gameserver.model.MultiSellListContainer;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.MultiSellEntry;
import l2s.gameserver.model.base.MultiSellIngredient;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.MultiSellListPacket;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * Multisell list manager
 */
public class MultiSellHolder extends AbstractHolder
{
	private static MultiSellHolder _instance = new MultiSellHolder();

	public static MultiSellHolder getInstance()
	{
		return _instance;
	}

	private TIntObjectHashMap<MultiSellListContainer> _entries = new TIntObjectHashMap<MultiSellListContainer>();

	public MultiSellListContainer getList(int id)
	{
		return _entries.get(id);
	}

	public MultiSellHolder()
	{
		//
	}

	public void addMultiSellListContainer(int id, MultiSellListContainer list)
	{
		if(_entries.containsKey(id))
			_log.warn("MultiSell redefined: " + id);

		list.setListId(id);
		_entries.put(id, list);
	}

	public MultiSellListContainer remove(String s)
	{
		return remove(new File(s));
	}

	public MultiSellListContainer remove(File f)
	{
		return remove(Integer.parseInt(f.getName().replaceAll(".xml", "")));
	}

	public MultiSellListContainer remove(int id)
	{
		return _entries.remove(id);
	}

	private long[] parseItemIdAndCount(String s)
	{
		if(s == null || s.isEmpty())
			return null;
		String[] a = s.split(":");
		try
		{
			long id = Integer.parseInt(a[0]);
			long count = a.length > 1 ? Long.parseLong(a[1]) : 1;
			return new long[] { id, count };
		}
		catch(Exception e)
		{
			error("", e);
			return null;
		}
	}

	public MultiSellEntry parseEntryFromStr(String s)
	{
		if(s == null || s.isEmpty())
			return null;

		String[] a = s.split("->");
		if(a.length != 2)
			return null;

		long[] ingredient, production;
		if((ingredient = parseItemIdAndCount(a[0])) == null || (production = parseItemIdAndCount(a[1])) == null)
			return null;

		MultiSellEntry entry = new MultiSellEntry();
		entry.addIngredient(new MultiSellIngredient((int) ingredient[0], ingredient[1]));
		entry.addProduct(new MultiSellIngredient((int) production[0], production[1]));
		return entry;
	}

	public void SeparateAndSend(int listId, Player player, double taxRate)
	{
		for(int i : Config.ALT_DISABLED_MULTISELL)
			if(i == listId)
			{
				player.sendMessage(new CustomMessage("common.Disabled"));
				return;
			}

		MultiSellListContainer list = getList(listId);
		if(list == null)
		{
			player.sendMessage(new CustomMessage("common.Disabled"));
			return;
		}

		SeparateAndSend(list, player, taxRate);
	}

	public void SeparateAndSend(MultiSellListContainer list, Player player, double taxRate)
	{
		list = generateMultiSell(list, player, taxRate);

		MultiSellListContainer temp = new MultiSellListContainer();
		int page = 1;

		temp.setListId(list.getListId());
		temp.setType(list.getType());

		// Запоминаем отсылаемый лист, чтобы не подменили
		player.setMultisell(list);

		for(MultiSellEntry e : list.getEntries())
		{
			if(temp.getEntries().size() == Config.MULTISELL_SIZE)
			{
				player.sendPacket(new MultiSellListPacket(temp, page, 0));
				page++;
				temp = new MultiSellListContainer();
				temp.setListId(list.getListId());
			}
			temp.addEntry(e);
		}

		player.sendPacket(new MultiSellListPacket(temp, page, 1));
	}

	private MultiSellListContainer generateMultiSell(MultiSellListContainer container, Player player, double taxRate)
	{
		MultiSellListContainer list = new MultiSellListContainer();
		list.setListId(container.getListId());
		list.setType(container.getType());

		// Все мультиселлы из датапака
		boolean enchant = container.isKeepEnchant();
		boolean notax = container.isNoTax();
		boolean showall = container.isShowAll();
		boolean nokey = container.isNoKey();

		list.setShowAll(showall);
		list.setKeepEnchant(enchant);
		list.setNoTax(notax);
		list.setNoKey(nokey);

		ItemInstance[] items = player.getInventory().getItems();
		for(MultiSellEntry origEntry : container.getEntries())
		{
			MultiSellEntry ent = origEntry.clone();

			// Обработка налога, если лист не безналоговый
			// Адены добавляются в лист если отсутствуют или прибавляются к существующим
			List<MultiSellIngredient> ingridients;
			if(!notax && taxRate > 0.)
			{
				double tax = 0;
				long adena = 0;
				ingridients = new ArrayList<MultiSellIngredient>(ent.getIngredients().size() + 1);
				for(MultiSellIngredient i : ent.getIngredients())
				{
					if(i.getItemId() == 57)
					{
						adena += i.getItemCount();
						tax += i.getItemCount() * taxRate;
						continue;
					}
					ingridients.add(i);
					if(i.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
						//FIXME hardcoded. Налог на клановую репутацию. Формула проверена на с6 и соответсвует на 100%.
						//TODO Проверить на корейском(?) оффе налог на банг поинты и fame
						tax += i.getItemCount() / 120 * 1000 * taxRate * 100;
					if(i.getItemId() < 1)
						continue;

					ItemTemplate item = ItemHolder.getInstance().getTemplate(i.getItemId());
					if(item.isStackable())
						tax += item.getReferencePrice() * i.getItemCount() * taxRate;
				}

				adena = Math.round(adena + tax);
				if(adena > 0)
					ingridients.add(new MultiSellIngredient(57, adena));

				ent.setTax(Math.round(tax));

				ent.getIngredients().clear();
				ent.getIngredients().addAll(ingridients);
			}
			else
				ingridients = ent.getIngredients();

			// Если стоит флаг "показывать все" не проверять наличие ингридиентов
			if(showall)
				list.addEntry(ent);
			else
			{
				List<Integer> itms = new ArrayList<Integer>();
				// Проверка наличия у игрока ингридиентов
				for(MultiSellIngredient ingredient : ingridients)
				{
					ItemTemplate template = ingredient.getItemId() <= 0 ? null : ItemHolder.getInstance().getTemplate(ingredient.getItemId());
					if(ingredient.getItemId() <= 0 || nokey || template.isEquipment())
					{
						if(ingredient.getItemId() == 12374) // Mammon's Varnish Enhancer
							continue;

						//TODO: а мы должны тут сверять count?
						if(ingredient.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
						{
							if(!itms.contains(ingredient.getItemId()) && player.getClan() != null && player.getClan().getReputationScore() >= ingredient.getItemCount())
								itms.add(ingredient.getItemId());
							continue;
						}
						else if(ingredient.getItemId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
						{
							if(!itms.contains(ingredient.getItemId()) && player.getPcBangPoints() >= ingredient.getItemCount())
								itms.add(ingredient.getItemId());
							continue;
						}
						else if(ingredient.getItemId() == ItemTemplate.ITEM_ID_FAME)
						{
							if(!itms.contains(ingredient.getItemId()) && player.getFame() >= ingredient.getItemCount())
								itms.add(ingredient.getItemId());
							continue;
						}

						for(final ItemInstance item : items)
						{
							if(item.getItemId() == ingredient.getItemId())
							{
								//FIX ME если перевалит за long - косяк(VISTALL)
								if(itms.contains(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000L : ingredient.getItemId())) // Не проверять одинаковые вещи
									continue;

								if(item.getEnchantLevel() < ingredient.getItemEnchant()) // Некоторые мультиселлы требуют заточки
									continue;

								if(item.isStackable() && item.getCount() < ingredient.getItemCount())
									break;

								itms.add(enchant ? ingredient.getItemId() + ingredient.getItemEnchant() * 100000 : ingredient.getItemId());
								MultiSellEntry possibleEntry = new MultiSellEntry(enchant ? ent.getEntryId() + item.getEnchantLevel() * 100000 : ent.getEntryId());

								for(MultiSellIngredient p : ent.getProduction())
								{
									if(enchant && template.canBeEnchanted())
									{
										p.setItemEnchant(item.getEnchantLevel());
										p.setItemAttributes(item.getAttributes().clone());
									}
									possibleEntry.addProduct(p);
								}

								for(MultiSellIngredient ig : ingridients)
								{
									if(enchant && ig.getItemId() > 0 && ItemHolder.getInstance().getTemplate(ig.getItemId()).canBeEnchanted())
									{
										ig.setItemEnchant(item.getEnchantLevel());
										ig.setItemAttributes(item.getAttributes().clone());
									}
									possibleEntry.addIngredient(ig);
								}

								list.addEntry(possibleEntry);
								break;
							}
						}
					}
				}
			}
		}

		return list;
	}

	@Override
	public int size()
	{
		return _entries.size();
	}

	@Override
	public void clear()
	{
		_entries.clear();
	}
}