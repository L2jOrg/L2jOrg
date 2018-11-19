package l2s.gameserver.templates.fakeplayer.actions;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.utils.Location;

import org.dom4j.Element;

public class TeleportToPointAction extends AbstractAction
{
	private final Location _loc;
	private final int _minRange;
	private final int _maxRange;

	public TeleportToPointAction(Location loc, int minRange, int maxRange, double chance)
	{
		super(chance);
		_loc = loc;
		_minRange = minRange;
		_maxRange = maxRange;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		ai.getActor().teleToLocation(Location.coordsRandomize(_loc, _minRange, _maxRange));
		return true;
	}

	public static TeleportToPointAction parse(Element element)
	{
		Location loc = Location.parse(element);
		int minRange = element.attributeValue("range") != null ? Integer.parseInt(element.attributeValue("range")) : Integer.parseInt(element.attributeValue("min_range"));
		int maxRange = element.attributeValue("max_range") == null ? minRange : Integer.parseInt(element.attributeValue("max_range"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new TeleportToPointAction(loc, minRange, maxRange, chance);
	}
}