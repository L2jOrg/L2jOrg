package org.l2j.gameserver.tables;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.ItemTemplate;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.StringTokenizer;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class EnchantHPBonusTable
{
	private static Logger _log = LoggerFactory.getLogger(EnchantHPBonusTable.class);

	private final HashIntObjectMap<Integer[]> _armorHPBonus = new HashIntObjectMap<Integer[]>();

	private int _onepieceFactor = 100;

	private static EnchantHPBonusTable _instance = new EnchantHPBonusTable();

	public static EnchantHPBonusTable getInstance()
	{
		if(_instance == null)
			_instance = new EnchantHPBonusTable();
		return _instance;
	}

	public void reload()
	{
		_instance = new EnchantHPBonusTable();
	}

	private EnchantHPBonusTable()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			File file = getSettings(ServerSettings.class).dataPackRootPath().resolve("data/enchant_bonus.xml").toFile();
			Document doc = factory.newDocumentBuilder().parse(file);

			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				if("list".equalsIgnoreCase(n.getNodeName()))
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						NamedNodeMap attrs = d.getAttributes();
						Node att;
						if("options".equalsIgnoreCase(d.getNodeName()))
						{
							att = attrs.getNamedItem("onepiece_factor");
							if(att == null)
							{
								_log.info("EnchantHPBonusTable: Missing onepiece_factor, skipping");
								continue;
							}
							_onepieceFactor = Integer.parseInt(att.getNodeValue());
						}
						else if("enchant_bonus".equalsIgnoreCase(d.getNodeName()))
						{
							ItemGrade grade;

							att = attrs.getNamedItem("grade");
							if(att == null)
							{
								_log.info("EnchantHPBonusTable: Missing grade, skipping");
								continue;
							}
							grade = ItemGrade.valueOf(att.getNodeValue());

							att = attrs.getNamedItem("values");
							if(att == null)
							{
								_log.info("EnchantHPBonusTable: Missing bonus id: " + grade.ordinal() + ", skipping");
								continue;
							}
							StringTokenizer st = new StringTokenizer(att.getNodeValue(), ",");
							int tokenCount = st.countTokens();
							Integer[] bonus = new Integer[tokenCount];
							for(int i = 0; i < tokenCount; i++)
							{
								Integer value = Integer.decode(st.nextToken().trim());
								if(value == null)
								{
									_log.info("EnchantHPBonusTable: Bad Hp value!! grade: " + grade.ordinal() + " token: " + i);
									value = 0;
								}
								bonus[i] = value;
							}
							_armorHPBonus.put(grade.ordinal(), bonus);
						}
					}
			_log.info("EnchantHPBonusTable: Loaded bonuses for " + _armorHPBonus.size() + " grades.");
		}
		catch(Exception e)
		{
			_log.warn("EnchantHPBonusTable: Lists could not be initialized.");
			e.printStackTrace();
		}
	}

	public final int getHPBonus(Player player, ItemInstance item)
	{
		final Integer[] values;

		if(item.getFixedEnchantLevel(player) == 0)
			return 0;

		values = _armorHPBonus.get(item.getTemplate().getGrade().ordinal());

		if(values == null || values.length == 0)
			return 0;

		int bonus = values[Math.min(item.getFixedEnchantLevel(player), values.length) - 1];
		if(item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
			bonus = (int) (bonus * _onepieceFactor / 100.0D);

		return bonus;
	}
}