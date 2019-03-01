package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.gameserver.data.xml.holder.OptionDataHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.OptionDataTemplate;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author VISTALL
 * @date 20:36/19.05.2011
 */
public final class OptionDataParser extends StatParser<OptionDataHolder>
{
	private static final OptionDataParser _instance = new OptionDataParser();

	public static OptionDataParser getInstance()
	{
		return _instance;
	}

	protected OptionDataParser()
	{
		super(OptionDataHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/option_data");
	}

	@Override
	public String getDTDFileName()
	{
		return "option_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext();)
		{
			Element optionDataElement = itemIterator.next();
			OptionDataTemplate template = new OptionDataTemplate(Integer.parseInt(optionDataElement.attributeValue("id")));
			for(Iterator<Element> subIterator = optionDataElement.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();
				String subName = subElement.getName();
				if(subName.equalsIgnoreCase("for"))
					parseFor(subElement, template);
				else if(subName.equalsIgnoreCase("triggers"))
					parseTriggers(subElement, template);
				else if(subName.equalsIgnoreCase("skills"))
				{
					for(Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
					{
						Element nextElement = nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("id"));
						int level = Integer.parseInt(nextElement.attributeValue("level"));

						SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(id, level);

						if(skillEntry != null)
							template.addSkill(skillEntry);
						else
							logger.warn("Skill not found(" + id + "," + level + ") for option data:" + template.getId() + "; file:" + getCurrentFileName());
					}
				}
			}
			getHolder().addTemplate(template);
		}
	}

	@Override
	protected Object getTableValue(String name, int... arg)
	{
		return null;
	}
}