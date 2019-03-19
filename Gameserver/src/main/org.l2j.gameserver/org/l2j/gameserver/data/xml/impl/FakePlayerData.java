package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.holders.FakePlayerHolder;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Mobius
 */
public class FakePlayerData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakePlayerData.class);

    private final Map<Integer, FakePlayerHolder> _fakePlayerInfos = new HashMap<>();
    private final Map<String, String> _fakePlayerNames = new HashMap<>();
    private final Map<String, Integer> _fakePlayerIds = new HashMap<>();
    private final List<String> _talkableFakePlayerNames = new ArrayList<>();

    private FakePlayerData() {
        load();
    }

    @Override
    public void load() {
        if (Config.FAKE_PLAYERS_ENABLED) {
            _fakePlayerInfos.clear();
            _fakePlayerNames.clear();
            _fakePlayerIds.clear();
            _talkableFakePlayerNames.clear();
            parseDatapackFile("data/FakePlayerVisualData.xml");
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + _fakePlayerInfos.size() + " templates.");
        } else {
            LOGGER.info(getClass().getSimpleName() + ": Disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "fakePlayer", fakePlayerNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(fakePlayerNode));
            final int npcId = set.getInt("npcId");
            final L2NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
            final String name = template.getName();
            if (CharNameTable.getInstance().getIdByName(name) > 0) {
                LOGGER.info(getClass().getSimpleName() + ": Could not create fake player template " + npcId + ", player name already exists.");
            } else {
                _fakePlayerIds.put(name, npcId); // name - npcId
                _fakePlayerNames.put(name.toLowerCase(), name); // name to lowercase - name
                _fakePlayerInfos.put(npcId, new FakePlayerHolder(set));
                if (template.isFakePlayerTalkable()) {
                    _talkableFakePlayerNames.add(name.toLowerCase());
                }
            }
        }));
    }

    public int getNpcIdByName(String name) {
        return _fakePlayerIds.get(name);
    }

    public String getProperName(String name) {
        return _fakePlayerNames.get(name.toLowerCase());
    }

    public Boolean isTalkable(String name) {
        return _talkableFakePlayerNames.contains(name.toLowerCase());
    }

    public FakePlayerHolder getInfo(int npcId) {
        return _fakePlayerInfos.get(npcId);
    }

    public static FakePlayerData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FakePlayerData INSTANCE = new FakePlayerData();
    }
}
