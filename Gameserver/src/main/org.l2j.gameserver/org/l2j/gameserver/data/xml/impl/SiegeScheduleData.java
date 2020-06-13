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
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.SiegeScheduleDate;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class SiegeScheduleData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeScheduleData.class);

    private final List<SiegeScheduleDate> scheduleData = new ArrayList<>();

    private SiegeScheduleData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/siege-schedule.xsd");
    }

    @Override
    public synchronized void load() {
        scheduleData.clear();
        parseFile(new File("config/siege-schedule.xml"));
        LOGGER.info("Loaded: {}  siege schedulers.", scheduleData.size());
        if (scheduleData.isEmpty()) {
            scheduleData.add(new SiegeScheduleDate());
            LOGGER.info("Loaded: default siege schedulers.");
        }
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "schedule", scheduleNode -> {
            var attrs = scheduleNode.getAttributes();
            var day = parseEnum(attrs, DayOfWeek.class, "day", DayOfWeek.SUNDAY);
            scheduleData.add(new SiegeScheduleDate(day, parseInt(attrs, "hour"), parseInt(attrs, "max-concurrent")));
        }));
    }

    public List<SiegeScheduleDate> getScheduleDates() {
        return scheduleData;
    }

    public static SiegeScheduleData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SiegeScheduleData INSTANCE = new SiegeScheduleData();
    }
}
