package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NosBit
 */
public class SecondaryAuthData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryAuthData.class.getName());

    private final Set<String> forbiddenPasswords = new HashSet<>();
    private boolean _enabled = false;
    private int _maxAttempts = 5;
    private int _banTime = 480;
    private String _recoveryLink = "";

    private SecondaryAuthData() {
        load();
    }

    @Override
    public synchronized void load() {
        forbiddenPasswords.clear();
        parseFile(new File("config/SecondaryAuth.xml"));
        LOGGER.info("Loaded {}forbidden passwords.", forbiddenPasswords.size() );
    }

    @Override
    public void parseDocument(Document doc, File f) {
        try {
            for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
                if ("list".equalsIgnoreCase(node.getNodeName())) {
                    for (Node list_node = node.getFirstChild(); list_node != null; list_node = list_node.getNextSibling()) {
                        if ("enabled".equalsIgnoreCase(list_node.getNodeName())) {
                            _enabled = Boolean.parseBoolean(list_node.getTextContent());
                        } else if ("maxAttempts".equalsIgnoreCase(list_node.getNodeName())) {
                            _maxAttempts = Integer.parseInt(list_node.getTextContent());
                        } else if ("banTime".equalsIgnoreCase(list_node.getNodeName())) {
                            _banTime = Integer.parseInt(list_node.getTextContent());
                        } else if ("recoveryLink".equalsIgnoreCase(list_node.getNodeName())) {
                            _recoveryLink = list_node.getTextContent();
                        } else if ("forbiddenPasswords".equalsIgnoreCase(list_node.getNodeName())) {
                            for (Node forbiddenPasswords_node = list_node.getFirstChild(); forbiddenPasswords_node != null; forbiddenPasswords_node = forbiddenPasswords_node.getNextSibling()) {
                                if ("password".equalsIgnoreCase(forbiddenPasswords_node.getNodeName())) {
                                    forbiddenPasswords.add(forbiddenPasswords_node.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load secondary auth data from xml.", e);
        }
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public int getMaxAttempts() {
        return _maxAttempts;
    }

    public boolean isForbiddenPassword(String password) {
        return forbiddenPasswords.contains(password);
    }

    public static SecondaryAuthData getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton {
        private static final SecondaryAuthData INSTANCE = new SecondaryAuthData();
    }
}
