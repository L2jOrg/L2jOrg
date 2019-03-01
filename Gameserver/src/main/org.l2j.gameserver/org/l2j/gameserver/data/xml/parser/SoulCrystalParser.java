package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.data.xml.holder.SoulCrystalHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.SoulCrystal;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author VISTALL
 * @date  10:55/08.12.2010
 */
public final class SoulCrystalParser extends AbstractParser<SoulCrystalHolder>
{
	private static final SoulCrystalParser _instance = new SoulCrystalParser();

	public static SoulCrystalParser getInstance()
	{
		return _instance;
	}

	private SoulCrystalParser()
	{
		super(SoulCrystalHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/soul_crystals.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "soul_crystals.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("crystal"); iterator.hasNext();)
		{
			Element element = iterator.next();
			int itemId = Integer.parseInt(element.attributeValue("item_id"));
			int level = Integer.parseInt(element.attributeValue("level"));
			int nextItemId = Integer.parseInt(element.attributeValue("next_item_id"));
			int cursedNextItemId = element.attributeValue("cursed_next_item_id") == null ? 0 : Integer.parseInt(element.attributeValue("cursed_next_item_id"));

			getHolder().addCrystal(new SoulCrystal(itemId, level, nextItemId, cursedNextItemId));
		}
	}
}
