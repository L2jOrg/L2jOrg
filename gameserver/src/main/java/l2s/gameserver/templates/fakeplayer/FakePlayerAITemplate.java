package l2s.gameserver.templates.fakeplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.templates.fakeplayer.actions.OrdinaryActions;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakePlayerAITemplate
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerAITemplate.class);

	private final Race _race;
	private final ClassType _type;
	private final OrdinaryActions _onCreateAction;
	private final List<FarmZoneTemplate> _farmZones = new ArrayList<FarmZoneTemplate>();

	public FakePlayerAITemplate(Race race, ClassType type, OrdinaryActions onCreateAction)
	{
		_race = race;
		_type = type;
		_onCreateAction = onCreateAction;
	}

	public Race getRace()
	{
		return _race;
	}

	public ClassType getType()
	{
		return _type;
	}

	public OrdinaryActions getOnCreateAction()
	{
		return _onCreateAction;
	}

	public void addFarmZone(FarmZoneTemplate farmZone)
	{
		_farmZones.add(farmZone);
	}

	public List<FarmZoneTemplate> getFarmZones()
	{
		return _farmZones;
	}

	public static FakePlayerAITemplate parse(Element element)
	{
		Element charCreateElement = element.element("on_character_create");
		if(charCreateElement == null)
		{
			_log.warn("Cannot find \"on_character_create\" element!");
			return null;
		}

		FakePlayerActionsHolder actionsHolder = new FakePlayerActionsHolder();

		Race race = Race.valueOf(element.attributeValue("race").toUpperCase());
		ClassType type = ClassType.valueOf(element.attributeValue("type").toUpperCase());
		OrdinaryActions onCreateAction = OrdinaryActions.parse(actionsHolder, charCreateElement);

		FakePlayerAITemplate template = new FakePlayerAITemplate(race, type, onCreateAction);
		for(Iterator<Element> iterator = element.elementIterator("farm"); iterator.hasNext();)
			template.addFarmZone(FarmZoneTemplate.parse(actionsHolder, iterator.next()));

		for(Iterator<Element> iterator = element.elementIterator("action"); iterator.hasNext();)
		{
			Element e = iterator.next();
			int actionId = Integer.parseInt(e.attributeValue("id"));
			OrdinaryActions action = OrdinaryActions.parse(actionsHolder, e);
			actionsHolder.addAction(actionId, action);
		}

		return template;
	}
}