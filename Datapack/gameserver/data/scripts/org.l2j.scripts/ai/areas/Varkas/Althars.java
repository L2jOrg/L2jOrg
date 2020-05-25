package ai.areas.Varkas;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class Althars extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Althars.class);

    private final int _DELAY = 30000;
    private boolean _ACTIVATED = false;


    private Althars() {
        var altharsDatas = new AltharsData();
        altharsDatas.load();
        startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if("ALTHARS_TIMER".equals(event)) {
            _ACTIVATED = !_ACTIVATED;

            if (_ACTIVATED) {
                spawnMonsters();
            }
            if (!_ACTIVATED) {
                unSpawnMonsters();
            }

            startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
        }

        return super.onAdvEvent(event, npc, player);
    }

    private void spawnMonsters() {
        //TODO: activate the glow of althars
        //TODO: spawn monsters
    }

    private void unSpawnMonsters() {
        //TODO: deactivate the glow of althars
        //TODO: unspawn monsters
    }

    public static AbstractNpcAI provider() {
        return new Althars();
    }

    private class AltharsData extends GameXmlReader {

        @Override
        protected Path getSchemaFilePath() {
            //TODO: Create file althars.xsd
            return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/althars.xsd");
        }

        @Override
        public void load() {
            //TODO: Create file Althars.xml
            parseDatapackFile("data/Althars.xml");
            LOGGER.info("Loaded Althars data.");
        }

        @Override
        protected void parseDocument(Document doc, File f) {
            //TODO: Parse file
        }
    }

}
