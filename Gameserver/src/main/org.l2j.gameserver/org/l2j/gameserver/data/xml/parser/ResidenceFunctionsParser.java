package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.data.xml.holder.ResidenceFunctionsHolder;
import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.residence.ResidenceFunctionTemplate;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

public final class ResidenceFunctionsParser extends AbstractParser<ResidenceFunctionsHolder>
{
	private static final ResidenceFunctionsParser _instance = new ResidenceFunctionsParser();

	public static ResidenceFunctionsParser getInstance()
	{
		return _instance;
	}

	private ResidenceFunctionsParser()
	{
		super(ResidenceFunctionsHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve( "data/residence_functions.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "residence_functions.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); )
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			ResidenceFunctionType type = ResidenceFunctionType.valueOf(element.attributeValue("type").toUpperCase());
			int level = Integer.parseInt(element.attributeValue("level"));
			int depth = Integer.parseInt(element.attributeValue("depth"));
			int period = Integer.parseInt(element.attributeValue("period"));
			long cost = Integer.parseInt(element.attributeValue("cost"));

			ResidenceFunctionTemplate template = new ResidenceFunctionTemplate(id, type, level, depth, period, cost);

			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("funcs".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("hp_regen".equalsIgnoreCase(e.getName()))
							template.setHpRegen(Double.parseDouble(e.attributeValue("value")));
						else if("mp_regen".equalsIgnoreCase(e.getName()))
							template.setMpRegen(Double.parseDouble(e.attributeValue("value")));
						else if("exp_restore".equalsIgnoreCase(e.getName()))
							template.setExpRestore(Double.parseDouble(e.attributeValue("value")));
					}
				}
			}
			getHolder().addTemplate(template);
		}
	}
}