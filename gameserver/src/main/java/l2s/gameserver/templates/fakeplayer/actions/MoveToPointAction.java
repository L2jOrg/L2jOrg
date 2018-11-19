package l2s.gameserver.templates.fakeplayer.actions;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

import org.dom4j.Element;

public class MoveToPointAction extends MoveAction
{
	private final Location _loc;
	private final int _minRange;
	private final int _maxRange;

	public MoveToPointAction(Location loc, int minRange, int maxRange, double chance)
	{
		super(chance);
		_loc = loc;
		_minRange = minRange;
		_maxRange = maxRange;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		Player player = ai.getActor();
		Location loc = Location.coordsRandomize(_loc, _minRange, _maxRange);
		if(player.getDistance(loc) > 2000 || !player.moveToLocation(loc, 0, true))
			player.teleToLocation(loc);
		return true;
	}

	public static MoveToPointAction parse(Element element)
	{
		Location loc = Location.parse(element);
		int minRange = element.attributeValue("range") != null ? Integer.parseInt(element.attributeValue("range")) : (element.attributeValue("min_range") != null ? Integer.parseInt(element.attributeValue("min_range")) : 0);
		int maxRange = element.attributeValue("max_range") == null ? minRange : Integer.parseInt(element.attributeValue("max_range"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new MoveToPointAction(loc, minRange, maxRange, chance);
	}
}