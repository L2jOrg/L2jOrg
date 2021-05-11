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
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.MacroCmdData;
import org.l2j.gameserver.data.database.data.MacroData;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;


/**
 * This class holds the Initial Shortcuts information.<br>
 * What shortcuts get each newly created character.
 *
 * @author Zoey76
 */
public final class InitialShortcutData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialShortcutData.class);

    private final Map<ClassId, List<Shortcut>> _initialShortcutData = new EnumMap<>(ClassId.class);
    private final List<Shortcut> _initialGlobalShortcutList = new ArrayList<>();
    private final IntMap<Macro> _macroPresets = new HashIntMap<>();

    private InitialShortcutData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/initialShortcuts.xsd");
    }

    @Override
    public void load() {
        _initialShortcutData.clear();
        _initialGlobalShortcutList.clear();

        parseDatapackFile("data/stats/initialShortcuts.xml");

        LOGGER.info("Loaded {} Initial Global Shortcuts data.", _initialGlobalShortcutList.size());
        LOGGER.info("Loaded {} Initial Shortcuts data.", _initialShortcutData.size());
        LOGGER.info("Loaded {} Macros presets.", _macroPresets.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    switch (d.getNodeName()) {
                        case "shortcuts": {
                            parseShortcuts(d);
                            break;
                        }
                        case "macros": {
                            parseMacros(d);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void parseShortcuts(Node d) {
        NamedNodeMap attrs = d.getAttributes();
        final Node classIdNode = attrs.getNamedItem("classId");
        final List<Shortcut> list = new ArrayList<>();
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            if ("page".equals(c.getNodeName())) {
                attrs = c.getAttributes();
                final int pageId = parseInt(attrs, "pageId");
                for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("slot".equals(b.getNodeName())) {
                        list.add(createShortcut(pageId, b));
                    }
                }
            }
        }

        if (classIdNode != null) {
            _initialShortcutData.put(ClassId.getClassId(Integer.parseInt(classIdNode.getNodeValue())), list);
        } else {
            _initialGlobalShortcutList.addAll(list);
        }
    }

    private void parseMacros(Node d) {
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            if ("macro".equals(c.getNodeName())) {
                NamedNodeMap attrs = c.getAttributes();
                if (!parseBoolean(attrs, "enabled", true)) {
                    continue;
                }

                final int macroId = parseInt(attrs, "macroId");

                MacroData data = new MacroData();
                data.setId(macroId);
                data.setIcon( parseInt(attrs, "icon"));
                data.setName(parseString(attrs, "name"));
                data.setDescription(parseString(attrs, "description"));
                data.setAcronym(parseString(attrs, "acronym"));

                final List<MacroCmdData> commands = new ArrayList<>(1);
                int entry = 0;

                for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("command".equals(b.getNodeName())) {
                        MacroCmdData cmdData = new MacroCmdData();
                        cmdData.setMacroId(macroId);
                        attrs = b.getAttributes();
                        cmdData.setType(parseEnum(attrs, MacroType.class, "type"));
                        cmdData.setCommand(b.getTextContent());

                        setDatas(attrs, cmdData);
                        cmdData.setIndex(entry++);
                        commands.add(cmdData);
                    }
                }
                _macroPresets.put(macroId, new Macro(data, commands));
            }
        }
    }

    private void setDatas(NamedNodeMap attrs, MacroCmdData cmdData) {
        switch (cmdData.getType()) {
            case SKILL -> {
                cmdData.setData1(parseInt(attrs, "skillId")); // Skill ID
                cmdData.setData2(parseInt(attrs, "skillLvl", 0)); // Skill level
            }
            case SHORTCUT -> {
                cmdData.setData1(parseInt(attrs, "page"));
                cmdData.setData2(parseInt(attrs, "slot", 0));
            }
            case ACTION -> cmdData.setData1(parseInt(attrs, "actionId"));
            case ITEM -> cmdData.setData1(parseInt(attrs, "itemId"));
            case DELAY -> cmdData.setData1(parseInt(attrs, "delay"));
        }
    }

    /**
     * Parses a node an create a shortcut from it.
     *
     * @param pageId the page ID
     * @param b      the node to parse
     * @return the new shortcut
     */
    private Shortcut createShortcut(int pageId, Node b) {
        final NamedNodeMap attrs = b.getAttributes();
        final int slotId = parseInt(attrs, "slotId");
        final ShortcutType shortcutType = parseEnum(attrs, ShortcutType.class, "shortcutType");
        final int shortcutId = parseInt(attrs, "shortcutId");
        final int shortcutLevel = parseInt(attrs, "shortcutLevel", 0);
        final int characterType = parseInt(attrs, "characterType", 0);
        return new Shortcut(Shortcut.pageAndSlotToClientId(pageId, slotId), shortcutType, shortcutId, shortcutLevel, 0, characterType);
    }

    /**
     * Register all the available shortcuts for the given player.
     *
     * @param player the player
     */
    public void registerAllShortcuts(Player player) {
        if (isNull(player)) {
            return;
        }

        // Register global shortcuts.
        for (Shortcut shortcut : _initialGlobalShortcutList) {
            int shortcutId = shortcut.getShortcutId();
            switch (shortcut.getType()) {
                case ITEM: {
                    final Item item = player.getInventory().getItemByItemId(shortcutId);
                    if (item == null) {
                        continue;
                    }
                    shortcutId = item.getObjectId();
                    break;
                }
                case SKILL: {
                    if (!player.getSkills().containsKey(shortcutId)) {
                        continue;
                    }
                    break;
                }
                case MACRO: {
                    final Macro macro = _macroPresets.get(shortcutId);
                    if (macro == null) {
                        continue;
                    }
                    player.registerMacro(macro);
                    break;
                }
            }

            // Register shortcut
            final Shortcut newShortcut = new Shortcut(shortcut.getClientId(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
            player.sendPacket(new ShortCutRegister(newShortcut));
            player.registerShortCut(newShortcut);
        }

        // Register class specific shortcuts.
        if (_initialShortcutData.containsKey(player.getClassId())) {
            for (Shortcut shortcut : _initialShortcutData.get(player.getClassId())) {
                int shortcutId = shortcut.getShortcutId();
                switch (shortcut.getType()) {
                    case ITEM: {
                        final Item item = player.getInventory().getItemByItemId(shortcutId);
                        if (item == null) {
                            continue;
                        }
                        shortcutId = item.getObjectId();
                        break;
                    }
                    case SKILL: {
                        if (!player.getSkills().containsKey(shortcut.getShortcutId())) {
                            continue;
                        }
                        break;
                    }
                    case MACRO: {
                        final Macro macro = _macroPresets.get(shortcutId);
                        if (macro == null) {
                            continue;
                        }
                        player.registerMacro(macro);
                        break;
                    }
                }
                // Register shortcut
                final Shortcut newShortcut = new Shortcut(shortcut.getClientId(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
                player.sendPacket(new ShortCutRegister(newShortcut));
                player.registerShortCut(newShortcut);
            }
        }
    }

    public static InitialShortcutData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final InitialShortcutData INSTANCE = new InitialShortcutData();
    }
}