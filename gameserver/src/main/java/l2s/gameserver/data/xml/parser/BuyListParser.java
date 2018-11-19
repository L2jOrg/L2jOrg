package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BuyListHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.BuyListTemplate;

/**
 * @author Bonux
 */
public final class BuyListParser extends AbstractParser<BuyListHolder>
{
	private static BuyListParser _instance = new BuyListParser();

	public static BuyListParser getInstance()
	{
		return _instance;
	}

	private BuyListParser()
	{
		super(BuyListHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/buylists/");
	}

	@Override
	public String getDTDFileName()
	{
		return "buylist.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("npc"); iterator.hasNext();)
		{
			Element element = iterator.next();

			final int npcId = Integer.parseInt(element.attributeValue("id"));

			for(Iterator<Element> buylistIterator = element.elementIterator("buylist"); buylistIterator.hasNext();)
			{
				Element buylistElement = buylistIterator.next();

				final int buylistId = Integer.parseInt(buylistElement.attributeValue("id"));
				final int baseMarkup = buylistElement.attributeValue("base_markup") == null ? 0 : Integer.parseInt(buylistElement.attributeValue("base_markup"));

				BuyListTemplate buyList = new BuyListTemplate(npcId, buylistId, baseMarkup);
				for(Iterator<Element> itemIterator = buylistElement.elementIterator("item"); itemIterator.hasNext();)
				{
					Element itemElement = itemIterator.next();

					final int itemId = Integer.parseInt(itemElement.attributeValue("id"));
					final ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
					if(template == null)
					{
						_log.warn("Template not found for item ID: " + itemId + " for npc ID: " + npcId + " for buylist ID: " + buylistId);
						continue;
					}

					if(!checkItem(template))
						continue;

					final double itemMarkup = (itemElement.attributeValue("markup") == null ? baseMarkup : Integer.parseInt(itemElement.attributeValue("markup"))) / 100. + 1;
					final long itemPrice = npcId > 0 ? (itemElement.attributeValue("price") == null ? Math.round(template.getReferencePrice() * itemMarkup) : Long.parseLong(itemElement.attributeValue("price"))) : 0L;
					final long itemCount = itemElement.attributeValue("count") == null ? 0L : Long.parseLong(itemElement.attributeValue("count"));
					// Время респауна задается минутах.
					final int itemRechargeTime = itemElement.attributeValue("time") == null ? 0 : Integer.parseInt(itemElement.attributeValue("time"));

					TradeItem item = new TradeItem();
					item.setItemId(itemId);
					item.setOwnersPrice(itemPrice);
					item.setCount(itemCount);
					item.setCurrentValue(itemCount);
					item.setLastRechargeTime((int) (System.currentTimeMillis() / 60000));
					item.setRechargeTime(itemRechargeTime);

					buyList.addItem(item);
				}
				getHolder().addBuyList(buyList);
			}
		}
	}

	private static boolean checkItem(ItemTemplate template)
	{
		if(template.isEquipment() && !template.isForPet() && Config.ALT_SHOP_PRICE_LIMITS.length > 0)
		{
			for(int i = 0; i < Config.ALT_SHOP_PRICE_LIMITS.length; i += 2)
			{
				if(template.getBodyPart() == Config.ALT_SHOP_PRICE_LIMITS[i])
				{
					if(template.getReferencePrice() > Config.ALT_SHOP_PRICE_LIMITS[i + 1])
						return false;
					break;
				}
			}
		}

		if(Config.ALT_SHOP_UNALLOWED_ITEMS.length > 0)
		{
			for(int i : Config.ALT_SHOP_UNALLOWED_ITEMS)
			{
				if(template.getItemId() == i)
					return false;
			}
		}
		return true;
	}
}