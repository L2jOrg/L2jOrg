package org.l2j.gameserver.instancemanager;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.L2Spawn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.FakePlayerChatHolder;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Mobius
 */
public final class FakePlayerChatManager implements IGameXmlReader {
    private static final int MIN_DELAY = 5000;
    private static final int MAX_DELAY = 15000;
    private static final Logger LOGGER = LoggerFactory.getLogger(FakePlayerChatManager.class);
    final List<FakePlayerChatHolder> MESSAGES = new ArrayList<>();

    protected FakePlayerChatManager() {
        load();
    }

    public static FakePlayerChatManager getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        if (Config.FAKE_PLAYERS_ENABLED && Config.FAKE_PLAYER_CHAT) {
            MESSAGES.clear();
            parseDatapackFile("data/FakePlayerChatData.xml");
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + MESSAGES.size() + " chat templates.");
        } else {
            LOGGER.info(getClass().getSimpleName() + ": Disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "fakePlayerChat", fakePlayerChatNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(fakePlayerChatNode));
            MESSAGES.add(new FakePlayerChatHolder(set.getString("fpcName"), set.getString("searchMethod"), set.getString("searchText"), set.getString("answers")));
        }));
    }

    public void manageChat(L2PcInstance player, String fpcName, String message) {
        ThreadPoolManager.getInstance().schedule(() -> manageResponce(player, fpcName, message), Rnd.get(MIN_DELAY, MAX_DELAY));
    }

    public void manageChat(L2PcInstance player, String fpcName, String message, int minDelay, int maxDelay) {
        ThreadPoolManager.getInstance().schedule(() -> manageResponce(player, fpcName, message), Rnd.get(minDelay, maxDelay));
    }

    private void manageResponce(L2PcInstance player, String fpcName, String message) {
        if (player == null) {
            return;
        }

        final String text = message.toLowerCase();

        // tricky question
        if (text.contains("can you see me")) {
            final L2Spawn spawn = SpawnTable.getInstance().getAnySpawn(FakePlayerData.getInstance().getNpcIdByName(fpcName));
            if (spawn != null) {
                final L2Npc npc = spawn.getLastSpawn();
                if (npc != null) {
                    if (npc.calculateDistance2D(player) < 3000) {
                        if (GeoEngine.getInstance().canSeeTarget(npc, player) && !player.isInvisible()) {
                            sendChat(player, fpcName, Rnd.nextBoolean() ? "i am not blind" : Rnd.nextBoolean() ? "of course i can" : "yes");
                        } else {
                            sendChat(player, fpcName, Rnd.nextBoolean() ? "i know you are around" : Rnd.nextBoolean() ? "not at the moment :P" : "no, where are you?");
                        }
                    } else {
                        sendChat(player, fpcName, Rnd.nextBoolean() ? "nope, can't see you" : Rnd.nextBoolean() ? "nope" : "no");
                    }
                    return;
                }
            }
        }

        for (FakePlayerChatHolder chatHolder : MESSAGES) {
            if (!chatHolder.getFpcName().equals(fpcName) && !chatHolder.getFpcName().equals("ALL")) {
                continue;
            }

            switch (chatHolder.getSearchMethod()) {
                case "EQUALS": {
                    if (text.equals(chatHolder.getSearchText().get(0))) {
                        sendChat(player, fpcName, chatHolder.getAnswers().get(Rnd.get(chatHolder.getAnswers().size())));
                    }
                    break;
                }
                case "STARTS_WITH": {
                    if (text.startsWith(chatHolder.getSearchText().get(0))) {
                        sendChat(player, fpcName, chatHolder.getAnswers().get(Rnd.get(chatHolder.getAnswers().size())));
                    }
                    break;
                }
                case "CONTAINS": {
                    boolean allFound = true;
                    for (String word : chatHolder.getSearchText()) {
                        if (!text.contains(word)) {
                            allFound = false;
                        }
                    }
                    if (allFound) {
                        sendChat(player, fpcName, chatHolder.getAnswers().get(Rnd.get(chatHolder.getAnswers().size())));
                    }
                    break;
                }
            }
        }
    }

    public void sendChat(L2PcInstance player, String fpcName, String message) {
        final L2Spawn spawn = SpawnTable.getInstance().getAnySpawn(FakePlayerData.getInstance().getNpcIdByName(fpcName));
        if (spawn != null) {
            final L2Npc npc = spawn.getLastSpawn();
            if (npc != null) {
                player.sendPacket(new CreatureSay(npc, player, fpcName, ChatType.WHISPER, message));
            }
        }
    }

    private static class SingletonHolder {
        protected static final FakePlayerChatManager _instance = new FakePlayerChatManager();
    }
}
