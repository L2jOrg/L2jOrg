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
package org.l2j.gameserver.engine.item;

import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class AttendanceEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceEngine.class);

    private final List<AttendanceItem> rewards = new ArrayList<>(28);
    private final List<AttendanceItem> vipRewards = new ArrayList<>(4);
    private int pcCafeMask;

    private AttendanceEngine() {

    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/attendance.xsd");
    }

    @Override
    public void load() {
        if (AttendanceSettings.enabled()) {
            pcCafeMask = 0;
            rewards.clear();
            parseDatapackFile("data/attendance.xml");
            LOGGER.info("Loaded {} rewards.", rewards.size() );
            LOGGER.info("Loaded {} vip rewards.", vipRewards.size() );
            releaseResources();
        } else {
            LOGGER.info("Disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final var listNode = doc.getFirstChild();

        for(var itemNode = listNode.getFirstChild(); nonNull(itemNode); itemNode = itemNode.getNextSibling() ) {
            parseItemNode(itemNode);
        }
    }

    private void parseItemNode(Node itemNode) {
        var attr = itemNode.getAttributes();
        var id = parseInt(attr, "id");

        if(nonNull(ItemEngine.getInstance().getTemplate(id))) {
            long count = parseLong(attr, "count");
            boolean highlight = parseBoolean(attr, "highlight");
            int cafePoints = parseInt(attr, "cafe-points");

            if("vip-item".equals(itemNode.getNodeName())) {
                byte vipLevel = parseByte(attr, "vip-level");
                vipRewards.add(new AttendanceItem(id, count, highlight, cafePoints, vipLevel));
            }  else {
                rewards.add(new AttendanceItem(id, count, highlight, cafePoints, (byte) 0));
                if(cafePoints > 0) {
                    pcCafeMask |= 1 << rewards.size();
                }
            }
        } else {
            LOGGER.warn("There is no Item with id {} does not exist.", id);
        }
    }

    public List<AttendanceItem> getRewards() {
        return rewards;
    }

    public List<AttendanceItem> getVipRewards() {
        return vipRewards;
    }

    public int getPcCafeMask() {
        return pcCafeMask;
    }

    public static void init() {
        getInstance().load();
    }

    public static AttendanceEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AttendanceEngine INSTANCE = new AttendanceEngine();
    }
}
