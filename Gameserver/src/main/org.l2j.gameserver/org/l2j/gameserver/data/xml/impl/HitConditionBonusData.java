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

import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class load, holds and calculates the hit condition bonuses.
 *
 * @author Nik
 */
public final class HitConditionBonusData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitConditionBonusData.class);

    private int frontBonus = 0;
    private int sideBonus = 0;
    private int backBonus = 0;
    private int highBonus = 0;
    private int lowBonus = 0;
    private int darkBonus = 0;
    @SuppressWarnings("unused")
    private int rainBonus = 0;

    private HitConditionBonusData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/hitConditionBonus.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/stats/hitConditionBonus.xml");
        LOGGER.info("Loaded Hit Condition bonuses.");
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node d = doc.getFirstChild().getFirstChild(); d != null; d = d.getNextSibling()) {
            final NamedNodeMap attrs = d.getAttributes();
            switch (d.getNodeName()) {
                case "front": {
                    frontBonus = parseInteger(attrs, "val");
                    break;
                }
                case "side": {
                    sideBonus = parseInteger(attrs, "val");
                    break;
                }
                case "back": {
                    backBonus = parseInteger(attrs, "val");
                    break;
                }
                case "high": {
                    highBonus = parseInteger(attrs, "val");
                    break;
                }
                case "low": {
                    lowBonus = parseInteger(attrs, "val");
                    break;
                }
                case "dark": {
                    darkBonus = parseInteger(attrs, "val");
                    break;
                }
                case "rain": {
                    rainBonus = parseInteger(attrs, "val");
                    break;
                }
            }
        }
    }

    /**
     * Gets the condition bonus.
     *
     * @param attacker the attacking character.
     * @param target   the attacked character.
     * @return the bonus of the attacker against the target.
     */
    public double getConditionBonus(Creature attacker, Creature target) {
        double mod = 100;
        // Get high or low bonus
        if ((attacker.getZ() - target.getZ()) > 50) {
            mod += highBonus;
        } else if ((attacker.getZ() - target.getZ()) < -50) {
            mod += lowBonus;
        }

        // Get weather bonus
        if (WorldTimeController.getInstance().isNight()) {
            mod += darkBonus;
            // else if () No rain support yet.
            // chance += hitConditionBonus.rainBonus;
        }

        // Get side bonus
        switch (Position.getPosition(attacker, target)) {
            case SIDE: {
                mod += sideBonus;
                break;
            }
            case BACK: {
                mod += backBonus;
                break;
            }
            default: {
                mod += frontBonus;
                break;
            }
        }

        // If (mod / 100) is less than 0, return 0, because we can't lower more than 100%.
        return Math.max(mod / 100, 0);
    }

    public static HitConditionBonusData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final HitConditionBonusData INSTANCE = new HitConditionBonusData();
    }
}