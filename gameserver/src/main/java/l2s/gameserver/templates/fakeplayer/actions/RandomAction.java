package l2s.gameserver.templates.fakeplayer.actions;

import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.templates.fakeplayer.FakePlayerActionsHolder;

import org.dom4j.Element;

public class RandomAction extends OrdinaryActions
{
	public RandomAction(List<AbstractAction> actions, double chance)
	{
		super(actions, chance);
	}

	@Override
	public List<AbstractAction> makeActionsList()
	{
		AbstractAction action = Rnd.get(super.makeActionsList());
		if(action != null)
			return Collections.singletonList(action);
		return Collections.emptyList();
	}

	public static RandomAction parse(FakePlayerActionsHolder actionsHolder, Element element)
	{
		List<AbstractAction> actions = parseActions(actionsHolder, element);
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new RandomAction(actions, chance);
	}
}