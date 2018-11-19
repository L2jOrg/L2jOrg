package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ExperienceDataHolder;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.templates.ExperienceData;
import org.dom4j.Element;

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
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/pc_parameters/experience.xml");
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
				error("Not found experience data for " + level + " level!");
				Runtime.getRuntime().exit(0);
			}
		}
	}
}