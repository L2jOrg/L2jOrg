package l2s.gameserver.templates.fakeplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import l2s.gameserver.data.xml.holder.ZoneHolder;
import l2s.gameserver.data.xml.parser.ZoneParser;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.templates.fakeplayer.actions.OrdinaryActions;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TownZoneTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerAITemplate.class);

	private final List<ZoneTemplate> _zoneTemplates;
	private final OrdinaryActions _actions;

	public TownZoneTemplate(List<ZoneTemplate> zoneTemplates, OrdinaryActions actions)
	{
		_zoneTemplates = zoneTemplates;
		_actions = actions;
	}

	public List<ZoneTemplate> getZoneTemplates()
	{
		return _zoneTemplates;
	}

	public OrdinaryActions getActions()
	{
		return _actions;
	}

	public static TownZoneTemplate parse(Element element)
	{
		Element tempElement = element.element("zones");
		if(tempElement == null)
		{
			_log.warn("Cannot find \"zones\" element!");
			return null;
		}

		String name = element.attributeValue("name");

		List<ZoneTemplate> zoneTemplates = new ArrayList<ZoneTemplate>();
		for(Iterator<Element> i1 = tempElement.elementIterator("zone"); i1.hasNext();)
		{
			Element e1 = i1.next();

			try
			{
				String zoneName = e1.attributeValue("name");
				ZoneTemplate zoneTemplate;
				if(zoneName != null)
					zoneTemplate = ZoneHolder.getInstance().getTemplate(zoneName);
				else
				{
					StatsSet zoneDat = new StatsSet();
					zoneDat.set("name", name);
					zoneDat.set("type", ZoneType.dummy.toString());
					zoneTemplate = ZoneParser.parseZone(e1, zoneDat);
				}

				if(zoneTemplate != null)
					zoneTemplates.add(zoneTemplate);
			}
			catch(Exception e)
			{
				_log.error("Error while parse zone: ", e);
				return null;
			}
		}

		if(zoneTemplates.isEmpty())
		{
			_log.warn("Zones is empty! Please add one or more zones for town zone.");
			return null;
		}

		tempElement = element.element("actions");
		if(tempElement == null)
		{
			_log.warn("Cannot find \"actions\" element!");
			return null;
		}

		FakePlayerActionsHolder actionsHolder = new FakePlayerActionsHolder();

		OrdinaryActions actions = OrdinaryActions.parse(actionsHolder, tempElement);

		TownZoneTemplate template = new TownZoneTemplate(zoneTemplates, actions);

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