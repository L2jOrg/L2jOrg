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
package org.l2j.gameserver.datatables;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.world.MapRegionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Objects.isNull;

/**
 * Spawn data retriever.
 *
 * @author Zoey76, Mobius
 * @author JoeAlisson
 * TODO move edited spawns to Database.
 */
public final class SpawnTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnTable.class);
    private static final IntMap<Set<Spawn>> _spawnTable = new CHashIntMap<>();
    private static final String CUSTOM_XML_FOLDER = "data/spawns/custom/";

    private SpawnTable() {}

    /**
     * Gets the spawns for the NPC Id.
     *
     * @param npcId the NPC Id
     * @return the spawn set for the given npcId
     */
    public Set<Spawn> getSpawns(int npcId) {
        return _spawnTable.getOrDefault(npcId, Collections.emptySet());
    }

    /**
     * Gets a spawn for the given NPC ID.
     *
     * @param npcId the NPC Id
     * @return a spawn for the given NPC ID or {@code null}
     */
    public Spawn getAnySpawn(int npcId) {
        return getSpawns(npcId).stream().findFirst().orElse(null);
    }

    /**
     * Adds a new spawn to the spawn table.
     *
     * @param spawn the spawn to add
     * @param store if {@code true} it'll be saved in the spawn XML files
     */
    public synchronized void addNewSpawn(Spawn spawn, boolean store) {
        addSpawn(spawn);

        if (store) {
            try {
                storeNewSpawn(spawn);
            } catch (IOException e) {
                LOGGER.error("Spawn {} couldn't be created ", spawn, e);
            }
        }
    }

    private void storeNewSpawn(Spawn spawn) throws IOException {
        Path outputFolder = ServerSettings.dataPackDirectory().resolve(CUSTOM_XML_FOLDER);
        Files.createDirectories(outputFolder);

        final int x = MapRegionManager.getInstance().getMapRegionX(spawn.getX());
        final int y = MapRegionManager.getInstance().getMapRegionY(spawn.getY());

        var filePath = outputFolder.resolve(x + "_" + y + ".xml");

        if (Files.exists(filePath)) {
            addSpawnInFile(spawn, filePath);
        } else {
            createSpawnFile(spawn, x, y, filePath);
        }
    }

    private synchronized void createSpawnFile(Spawn spawn, int x, int y, Path filePath) {
        try(final var writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE_NEW)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
            writer.write("<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../../xsd/spawns.xsd\">" + System.lineSeparator());
            writer.write("	<spawn name=\"" + x + "_" + y + "\">" + System.lineSeparator());
            writer.write("		<group>" + System.lineSeparator());
            writeSpawnInfo(spawn, writer);
            writer.write("		</group>" + System.lineSeparator());
            writer.write("	</spawn>" + System.lineSeparator());
            writer.write("</list>" + System.lineSeparator());
            writer.close();
            LOGGER.info("Created file: {}", filePath);
        } catch (Exception e) {
            LOGGER.warn("Spawn {} could not be added to the spawn XML files: ", spawn,  e);
        }
    }

    private void writeSpawnInfo(Spawn spawn, BufferedWriter writer) throws IOException {
        writer.write("			<npc id=\"" + spawn.getId() + (spawn.getAmount() > 1 ? "\" count=\"" + spawn.getAmount() : "")
                + "\" x=\"" + spawn.getX() + "\" y=\"" + spawn.getY() + "\" z=\"" + spawn.getZ() + (spawn.getHeading() > 0 ? "\" heading=\"" + spawn.getHeading() : "")
                + "\" respawnTime=\"" + (spawn.getRespawnDelay() / 1000) + "sec\" /> <!-- " + NpcData.getInstance().getTemplate(spawn.getId()).getName() + " -->" + System.lineSeparator());
    }

    private synchronized void addSpawnInFile(Spawn spawn, Path filePath) throws IOException {
        final var tmpFilePath = filePath.resolveSibling(filePath.getFileName().toString() + ".tmp");
        Files.move(filePath, tmpFilePath);

        try(final var reader = Files.newBufferedReader(tmpFilePath);
            final var writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE_NEW)) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains("</group>")) {
                    writeSpawnInfo(spawn, writer);
                    writer.write(currentLine + System.lineSeparator());
                    continue;
                }
                writer.write(currentLine + System.lineSeparator());
            }
            Files.delete(tmpFilePath);
        } catch (Exception e) {
            LOGGER.warn("Could not store spawn in the spawn XML files {}", filePath, e);
        }
    }

    /**
     * Delete an spawn from the spawn table.
     *
     * @param spawn  the spawn to delete
     * @param update if {@code true} the spawn XML files will be updated
     */
    public void deleteSpawn(Spawn spawn, boolean update) {
        if (!removeSpawn(spawn)) {
            return;
        }

        if (update) {
            try {
                removeSpawnFromFile(spawn);
            } catch (IOException e) {
                LOGGER.warn("Couldn't remove spawn from file", e);
            }
        }
    }

    private synchronized void removeSpawnFromFile(Spawn spawn) throws IOException {
        Path filePath = spawnFilePath(spawn);

        if(Files.notExists(filePath)) {
            LOGGER.warn("Spawn file not found {}", filePath);
            return;
        }
        
        final var tmpPath = filePath.resolveSibling(filePath.getFileName().toString() + ".tmp");
        Files.move(filePath, tmpPath);
        try(final var reader = Files.newBufferedReader(tmpPath);
            final var writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE_NEW)) {

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
                writer.write(currentLine + System.lineSeparator());
                if (currentLine.contains("</list>")) {
                    lastLineFound = true;
                }
                if (!lastLineFound) {
                    lineCount++;
                }
            }
            Files.delete(tmpPath);
            if (lineCount < 7) {
                LOGGER.info("Deleted empty file {}", filePath);
                Files.delete(filePath);
            }
        } catch (Exception e) {
            LOGGER.warn("Spawn {} could not be removed from the spawn XML file", spawn, e);
        }
    }

    private Path spawnFilePath(Spawn spawn) {
        Path filePath;
        if(isNull(spawn.getNpcSpawnTemplate())) {
            final int x = MapRegionManager.getInstance().getMapRegionX(spawn.getX());
            final int y = MapRegionManager.getInstance().getMapRegionY(spawn.getY());
            filePath = ServerSettings.dataPackDirectory().resolve(CUSTOM_XML_FOLDER + x + "_" + y + ".xml");
        } else {
            filePath = ServerSettings.dataPackDirectory().resolve(spawn.getNpcSpawnTemplate().getSpawnTemplate().getFilePath());
        }
        return filePath;
    }

    /**
     * Add a spawn to the spawn set if present, otherwise add a spawn set and add the spawn to the newly created spawn set.
     *
     * @param spawn the NPC spawn to add
     */
    private void addSpawn(Spawn spawn) {
        _spawnTable.computeIfAbsent(spawn.getId(), k -> ConcurrentHashMap.newKeySet(1)).add(spawn);
    }

    /**
     * Remove a spawn from the spawn set, if the spawn set is empty, remove it as well.
     *
     * @param spawn the NPC spawn to remove
     * @return {@code true} if the spawn was successfully removed, {@code false} otherwise
     */
    private boolean removeSpawn(Spawn spawn) {
        final Set<Spawn> set = _spawnTable.get(spawn.getId());
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

    private void notifyRemoved(Spawn spawn) {
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
    public boolean forEachSpawn(Function<Spawn, Boolean> function) {
        for (Set<Spawn> set : _spawnTable.values()) {
            for (Spawn spawn : set) {
                if (!function.apply(spawn)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static SpawnTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SpawnTable INSTANCE = new SpawnTable();
    }
}
