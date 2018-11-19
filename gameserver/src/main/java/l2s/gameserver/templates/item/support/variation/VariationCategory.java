package l2s.gameserver.templates.item.support.variation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 */
public class VariationCategory
{
	private final double _probability;
	private final List<VariationOption> _options = new ArrayList<VariationOption>();

	public VariationCategory(double probability)
	{
		_probability = probability;
	}

	public double getProbability()
	{
		return _probability;
	}

	public void addOption(VariationOption option)
	{
		_options.add(option);
	}

	public VariationOption[] getOptions()
	{
		return _options.toArray(new VariationOption[_options.size()]);
	}
}