/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.engine.mission;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class MissionData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MissionData.class);

    private final IntMap<IntMap<MissionPlayerData>> missionsData = new CHashIntMap<>();
    private final IntMap<List<MissionDataHolder>> missions = new HashIntMap<>();

    private boolean available;

    private MissionData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/mission.xsd");
    }

    @Override
    public void load() {
        missions.clear();
        parseDatapackFile("data/mission.xml");
        available = !missions.isEmpty();
        LOGGER.info("Loaded {} missions.",  missions.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "mission", missionNode -> {
            final StatsSet set = new StatsSet(parseAttributes(missionNode));

            final List<ItemHolder> items = new ArrayList<>(1);

            forEach(missionNode, "reward", itemNode -> {
                final int itemId = parseInteger(itemNode.getAttributes(), "id");
                final int itemCount = parseInteger(itemNode.getAttributes(), "count");
                items.add(new ItemHolder(itemId, itemCount));
            });

            set.set("rewards", items);

            final List<ClassId> classRestriction = new ArrayList<>(1);
            forEach(missionNode, "classes", classesNode -> {
                if(isNullOrEmpty(classesNode.getTextContent())) {
                    return;
                }
                classRestriction.addAll(Arrays.stream(classesNode.getTextContent().split(" ")).map(id -> ClassId.getClassId(Integer.parseInt(id))).collect(Collectors.toList()));
            });

            set.set("classRestriction", classRestriction);

            // Initial values in case handler doesn't exists
            set.set("handler", "");
            set.set("params", StatsSet.EMPTY_STATSET);

            // Parse handler and parameters
            forEach(missionNode, "handler", handlerNode -> {
                set.set("handler", parseString(handlerNode.getAttributes(), "name"));

                final StatsSet params = new StatsSet();
                set.set("params", params);
                forEach(handlerNode, "param", paramNode -> params.set(parseString(paramNode.getAttributes(), "name"), paramNode.getTextContent()));
            });

            final MissionDataHolder holder = new MissionDataHolder(set);
            missions.computeIfAbsent(holder.getId(), k -> new ArrayList<>()).add(holder);
        }));
    }

    public Collection<MissionDataHolder> getMissions() {
        //@formatter:off
        return missions.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Collection<MissionDataHolder> getMissions(Player player) {
        //@formatter:off
        return missions.values()
                .stream()
                .flatMap(List::stream)
                .filter(o -> o.isDisplayable(player))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public boolean isCompleted(Player player, int missionId) {
        var mission = missions.get(missionId);
        return nonNull(mission) && mission.stream().anyMatch(data -> data.isCompleted(player));
    }

    public int getAvailableMissionCount(Player player) {
        return (int) missions.values().stream().flatMap(List::stream).filter(mission -> mission.isAvailable(player)).count();
    }

    public Collection<MissionDataHolder> getMissions(int id) {
        return missions.get(id);
    }

    public void clearMissionData(int id) {
        missionsData.values().forEach(map -> map.remove(id));
    }

    public void storeMissionData(int missionId, MissionPlayerData data) {
        if(nonNull(data)) {
            missionsData.computeIfAbsent(data.getObjectId(), id -> new CHashIntMap<>()).putIfAbsent(missionId, data);
        }
    }

    public IntMap<MissionPlayerData> getStoredMissionData(Player player) {
        return missionsData.computeIfAbsent(player.getObjectId(), id -> new CHashIntMap<>());
    }

    public boolean isAvailable() {
        return available;
    }

    public static void init() {
        getInstance().load();
    }

    public static MissionData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MissionData INSTANCE = new MissionData();
    }
}
