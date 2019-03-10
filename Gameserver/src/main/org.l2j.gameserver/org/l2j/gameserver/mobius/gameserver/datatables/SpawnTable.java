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
package org.l2j.gameserver.mobius.gameserver.datatables;

import com.l2jmobius.Config;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.mobius.gameserver.model.L2Spawn;
import org.l2j.gameserver.mobius.gameserver.model.L2World;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Spawn data retriever.
 *
 * @author Zoey76, Mobius
 */
public final class SpawnTable {
    private static final Logger LOGGER = Logger.getLogger(SpawnTable.class.getName());
    private static final Map<Integer, Set<L2Spawn>> _spawnTable = new ConcurrentHashMap<>();
    private static final String OTHER_XML_FOLDER = "data/spawns/Others";

    public static SpawnTable getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Gets the spawn data.
     *
     * @return the spawn data
     */
    public Map<Integer, Set<L2Spawn>> getSpawnTable() {
        return _spawnTable;
    }

    /**
     * Gets the spawns for the NPC Id.
     *
     * @param npcId the NPC Id
     * @return the spawn set for the given npcId
     */
    public Set<L2Spawn> getSpawns(int npcId) {
        return _spawnTable.getOrDefault(npcId, Collections.emptySet());
    }

    /**
     * Gets the spawn count for the given NPC ID.
     *
     * @param npcId the NPC Id
     * @return the spawn count
     */
    public int getSpawnCount(int npcId) {
        return getSpawns(npcId).size();
    }

    /**
     * Gets a spawn for the given NPC ID.
     *
     * @param npcId the NPC Id
     * @return a spawn for the given NPC ID or {@code null}
     */
    public L2Spawn getAnySpawn(int npcId) {
        return getSpawns(npcId).stream().findFirst().orElse(null);
    }

    /**
     * Adds a new spawn to the spawn table.
     *
     * @param spawn the spawn to add
     * @param store if {@code true} it'll be saved in the spawn XML files
     */
    public synchronized void addNewSpawn(L2Spawn spawn, boolean store) {
        addSpawn(spawn);

        if (store) {
            // Create output directory if it doesn't exist
            final File outputDirectory = new File(OTHER_XML_FOLDER);
            if (!outputDirectory.exists()) {
                boolean result = false;
                try {
                    outputDirectory.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    // empty
                }
                if (result) {
                    LOGGER.info(getClass().getSimpleName() + ": Created directory: " + OTHER_XML_FOLDER);
                }
            }

            // XML file for spawn
            final int x = ((spawn.getX() - L2World.MAP_MIN_X) >> 15) + L2World.TILE_X_MIN;
            final int y = ((spawn.getY() - L2World.MAP_MIN_Y) >> 15) + L2World.TILE_Y_MIN;
            final File spawnFile = new File(OTHER_XML_FOLDER + "/" + x + "_" + y + ".xml");

            // Write info to XML
            final String spawnId = String.valueOf(spawn.getId());
            final String spawnCount = String.valueOf(spawn.getAmount());
            final String spawnX = String.valueOf(spawn.getX());
            final String spawnY = String.valueOf(spawn.getY());
            final String spawnZ = String.valueOf(spawn.getZ());
            final String spawnHeading = String.valueOf(spawn.getHeading());
            final String spawnDelay = String.valueOf(spawn.getRespawnDelay() / 1000);
            if (spawnFile.exists()) // update
            {
                final File tempFile = new File(spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/') + ".tmp");
                try {
                    final BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                    String currentLine;
                    while ((currentLine = reader.readLine()) != null) {
                        if (currentLine.contains("</group>")) {
                            writer.write("			<npc id=\"" + spawnId + (spawn.getAmount() > 1 ? "\" count=\"" + spawnCount : "") + "\" x=\"" + spawnX + "\" y=\"" + spawnY + "\" z=\"" + spawnZ + (spawn.getHeading() > 0 ? "\" heading=\"" + spawnHeading : "") + "\" respawnTime=\"" + spawnDelay + "sec\" /> <!-- " + NpcData.getInstance().getTemplate(spawn.getId()).getName() + " -->" + Config.EOL);
                            writer.write(currentLine + Config.EOL);
                            continue;
                        }
                        writer.write(currentLine + Config.EOL);
                    }
                    writer.close();
                    reader.close();
                    spawnFile.delete();
                    tempFile.renameTo(spawnFile);
                } catch (Exception e) {
                    LOGGER.warning(getClass().getSimpleName() + ": Could not store spawn in the spawn XML files: " + e);
                }
            } else // new file
            {
                try {
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(spawnFile));
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Config.EOL);
                    writer.write("<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../../xsd/spawns.xsd\">" + Config.EOL);
                    writer.write("	<spawn name=\"" + x + "_" + y + "\">" + Config.EOL);
                    writer.write("		<group>" + Config.EOL);
                    writer.write("			<npc id=\"" + spawnId + (spawn.getAmount() > 1 ? "\" count=\"" + spawnCount : "") + "\" x=\"" + spawnX + "\" y=\"" + spawnY + "\" z=\"" + spawnZ + (spawn.getHeading() > 0 ? "\" heading=\"" + spawnHeading : "") + "\" respawnTime=\"" + spawnDelay + "sec\" /> <!-- " + NpcData.getInstance().getTemplate(spawn.getId()).getName() + " -->" + Config.EOL);
                    writer.write("		</group>" + Config.EOL);
                    writer.write("	</spawn>" + Config.EOL);
                    writer.write("</list>" + Config.EOL);
                    writer.close();
                    LOGGER.info(getClass().getSimpleName() + ": Created file: " + OTHER_XML_FOLDER + "/" + x + "_" + y + ".xml");
                } catch (Exception e) {
                    LOGGER.warning(getClass().getSimpleName() + ": Spawn " + spawn + " could not be added to the spawn XML files: " + e);
                }
            }
        }
    }

    /**
     * Delete an spawn from the spawn table.
     *
     * @param spawn  the spawn to delete
     * @param update if {@code true} the spawn XML files will be updated
     */
    public synchronized void deleteSpawn(L2Spawn spawn, boolean update) {
        if (!removeSpawn(spawn)) {
            return;
        }

        if (update) {
            final int x = ((spawn.getX() - L2World.MAP_MIN_X) >> 15) + L2World.TILE_X_MIN;
            final int y = ((spawn.getY() - L2World.MAP_MIN_Y) >> 15) + L2World.TILE_Y_MIN;
            final File spawnFile = spawn.getNpcSpawnTemplate() != null ? spawn.getNpcSpawnTemplate().getSpawnTemplate().getFile() : new File(OTHER_XML_FOLDER + "/" + x + "_" + y + ".xml");
            final File tempFile = new File(spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/') + ".tmp");
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
                final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                final String spawnId = String.valueOf(spawn.getId());
                final String spawnX = String.valueOf(spawn.getX());
                final String spawnY = String.valueOf(spawn.getY());
                final String spawnZ = String.valueOf(spawn.getZ());
                boolean found = false; // in XML you can have more than one spawn with same coords
                boolean isMultiLine = false; // in case spawn has more stats
                boolean lastLineFound = false; // used to check for empty file
                int lineCount = 0;
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    if (!found) {
                        if (isMultiLine) {
                            if (currentLine.contains("</npc>")) {
                                found = true;
                            }
                            continue;
                        }
                        if (currentLine.contains(spawnId) && currentLine.contains(spawnX) && currentLine.contains(spawnY) && currentLine.contains(spawnZ)) {
                            if (!currentLine.contains("/>") && !currentLine.contains("</npc>")) {
                                isMultiLine = true;
                            } else {
                                found = true;
                            }
                            continue;
                        }
                    }
                    writer.write(currentLine + Config.EOL);
                    if (currentLine.contains("</list>")) {
                        lastLineFound = true;
                    }
                    if (!lastLineFound) {
                        lineCount++;
                    }
                }
                writer.close();
                reader.close();
                spawnFile.delete();
                tempFile.renameTo(spawnFile);
                // Delete empty file
                if (lineCount < 7) {
                    LOGGER.info(getClass().getSimpleName() + ": Deleted empty file: " + spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/'));
                    spawnFile.delete();
                }
            } catch (Exception e) {
                LOGGER.warning(getClass().getSimpleName() + ": Spawn " + spawn + " could not be removed from the spawn XML files: " + e);
            }
        }
    }

    /**
     * Add a spawn to the spawn set if present, otherwise add a spawn set and add the spawn to the newly created spawn set.
     *
     * @param spawn the NPC spawn to add
     */
    private void addSpawn(L2Spawn spawn) {
        _spawnTable.computeIfAbsent(spawn.getId(), k -> ConcurrentHashMap.newKeySet(1)).add(spawn);
    }

    /**
     * Remove a spawn from the spawn set, if the spawn set is empty, remove it as well.
     *
     * @param spawn the NPC spawn to remove
     * @return {@code true} if the spawn was successfully removed, {@code false} otherwise
     */
    private boolean removeSpawn(L2Spawn spawn) {
        final Set<L2Spawn> set = _spawnTable.get(spawn.getId());
        if (set != null) {
            final boolean removed = set.remove(spawn);
            if (set.isEmpty()) {
                _spawnTable.remove(spawn.getId());
            }
            set.forEach(this::notifyRemoved);
            return removed;
        }
        notifyRemoved(spawn);
        return false;
    }

    private void notifyRemoved(L2Spawn spawn) {
        if ((spawn != null) && (spawn.getLastSpawn() != null) && (spawn.getNpcSpawnTemplate() != null)) {
            spawn.getNpcSpawnTemplate().notifyDespawnNpc(spawn.getLastSpawn());
        }
    }

    /**
     * Execute a procedure over all spawns.<br>
     * <font size="4" color="red">Do not use it!</font>
     *
     * @param function the function to execute
     * @return {@code true} if all procedures were executed, {@code false} otherwise
     */
    public boolean forEachSpawn(Function<L2Spawn, Boolean> function) {
        for (Set<L2Spawn> set : _spawnTable.values()) {
            for (L2Spawn spawn : set) {
                if (!function.apply(spawn)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class SingletonHolder {
        protected static final SpawnTable _instance = new SpawnTable();
    }
}
