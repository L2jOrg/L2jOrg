package org.l2j.gameserver.templates.item.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import org.l2j.commons.util.Rnd;

/**
 * айди категорий 1 - enchant 2 - supplies  3 - decoration 4 - package 5 - other
 */
public class ProductItem implements Comparable<ProductItem>
{
	//private static final int EVENT_MASK = 1 << 0;
	//private static final int BEST_MASK = 1 << 1;
	//private static final int NEW_MASK = 1 << 2;

	// Базовые параметры, если продукт не имеет лимита времени продаж
	public static final long NOT_LIMITED_START_TIME = 315547200000L;
	public static final long NOT_LIMITED_END_TIME = 2127445200000L;
	public static final int NOT_LIMITED_START_HOUR = 0;
	public static final int NOT_LIMITED_END_HOUR = 23;
	public static final int NOT_LIMITED_START_MIN = 0;
	public static final int NOT_LIMITED_END_MIN = 59;

	private final int _id;
	private final int _category;
	private final int _points;
	private final int _tabId;
	private final int _locationId;
	private final ProductPointsType _pointsType;

	private final long _startTimeSale;
	private final long _endTimeSale;
	private final int _startHour;
	private final int _endHour;
	private final int _startMin;
	private final int _endMin;
	private final int _discount;
	private final int _mainCategory;

	private int _boughtCount = 0;

	private final boolean _onSale;

	private final List<ProductItemComponent> _components = new ArrayList<ProductItemComponent>();

	public ProductItem(int id, int category, int points, int tabId, int mainCategory, long startTimeSale, long endTimeSale, boolean onSale, int discount, int locationId, ProductPointsType pointsType)
	{
		_id = id;
		_category = category;
		_points = points;
		_tabId = tabId;
		_mainCategory = mainCategory;
		_onSale = onSale;
		_discount = discount;
		_locationId = locationId;
		_pointsType = pointsType;

		Calendar calendar;
		if(startTimeSale > 0)
		{
			calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startTimeSale);

			_startTimeSale = startTimeSale;
			_startHour = calendar.get(Calendar.HOUR_OF_DAY);
			_startMin = calendar.get(Calendar.MINUTE);
		}
		else
		{
			_startTimeSale = NOT_LIMITED_START_TIME;
			_startHour = NOT_LIMITED_START_HOUR;
			_startMin = NOT_LIMITED_START_MIN;
		}

		if(endTimeSale > 0)
		{
			calendar = Calendar.getInstance();
			calendar.setTimeInMillis(endTimeSale);

			_endTimeSale = endTimeSale;
			_endHour = calendar.get(Calendar.HOUR_OF_DAY);
			_endMin = calendar.get(Calendar.MINUTE);
		}
		else
		{
			_endTimeSale = NOT_LIMITED_END_TIME;
			_endHour = NOT_LIMITED_END_HOUR;
			_endMin = NOT_LIMITED_END_MIN;
		}
	}

	public void addComponent(ProductItemComponent component)
	{
		_components.add(component);
	}

	public List<ProductItemComponent> getComponents()
	{
		return _components;
	}

	public int getId()
	{
		return _id;
	}

	public int getCategory()
	{
		return _category;
	}

	public int getPoints(boolean withDiscount)
	{
		if(withDiscount)
		{
			return (int) (_points * ((100 - _discount) * 0.01));
		}
		return _points;
	}

	public int getTabId()
	{
		return _tabId;
	}

	public int getMainCategory()
	{
		return _mainCategory;
	}

	public int getLocationId()
	{
		return _locationId;
	}

	public ProductPointsType getPointsType()
	{
		return _pointsType;
	}

	public long getStartTimeSale()
	{
		return _startTimeSale;
	}

	public int getStartHour()
	{
		return _startHour;
	}

	public int getStartMin()
	{
		return _startMin;
	}

	public long getEndTimeSale()
	{
		return _endTimeSale;
	}

	public int getEndHour()
	{
		return _endHour;
	}

	public int getEndMin()
	{
		return _endMin;
	}

	public boolean isOnSale()
	{
		return _onSale;
	}

	public int getDiscount()
	{
		return _discount;
	}

	public void setBoughtCount(int val)
	{
		_boughtCount = val;
	}

	public int getBoughtCount()
	{
		return _boughtCount;
	}

	@Override
	public int compareTo(ProductItem o)
	{
		return o.getId() - getId();
	}
}