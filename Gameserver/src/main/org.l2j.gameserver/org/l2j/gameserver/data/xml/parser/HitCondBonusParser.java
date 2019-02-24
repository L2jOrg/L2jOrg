package org.l2j.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.base.HitCondBonusType;
import org.l2j.gameserver.data.xml.holder.HitCondBonusHolder;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public final class HitCondBonusParser extends AbstractParser<HitCondBonusHolder>
{
	private static final HitCondBonusParser _instance = new HitCondBonusParser();

	public static HitCondBonusParser getInstance()
	{
		return _instance;
	}

	private HitCondBonusParser()
	{
		super(HitCondBonusHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/hit_cond_bonus.xml").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "hit_cond_bonus.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			HitCondBonusType type = HitCondBonusType.valueOf(element.attributeValue("type"));
			double value = Double.parseDouble(element.attributeValue("value"));

			getHolder().addHitCondBonus(type, value);
		}
	}
}