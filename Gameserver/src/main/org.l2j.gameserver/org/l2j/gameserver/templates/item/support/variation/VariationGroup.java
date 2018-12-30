package org.l2j.gameserver.templates.item.support.variation;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public class VariationGroup
{
	private final int _id;
	private final IntObjectMap<VariationFee> _fees = new HashIntObjectMap<VariationFee>();

	public VariationGroup(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void addFee(VariationFee fee)
	{
		_fees.put(fee.getStoneId(), fee);
	}

	public VariationFee getFee(int id)
	{
		return _fees.get(id);
	}

	public VariationFee[] getFees()
	{
		return _fees.values().toArray(new VariationFee[_fees.size()]);
	}
}