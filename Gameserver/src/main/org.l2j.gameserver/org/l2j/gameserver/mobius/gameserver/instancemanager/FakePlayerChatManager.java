/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import com.l2jmobius.Config;
import org.l2j.commons.util.IGameXmlReader;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.mobius.gameserver.model.L2Spawn;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.FakePlayerChatHolder;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.CreatureSay;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Mobius
 */
public final class FakePlayerChatManager implements IGameXmlReader {
    private static final int MIN_DELAY = 5000;
    private static final int MAX_DELAY = 15000;
    private static Logger LOGGER = Logger.getLogger(FakePlayerChatManager.class.getName());
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
