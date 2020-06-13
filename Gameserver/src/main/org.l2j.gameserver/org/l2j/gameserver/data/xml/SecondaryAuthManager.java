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
package org.l2j.gameserver.data.xml;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

/**
 * @author NosBit
 * @author JoeAlisson
 */
public class SecondaryAuthManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryAuthManager.class.getName());

    private final IntSet forbiddenPasswords = new HashIntSet();
    private boolean enabled = false;
    private int maxAttempts = 5;
    private int banTime = 480;
    private String recoveryLink = "";

    private SecondaryAuthManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/secondary-auth.xsd");
    }

    @Override
    public synchronized void load() {
        forbiddenPasswords.clear();
        parseFile(new File("config/secondary-auth.xml"));
        LOGGER.info("Loaded {} forbidden passwords.", forbiddenPasswords.size() );
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "secondary-auth", node -> {
            var attrs = node.getAttributes();
            if(! (enabled = parseBoolean(attrs, "enabled"))) {
                return;
            }

            maxAttempts = parseInteger(attrs, "max-attempts");
            banTime = parseInteger(attrs, "ban-time");
            recoveryLink = parseString(attrs, "recovery-link");

            forEach(node, "forbidden-passwords", forbiddenList -> forEach(forbiddenList, "password", pwdNode -> forbiddenPasswords.add(Integer.parseInt(pwdNode.getTextContent()))));
        });
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isForbiddenPassword(int password) {
        return forbiddenPasswords.contains(password);
    }

    public static void init() {
        getInstance().load();
    }

    public static SecondaryAuthManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton {
        private static final SecondaryAuthManager INSTANCE = new SecondaryAuthManager();
    }
}
