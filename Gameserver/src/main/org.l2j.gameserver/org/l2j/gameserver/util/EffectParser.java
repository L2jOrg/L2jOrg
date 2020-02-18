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
            levelInfo.computeIfAbsent(level, l -> new StatsSet()).set(node.getParentNode().getNodeName(), node.getTextContent());

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

    protected IntMap<StatsSet> parseEffectChildNodes(Node node, int startLevel, StatsSet staticStatSet) {
        IntMap<StatsSet> levelInfo = new HashIntMap<>();

        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if(isUnboundNode(child)) {
                parseNodeList(startLevel, staticStatSet, levelInfo, child);
            } else {
                parseEffectNode(child, levelInfo, startLevel, staticStatSet, false);
            }
        }
        return levelInfo;
    }

    protected void parseNodeList(int startLevel, StatsSet staticStatSet, IntMap<StatsSet> levelInfo, Node child) {
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
