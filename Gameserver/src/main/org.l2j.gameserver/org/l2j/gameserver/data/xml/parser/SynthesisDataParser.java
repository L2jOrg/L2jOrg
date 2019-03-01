package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.data.xml.holder.SynthesisDataHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.item.support.SynthesisData;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public class SynthesisDataParser extends AbstractParser<SynthesisDataHolder>
{
	private static SynthesisDataParser _instance = new SynthesisDataParser();

	public static SynthesisDataParser getInstance()
	{
		return _instance;
	}

	private SynthesisDataParser()
	{
		super(SynthesisDataHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/synthesis_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "synthesis_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("synthesis"); iterator.hasNext();)
		{
			final Element synthesisElement = iterator.next();

			final int item_1_id = Integer.parseInt(synthesisElement.attributeValue("item_1_id"));
			final int item_2_id = Integer.parseInt(synthesisElement.attributeValue("item_2_id"));
			final double chance = Double.parseDouble(synthesisElement.attributeValue("chance"));
			final int synthesized_item_id = Integer.parseInt(synthesisElement.attributeValue("synthesized_item_id"));
			final int fail_item_id = Integer.parseInt(synthesisElement.attributeValue("fail_item_id"));

			getHolder().addData(new SynthesisData(item_1_id, item_2_id, chance, synthesized_item_id, fail_item_id));
		}
	}
}