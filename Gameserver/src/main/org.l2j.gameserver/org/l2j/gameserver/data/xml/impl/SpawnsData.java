package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.interfaces.ITerritorized;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.model.zone.form.ZoneNPoly;
import org.l2j.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.model.zone.type.SpawnTerritory;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class SpawnsData extends GameXmlReader {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SpawnsData.class);

    private final List<SpawnTemplate> _spawns = new LinkedList<>();

    private SpawnsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/spawns.xsd");
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/spawns", true);
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _spawns.stream().flatMap(c -> c.getGroups().stream()).flatMap(c -> c.getSpawns().stream()).count() + " spawns");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "spawn", spawnNode ->
        {
            try {
                parseSpawn(spawnNode, f, _spawns);
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Error while processing spawn in file: " + f.getAbsolutePath(), e);
            }
        }));
    }

    /**
     * Initializing all spawns
     */
    public void init() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }

        LOGGER.info(getClass().getSimpleName() + ": Initializing spawns...");
        _spawns.stream().filter(SpawnTemplate::isSpawningByDefault).forEach(template ->
        {
            template.spawnAll(null);
            template.notifyActivate();
        });
        LOGGER.info(getClass().getSimpleName() + ": All spawns has been initialized!");
    }

    /**
     * Removing all spawns
     */
    public void despawnAll() {
        LOGGER.info(getClass().getSimpleName() + ": Removing all spawns...");
        _spawns.forEach(SpawnTemplate::despawnAll);
        LOGGER.info(getClass().getSimpleName() + ": All spawns has been removed!");
    }

    public List<SpawnTemplate> getSpawns() {
        return _spawns;
    }

    public List<SpawnTemplate> getSpawns(Predicate<SpawnTemplate> condition) {
        return _spawns.stream().filter(condition).collect(Collectors.toList());
    }

    public List<SpawnGroup> getGroupsByName(String groupName) {
        return _spawns.stream().filter(template -> (template.getName() != null) && groupName.equalsIgnoreCase(template.getName())).flatMap(template -> template.getGroups().stream()).collect(Collectors.toList());
    }

    public List<NpcSpawnTemplate> getNpcSpawns(Predicate<NpcSpawnTemplate> condition) {
        return _spawns.stream().flatMap(template -> template.getGroups().stream()).flatMap(group -> group.getSpawns().stream()).filter(condition).collect(Collectors.toList());
    }

    public void parseSpawn(Node spawnsNode, File file, List<SpawnTemplate> spawns) {
        final SpawnTemplate spawnTemplate = new SpawnTemplate(new StatsSet(parseAttributes(spawnsNode)), file);
        SpawnGroup defaultGroup = null;
        for (Node innerNode = spawnsNode.getFirstChild(); innerNode != null; innerNode = innerNode.getNextSibling()) {
            if ("territories".equalsIgnoreCase(innerNode.getNodeName())) {
                parseTerritories(innerNode, spawnTemplate.getFile(), spawnTemplate);
            } else if ("group".equalsIgnoreCase(innerNode.getNodeName())) {
                parseGroup(innerNode, spawnTemplate);
            } else if ("npc".equalsIgnoreCase(innerNode.getNodeName())) {
                if (defaultGroup == null) {
                    defaultGroup = new SpawnGroup(StatsSet.EMPTY_STATSET);
                }
                parseNpc(innerNode, spawnTemplate, defaultGroup);
            } else if ("parameters".equalsIgnoreCase(innerNode.getNodeName())) {
                parseParameters(spawnsNode, spawnTemplate);
            }
        }

        // One static group for all npcs outside group scope
        if (defaultGroup != null) {
            spawnTemplate.addGroup(defaultGroup);
        }
        spawns.add(spawnTemplate);
    }

    /**
     * @param innerNode
     * @param file
     * @param spawnTemplate
     */
    private void parseTerritories(Node innerNode, File file, ITerritorized spawnTemplate) {
        forEach(innerNode, XmlReader::isNode, territoryNode ->
        {
            final String name = parseString(territoryNode.getAttributes(), "name", file.getName() + "_" + (spawnTemplate.getTerritories().size() + 1));
            final int minZ = parseInteger(territoryNode.getAttributes(), "minZ");
            final int maxZ = parseInteger(territoryNode.getAttributes(), "maxZ");

            final List<Integer> xNodes = new ArrayList<>();
            final List<Integer> yNodes = new ArrayList<>();
            forEach(territoryNode, "node", node ->
            {
                xNodes.add(parseInteger(node.getAttributes(), "x"));
                yNodes.add(parseInteger(node.getAttributes(), "y"));
            });
            final int[] x = xNodes.stream().mapToInt(Integer::valueOf).toArray();
            final int[] y = yNodes.stream().mapToInt(Integer::valueOf).toArray();

            switch (territoryNode.getNodeName()) {
                case "territory": {
                    spawnTemplate.addTerritory(new SpawnTerritory(name, new ZoneNPoly(x, y, minZ, maxZ)));
                    break;
                }
                case "banned_territory": {
                    spawnTemplate.addBannedTerritory(new BannedSpawnTerritory(name, new ZoneNPoly(x, y, minZ, maxZ)));
                    break;
                }
            }
        });
    }

    private void parseGroup(Node n, SpawnTemplate spawnTemplate) {
        final SpawnGroup group = new SpawnGroup(new StatsSet(parseAttributes(n)));
        forEach(n, XmlReader::isNode, npcNode ->
        {
            switch (npcNode.getNodeName()) {
                case "territories": {
                    parseTerritories(npcNode, spawnTemplate.getFile(), group);
                    break;
                }
                case "npc": {
                    parseNpc(npcNode, spawnTemplate, group);
                    break;
                }
            }
        });
        spawnTemplate.addGroup(group);
    }

    /**
     * @param n
     * @param spawnTemplate
     * @param group
     */
    private void parseNpc(Node n, SpawnTemplate spawnTemplate, SpawnGroup group) {
        final NpcSpawnTemplate npcTemplate = new NpcSpawnTemplate(spawnTemplate, group, new StatsSet(parseAttributes(n)));
        final NpcTemplate template = NpcData.getInstance().getTemplate(npcTemplate.getId());
        if (template == null) {
            LOGGER.warn(": Requested spawn for non existing npc: " + npcTemplate.getId() + " in file: " + spawnTemplate.getFile().getName());
            return;
        }

        if (template.isType("L2Servitor") || template.isType("L2Pet")) {
            LOGGER.warn(": Requested spawn for " + template.getType() + " " + template.getName() + "(" + template.getId() + ") file: " + spawnTemplate.getFile().getName());
            return;
        }


        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("parameters".equalsIgnoreCase(d.getNodeName())) {
                parseParameters(d, npcTemplate);
            } else if ("minions".equalsIgnoreCase(d.getNodeName())) {
                parseMinions(d, npcTemplate);
            } else if ("locations".equalsIgnoreCase(d.getNodeName())) {
                parseLocations(d, npcTemplate);
            }
        }
        group.addSpawn(npcTemplate);
    }

    /**
     * @param n
     * @param npcTemplate
     */
    private void parseLocations(Node n, NpcSpawnTemplate npcTemplate) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if ("location".equalsIgnoreCase(d.getNodeName())) {
                final int x = parseInteger(d.getAttributes(), "x");
                final int y = parseInteger(d.getAttributes(), "y");
                final int z = parseInteger(d.getAttributes(), "z");
                final int heading = parseInteger(d.getAttributes(), "heading", 0);
                final double chance = parseDouble(d.getAttributes(), "chance");
                npcTemplate.addSpawnLocation(new ChanceLocation(x, y, z, heading, chance));
            }
        }
    }

    private void parseParameters(Node n, IParameterized<StatsSet> npcTemplate) {
        final Map<String, Object> params = parseParameters(n);
        npcTemplate.setParameters(!params.isEmpty() ? new StatsSet(Collections.unmodifiableMap(params)) : StatsSet.EMPTY_STATSET);
    }

    private void parseMinions(Node n, NpcSpawnTemplate npcTemplate) {
        forEach(n, "minion", minionNode ->
        {
            npcTemplate.addMinion(new MinionHolder(new StatsSet(parseAttributes(minionNode))));
        });
    }

    public static SpawnsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SpawnsData INSTANCE = new SpawnsData();
    }
}
