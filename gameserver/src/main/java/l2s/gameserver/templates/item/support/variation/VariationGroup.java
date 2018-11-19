package l2s.gameserver.templates.item.support.variation;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
 */
public class VariationGroup
{
	private final int _id;
	private final TIntObjectMap<VariationFee> _fees = new TIntObjectHashMap<VariationFee>();

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
		return _fees.values(new VariationFee[_fees.size()]);
	}
}