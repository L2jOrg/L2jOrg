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
package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author DS, Sdw, UnAfraid
 * @author JoeAlisson
 */
public enum BaseStats {
    STR(Stat.STAT_STR),
    INT(Stat.STAT_INT),
    DEX(Stat.STAT_DEX),
    WIT(Stat.STAT_WIT),
    CON(Stat.STAT_CON),
    MEN(Stat.STAT_MEN);

    public static final int MAX_STAT_VALUE = 201;
    private static final BaseStats[] CACHE = BaseStats.values();

    private final double[] bonus = new double[MAX_STAT_VALUE];
    private final Stat stat;
    private int enhancementSkillId;
    private int enhancementFirstLevel;
    private int enhancementSecondLevel;
    private int enhancementThirdLevel;

    BaseStats(Stat stat) {
        this.stat = stat;
    }

    public static BaseStats valueOf(Stat stat) {
        for (BaseStats baseStat : CACHE) {
            if (baseStat.getStat() == stat) {
                return baseStat;
            }
        }
        throw new NoSuchElementException("Unknown base stat '" + stat + "' for enum BaseStats");
    }

    public Stat getStat() {
        return stat;
    }

    public int calcValue(Creature creature) {
        if (nonNull(creature)) {
            return (int) creature.getStats().getValue(stat);
        }
        return 0;
    }

    public double calcBonus(Creature creature) {
        if (nonNull(creature)) {
            final int value = calcValue(creature);
            if (value < 1) {
                return 1;
            }
            return bonus[value];
        }
        return 1;
    }

    void setValue(int index, double value) {
        bonus[index] = value;
    }

    public double getValue(int index) {
        return bonus[index];
    }

    public int getEnhancementSkillId() {
        return enhancementSkillId;
    }

    public int getEnhancementSkillLevel(double value) {
        if(value >= enhancementThirdLevel) {
            return 3;
        }

        if(value >= enhancementSecondLevel) {
            return 2;
        }

        if(value >= enhancementFirstLevel) {
            return 1;
        }
        return 0;
    }

    static {
        new GameXmlReader() {
            @Override
            protected Path getSchemaFilePath() {
                return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/statBonus.xsd");
            }

            @Override
            public void load() {
                parseDatapackFile("data/stats/statBonus.xml");
            }

            @Override
            public void parseDocument(Document doc, File f) {
                forEach(doc, "list", listNode -> forEach(listNode, "stat", statNode -> {
                    final var baseStat = parseEnum(statNode.getAttributes(), BaseStats.class, "type");
                    for(var node = statNode.getFirstChild(); nonNull(node); node = node.getNextSibling()){
                        switch (node.getNodeName()) {
                            case "enhancement" -> parseStatEnhancement(baseStat, node);
                            case "bonus" -> parseStatBonus(baseStat, node);
                        }
                    }
                }));
            }

            private void parseStatBonus(BaseStats baseStat, Node bonusNode) {
                forEach(bonusNode, "value", statValue -> {
                    final int index = parseInt(statValue.getAttributes(), "level");
                    final double bonus = Double.parseDouble(statValue.getTextContent());
                    baseStat.setValue(index, bonus);
                });
            }

            private void parseStatEnhancement(BaseStats baseStat, Node node) {
                final var attr = node.getAttributes();
                baseStat.enhancementSkillId = parseInt(attr, "skill-id");
                baseStat.enhancementFirstLevel = parseInt(attr, "first-level");
                baseStat.enhancementSecondLevel = parseInt(attr, "second-level");
                baseStat.enhancementThirdLevel = parseInt(attr, "third-level");
            }
        }.load();
    }
}