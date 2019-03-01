package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.data.xml.holder.ClassDataHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.player.ClassData;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public final class ClassDataParser extends AbstractParser<ClassDataHolder>
{
	private static final ClassDataParser _instance = new ClassDataParser();

	public static ClassDataParser getInstance()
	{
		return _instance;
	}

	private ClassDataParser()
	{
		super(ClassDataHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/class_data/");
	}

	@Override
	public String getDTDFileName()
	{
		return "class_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			int classId = Integer.parseInt(element.attributeValue("class_id"));
			ClassData template = new ClassData(classId);
			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("hp_mp_cp_data".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						int lvl = Integer.parseInt(e.attributeValue("lvl"));
						double hp = Double.parseDouble(e.attributeValue("hp"));
						double mp = Double.parseDouble(e.attributeValue("mp"));
						double cp = Double.parseDouble(e.attributeValue("cp"));

						template.addHpMpCpData(lvl, hp, mp, cp);
					}
				}
			}
			getHolder().addClassData(template);
		}
	}
}