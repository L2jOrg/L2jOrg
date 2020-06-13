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

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Loads the the list of classes and it's info.
 *
 * @author Zoey76
 */
public final class ClassListData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassListData.class);

    private final Map<ClassId, ClassInfo> _classData = new HashMap<>();

    private ClassListData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/classList.xsd");
    }

    @Override
    public void load() {
        _classData.clear();
        parseDatapackFile("data/stats/chars/classList.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _classData.size() + " Class data.");
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attr;
        ClassId classId;
        String className;
        ClassId parentClassId;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    attrs = d.getAttributes();
                    if ("class".equals(d.getNodeName())) {
                        attr = attrs.getNamedItem("classId");
                        classId = ClassId.getClassId(parseInteger(attr));
                        attr = attrs.getNamedItem("name");
                        className = attr.getNodeValue();
                        attr = attrs.getNamedItem("parentClassId");
                        parentClassId = (attr != null) ? ClassId.getClassId(parseInteger(attr)) : null;
                        _classData.put(classId, new ClassInfo(classId, className, parentClassId));
                    }
                }
            }
        }
    }

    /**
     * Gets the class list.
     *
     * @return the complete class list.
     */
    public Map<ClassId, ClassInfo> getClassList() {
        return _classData;
    }

    /**
     * Gets the class info.
     *
     * @param classId the class Id.
     * @return the class info related to the given {@code classId}.
     */
    public ClassInfo getClass(ClassId classId) {
        return _classData.get(classId);
    }

    /**
     * Gets the class info.
     *
     * @param classId the class Id as integer.
     * @return the class info related to the given {@code classId}.
     */
    public ClassInfo getClass(int classId) {
        final ClassId id = ClassId.getClassId(classId);
        return (id != null) ? _classData.get(id) : null;
    }

    public static ClassListData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClassListData INSTANCE = new ClassListData();
    }
}
