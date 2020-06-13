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

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author Mobius
 */
public class AttendanceRewardData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceRewardData.class);
    private final List<ItemHolder> _rewards = new ArrayList<>();
    private int _rewardsCount = 0;

    private AttendanceRewardData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/AttendanceRewards.xsd");
    }

    @Override
    public void load() {
        if (getSettings(AttendanceSettings.class).enabled()) {
            _rewards.clear();
            parseDatapackFile("data/AttendanceRewards.xml");
            _rewardsCount = _rewards.size();
            LOGGER.info("Loaded {}  rewards.", _rewardsCount );
            releaseResources();
        } else {
            LOGGER.info("Disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "item", rewardNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(rewardNode));
            final int itemId = set.getInt("id");
            final int itemCount = set.getInt("count");
            if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                LOGGER.info(getClass().getSimpleName() + ": Item with id " + itemId + " does not exist.");
            } else {
                _rewards.add(new ItemHolder(itemId, itemCount));
            }
        }));
    }

    public List<ItemHolder> getRewards() {
        return _rewards;
    }

    public int getRewardsCount() {
        return _rewardsCount;
    }

    public static AttendanceRewardData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AttendanceRewardData INSTANCE = new AttendanceRewardData();
    }
}
