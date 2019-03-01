package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.OptionDataHolder;
import org.l2j.gameserver.data.xml.holder.VariationDataHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.item.WeaponFightType;
import org.l2j.gameserver.templates.item.support.variation.*;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
 */
public final class VariationDataParser extends AbstractParser<VariationDataHolder>
{
	private static VariationDataParser _instance = new VariationDataParser();

	public static VariationDataParser getInstance()
	{
		return _instance;
	}

	private VariationDataParser()
	{
		super(VariationDataHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/variationdata/");
	}

	@Override
	public String getDTDFileName()
	{
		return "variationdata.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		if(!Config.ALLOW_AUGMENTATION)
			return;

		for(Iterator<Element> iterator = rootElement.elementIterator("weapon"); iterator.hasNext();)
		{
			Element element = iterator.next();

			WeaponFightType type = WeaponFightType.valueOf(element.attributeValue("type").toUpperCase());

			for(Iterator<Element> stoneIterator = element.elementIterator("stone"); stoneIterator.hasNext();)
			{
				Element stoneElement = stoneIterator.next();

				int stoneId = Integer.parseInt(stoneElement.attributeValue("id"));
				VariationStone stone = new VariationStone(stoneId);

				for(Iterator<Element> variationIterator = stoneElement.elementIterator("variation"); variationIterator.hasNext();)
				{
					Element variationElement = variationIterator.next();

					int variationId = Integer.parseInt(variationElement.attributeValue("id"));
					VariationInfo variation = new VariationInfo(variationId);

					for(Iterator<Element> categoryIterator = variationElement.elementIterator("category"); categoryIterator.hasNext();)
					{
						Element categoryElement = categoryIterator.next();

						double probability = Double.parseDouble(categoryElement.attributeValue("probability"));
						VariationCategory category = new VariationCategory(probability);

						for(Iterator<Element> optionIterator = categoryElement.elementIterator("option"); optionIterator.hasNext();)
						{
							Element optionElement = optionIterator.next();

							int optionId = Integer.parseInt(optionElement.attributeValue("id"));
							if(OptionDataHolder.getInstance().getTemplate(optionId) == null)
							{
								logger.warn("Cannot find option ID: " + optionId + " for variation ID: " + variationId);
								continue;
							}

							double chance = Double.parseDouble(optionElement.attributeValue("chance"));

							category.addOption(new VariationOption(optionId, chance));
						}

						variation.addCategory(category);
					}

					stone.addVariation(variation);
				}

				getHolder().addStone(type, stone);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("group"); iterator.hasNext();)
		{
			Element element = iterator.next();

			int groupId = Integer.parseInt(element.attributeValue("id"));
			VariationGroup group = new VariationGroup(groupId);

			for(Iterator<Element> feeIterator = element.elementIterator("fee"); feeIterator.hasNext();)
			{
				Element feeElement = feeIterator.next();

				int stoneId = Integer.parseInt(feeElement.attributeValue("stone_id"));
				int feeItemId = Integer.parseInt(feeElement.attributeValue("fee_item_id"));
				long feeItemCount = Long.parseLong(feeElement.attributeValue("fee_item_count"));
				long cancelFee = Long.parseLong(feeElement.attributeValue("cancel_fee"));

				group.addFee(new VariationFee(stoneId, feeItemId, feeItemCount, cancelFee));

			}
			getHolder().addGroup(group);
		}
	}
}