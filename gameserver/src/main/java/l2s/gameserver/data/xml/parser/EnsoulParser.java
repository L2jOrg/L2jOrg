package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnsoulHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.Ensoul;
import l2s.gameserver.templates.item.support.EnsoulFee;
import l2s.gameserver.templates.item.support.EnsoulFee.EnsoulFeeInfo;
import l2s.gameserver.templates.item.support.EnsoulFee.EnsoulFeeItem;
import org.dom4j.Element;

/**
 * @author Bonux
 **/
public final class EnsoulParser extends AbstractParser<EnsoulHolder>
{
	private static final EnsoulParser _instance = new EnsoulParser();

	public static EnsoulParser getInstance()
	{
		return _instance;
	}

	private EnsoulParser()
	{
		super(EnsoulHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/ensoul_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "ensoul_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("ensoul_fee_data"); iterator.hasNext();)
		{
			Element element = iterator.next();

			for(Iterator<Element> feeIterator = element.elementIterator("ensoul_fee"); feeIterator.hasNext();)
			{
				Element feeElement = feeIterator.next();

				ItemGrade grade = ItemGrade.valueOf(feeElement.attributeValue("grade").toUpperCase());

				EnsoulFee ensoulFee = new EnsoulFee();

				for(Iterator<Element> feeInfoIterator = feeElement.elementIterator("ensoul_fee_info"); feeInfoIterator.hasNext();)
				{
					Element feeInfoElement = feeInfoIterator.next();

					int type = Integer.parseInt(feeInfoElement.attributeValue("type"));

					for(Iterator<Element> feeItemsIterator = feeInfoElement.elementIterator("ensoul_fee_items"); feeItemsIterator.hasNext();)
					{
						Element feeItemsElement = feeItemsIterator.next();

						int id = Integer.parseInt(feeItemsElement.attributeValue("id"));

						ensoulFee.addFeeInfo(type, id, parseFeeInfo(feeItemsElement));
					}
				}
				getHolder().addEnsoulFee(grade, ensoulFee);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("ensoul_data"); iterator.hasNext();)
		{
			Element element = iterator.next();

			for(Iterator<Element> ensoulIterator = element.elementIterator("ensoul"); ensoulIterator.hasNext();)
			{
				Element ensoulElement = ensoulIterator.next();

				int id = Integer.parseInt(ensoulElement.attributeValue("id"));
				int itemId = ensoulElement.attributeValue("item_id") == null ? 0 : Integer.parseInt(ensoulElement.attributeValue("item_id"));
				Ensoul ensoul = new Ensoul(id, itemId);

				for(Iterator<Element> skillIterator = ensoulElement.elementIterator("skill"); skillIterator.hasNext();)
				{
					Element skillElement = skillIterator.next();

					int skillId = Integer.parseInt(skillElement.attributeValue("id"));
					int skillLevel = Integer.parseInt(skillElement.attributeValue("level"));

					ensoul.addSkill(skillId, skillLevel);
				}
				getHolder().addEnsoul(ensoul);
			}
		}
	}

	private static EnsoulFeeInfo parseFeeInfo(Element rootElement)
	{
		EnsoulFeeInfo feeInfo = new EnsoulFeeInfo();
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			if(element.getName().equals("insert"))
				feeInfo.setInsertFee(parseFeeItems(element));
			else if(element.getName().equals("change"))
				feeInfo.setChangeFee(parseFeeItems(element));
			else if(element.getName().equals("remove"))
				feeInfo.setRemoveFee(parseFeeItems(element));
		}
		return feeInfo;
	}

	private static List<EnsoulFeeItem> parseFeeItems(Element rootElement)
	{
		List<EnsoulFeeItem> items = new ArrayList<EnsoulFeeItem>();
		for(Iterator<Element> iterator = rootElement.elementIterator("item"); iterator.hasNext();)
		{
			Element element = iterator.next();

			int itemId = Integer.parseInt(element.attributeValue("id"));
			long itemCount = Long.parseLong(element.attributeValue("count"));

			items.add(new EnsoulFee.EnsoulFeeItem(itemId, itemCount));
		}
		return items;
	}
}