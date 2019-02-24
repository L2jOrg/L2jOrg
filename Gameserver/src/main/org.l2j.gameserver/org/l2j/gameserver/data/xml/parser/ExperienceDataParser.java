package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.ExperienceDataHolder;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.ExperienceData;

import java.io.File;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

public final class ExperienceDataParser extends AbstractParser<ExperienceDataHolder>
{
	private static final ExperienceDataParser _instance = new ExperienceDataParser();

	public static ExperienceDataParser getInstance()
	{
		return _instance;
	}

	private ExperienceDataParser()
	{
		super(ExperienceDataHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/experience.xml").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "experience.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("experience"); iterator.hasNext();)
		{
			Element element = iterator.next();

			int level = Integer.parseInt(element.attributeValue("level")) + 1;
			long exp = Long.parseLong(element.attributeValue("exp"));
			double training_rate = Double.parseDouble(element.attributeValue("training_rate"));

			getHolder().addData(new ExperienceData(level, exp, training_rate));
		}
	}

	@Override
	protected void afterParseActions()
	{
		for(int level = 1; level < Math.max(getHolder().getMaxLevel(), Math.max(Experience.getMaxLevel(), Experience.getMaxSubLevel())); level++)
		{
			if(!getHolder().containsData(level))
			{
				logger.error("Not found experience data for " + level + " level!");
				Runtime.getRuntime().exit(0);
			}
		}
	}
}