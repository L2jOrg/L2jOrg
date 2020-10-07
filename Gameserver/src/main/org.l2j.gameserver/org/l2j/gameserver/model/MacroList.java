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
import org.l2j.gameserver.data.database.dao.MacroDAO;
import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.network.serverpackets.SendMacroList;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class MacroList implements IRestorable {

    private final Player owner;
    private IntMap<Macro> macros = new HashIntMap<>();
    private int nextMacroId;

    public MacroList(Player owner) {
        this.owner = owner;
        nextMacroId = 1000;
    }

    public int size() {
        return macros.size();
    }

    public void registerMacro(Macro macro) {
        macro.updatePlayer(owner);
        MacroUpdateType updateType = macro.getId() == 0 ? MacroUpdateType.ADD : MacroUpdateType.MODIFY;
        if (macro.getId() == 0) {
            macro.updateId(nextMacroId++);
        }
        macros.put(macro.getId(), macro);
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
        if (macros.isEmpty()) {
            owner.sendPacket(new SendMacroList(0, null, MacroUpdateType.LIST));
        } else {
            final int count = macros.size();
            for (Macro m : macros.values()) {
                owner.sendPacket(new SendMacroList(count, m, MacroUpdateType.LIST));
            }
        }

    }

    private void registerMacroInDb(Macro macro) {
        getDAO(MacroDAO.class).save(macro.getData());
        getDAO(MacroDAO.class).save(macro.getCommands());
    }

    private void deleteMacroFromDb(Macro macro) {
        getDAO(MacroDAO.class).deleteMacro(owner.getObjectId(), macro.getId());
    }

    @Override
    public boolean restoreMe() {
        macros = getDAO(MacroDAO.class).findAllByPlayer(owner.getObjectId());
        return true;
    }
}
