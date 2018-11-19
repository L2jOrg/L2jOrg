package l2s.gameserver.data.xml.parser;

import java.io.File;

import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.templates.fakeplayer.FakePlayerAITemplate;
import l2s.gameserver.templates.fakeplayer.FarmZoneTemplate;
import l2s.gameserver.templates.fakeplayer.TownZoneTemplate;

import org.dom4j.Element;

/**
 * @author Bonux
**/
public final class FakePlayersParser extends AbstractParser<FakePlayersHolder>
{
	private static final FakePlayersParser _instance = new FakePlayersParser();

	public static FakePlayersParser getInstance()
	{
		return _instance;
	}

	private FakePlayersParser()
	{
		super(FakePlayersHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/fake_players/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		if(f.equals(FakeItemParser.getInstance().getXMLPath()))
			return true;
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "fake_player.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		String rootElementName = rootElement.getName();
		if(rootElementName.equalsIgnoreCase("fake_player_ai"))
		{
			FakePlayerAITemplate template = FakePlayerAITemplate.parse(rootElement);
			if(template != null)
				getHolder().addAITemplate(template);
		}
		else if(rootElementName.equalsIgnoreCase("farm"))
			getHolder().addFarmZone(FarmZoneTemplate.parse(null, rootElement));
		else if(rootElementName.equalsIgnoreCase("town"))
			getHolder().addTownZone(TownZoneTemplate.parse(rootElement));
	}

	protected void afterParseActions()
	{
		for(FakePlayerAITemplate aiTemplate : getHolder().getAITemplates())
		{
			for(FarmZoneTemplate farmTemplate : getHolder().getFarmZones())
				aiTemplate.addFarmZone(farmTemplate);
		}
	}
}