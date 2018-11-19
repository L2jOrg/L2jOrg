package l2s.gameserver.data.xml.parser;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.DailyMissionsHolder;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;
import l2s.gameserver.templates.dailymissions.DailyRewardTemplate;
import l2s.gameserver.templates.item.data.ItemData;
import org.dom4j.Element;

/**
 * @author Bonux
 **/
public final class DailyMissionsParser extends AbstractParser<DailyMissionsHolder>
{
	private static final DailyMissionsParser _instance = new DailyMissionsParser();

	public static DailyMissionsParser getInstance()
	{
		return _instance;
	}

	private DailyMissionsParser()
	{
		super(DailyMissionsHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/daily_missions.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "daily_missions.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			String handler = element.attributeValue("handler");
			int value = element.attributeValue("value") == null ? 1 : Integer.parseInt(element.attributeValue("value"));
			int minLevel = element.attributeValue("min_level") == null ? 1 : Integer.parseInt(element.attributeValue("min_level"));
			int maxLevel = element.attributeValue("max_level") == null ? Integer.MAX_VALUE : Integer.parseInt(element.attributeValue("max_level"));

			DailyMissionTemplate mission = new DailyMissionTemplate(id, handler, value, minLevel, maxLevel);

			for(Iterator<Element> rewardsIterator = element.elementIterator("rewards"); rewardsIterator.hasNext();)
			{
				Element rewardsElement = rewardsIterator.next();

				int[] classes = rewardsElement.attributeValue("classes") == null ? null : StringArrayUtils.stringToIntArray(rewardsElement.attributeValue("classes"), ",");
				TIntSet classIds = classes == null ? null : new TIntHashSet(classes);

				DailyRewardTemplate reward = new DailyRewardTemplate(classIds);

				for(Iterator<Element> rewardIterator = rewardsElement.elementIterator("reward"); rewardIterator.hasNext();)
				{
					Element rewardElement = rewardIterator.next();

					int rewardId = Integer.parseInt(rewardElement.attributeValue("id"));
					long rewardCount = Long.parseLong(rewardElement.attributeValue("count"));

					reward.addRewardItem(new ItemData(rewardId, rewardCount));
				}
				mission.addReward(reward);
			}
			getHolder().addMission(mission);
		}
	}
}