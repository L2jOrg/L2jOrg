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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.network.serverpackets.SendMacroList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class MacroList implements IRestorable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroList.class);

    private final Player owner;
    private final IntMap<Macro> macros = new HashIntMap<>();
    private int macroId;

    public MacroList(Player owner) {
        this.owner = owner;
        macroId = 1000;
    }

    public int size() {
        return macros.size();
    }

    public void registerMacro(Macro macro) {
        MacroUpdateType updateType = MacroUpdateType.ADD;
        if (macro.getId() == 0) {
            macro.setId(macroId++);
            while (macros.containsKey(macro.getId())) {
                macro.setId(macroId++);
            }
            macros.put(macro.getId(), macro);
        } else {
            updateType = MacroUpdateType.MODIFY;
            doIfNonNull(macros.put(macro.getId(), macro), this::deleteMacroFromDb);
        }
        registerMacroInDb(macro);
        owner.sendPacket(new SendMacroList(1, macro, updateType));
    }

    public void deleteMacro(int id) {
        doIfNonNull(macros.remove(id), removed -> {
            deleteMacroFromDb(removed);
            owner.deleteShortcuts(s -> s.getShortcutId() == id && s.getType() == ShortcutType.MACRO);
            owner.sendPacket(new SendMacroList(0, removed, MacroUpdateType.DELETE));
        });
    }

    public void sendAllMacros() {
        final Collection<Macro> allMacros = macros.values();
        final int count = allMacros.size();

        synchronized (macros) {
            if (allMacros.isEmpty()) {
                owner.sendPacket(new SendMacroList(0, null, MacroUpdateType.LIST));
            } else {
                for (Macro m : allMacros) {
                    owner.sendPacket(new SendMacroList(count, m, MacroUpdateType.LIST));
                }
            }
        }
    }

    private void registerMacroInDb(Macro macro) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO character_macroses (charId,id,icon,name,descr,acronym,commands) values(?,?,?,?,?,?,?)")) {
            ps.setInt(1, owner.getObjectId());
            ps.setInt(2, macro.getId());
            ps.setInt(3, macro.getIcon());
            ps.setString(4, macro.getName());
            ps.setString(5, macro.getDescr());
            ps.setString(6, macro.getAcronym());
            final StringBuilder sb = new StringBuilder(300);
            for (MacroCmd cmd : macro.getCommands()) {
                sb.append(cmd.getType().ordinal() + "," + cmd.getD1() + "," + cmd.getD2());
                if ((cmd.getCmd() != null) && (cmd.getCmd().length() > 0)) {
                    sb.append("," + cmd.getCmd());
                }
                sb.append(';');
            }

            if (sb.length() > 255) {
                sb.setLength(255);
            }

            ps.setString(7, sb.toString());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("could not store macro:", e);
        }
    }

    private void deleteMacroFromDb(Macro macro) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM character_macroses WHERE charId=? AND id=?")) {
            ps.setInt(1, owner.getObjectId());
            ps.setInt(2, macro.getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("could not delete macro:", e);
        }
    }

    @Override
    public boolean restoreMe() {
        macros.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT charId, id, icon, name, descr, acronym, commands FROM character_macroses WHERE charId=?")) {
            ps.setInt(1, owner.getObjectId());
            try (ResultSet rset = ps.executeQuery()) {
                while (rset.next()) {
                    final int id = rset.getInt("id");
                    final int icon = rset.getInt("icon");
                    final String name = rset.getString("name");
                    final String descr = rset.getString("descr");
                    final String acronym = rset.getString("acronym");
                    final List<MacroCmd> commands = new ArrayList<>();
                    final StringTokenizer st1 = new StringTokenizer(rset.getString("commands"), ";");
                    while (st1.hasMoreTokens()) {
                        final StringTokenizer st = new StringTokenizer(st1.nextToken(), ",");
                        if (st.countTokens() < 3) {
                            continue;
                        }
                        final MacroType type = MacroType.values()[Integer.parseInt(st.nextToken())];
                        final int d1 = Integer.parseInt(st.nextToken());
                        final int d2 = Integer.parseInt(st.nextToken());
                        String cmd = "";
                        if (st.hasMoreTokens()) {
                            cmd = st.nextToken();
                        }
                        commands.add(new MacroCmd(commands.size(), type, d1, d2, cmd));
                    }
                    macros.put(id, new Macro(id, icon, name, descr, acronym, commands));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("could not store shortcuts:", e);
            return false;
        }
        return true;
    }
}
