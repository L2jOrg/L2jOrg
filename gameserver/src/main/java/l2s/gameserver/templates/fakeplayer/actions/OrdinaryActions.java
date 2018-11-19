package l2s.gameserver.templates.fakeplayer.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import l2s.gameserver.templates.fakeplayer.FakePlayerActionsHolder;

import org.dom4j.Element;

public class OrdinaryActions extends AbstractAction
{
	private final List<AbstractAction> _actions;

	public OrdinaryActions(List<AbstractAction> actions, double chance)
	{
		super(chance);
		_actions = actions;
	}

	@Override
	public List<AbstractAction> makeActionsList()
	{
		return _actions;
	}

	public static List<AbstractAction> parseActions(FakePlayerActionsHolder actionsHolder, Element element)
	{
		List<AbstractAction> actions = new ArrayList<AbstractAction>();
		for(Iterator<Element> iterator = element.elementIterator(); iterator.hasNext();)
		{
			Element actionElement = iterator.next();
			String actionElementName = actionElement.getName();
			if(actionElementName.equals("action"))
				actions.add(ActionById.parse(actionsHolder, actionElement));
			else if(actionElementName.equals("add_item"))
				actions.add(AddItemAction.parse(actionElement));
			else if(actionElementName.equals("farm"))
				actions.add(FarmAction.parse(actionElement));
			else if(actionElementName.equals("move_to_npc"))
				actions.add(MoveToNpcAction.parse(actionElement));
			else if(actionElementName.equals("move_to_point"))
				actions.add(MoveToPointAction.parse(actionElement));
			else if(actionElementName.equals("ordinary_actions"))
				actions.add(parse(actionsHolder, actionElement));
			else if(actionElementName.equals("random_action"))
				actions.add(RandomAction.parse(actionsHolder, actionElement));
			else if(actionElementName.equals("repeat_actions"))
				actions.add(RepeatActions.parse(actionsHolder, actionElement));
			else if(actionElementName.equals("speak_with_npc"))
				actions.add(SpeakWithNpcAction.parse(actionElement));
			else if(actionElementName.equals("teleport_to_closest_town"))
				actions.add(TeleportToClosestTownAction.parse(actionElement));
			else if(actionElementName.equals("teleport_to_point"))
				actions.add(TeleportToPointAction.parse(actionElement));
			else if(actionElementName.equals("use_community"))
				actions.add(UseCommunityAction.parse(actionElement));
			else if(actionElementName.equals("use_item"))
				actions.add(UseItemAction.parse(actionElement));
			else if(actionElementName.equals("wait"))
				actions.add(WaitAction.parse(actionElement));
		}
		return actions;
	}

	public static OrdinaryActions parse(FakePlayerActionsHolder actionsHolder, Element element)
	{
		List<AbstractAction> actions = parseActions(actionsHolder, element);
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new OrdinaryActions(actions, chance);
	}
}