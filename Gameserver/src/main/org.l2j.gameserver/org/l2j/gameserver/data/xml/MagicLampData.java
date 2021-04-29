package org.l2j.gameserver.data.xml;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.GreaterMagicLampHolder;
import org.l2j.gameserver.model.holders.MagicLampDataHolder;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.magiclamp.ExMagicLampExpInfoUI;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MagicLampData  extends GameXmlReader {
    private static final List<MagicLampDataHolder> LAMPS = new ArrayList<>();
    private static final List<GreaterMagicLampHolder> GREATER_LAMPS = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(MagicLampData.class);


    protected MagicLampData()
    {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/magic-lamp.xsd");
    }

    @Override
    public void load() {
        LAMPS.clear();
        GREATER_LAMPS.clear();
        parseDatapackFile("data/magic-lamp.xml");
        LOGGER.info("MagicLampData: Loaded " + (LAMPS.size() + GREATER_LAMPS.size()) + " magic lamps.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, node ->
        {
            if (node.getNodeName().equalsIgnoreCase("greater_lamp_mode")) {
                GREATER_LAMPS.add(new GreaterMagicLampHolder(new StatsSet(parseAttributes(node))));
            } else if (node.getNodeName().equalsIgnoreCase("lamp")) {
                LAMPS.add(new MagicLampDataHolder(new StatsSet(parseAttributes(node))));
            }
        }));
    }


    public void addLampExp(Player player, double exp, boolean rateModifiers)
    {
        if (Config.ENABLE_MAGIC_LAMP)
        {
            final int lampExp = (int) (exp * (rateModifiers ? Config.MAGIC_LAMP_CHARGE_RATE * player.getStats().getMul(Stat.MAGIC_LAMP_EXP_RATE, 1) : 1));
            int calc = lampExp + player.getLampExp();
            if (calc > Config.MAGIC_LAMP_MAX_LEVEL_EXP)
            {
                calc %= Config.MAGIC_LAMP_MAX_LEVEL_EXP;
                player.setLampCount(player.getLampCount() + 1);
            }
            player.setLampExp(calc);
            player.sendPacket(new ExMagicLampExpInfoUI(player));
        }
    }

    public List<MagicLampDataHolder> getLamps()
    {
        return LAMPS;
    }

    public List<GreaterMagicLampHolder> getGreaterLamps()
    {
        return GREATER_LAMPS;
    }

    public static MagicLampData getInstance()
    {
        return Singleton.INSTANCE;
    }

    private static class Singleton
    {
        protected static final MagicLampData INSTANCE = new MagicLampData();
    }
}
