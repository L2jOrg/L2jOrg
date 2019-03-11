package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.actor.transform.TransformLevelData;
import org.l2j.gameserver.model.actor.transform.TransformTemplate;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Transformation data.
 *
 * @author UnAfraid
 */
public final class TransformData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(TransformData.class.getName());

    private final Map<Integer, Transform> _transformData = new HashMap<>();

    protected TransformData() {
        load();
    }

    public static TransformData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public synchronized void load() {
        _transformData.clear();
        parseDatapackDirectory("data/stats/transformations", false);
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _transformData.size() + " transform templates.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("transform".equalsIgnoreCase(d.getNodeName())) {
                        NamedNodeMap attrs = d.getAttributes();
                        final StatsSet set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            final Node att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        final Transform transform = new Transform(set);
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            final boolean isMale = "Male".equalsIgnoreCase(cd.getNodeName());
                            if ("Male".equalsIgnoreCase(cd.getNodeName()) || "Female".equalsIgnoreCase(cd.getNodeName())) {
                                TransformTemplate templateData = null;
                                for (Node z = cd.getFirstChild(); z != null; z = z.getNextSibling()) {
                                    switch (z.getNodeName()) {
                                        case "common": {
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                switch (s.getNodeName()) {
                                                    case "base":
                                                    case "stats":
                                                    case "defense":
                                                    case "magicDefense":
                                                    case "collision":
                                                    case "moving": {
                                                        attrs = s.getAttributes();
                                                        for (int i = 0; i < attrs.getLength(); i++) {
                                                            final Node att = attrs.item(i);
                                                            set.set(att.getNodeName(), att.getNodeValue());
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                            templateData = new TransformTemplate(set);
                                            transform.setTemplate(isMale, templateData);
                                            break;
                                        }
                                        case "skills": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("skill".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int skillId = parseInteger(attrs, "id");
                                                    final int skillLevel = parseInteger(attrs, "level");
                                                    templateData.addSkill(new SkillHolder(skillId, skillLevel));
                                                }
                                            }
                                            break;
                                        }
                                        case "actions": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            set.set("actions", z.getTextContent());
                                            final int[] actions = set.getIntArray("actions", " ");
                                            templateData.setBasicActionList(new ExBasicActionList(actions));
                                            break;
                                        }
                                        case "additionalSkills": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("skill".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int skillId = parseInteger(attrs, "id");
                                                    final int skillLevel = parseInteger(attrs, "level");
                                                    final int minLevel = parseInteger(attrs, "minLevel");
                                                    templateData.addAdditionalSkill(new AdditionalSkillHolder(skillId, skillLevel, minLevel));
                                                }
                                            }
                                            break;
                                        }
                                        case "items": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("item".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int itemId = parseInteger(attrs, "id");
                                                    final boolean allowed = parseBoolean(attrs, "allowed");
                                                    templateData.addAdditionalItem(new AdditionalItemHolder(itemId, allowed));
                                                }
                                            }
                                            break;
                                        }
                                        case "levels": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }

                                            final StatsSet levelsSet = new StatsSet();
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("level".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    for (int i = 0; i < attrs.getLength(); i++) {
                                                        final Node att = attrs.item(i);
                                                        levelsSet.set(att.getNodeName(), att.getNodeValue());
                                                    }
                                                }
                                            }
                                            templateData.addLevelData(new TransformLevelData(levelsSet));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        _transformData.put(transform.getId(), transform);
                    }
                }
            }
        }
    }

    public Transform getTransform(int id) {
        return _transformData.get(id);
    }

    private static class SingletonHolder {
        protected static final TransformData _instance = new TransformData();
    }
}
