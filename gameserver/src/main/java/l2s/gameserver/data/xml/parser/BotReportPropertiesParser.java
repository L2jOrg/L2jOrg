package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BotReportPropertiesHolder;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.BotPunishment;
import org.dom4j.Element;

public class BotReportPropertiesParser extends AbstractParser<BotReportPropertiesHolder>
{
	private static BotReportPropertiesParser _instance = new BotReportPropertiesParser();

	public static BotReportPropertiesParser getInstance()
	{
		return _instance;
	}

	private BotReportPropertiesParser()
	{
		super(BotReportPropertiesHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/bot_report_properties.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "bot_report_properties.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		Config.BOTREPORT_ENABLED = false;
		Config.BOTREPORT_REPORT_DELAY = 1800000;
		Config.BOTREPORT_REPORTS_RESET_TIME = "00 00 * * *";
		Config.BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = false;

		for(Iterator<Element> iterator = rootElement.elementIterator("config"); iterator.hasNext();)
		{
			Element configElement = iterator.next();

			String attributeValue = configElement.attributeValue("enabled");
			if(attributeValue != null)
				Config.BOTREPORT_ENABLED = Boolean.parseBoolean(attributeValue);

			attributeValue = configElement.attributeValue("report_delay");
			if(attributeValue != null)
				Config.BOTREPORT_REPORT_DELAY = Integer.parseInt(attributeValue);

			attributeValue = configElement.attributeValue("reports_reset_time");
			if(attributeValue != null)
				Config.BOTREPORT_REPORTS_RESET_TIME = attributeValue;

			attributeValue = configElement.attributeValue("allow_reports_from_same_clan");
			if(attributeValue != null)
				Config.BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = Boolean.parseBoolean(attributeValue);
		}
		if(!Config.BOTREPORT_ENABLED)
			return;

		for(Iterator<Element> iterator = rootElement.elementIterator("punishments"); iterator.hasNext();)
		{
			Element punishmentsElement = iterator.next();

			for(Iterator<Element> punishmentsIterator = punishmentsElement.elementIterator("punishment"); punishmentsIterator.hasNext();)
			{
				Element punishmentElement = punishmentsIterator.next();

				int need_report_count = Integer.parseInt(punishmentElement.attributeValue("need_report_count"));
				int skill_id = Integer.parseInt(punishmentElement.attributeValue("skill_id"));
				int skill_level = Integer.parseInt(punishmentElement.attributeValue("skill_level"));
				SystemMsg message = punishmentElement.attributeValue("message_id") == null ? null : SystemMsg.valueOf(Integer.parseInt(punishmentElement.attributeValue("message_id")));

				getHolder().addBotPunishment(new BotPunishment(need_report_count, skill_id, skill_level, message));
			}
		}
	}
}