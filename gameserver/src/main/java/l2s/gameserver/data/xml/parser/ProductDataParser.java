package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import l2s.gameserver.templates.item.product.ProductPointsType;
import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;

/**
 * @author Bonux
 **/
public final class ProductDataParser extends AbstractParser<ProductDataHolder>
{
	private static final ProductDataParser _instance = new ProductDataParser();

	private static final int FIRST_MASK = 1 << 0;
	private static final int SECOND_MASK = 1 << 1;
	private static final int THIRD_MASK = 1 << 2;

	public static ProductDataParser getInstance()
	{
		return _instance;
	}

	private ProductDataParser()
	{
		super(ProductDataHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/product_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "product_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		ProductPointsType defaultPointsType = ProductPointsType.POINTS;
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("config".equalsIgnoreCase(element.getName()))
			{
				Config.IM_PAYMENT_ITEM_ID = element.attributeValue("points_item_id") == null ? -1 : Integer.parseInt(element.attributeValue("points_item_id"));
				Config.IM_MAX_ITEMS_IN_RECENT_LIST = element.attributeValue("recent_list_size") == null ? 5 : Integer.parseInt(element.attributeValue("recent_list_size"));
				defaultPointsType = ProductPointsType.valueOf(element.attributeValue("default_points_type"));
			}
			else if("product".equalsIgnoreCase(element.getName()))
			{
				int productId = Integer.parseInt(element.attributeValue("id"));
				int category = Integer.parseInt(element.attributeValue("category"));
				int price = Integer.parseInt(element.attributeValue("price"));
				boolean isEvent = element.attributeValue("is_event") == null ? false : Boolean.parseBoolean(element.attributeValue("is_event"));
				boolean isBest = element.attributeValue("is_best") == null ? false : Boolean.parseBoolean(element.attributeValue("is_best"));
				boolean isNew = element.attributeValue("is_new") == null ? false : Boolean.parseBoolean(element.attributeValue("is_new"));
				boolean isTop = element.attributeValue("is_top") == null ? false : Boolean.parseBoolean(element.attributeValue("is_top"));
				boolean isRecommended = element.attributeValue("is_recommended") == null ? false : Boolean.parseBoolean(element.attributeValue("is_recommended"));
				boolean isPopular = element.attributeValue("is_popular") == null ? false : Boolean.parseBoolean(element.attributeValue("is_popular"));
				boolean onSale = element.attributeValue("on_sale") == null ? false : Boolean.parseBoolean(element.attributeValue("on_sale"));
				long startTimeSale = element.attributeValue("sale_start_date") == null ? 0 : getMillisecondsFromString(element.attributeValue("sale_start_date"));
				long endTimeSale = element.attributeValue("sale_end_date") == null ? 0 : getMillisecondsFromString(element.attributeValue("sale_end_date"));
				int discount = element.attributeValue("discount") == null ? 0 : Integer.parseInt(element.attributeValue("discount"));
				int locationId = element.attributeValue("location_id") == null ? -1 : Integer.parseInt(element.attributeValue("location_id"));
				ProductPointsType pointsType = element.attributeValue("points_type") == null ? defaultPointsType : ProductPointsType.valueOf(element.attributeValue("points_type"));

				int tabId = getTabMask(isEvent, isBest, isNew);
				int mainCategoryId = getTabMask(isTop, isRecommended, isPopular);

				ProductItem product = new ProductItem(productId, category, price, tabId, mainCategoryId, startTimeSale, endTimeSale, onSale, discount, locationId, pointsType);
				for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext(); )
				{
					Element subElement = subIterator.next();
					if("component".equalsIgnoreCase(subElement.getName()))
					{
						int item_id = Integer.parseInt(subElement.attributeValue("item_id"));
						int count = Integer.parseInt(subElement.attributeValue("count"));

						product.addComponent(new ProductItemComponent(item_id, count));
					}
				}

				getHolder().addProduct(product);
			}
		}
	}

	private static int getTabMask(boolean first, boolean second, boolean third)
	{
		int val = 0;

		if(first)
			val |= FIRST_MASK;

		if(second)
			val |= SECOND_MASK;

		if(third)
			val |= THIRD_MASK;

		return val;
	}

	private static long getMillisecondsFromString(String datetime)
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try
		{
			Date time = df.parse(datetime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);

			return calendar.getTimeInMillis();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return 0;
	}
}