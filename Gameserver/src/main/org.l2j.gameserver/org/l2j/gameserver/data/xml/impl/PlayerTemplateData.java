package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.templates.L2PcTemplate;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Loads player's base stats.
 *
 * @author Forsaiken, Zoey76, GKR
 */
public final class PlayerTemplateData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerTemplateData.class);

    private final Map<ClassId, L2PcTemplate> _playerTemplates = new HashMap<>();

    private int _dataCount = 0;

    private PlayerTemplateData() {
        load();
    }

    @Override
    public void load() {
        _playerTemplates.clear();
        parseDatapackDirectory("data/stats/chars/baseStats", false);
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _playerTemplates.size() + " character templates.");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _dataCount + " level up gain records.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        int classId = 0;

        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("classId".equalsIgnoreCase(d.getNodeName())) {
                        classId = Integer.parseInt(d.getTextContent());
                    } else if ("staticData".equalsIgnoreCase(d.getNodeName())) {
                        final StatsSet set = new StatsSet();
                        set.set("classId", classId);
                        final List<Location> creationPoints = new ArrayList<>();

                        for (Node nd = d.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
                            // Skip odd nodes
                            if (nd.getNodeName().equals("#text")) {
                                continue;
                            }

                            if (nd.getChildNodes().getLength() > 1) {
                                for (Node cnd = nd.getFirstChild(); cnd != null; cnd = cnd.getNextSibling()) {
                                    // use L2CharTemplate(superclass) fields for male collision height and collision radius
                                    if (nd.getNodeName().equalsIgnoreCase("collisionMale")) {
                                        if (cnd.getNodeName().equalsIgnoreCase("radius")) {
                                            set.set("collision_radius", cnd.getTextContent());
                                        } else if (cnd.getNodeName().equalsIgnoreCase("height")) {
                                            set.set("collision_height", cnd.getTextContent());
                                        }
                                    }
                                    if ("node".equalsIgnoreCase(cnd.getNodeName())) {
                                        attrs = cnd.getAttributes();
                                        creationPoints.add(new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z")));
                                    } else if ("walk".equalsIgnoreCase(cnd.getNodeName())) {
                                        set.set("baseWalkSpd", cnd.getTextContent());
                                    } else if ("run".equalsIgnoreCase(cnd.getNodeName())) {
                                        set.set("baseRunSpd", cnd.getTextContent());
                                    } else if ("slowSwim".equals(cnd.getNodeName())) {
                                        set.set("baseSwimWalkSpd", cnd.getTextContent());
                                    } else if ("fastSwim".equals(cnd.getNodeName())) {
                                        set.set("baseSwimRunSpd", cnd.getTextContent());
                                    } else if (!cnd.getNodeName().equals("#text")) {
                                        set.set((nd.getNodeName() + cnd.getNodeName()), cnd.getTextContent());
                                    }
                                }
                            } else {
                                set.set(nd.getNodeName(), nd.getTextContent());
                            }
                        }
                        // calculate total pdef and mdef from parts
                        set.set("basePDef", (set.getInt("basePDefchest", 0) + set.getInt("basePDeflegs", 0) + set.getInt("basePDefhead", 0) + set.getInt("basePDeffeet", 0) + set.getInt("basePDefgloves", 0) + set.getInt("basePDefunderwear", 0) + set.getInt("basePDefcloak", 0) + set.getInt("basePDefhair", 0)));
                        set.set("baseMDef", (set.getInt("baseMDefrear", 0) + set.getInt("baseMDeflear", 0) + set.getInt("baseMDefrfinger", 0) + set.getInt("baseMDefrfinger", 0) + set.getInt("baseMDefneck", 0)));

                        _playerTemplates.put(ClassId.getClassId(classId), new L2PcTemplate(set, creationPoints));
                    } else if ("lvlUpgainData".equalsIgnoreCase(d.getNodeName())) {
                        for (Node lvlNode = d.getFirstChild(); lvlNode != null; lvlNode = lvlNode.getNextSibling()) {
                            if ("level".equalsIgnoreCase(lvlNode.getNodeName())) {
                                attrs = lvlNode.getAttributes();
                                final int level = parseInteger(attrs, "val");

                                for (Node valNode = lvlNode.getFirstChild(); valNode != null; valNode = valNode.getNextSibling()) {
                                    final String nodeName = valNode.getNodeName();

                                    if ((level < Config.PLAYER_MAXIMUM_LEVEL) && (nodeName.startsWith("hp") || nodeName.startsWith("mp") || nodeName.startsWith("cp")) && _playerTemplates.containsKey(ClassId.getClassId(classId))) {
                                        _playerTemplates.get(ClassId.getClassId(classId)).setUpgainValue(nodeName, level, Double.parseDouble(valNode.getTextContent()));
                                        _dataCount++;
                                    }
                                }
                            }
                        }
                        // TODO: Generate stats automatically.
                    }
                }
            }
        }
    }

    public L2PcTemplate getTemplate(ClassId classId) {
        return _playerTemplates.get(classId);
    }

    public L2PcTemplate getTemplate(int classId) {
        return _playerTemplates.get(ClassId.getClassId(classId));
    }

    public static PlayerTemplateData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerTemplateData INSTANCE = new PlayerTemplateData();
    }
}
