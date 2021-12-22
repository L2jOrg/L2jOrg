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
package org.l2j.gameserver.datatables;

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.data.database.dao.SchemeBufferDAO;
import org.l2j.gameserver.data.database.data.SchemeBufferData;
import org.l2j.gameserver.model.holders.BuffSkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * This class loads available skills and stores players' buff schemes into _schemesTable.
 */
public class SchemeBufferTable extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemeBufferTable.class);
    private static byte maxSchemes = 4;
    private static int staticCost = -1;

    private final IntMap<Map<String, IntList>> schemes = new CHashIntMap<>();
    private final IntMap<BuffSkillHolder> availableBuffs = new LinkedHashIntMap<>();

    private SchemeBufferTable() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/SchemeBufferSkills.xsd");
    }

    @Override
    public void load() {
        getDAO(SchemeBufferDAO.class).loadAll(this::loadBufferSchema);
        parseDatapackFile("data/SchemeBufferSkills.xml");
        releaseResources();
        LOGGER.info("Loaded {} players schemes and {} available buffs.", schemes.size(), availableBuffs.size());
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        var list = doc.getFirstChild();
        for (var n = list.getFirstChild(); n != null; n = n.getNextSibling()) {
            if("config".equals(n.getNodeName())) {
                parseConfig(n);
            } else if("category".equals(n.getNodeName())) {
                parseCategory(n);
            }
        }
    }

    private void parseCategory(Node node) {
        var type = parseString(node.getAttributes(), "type");
        for (var n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            if("buff".equals(n.getNodeName())) {
                var attrs = n.getAttributes();
                var skillId = parseInt(attrs, "id");
                var price = parseInt(attrs, "price");
                var desc = parseString(attrs, "desc");
                availableBuffs.put(skillId, new BuffSkillHolder(skillId, price, type, desc));
            }
        }
    }

    private void parseConfig(Node node) {
        var attr = node.getAttributes();
        setMaxSchemas(parseByte(attr, "max-schemas"));
        setStaticCost(parseInt(attr, "static-cost"));
    }

    private void loadBufferSchema(ResultSet resultSet) {
        try {
            while(resultSet.next()) {
                final int objectId = resultSet.getInt("object_id");

                final String schemeName = resultSet.getString("scheme_name");
                final String[] skills = resultSet.getString("skills").split(",");

                IntList schemeList = new ArrayIntList();

                for (String skill : skills) {
                    // Don't feed the skills list if the list is empty.
                    if (skill.isEmpty()) {
                        break;
                    }

                    schemeList.add(Integer.parseInt(skill));
                }

                setScheme(objectId, schemeName, schemeList);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public void saveSchemes() {
        getDAO(SchemeBufferDAO.class).deleteAll();
        List<SchemeBufferData> data = new ArrayList<>(schemes.size() << 1);

        for(var entry : schemes.entrySet()) {
            for (var schema : entry.getValue().entrySet()) {
                data.add(SchemeBufferData.of(entry.getKey(), schema.getKey(), intListToString(schema.getValue())));
            }
        }

        getDAO(SchemeBufferDAO.class).save(data);
    }

    private String intListToString(IntList list) {
        final StringBuilder sb = new StringBuilder();
        final var it = list.iterator();
        while(it.hasNext()) {
            sb.append(it.nextInt()).append(",");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public void setScheme(int playerId, String schemeName, IntList list) {
        if (!schemes.containsKey(playerId)) {
            schemes.put(playerId, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        } else if (schemes.get(playerId).size() >= maxSchemes) {
            return;
        }

        schemes.get(playerId).put(schemeName, list);
    }

    /**
     * @param playerId : The player objectId to check.
     * @return the list of schemes for a given player.
     */
    public Map<String, IntList> getPlayerSchemes(int playerId) {
        return schemes.get(playerId);
    }

    /**
     * @param playerId   : The player objectId to check.
     * @param schemeName : The scheme name to check.
     * @return the List holding skills for the given scheme name and player, or null (if scheme or player isn't registered).
     */
    public IntList getScheme(int playerId, String schemeName) {
        if ((schemes.get(playerId) == null) || (schemes.get(playerId).get(schemeName) == null)) {
            return Containers.emptyList();
        }

        return schemes.get(playerId).get(schemeName);
    }

    /**
     * @param groupType : The type of skills to return.
     * @return a list of skills ids based on the given groupType.
     */
    public List<Integer> getSkillsIdsByType(String groupType) {
        List<Integer> skills = new ArrayList<>();
        for (BuffSkillHolder skill : availableBuffs.values()) {
            if (skill.type().equalsIgnoreCase(groupType)) {
                skills.add(skill.id());
            }
        }
        return skills;
    }

    /**
     * @return a list of all buff types available.
     */
    public List<String> getSkillTypes() {
        List<String> skillTypes = new ArrayList<>();
        for (BuffSkillHolder skill : availableBuffs.values()) {
            if (!skillTypes.contains(skill.type())) {
                skillTypes.add(skill.type());
            }
        }
        return skillTypes;
    }

    public BuffSkillHolder getAvailableBuff(int skillId) {
        return availableBuffs.get(skillId);
    }

    private static synchronized void setMaxSchemas(byte value) {
        maxSchemes = value;
    }

    public static byte maxSchemes() {
        return maxSchemes;
    }

    private static synchronized void setStaticCost(int value) {
        staticCost = value;
    }
    public static int staticCost() {
        return staticCost;
    }

    public static void init() {
        getInstance().load();
    }

    public static SchemeBufferTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SchemeBufferTable INSTANCE = new SchemeBufferTable();
    }
}