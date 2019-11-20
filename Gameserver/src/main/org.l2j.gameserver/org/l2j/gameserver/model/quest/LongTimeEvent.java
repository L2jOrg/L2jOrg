package org.l2j.gameserver.model.quest;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.EventDroplist;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.instancemanager.EventShrineManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.announce.EventAnnouncement;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.script.DateRange;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * Parent class for long time events.<br>
 * Maintains config reading, spawn of NPCs, adding of event's drop.
 *
 * @author GKR
 */
public class LongTimeEvent extends Quest {
    // NPCs to spawm and their spawn points
    protected final List<NpcSpawn> _spawnList = new ArrayList<>();
    // Drop data for event
    protected final List<DropHolder> _dropList = new ArrayList<>();
    // Items to destroy when event ends.
    protected final List<Integer> _destoyItemsOnEnd = new ArrayList<>();
    protected Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    protected String _eventName;
    // Messages
    protected String _onEnterMsg = "Event is in process";
    protected String _endMsg = "Event ends!";
    protected DateRange _eventPeriod = null;
    protected DateRange _dropPeriod;
    boolean _enableShrines = false;

    public LongTimeEvent() {
        super(-1);
        loadConfig();

        if (_eventPeriod != null) {
            if (_eventPeriod.isWithinRange(new Date())) {
                startEvent();
                LOGGER.info("Event " + _eventName + " active till " + _eventPeriod.getEndDate());
            } else if (_eventPeriod.getStartDate().after(new Date())) {
                final long delay = _eventPeriod.getStartDate().getTime() - System.currentTimeMillis();
                ThreadPool.schedule(new ScheduleStart(), delay);
                LOGGER.info("Event " + _eventName + " will be started at " + _eventPeriod.getStartDate());
            } else {
                // Destroy items that must exist only on event period.
                destoyItemsOnEnd();
                LOGGER.info("Event " + _eventName + " has passed... Ignored ");
            }
        }
    }

    /**
     * Load event configuration file
     */
    private void loadConfig() {
        new GameXmlReader() {
            @Override
            protected Path getSchemaFilePath() {
                return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/eventConfig.xsd");
            }

            @Override
            public void load() {
                parseDatapackFile("data/scripts/org.l2j.scripts/events/" + getScriptName() + "/config.xml");
            }

            @Override
            public void parseDocument(Document doc, File f) {
                if (!doc.getDocumentElement().getNodeName().equalsIgnoreCase("event")) {
                    throw new NullPointerException("WARNING!!! " + getScriptName() + " event: bad config file!");
                }
                _eventName = doc.getDocumentElement().getAttributes().getNamedItem("name").getNodeValue();
                final String period = doc.getDocumentElement().getAttributes().getNamedItem("active").getNodeValue();
                _eventPeriod = DateRange.parse(period, new SimpleDateFormat("dd MM yyyy", Locale.US));

                if ((doc.getDocumentElement().getAttributes().getNamedItem("enableShrines") != null) && doc.getDocumentElement().getAttributes().getNamedItem("enableShrines").getNodeValue().equalsIgnoreCase("true")) {
                    _enableShrines = true;
                }

                if (doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod") != null) {
                    final String dropPeriod = doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod").getNodeValue();
                    _dropPeriod = DateRange.parse(dropPeriod, new SimpleDateFormat("dd MM yyyy", Locale.US));
                    // Check if drop period is within range of event period
                    if (!_eventPeriod.isWithinRange(_dropPeriod.getStartDate()) || !_eventPeriod.isWithinRange(_dropPeriod.getEndDate())) {
                        _dropPeriod = _eventPeriod;
                    }
                } else {
                    _dropPeriod = _eventPeriod; // Drop period, if not specified, assumes all event period.
                }

                if (_eventPeriod == null) {
                    throw new NullPointerException("WARNING!!! " + getScriptName() + " event: illegal event period");
                }

                final Date today = new Date();

                if (_eventPeriod.getStartDate().after(today) || _eventPeriod.isWithinRange(today)) {
                    final Node first = doc.getDocumentElement().getFirstChild();
                    for (Node n = first; n != null; n = n.getNextSibling()) {
                        // Loading droplist
                        if (n.getNodeName().equalsIgnoreCase("droplist")) {
                            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                                if (d.getNodeName().equalsIgnoreCase("add")) {
                                    try {
                                        final int itemId = Integer.parseInt(d.getAttributes().getNamedItem("item").getNodeValue());
                                        final int minCount = Integer.parseInt(d.getAttributes().getNamedItem("min").getNodeValue());
                                        final int maxCount = Integer.parseInt(d.getAttributes().getNamedItem("max").getNodeValue());
                                        final String chance = d.getAttributes().getNamedItem("chance").getNodeValue();
                                        int finalChance = 0;

                                        if (!chance.isEmpty() && chance.endsWith("%")) {
                                            finalChance = Integer.parseInt(chance.substring(0, chance.length() - 1)) * 10000;
                                        }

                                        if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                                            LOGGER.warn(getScriptName() + " event: " + itemId + " is wrong item id, item was not added in droplist");
                                            continue;
                                        }

                                        if (minCount > maxCount) {
                                            LOGGER.warn(getScriptName() + " event: item " + itemId + " - min greater than max, item was not added in droplist");
                                            continue;
                                        }

                                        if ((finalChance < 10000) || (finalChance > 1000000)) {
                                            LOGGER.warn(getScriptName() + " event: item " + itemId + " - incorrect drop chance, item was not added in droplist");
                                            continue;
                                        }

                                        _dropList.add(new DropHolder(null, itemId, minCount, maxCount, finalChance));
                                    } catch (NumberFormatException nfe) {
                                        LOGGER.warn("Wrong number format in config.xml droplist block for " + getScriptName() + " event");
                                    }
                                }
                            }
                        } else if (n.getNodeName().equalsIgnoreCase("spawnlist")) {
                            // Loading spawnlist
                            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                                if (d.getNodeName().equalsIgnoreCase("add")) {
                                    try {
                                        final int npcId = Integer.parseInt(d.getAttributes().getNamedItem("npc").getNodeValue());
                                        final int xPos = Integer.parseInt(d.getAttributes().getNamedItem("x").getNodeValue());
                                        final int yPos = Integer.parseInt(d.getAttributes().getNamedItem("y").getNodeValue());
                                        final int zPos = Integer.parseInt(d.getAttributes().getNamedItem("z").getNodeValue());
                                        final int heading = d.getAttributes().getNamedItem("heading").getNodeValue() != null ? Integer.parseInt(d.getAttributes().getNamedItem("heading").getNodeValue()) : 0;

                                        if (NpcData.getInstance().getTemplate(npcId) == null) {
                                            LOGGER.warn(getScriptName() + " event: " + npcId + " is wrong NPC id, NPC was not added in spawnlist");
                                            continue;
                                        }

                                        _spawnList.add(new NpcSpawn(npcId, new Location(xPos, yPos, zPos, heading)));
                                    } catch (NumberFormatException nfe) {
                                        LOGGER.warn("Wrong number format in config.xml spawnlist block for " + getScriptName() + " event");
                                    }
                                }
                            }
                        } else if (n.getNodeName().equalsIgnoreCase("messages")) {
                            // Loading Messages
                            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                                if (d.getNodeName().equalsIgnoreCase("add")) {
                                    final String msgType = d.getAttributes().getNamedItem("type").getNodeValue();
                                    final String msgText = d.getAttributes().getNamedItem("text").getNodeValue();
                                    if ((msgType != null) && (msgText != null)) {
                                        if (msgType.equalsIgnoreCase("onEnd")) {
                                            _endMsg = msgText;
                                        } else if (msgType.equalsIgnoreCase("onEnter")) {
                                            _onEnterMsg = msgText;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Load destroy item list at all times.
                final Node first = doc.getDocumentElement().getFirstChild();
                for (Node n = first; n != null; n = n.getNextSibling()) {
                    if (n.getNodeName().equalsIgnoreCase("destoyItemsOnEnd")) {
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if (d.getNodeName().equalsIgnoreCase("item")) {
                                try {
                                    final int itemId = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
                                    if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                                        LOGGER.warn(getScriptName() + " event: Item " + itemId + " does not exist.");
                                        continue;
                                    }
                                    _destoyItemsOnEnd.add(itemId);
                                } catch (NumberFormatException nfe) {
                                    LOGGER.warn("Wrong number format in config.xml destoyItemsOnEnd block for " + getScriptName() + " event");
                                }
                            }
                        }
                    }
                }
            }
        }.load();

    }

    /**
     * Maintenance event start - adds global drop, spawns event NPCs, shows start announcement.
     */
    protected void startEvent() {
        // Add drop
        if (_dropList != null) {
            for (DropHolder drop : _dropList) {
                EventDroplist.getInstance().addGlobalDrop(drop.getItemId(), drop.getMin(), drop.getMax(), (int) drop.getChance(), _dropPeriod);
            }
        }

        // Add spawns
        final Long millisToEventEnd = _eventPeriod.getEndDate().getTime() - System.currentTimeMillis();
        if (_spawnList != null) {
            for (NpcSpawn spawn : _spawnList) {
                AbstractScript.addSpawn(spawn.npcId, spawn.loc.getX(), spawn.loc.getY(), spawn.loc.getZ(), spawn.loc.getHeading(), false, millisToEventEnd, false);
            }
        }

        // Enable town shrines
        if (_enableShrines) {
            EventShrineManager.getInstance().setEnabled(true);
        }

        // Send message on begin
        Broadcast.toAllOnlinePlayers(_onEnterMsg);

        // Add announce for entering players
        AnnouncementsManager.getInstance().addAnnouncement(new EventAnnouncement(_eventPeriod, _onEnterMsg));

        // Schedule event end (now only for message sending)
        ThreadPool.schedule(new ScheduleEnd(), millisToEventEnd);
    }

    /**
     * @return event period
     */
    public DateRange getEventPeriod() {
        return _eventPeriod;
    }

    /**
     * @return {@code true} if now is event period
     */
    public boolean isEventPeriod() {
        return _eventPeriod.isWithinRange(new Date());
    }

    /**
     * @return {@code true} if now is drop period
     */
    public boolean isDropPeriod() {
        return _dropPeriod.isWithinRange(new Date());
    }

    void destoyItemsOnEnd() {
        if (!_destoyItemsOnEnd.isEmpty()) {
            for (int itemId : _destoyItemsOnEnd) {
                // Remove item from online players.
                for (Player player : World.getInstance().getPlayers()) {
                    if (player != null) {
                        player.destroyItemByItemId(_eventName, itemId, -1, player, true);
                    }
                }
                // Update database
                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE item_id=?")) {
                    statement.setInt(1, itemId);
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected class NpcSpawn {
        protected final Location loc;
        protected final int npcId;

        protected NpcSpawn(int pNpcId, Location spawnLoc) {
            loc = spawnLoc;
            npcId = pNpcId;
        }
    }

    protected class ScheduleStart implements Runnable {
        @Override
        public void run() {
            startEvent();
        }
    }

    protected class ScheduleEnd implements Runnable {
        @Override
        public void run() {
            // Disable town shrines
            if (_enableShrines) {
                EventShrineManager.getInstance().setEnabled(false);
            }
            // Destroy item that must exist only on event period.
            destoyItemsOnEnd();
            // Send message on end
            Broadcast.toAllOnlinePlayers(_endMsg);
        }
    }
}
