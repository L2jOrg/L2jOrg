package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * The residence functions data
 *
 * @author UnAfraid
 */
public final class ResidenceFunctionsData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceFunctionsData.class);
    private final Map<Integer, List<ResidenceFunctionTemplate>> _functions = new HashMap<>();

    private ResidenceFunctionsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/ResidenceFunctions.xsd");
    }

    @Override
    public synchronized void load() {
        _functions.clear();
        parseDatapackFile("data/ResidenceFunctions.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _functions.size() + " functions.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "function", func ->
        {
            final NamedNodeMap attrs = func.getAttributes();
            final StatsSet set = new StatsSet(HashMap::new);
            for (int i = 0; i < attrs.getLength(); i++) {
                final Node node = attrs.item(i);
                set.set(node.getNodeName(), node.getNodeValue());
            }
            forEach(func, "function", levelNode ->
            {
                final NamedNodeMap levelAttrs = levelNode.getAttributes();
                final StatsSet levelSet = new StatsSet(HashMap::new);
                levelSet.merge(set);
                for (int i = 0; i < levelAttrs.getLength(); i++) {
                    final Node node = levelAttrs.item(i);
                    levelSet.set(node.getNodeName(), node.getNodeValue());
                }
                final ResidenceFunctionTemplate template = new ResidenceFunctionTemplate(levelSet);
                _functions.computeIfAbsent(template.getId(), key -> new ArrayList<>()).add(template);
            });
        }));
    }

    /**
     * @param id
     * @param level
     * @return function template by id and level, null if not available
     */
    public ResidenceFunctionTemplate getFunction(int id, int level) {
        return _functions.getOrDefault(id, Collections.emptyList()).stream().filter(template -> template.getLevel() == level).findAny().orElse(null);
    }

    /**
     * @param id
     * @return function template by id, null if not available
     */
    public List<ResidenceFunctionTemplate> getFunctions(int id) {
        return _functions.get(id);
    }

    public static ResidenceFunctionsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ResidenceFunctionsData INSTANCE = new ResidenceFunctionsData();
    }
}
