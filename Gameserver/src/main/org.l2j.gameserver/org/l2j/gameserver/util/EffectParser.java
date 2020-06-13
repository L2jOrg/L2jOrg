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
package org.l2j.gameserver.util;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public abstract class EffectParser extends GameXmlReader {

    protected void parseEffectNode(Node node, IntMap<StatsSet> levelInfo, int startLevel, StatsSet staticStatSet, boolean forceLevel) {
        var attr = node.getAttributes();
        if(nonNull(attr) && nonNull(attr.getNamedItem("initial"))) {
            levelInfo.computeIfAbsent(startLevel, l -> new StatsSet()).set(node.getNodeName(), parseString(attr, "initial"));
            for (var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                parseEffectNode(child, levelInfo, startLevel, staticStatSet, forceLevel);
            }

        } else if("value".equals(node.getNodeName())) {
            var level = parseInt(node.getAttributes(), "level");
            var attributeName = node.getParentNode().getNodeName();
            levelInfo.computeIfAbsent(level, l -> new StatsSet()).set(attributeName, node.getTextContent());

            if(level > startLevel && !levelInfo.getOrDefault(level -1, StatsSet.EMPTY_STATSET).contains(attributeName)) {
                var previous = level -1;
                while (previous >= startLevel) {
                    if(levelInfo.getOrDefault(previous, StatsSet.EMPTY_STATSET).contains(attributeName)) {
                        var value = levelInfo.get(previous).getString(attributeName);
                        for (int i = previous + 1; i < level ; i++) {
                            levelInfo.computeIfAbsent(i, l -> new StatsSet()).set(attributeName, value);
                        }
                        break;
                    }
                    previous--;
                }
            }

        } else if(nonNull(attr) && nonNull(attr.getNamedItem("level"))) {
            var level = parseInt(attr, "level");
            levelInfo.computeIfAbsent(level, l -> new StatsSet()).merge(parseAttributes(node));
            if(node.hasChildNodes()) {
                for (var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                    parseEffectNode(child, levelInfo, level, staticStatSet, true);
                }
            } else {
                levelInfo.computeIfAbsent(level, l -> new StatsSet()).set(node.getNodeName(), node.getTextContent());
            }
        } else  {
            if(node.hasAttributes()) {
                var parsedAttr = parseAttributes(node);
                if(forceLevel) {
                    levelInfo.computeIfAbsent(startLevel, l -> new StatsSet()).merge(parsedAttr);
                } else {
                    staticStatSet.merge(parsedAttr);
                }
            }
            if(node.hasChildNodes()) {
                for (var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                    if("#text".equals(child.getNodeName())) {
                        if(forceLevel) {
                            levelInfo.computeIfAbsent(startLevel, l -> new StatsSet()).set(node.getNodeName(), child.getNodeValue());
                        } else {
                            staticStatSet.set(node.getNodeName(), child.getNodeValue());
                        }
                    } else {
                        parseEffectNode(child, levelInfo, startLevel, staticStatSet, forceLevel);
                    }
                }
            } else {
                if(forceLevel) {
                    levelInfo.computeIfAbsent(startLevel, l -> new StatsSet()).set(node.getNodeName(), node.getTextContent());
                } else {
                    staticStatSet.set(node.getNodeName(), node.getTextContent());
                }
            }
        }
    }

    protected IntMap<StatsSet> parseEffectChildNodes(Node node, int startLevel, int stopLevel, StatsSet staticStatSet) {
        IntMap<StatsSet> levelInfo = new HashIntMap<>();

        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if(isUnboundNode(child)) {
                parseNodeList(startLevel, stopLevel, staticStatSet, levelInfo, child);
            } else {
                parseEffectNode(child, levelInfo, startLevel, staticStatSet, false);

                if(node.getChildNodes().getLength() > 1) {
                    if(!levelInfo.getOrDefault(stopLevel, StatsSet.EMPTY_STATSET).contains(child.getNodeName()) && levelInfo.getOrDefault(startLevel, StatsSet.EMPTY_STATSET).contains(child.getNodeName())) {
                        var previous = stopLevel -1;
                        while (previous >= startLevel) {
                            if(levelInfo.getOrDefault(previous, StatsSet.EMPTY_STATSET).contains(child.getNodeName())) {
                                var value = levelInfo.get(previous).getString(child.getNodeName());
                                for (int i = previous + 1; i <= stopLevel ; i++) {
                                    levelInfo.computeIfAbsent(i, l -> new StatsSet()).set(child.getNodeName(), value);
                                }
                                break;
                            }
                            previous--;
                        }
                    }
                }
            }
        }
        return levelInfo;
    }

    protected void parseNodeList(int startLevel, int stopLevel, StatsSet staticStatSet, IntMap<StatsSet> levelInfo, Node child) {
        var childStats = new StatsSet(parseAttributes(child));

        var forceLevel = childStats.contains("level");
        var childLevel = childStats.getInt("level", startLevel);

        var childKey = child.getNodeName() + child.hashCode();

        if(child.hasChildNodes()) {
            IntMap<StatsSet> childLevelInfo = new HashIntMap<>();

            for (var n = child.getFirstChild(); nonNull(n); n = n.getNextSibling()) {
                parseEffectNode(n, childLevelInfo, childLevel, childStats, false);
            }

            if(!childLevelInfo.isEmpty()) {
                for (var entry : childLevelInfo.entrySet()) {
                    var stats = entry.getValue();
                    var level = entry.getKey();
                    stats.merge(childStats);
                    levelInfo.computeIfAbsent(level, i -> new StatsSet()).set(childKey, stats);
                }
                if(childLevelInfo.size() < stopLevel) {
                    var levelBase =  childLevelInfo.keySet().stream().max().orElse(0);
                    for (var i = levelBase + 1; i <= stopLevel; i++) {
                        levelInfo.computeIfAbsent(i, level -> new StatsSet()).set(childKey, childLevelInfo.get(levelBase));
                    }

                }
            } else if(forceLevel) {
                levelInfo.computeIfAbsent(childLevel, i -> new StatsSet()).set(childKey, childStats);
            } else {
                staticStatSet.set(childKey, childStats);
            }
        } else if(forceLevel){
            levelInfo.computeIfAbsent(childLevel, i -> new StatsSet()).set(childKey, childStats);
        } else {
            staticStatSet.set(childKey, childStats);
        }
    }

    protected boolean isUnboundNode(Node child) {
        return (nonNull(child.getNextSibling()) && child.getNextSibling().getNodeName().equals(child.getNodeName())) || ( nonNull(child.getPreviousSibling()) && !child.getPreviousSibling().equals(child) && child.getPreviousSibling().getNodeName().equals(child.getNodeName()) );
    }
}
