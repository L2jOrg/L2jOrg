package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnchantItemHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.EnchantScroll;
import l2s.gameserver.templates.item.support.EnchantType;
import l2s.gameserver.templates.item.support.EnchantVariation;
import l2s.gameserver.templates.item.support.EnchantVariation.EnchantLevel;
import l2s.gameserver.templates.item.support.FailResultType;

/**
 * @author VISTALL
 * @date 3:10/18.06.2011
 */
public class EnchantItemParser extends AbstractParser<EnchantItemHolder>
{
	private static EnchantItemParser _instance = new EnchantItemParser();

	public static EnchantItemParser getInstance()
	{
		return _instance;
	}

	private EnchantItemParser()
	{
		super(EnchantItemHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/enchant_items.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "enchant_items.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		int defaultMaxEnchant = 0;
		boolean defaultFailEffect = false;

		Element defaultElement = rootElement.element("default");
		if(defaultElement != null)
		{
			defaultMaxEnchant = Integer.parseInt(defaultElement.attributeValue("max_enchant"));
			defaultFailEffect = Boolean.parseBoolean(defaultElement.attributeValue("show_fail_effect"));
		}

		for(Iterator<Element> iterator1 = rootElement.elementIterator("chance_variations"); iterator1.hasNext();)
		{
			Element element1 = iterator1.next();
			for(Iterator<Element> iterator2 = element1.elementIterator("variation"); iterator2.hasNext();)
			{
				Element element2 = iterator2.next();

				EnchantVariation variation = new EnchantVariation(Integer.parseInt(element2.attributeValue("id")));
				for(Iterator<Element> iterator3 = element2.elementIterator("enchant"); iterator3.hasNext();)
				{
					Element element3 = iterator3.next();

					final int[] enchantLvl = StringArrayUtils.stringToIntArray(element3.attributeValue("level"), "-");
					final double baseChance = Double.parseDouble(element3.attributeValue("base_chance"));
					final double magicWeaponChance = element3.attributeValue("magic_weapon_chance") == null ? baseChance : Double.parseDouble(element3.attributeValue("magic_weapon_chance"));
					final double fullBodyChance = element3.attributeValue("full_body_armor_chance") == null ? baseChance : Double.parseDouble(element3.attributeValue("full_body_armor_chance"));
					final boolean succVisualEffect = element3.attributeValue("success_visual_effect") == null ? false : Boolean.parseBoolean(element3.attributeValue("success_visual_effect"));
					if(enchantLvl.length == 2)
					{
						for(int i = enchantLvl[0]; i <= enchantLvl[1]; i++)
							variation.addLevel(new EnchantLevel(i, baseChance, magicWeaponChance, fullBodyChance, succVisualEffect));
					}
					else
						variation.addLevel(new EnchantLevel(enchantLvl[0], baseChance, magicWeaponChance, fullBodyChance, succVisualEffect));
				}
				getHolder().addEnchantVariation(variation);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("enchant_scroll"); iterator.hasNext();)
		{
			Element enchantItemElement = iterator.next();
			final int itemId = Integer.parseInt(enchantItemElement.attributeValue("id"));
			final int variation = Integer.parseInt(enchantItemElement.attributeValue("variation"));
			final int maxEnchant = enchantItemElement.attributeValue("max_enchant") == null ? defaultMaxEnchant : Integer.parseInt(enchantItemElement.attributeValue("max_enchant"));
			final FailResultType resultType = FailResultType.valueOf(enchantItemElement.attributeValue("on_fail"));
			final int enchantDropCount = enchantItemElement.attributeValue("enchant_drop_count") == null ? Integer.MAX_VALUE : Integer.parseInt(enchantItemElement.attributeValue("enchant_drop_count"));
			final EnchantType enchantType = enchantItemElement.attributeValue("type") == null ? EnchantType.ALL : EnchantType.valueOf(enchantItemElement.attributeValue("type"));
			final Set<ItemGrade> gradesSet = new HashSet<ItemGrade>();
			final String[] grades = enchantItemElement.attributeValue("grade") == null ? new String[]{"NONE"} : enchantItemElement.attributeValue("grade").split(";");
			for(String grade : grades)
				gradesSet.add(ItemGrade.valueOf(grade.toUpperCase()));

			final boolean failEffect = enchantItemElement.attributeValue("show_fail_effect") == null ? defaultFailEffect : Boolean.parseBoolean(enchantItemElement.attributeValue("show_fail_effect"));
			final int minEnchantStep = enchantItemElement.attributeValue("min_enchant_step") == null ? 1 : Integer.parseInt(enchantItemElement.attributeValue("min_enchant_step"));
			final int maxEnchantStep = enchantItemElement.attributeValue("max_enchant_step") == null ? 1 : Integer.parseInt(enchantItemElement.attributeValue("max_enchant_step"));

			EnchantScroll item = new EnchantScroll(itemId, variation, maxEnchant, enchantType, gradesSet, resultType, enchantDropCount, failEffect, minEnchantStep, maxEnchantStep);

			for(Iterator<Element> iterator2 = enchantItemElement.elementIterator(); iterator2.hasNext();)
			{
				Element element2 = iterator2.next();
				if(element2.getName().equals("item_list"))
				{
					for(Element e : element2.elements())
						item.addItemId(Integer.parseInt(e.attributeValue("id")));
				}
			}
			getHolder().addEnchantScroll(item);
		}
	}
}