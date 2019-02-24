package org.l2j.gameserver.data.xml.parser;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.HennaHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.HennaTemplate;

import java.io.File;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author: VISTALL
 * @date:  9:04/06.12.2010
 */
public final class HennaParser extends AbstractParser<HennaHolder>
{
	private static final HennaParser _instance = new HennaParser();

	public static HennaParser getInstance()
	{
		return _instance;
	}

	protected HennaParser()
	{
		super(HennaHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve( "data/hennas.xml").toFile();
	}

	@Override
    public String getDTDFileName()
    {
        return "hennas.dtd";
    }

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element hennaElement = iterator.next();
			int symbolId = Integer.parseInt(hennaElement.attributeValue("dye_id"));
			int dyeId = Integer.parseInt(hennaElement.attributeValue("dye_item_id"));
			int dyeLvl = Integer.parseInt(hennaElement.attributeValue("dye_level")); //TODO
			long drawPrice = Integer.parseInt(hennaElement.attributeValue("wear_fee"));
			long drawCount = Integer.parseInt(hennaElement.attributeValue("need_count"));
			long removePrice = Integer.parseInt(hennaElement.attributeValue("cancel_fee"));
			long removeCount = Integer.parseInt(hennaElement.attributeValue("cancel_count"));
			int period = hennaElement.attributeValue("period") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("period"));
			//STATS
			int wit = hennaElement.attributeValue("wit") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("wit"));
			int str = hennaElement.attributeValue("str") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("str"));
			int _int = hennaElement.attributeValue("int") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("int"));
			int con = hennaElement.attributeValue("con") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("con"));
			int dex = hennaElement.attributeValue("dex") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("dex"));
			int men = hennaElement.attributeValue("men") == null ? 0 : Integer.parseInt(hennaElement.attributeValue("men"));

			IntSet list = new HashIntSet();
			for(Iterator<Element> classIterator = hennaElement.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				list.add(Integer.parseInt(classElement.attributeValue("id")));
			}

			IntIntMap skills = new HashIntIntMap();
			for(Iterator<Element> skillsIterator = hennaElement.elementIterator("skills"); skillsIterator.hasNext();)
			{
				Element skillsElement = skillsIterator.next();

				for(Iterator<Element> skillIterator = skillsElement.elementIterator("skill"); skillIterator.hasNext();)
				{
					Element skillElement = skillIterator.next();

					int skillId = Integer.parseInt(skillElement.attributeValue("id"));
					int skillLvl = Integer.parseInt(skillElement.attributeValue("level"));

					skills.put(skillId, skillLvl);
				}
			}

			HennaTemplate henna = new HennaTemplate(symbolId, dyeId, dyeLvl, drawPrice, drawCount, removePrice, removeCount, wit, _int, con, str, dex, men, list, skills, period);

			getHolder().addHenna(henna);
		}
	}
}