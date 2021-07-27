/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import io.github.joealisson.primitive.*;
import io.github.joealisson.primitive.IntLongMap;
import io.github.joealisson.primitive.CHashIntLongMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.InstanceDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.enums.InstanceRemoveBuffType;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.model.holders.InstanceReenterTimeHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.model.instancezone.conditions.Condition;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Instance manager.
 *
 * @author evill33t, GodKratos, malyelfik
 * @author JoeAlisson
 */
public final class InstanceManager extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceManager.class);
    private static final InstanceTemplate DEFAULT_TEMPLATE = new InstanceTemplate();

    private final IntMap<InstanceTemplate> instanceTemplates = new HashIntMap<>();
    private final IntMap<Instance> instanceWorlds = new CHashIntMap<>();
    private final IntMap<IntLongMap> playerInstanceTimes = new CHashIntMap<>();

    private InstanceManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
         return ServerSettings.dataPackDirectory().resolve("data/instances/instance.xsd");
    }

    @Override
    public void load() {
        instanceTemplates.clear();
        parseDatapackDirectory("data/instances", true);
        LOGGER.info("Loaded {} instance templates.", instanceTemplates.size());

        playerInstanceTimes.clear();
        restoreInstanceTimes();
        LOGGER.info("Loaded instance reenter times for {} players.", playerInstanceTimes.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for(var node = doc.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if ("instance".equals(node.getNodeName())) {
                parseInstanceTemplate(node, f);
            }
        }
    }

    /**
     * Parse instance template from XML file.
     *
     * @param instanceNode start XML tag
     * @param file         currently parsed file
     */
    private void parseInstanceTemplate(Node instanceNode, File file) {
        var attrs = instanceNode.getAttributes();
        final int id = parseInt(attrs, "id");
        if (instanceTemplates.containsKey(id)) {
            LOGGER.warn("Instance template with ID {} already exists", id);
            return;
        }
        final InstanceTemplate template = new InstanceTemplate(id, parseString(attrs, "name"), parseInt(attrs, "maxWorlds", -1));

        for(var innerNode = instanceNode.getFirstChild(); nonNull(innerNode); innerNode = innerNode.getNextSibling()) {
            switch (innerNode.getNodeName()) {
                case "time" -> parseTimes(template, innerNode);
                case "misc" -> parseMisc(template, innerNode);
                case "rates" -> parseRates(template, innerNode);
                case "locations" -> parseLocations(template, innerNode);
                case "spawnlist" -> parseSpawns(file, template, innerNode);
                case "doorlist" -> parseDoors(template, innerNode);
                case "removeBuffs" -> parseRemoveBuffs(template, innerNode);
                case "reenter" -> parseReenter(template, innerNode);
                case "conditions" -> parseConditions(id, template, innerNode);
            }
        }
        instanceTemplates.put(id, template);
    }

    private void parseConditions(int id, InstanceTemplate template, Node innerNode) {
        org.w3c.dom.NamedNodeMap attrs;
        final List<Condition> conditions = new ArrayList<>();
        for (Node conditionNode = innerNode.getFirstChild(); conditionNode != null; conditionNode = conditionNode.getNextSibling()) {

            if (conditionNode.getNodeName().equals("condition")) {
                attrs = conditionNode.getAttributes();
                final String type = parseString(attrs, "type");
                final boolean onlyLeader = parseBoolean(attrs, "onlyLeader", false);
                final boolean showMessageAndHtml = parseBoolean(attrs, "showMessageAndHtml", false);

                StatsSet params = null;
                for (Node f = conditionNode.getFirstChild(); f != null; f = f.getNextSibling()) {
                    if (f.getNodeName().equals("param")) {
                        if (params == null) {
                            params = new StatsSet();
                        }

                        params.set(parseString(f.getAttributes(), "name"), parseString(f.getAttributes(), "value"));
                    }
                }

                // If none parameters found then set empty StatSet
                if (params == null) {
                    params = StatsSet.EMPTY_STATSET;
                }

                // Now when everything is loaded register condition to template
                try {
                    final Class<?> clazz = Class.forName("org.l2j.gameserver.model.instancezone.conditions.Condition" + type);
                    final Constructor<?> constructor = clazz.getConstructor(InstanceTemplate.class, StatsSet.class, boolean.class, boolean.class);
                    conditions.add((Condition) constructor.newInstance(template, params, onlyLeader, showMessageAndHtml));
                } catch (Exception ex) {
                    LOGGER.warn("Unknown condition type " + type + " for instance " + template.getName() + " (" + id + ")!");
                }
            }
        }
        template.setConditions(conditions);
    }

    private void parseReenter(InstanceTemplate template, Node innerNode) {
        org.w3c.dom.NamedNodeMap attrs;
        final InstanceReenterType type = parseEnum(innerNode.getAttributes(), InstanceReenterType.class, "apply", InstanceReenterType.NONE);
        final List<InstanceReenterTimeHolder> data = new ArrayList<>();
        for (Node e = innerNode.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e.getNodeName().equals("reset")) {
                attrs = e.getAttributes();
                final int time = parseInt(attrs, "time", -1);
                if (time > 0) {
                    data.add(new InstanceReenterTimeHolder(time));
                } else {
                    final DayOfWeek day = parseEnum(attrs, DayOfWeek.class, "day");
                    final int hour = parseInt(attrs, "hour", -1);
                    final int minute = parseInt(attrs, "minute", -1);
                    data.add(new InstanceReenterTimeHolder(day, hour, minute));
                }
            }
        }
        template.setReenterData(type, data);
    }

    private void parseRemoveBuffs(InstanceTemplate template, Node innerNode) {
        final InstanceRemoveBuffType removeBuffType = parseEnum(innerNode.getAttributes(), InstanceRemoveBuffType.class, "type");
        final IntSet exceptionBuffList = new HashIntSet();
        for (Node e = innerNode.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (e.getNodeName().equals("skill")) {
                exceptionBuffList.add(parseInt(e.getAttributes(), "id"));
            }
        }
        template.setRemoveBuff(removeBuffType, exceptionBuffList);
    }

    private void parseSpawns(File file, InstanceTemplate template, Node innerNode) {
        final List<SpawnTemplate> spawns = new ArrayList<>();
        SpawnsData.getInstance().parseSpawn(innerNode, file, spawns);
        template.addSpawns(spawns);
    }

    private void parseRates(InstanceTemplate template, Node innerNode) {
        org.w3c.dom.NamedNodeMap attrs;
        attrs = innerNode.getAttributes();
        template.setExpRate(parseFloat(attrs, "exp", Config.RATE_INSTANCE_XP));
        template.setSPRate(parseFloat(attrs, "sp", Config.RATE_INSTANCE_SP));
        template.setExpPartyRate(parseFloat(attrs, "partyExp", Config.RATE_INSTANCE_PARTY_XP));
        template.setSPPartyRate(parseFloat(attrs, "partySp", Config.RATE_INSTANCE_PARTY_SP));
    }

    private void parseMisc(InstanceTemplate template, Node innerNode) {
        org.w3c.dom.NamedNodeMap attrs;
        attrs = innerNode.getAttributes();
        template.allowPlayerSummon(parseBoolean(attrs, "allowPlayerSummon"));
        template.setIsPvP(parseBoolean(attrs, "isPvP"));
    }

    private void parseTimes(InstanceTemplate template, Node innerNode) {
        org.w3c.dom.NamedNodeMap attrs;
        attrs = innerNode.getAttributes();
        template.setDuration(parseInt(attrs, "duration", -1));
        template.setEmptyDestroyTime(parseInt(attrs, "empty", -1));
        template.setEjectTime(parseInt(attrs, "eject", -1));
    }

    private void parseDoors(InstanceTemplate template, Node innerNode) {
        for (Node doorNode = innerNode.getFirstChild(); doorNode != null; doorNode = doorNode.getNextSibling()) {
            if (doorNode.getNodeName().equals("door")) {
                final StatsSet parsedSet = DoorDataManager.getInstance().parseDoor(doorNode);
                final StatsSet mergedSet = new StatsSet();
                final int doorId = parsedSet.getInt("id");
                final StatsSet templateSet = DoorDataManager.getInstance().getDoorTemplate(doorId);
                if (templateSet != null) {
                    mergedSet.merge(templateSet);
                } else {
                    LOGGER.warn("Cannot find template for door: " + doorId + ", instance: " + template.getName() + " (" + template.getId() + ")");
                }
                mergedSet.merge(parsedSet);

                try {
                    template.addDoor(doorId, new DoorTemplate(mergedSet));
                } catch (Exception e) {
                    LOGGER.warn("Cannot initialize template for door: {}, instance: {}", doorId, template, e);
                }
            }
        }
    }

    private void parseLocations(InstanceTemplate template, Node innerNode) {
        for(var locationsNode = innerNode.getFirstChild(); nonNull(locationsNode); locationsNode = locationsNode.getNextSibling()) {
            final InstanceTeleportType type = parseEnum(locationsNode.getAttributes(), InstanceTeleportType.class, "type");
            switch (locationsNode.getNodeName()) {
                case "enter" -> template.setEnterLocation(type, parseLocations(locationsNode));
                case "exit" -> {
                    if (type.equals(InstanceTeleportType.ORIGIN)) {
                        template.setExitLocation(type, null);
                    } else {
                        final List<Location> locations = parseLocations(locationsNode);
                        if (locations.isEmpty()) {
                            LOGGER.warn("Missing exit location data for instance {}!", template);
                        } else {
                            template.setExitLocation(type, locations);
                        }
                    }
                }
            }
        }
    }

    private List<Location> parseLocations(Node locationsNode) {
        final List<Location> locations = new ArrayList<>();
        for(var locationNode = locationsNode.getFirstChild(); nonNull(locationNode); locationNode = locationNode.getNextSibling()) {
            locations.add(parseLocation(locationNode));
        }
        return locations;
    }

    public Instance createInstance(int templateId) {
        return createInstance(templateId, null);
    }

    public Instance createInstance(int templateId, Player player) {
        var template= instanceTemplates.get(templateId);

        if(isNull(template)) {
            LOGGER.warn("Missing template for instance with id {}!", templateId);
            template = DEFAULT_TEMPLATE;
        }

        var id= IdFactory.getInstance().getNextId();
        var instance = new Instance(id, template);
        instanceWorlds.put(id, instance);
        instance.init(player);
        return instance;
    }

    /**
     * Get instance world with given ID.
     *
     * @param instanceId ID of instance
     * @return instance itself if found, otherwise {@code null}
     */
    public Instance getInstance(int instanceId) {
        return instanceWorlds.get(instanceId);
    }

    /**
     * Get all active instances.
     *
     * @return Collection of all instances
     */
    public Collection<Instance> getInstances() {
        return instanceWorlds.values();
    }

    /**
     * Get instance world for player.
     *
     * @param player   player who wants to get instance world
     * @param isInside when {@code true} find world where player is currently located, otherwise find world where player can enter
     * @return instance if found, otherwise {@code null}
     */
    public Instance getPlayerInstance(Player player, boolean isInside) {
        return instanceWorlds.values().stream().filter(i -> (isInside) ? i.containsPlayer(player) : i.isAllowed(player)).findFirst().orElse(null);
    }

    /**
     * Unregister instance world.<br>
     * <b><font color=red>To remove instance world properly use {@link Instance#destroy()}.</font></b>
     *
     * @param instanceId ID of instance to unregister
     */
    public void unregister(int instanceId) {
        if (instanceWorlds.containsKey(instanceId)) {
            instanceWorlds.remove(instanceId);
            IdFactory.getInstance().releaseId(instanceId);
        }
    }

    /**
     * Get instance name from file "InstanceNames.xml"
     *
     * @param templateId template ID of instance
     * @return name of instance if found, otherwise {@code null}
     */
    public String getInstanceName(int templateId) {
        return  instanceTemplates.get(templateId).getName();
    }

    /**
     * Restore instance reenter data for all players.
     */
    private void restoreInstanceTimes() {
        getDAO(InstanceDAO.class).findAllInstancesTime(this::addInstancesTime);
    }

    private void addInstancesTime(ResultSet rs) {
        try {
            var currentTime = System.currentTimeMillis();
            while (rs.next()) {

                final long time = rs.getLong("time");
                if (time > currentTime) {
                    final int charId = rs.getInt("charId");
                    final int instanceId = rs.getInt("instanceId");
                    setReenterPenalty(charId, instanceId, time);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * Get all instance re-enter times for specified player.<br>
     * This method also removes the penalties that have already expired.
     *
     * @param player instance of player who wants to get re-enter data
     * @return map in form templateId, penaltyEndTime
     */
    public IntLongMap getAllInstanceTimes(Player player) {
        // When player don't have any instance penalty
        final var instanceTimes = playerInstanceTimes.get(player.getObjectId());
        if ((instanceTimes == null) || instanceTimes.isEmpty()) {
            return Containers.EMPTY_INT_LONG_MAP;
        }

        final IntSet invalidPenalty = new HashIntSet(instanceTimes.size());
        for (var entry : instanceTimes.entrySet()) {
            if (entry.getValue() <= System.currentTimeMillis()) {
                invalidPenalty.add(entry.getKey());
            }
        }

        if (!invalidPenalty.isEmpty()) {
            getDAO(InstanceDAO.class).deleteInstanceTime(player.getObjectId(), invalidPenalty);
        }
        return instanceTimes;
    }

    /**
     * Set re-enter penalty for specified player.<br>
     * <font color=red><b>This method store penalty into memory only. Use {@link Instance#setReenterTime} to set instance penalty properly.</b></font>
     *
     * @param objectId object ID of player
     * @param id       instance template id
     * @param time     penalty time
     */
    public void setReenterPenalty(int objectId, int id, long time) {
        playerInstanceTimes.computeIfAbsent(objectId, k -> new CHashIntLongMap()).put(id, time);
    }

    /**
     * Get re-enter time to instance (by template ID) for player.<br>
     * This method also removes penalty if expired.
     *
     * @param player player who wants to get re-enter time
     * @param id     template ID of instance
     * @return penalty end time if penalty is found, otherwise -1
     */
    public long getInstanceTime(Player player, int id) {
        // Check if exists reenter data for player
        final var playerData = playerInstanceTimes.get(player.getObjectId());
        if ((playerData == null) || !playerData.containsKey(id)) {
            return -1;
        }

        // If reenter time is higher then current, delete it
        final long time = playerData.get(id);
        if (time <= System.currentTimeMillis()) {
            deleteInstanceTime(player, id);
            return -1;
        }
        return time;
    }

    /**
     * Remove re-enter penalty for specified instance from player.
     *
     * @param player player who wants to delete penalty
     * @param id     template id of instance world
     */
    public void deleteInstanceTime(Player player, int id) {
        getDAO(PlayerDAO.class).deleteInstanceTime(player.getObjectId(), id);
        playerInstanceTimes.get(player.getObjectId()).remove(id);
    }

    /**
     * Get instance template by template ID.
     *
     * @param id template id of instance
     * @return instance template if found, otherwise {@code null}
     */
    public InstanceTemplate getInstanceTemplate(int id) {
        return instanceTemplates.get(id);
    }

    public boolean hasInstanceTemplate(int templateId) {
        return instanceTemplates.containsKey(templateId);
    }

    /**
     * Get all instances template.
     *
     * @return Collection of all instance templates
     */
    public Collection<InstanceTemplate> getInstanceTemplates() {
        return instanceTemplates.values();
    }

    /**
     * Get count of created instance worlds with same template ID.
     *
     * @param templateId template id of instance
     * @return count of created instances
     */
    public long getWorldCount(int templateId) {
        return instanceWorlds.values().stream().filter(i -> i.getTemplateId() == templateId).count();
    }

    public List<Instance> getInstances(int templateId) {
        return instanceWorlds.values().stream().filter(i -> i.getTemplateId() == templateId).collect(Collectors.toList());
    }

    public static void init() {
        getInstance().load();
    }

    public static InstanceManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final InstanceManager INSTANCE = new InstanceManager();
    }
}
