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
package org.l2j.gameserver.util;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for XML parsers.
 *
 * @author Zoey76
 */
public abstract class GameXmlReader extends XmlReader {

    protected static final String ATTR_ID = "id";
    protected static final String ATTR_VALUE = "value";

    /**
     * Wrapper for {@link #parseFile(String)} method.
     *
     * @param path the relative path to the datapack root of the XML file to parse.
     */
    protected void parseDatapackFile(String path) {
        parseFile(ServerSettings.dataPackDirectory().resolve(path).toString());
    }

    /**
     * Wrapper for {@link #parseDirectory(File, boolean)}.
     *
     * @param path      the path to the directory where the XML files are
     * @param recursive parses all sub folders if there is
     * @return {@code false} if it fails to find the directory, {@code true} otherwise
     */
    protected boolean parseDatapackDirectory(String path, boolean recursive) {
        return parseDirectory(new File(ServerSettings.dataPackDirectory().toFile(), path), recursive);
    }

    protected Map<String, Object> parseParameters(Node n) {
        final Map<String, Object> parameters = new HashMap<>();
        for (Node parameters_node = n.getFirstChild(); parameters_node != null; parameters_node = parameters_node.getNextSibling()) {
            NamedNodeMap attrs = parameters_node.getAttributes();
            switch (parameters_node.getNodeName().toLowerCase()) {
                case "param" -> parameters.put(parseString(attrs, "name"), parseString(attrs, "value"));
                case "skill" -> parameters.put(parseString(attrs, "name"), new SkillHolder(parseInt(attrs, "id"), parseInt(attrs, "level")));
                case "location" -> parameters.put(parseString(attrs, "name"), new Location(parseInt(attrs, "x"), parseInt(attrs, "y"), parseInt(attrs, "z"), parseInt(attrs, "heading", 0)));
                case "minions" -> parseMinions(parameters, parameters_node);
            }
        }
        return parameters;
    }

    private void parseMinions(Map<String, Object> parameters, Node node) {
        NamedNodeMap attrs;
        final List<MinionHolder> minions = new ArrayList<>(1);
        for (Node minions_node = node.getFirstChild(); minions_node != null; minions_node = minions_node.getNextSibling()) {
            if (minions_node.getNodeName().equalsIgnoreCase("npc")) {
                attrs = minions_node.getAttributes();
                minions.add(new MinionHolder(parseInt(attrs, "id"), parseInt(attrs, "count")));
            }
        }

        if (!minions.isEmpty()) {
            parameters.put(parseString(node.getAttributes(), "name"), minions);
        }
    }

    public Location parseLocation(Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final int x = parseInt(attrs, "x");
        final int y = parseInt(attrs, "y");
        final int z = parseInt(attrs, "z");
        final int heading = parseInt(attrs, "heading", 0);
        return new Location(x, y, z, heading);
    }

    public ItemHolder parseItemHolder(Node n) {
        final var attrs = n.getAttributes();
        return new ItemHolder(parseInt(attrs, "id"), parseLong(attrs, "count"), parseInt(attrs, "enchant", 0));
    }

    public Skill parseSkillInfo(Node node) {
        final var attrs = node.getAttributes();
        return SkillEngine.getInstance().getSkill(parseInt(attrs, "id"), parseInt(attrs, "level"));
    }
}
