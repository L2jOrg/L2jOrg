package l2s.gameserver.templates.item.support.variation;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
 */
public class VariationStone
{
	private final int _id;
	private final TIntObjectMap<VariationInfo> _variations = new TIntObjectHashMap<VariationInfo>();

	public VariationStone(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void addVariation(VariationInfo variation)
	{
		_variations.put(variation.getId(), variation);
	}

	public VariationInfo getVariation(int id)
	{
		return _variations.get(id);
	}

	public VariationInfo[] getVariations()
	{
		return _variations.values(new VariationInfo[_variations.size()]);
	}
}