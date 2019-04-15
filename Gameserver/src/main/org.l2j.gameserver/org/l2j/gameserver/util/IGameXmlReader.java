package org.l2j.gameserver.util;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;
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

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Interface for XML parsers.
 *
 * @author Zoey76
 */
public abstract class IGameXmlReader extends XmlReader {

    /**
     * Wrapper for {@link #parseFile(File)} method.
     *
     * @param path the relative path to the datapack root of the XML file to parse.
     */
    protected void parseDatapackFile(String path) {
        parseFile(getSettings(ServerSettings.class).dataPackDirectory().resolve(path).toFile());
    }

    /**
     * Wrapper for {@link #parseDirectory(File, boolean)}.
     *
     * @param path      the path to the directory where the XML files are
     * @param recursive parses all sub folders if there is
     * @return {@code false} if it fails to find the directory, {@code true} otherwise
     */
    protected boolean parseDatapackDirectory(String path, boolean recursive) {
        return parseDirectory(new File(Config.DATAPACK_ROOT, path), recursive);
    }

    /**
     * @param n
     * @return a map of parameters
     */
    protected Map<String, Object> parseParameters(Node n) {
        final Map<String, Object> parameters = new HashMap<>();
        for (Node parameters_node = n.getFirstChild(); parameters_node != null; parameters_node = parameters_node.getNextSibling()) {
            NamedNodeMap attrs = parameters_node.getAttributes();
            switch (parameters_node.getNodeName().toLowerCase()) {
                case "param": {
                    parameters.put(parseString(attrs, "name"), parseString(attrs, "value"));
                    break;
                }
                case "skill": {
                    parameters.put(parseString(attrs, "name"), new SkillHolder(parseInteger(attrs, "id"), parseInteger(attrs, "level")));
                    break;
                }
                case "location": {
                    parameters.put(parseString(attrs, "name"), new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z"), parseInteger(attrs, "heading", 0)));
                    break;
                }
                case "minions": {
                    final List<MinionHolder> minions = new ArrayList<>(1);
                    for (Node minions_node = parameters_node.getFirstChild(); minions_node != null; minions_node = minions_node.getNextSibling()) {
                        if (minions_node.getNodeName().equalsIgnoreCase("npc")) {
                            attrs = minions_node.getAttributes();
                            minions.add(new MinionHolder(parseInteger(attrs, "id"), parseInteger(attrs, "count"), parseInteger(attrs, "respawnTime"), parseInteger(attrs, "weightPoint")));
                        }
                    }

                    if (!minions.isEmpty()) {
                        parameters.put(parseString(parameters_node.getAttributes(), "name"), minions);
                    }
                    break;
                }
            }
        }
        return parameters;
    }

    protected Location parseLocation(Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final int x = parseInteger(attrs, "x");
        final int y = parseInteger(attrs, "y");
        final int z = parseInteger(attrs, "z");
        final int heading = parseInteger(attrs, "heading", 0);
        return new Location(x, y, z, heading);
    }
}
