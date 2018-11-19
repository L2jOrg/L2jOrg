package l2s.gameserver.templates.fakeplayer.actions;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.templates.fakeplayer.FakePlayerActionsHolder;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionById extends AbstractAction
{
	private static final Logger _log = LoggerFactory.getLogger(ActionById.class);

	private final FakePlayerActionsHolder _actionsHolder;
	private final int _actionId;

	public ActionById(FakePlayerActionsHolder actionsHolder, int actionId, double chance)
	{
		super(chance);
		_actionsHolder = actionsHolder;
		_actionId = actionId;
	}

	@Override
	public List<AbstractAction> makeActionsList()
	{
		OrdinaryActions action = _actionsHolder.getAction(_actionId);
		if(action == null)
		{
			_log.warn("Cannot find action by ID[" + _actionId + "]!");
			return Collections.emptyList();
		}
		return action.makeActionsList();
	}

	public static ActionById parse(FakePlayerActionsHolder actionsHolder, Element element)
	{
		int actionId = Integer.parseInt(element.attributeValue("id"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new ActionById(actionsHolder, actionId, chance);
	}
}