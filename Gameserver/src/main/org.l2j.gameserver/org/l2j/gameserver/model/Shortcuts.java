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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

import java.util.BitSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class Shortcuts {

    private final Player owner;
    private final IntMap<Shortcut> shortcuts = new CHashIntMap<>();
    private final BitSet activeShortcuts = new BitSet(Shortcut.MAX_ROOM);
    private int nextAutoShortcut = 0;

    public Shortcuts(Player owner) {
        this.owner = owner;
    }

    public void registerShortCut(Shortcut shortcut) {
        if (shortcut.getType() == ShortcutType.ITEM) {
            var item = owner.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if (isNull(item)) {
                return;
            }
            shortcut.setSharedReuseGroup(item.getSharedReuseGroup());
        }
        registerShortCutInDb(shortcut, shortcuts.put(shortcut.getClientId(), shortcut));
    }

    private void registerShortCutInDb(Shortcut shortcut, Shortcut oldShortCut) {
        if (nonNull(oldShortCut)) {
            deleteShortcutFromDb(oldShortCut);
        }

        shortcut.setPlayerId(owner.getObjectId());
        shortcut.setClassIndex(owner.getClassIndex());
        getDAO(ShortcutDAO.class).save(shortcut);
    }

    public void setActive(int room, boolean active) {
        doIfNonNull(shortcuts.get(room), s -> {
            s.setActive(active);
            if(Shortcut.AUTO_POTION_ROOM != room) {
                if(active) {
                    activeShortcuts.set(room);
                } else {
                    activeShortcuts.clear(room);
                }
            }
        });
    }

    public Shortcut nextAutoShortcut() {
        if(activeShortcuts.isEmpty()) {
            return null;
        }
        Shortcut shortcut = null;
        var next = activeShortcuts.nextSetBit(nextAutoShortcut);
        if(next == -1) {
            next = activeShortcuts.nextSetBit(nextAutoShortcut = 0);
        }
        if(next >= 0) {
            shortcut = shortcuts.get(next);
            if(isNull(shortcut)) {
                deleteShortcut(next);
                activeShortcuts.clear(next);
            }
            nextAutoShortcut = next + 1;
        }
        return shortcut;
    }

    public void resetNextAutoShortcut() {
        nextAutoShortcut = 0;
    }
    
    private void deleteShortcutFromDb(Shortcut shortcut) {
        getDAO(ShortcutDAO.class).delete(owner.getObjectId(), shortcut.getClientId(), owner.getClassIndex());
    }

    public void deleteShortcuts(Predicate<Shortcut> filter) {
        shortcuts.values().stream().filter(filter).forEach(s -> deleteShortcut(s.getClientId()));
    }

    public void deleteShortcuts() {
        shortcuts.clear();
        getDAO(ShortcutDAO.class).deleteFromSubclass(owner.getObjectId(), owner.getClassIndex());
    }

    public void forEachShortcut(Consumer<Shortcut> action) {
        shortcuts.values().forEach(action);
    }

    public int getAmount() {
        return shortcuts.size();
    }


    public Shortcut getShortcut(int room) {
        Shortcut sc = shortcuts.get(room);
        if (nonNull(sc) && sc.getType() == ShortcutType.ITEM && isNull(owner.getInventory().getItemByObjectId(sc.getShortcutId())) ) {
            deleteShortcut(sc.getClientId());
            sc = null;
        }
        return sc;
    }

    public void deleteShortcut(int room) {
        final Shortcut old = shortcuts.remove(room);
        if (isNull(old) || (isNull(owner))) {
            return;
        }
        deleteShortcutFromDb(old);
    }

    public void deleteShortCutByObjectId(int objectId) {
        for (Shortcut shortcut : shortcuts.values()) {
            if ((shortcut.getType() == ShortcutType.ITEM) && (shortcut.getShortcutId() == objectId)) {
                deleteShortcut(shortcut.getClientId());
                break;
            }
        }
    }

    public void restoreMe() {
        shortcuts.clear();
        getDAO(ShortcutDAO.class).findByPlayer(owner.getObjectId(), owner.getClassIndex()).forEach(s -> shortcuts.put(s.getClientId(), s));

        // Verify shortcuts
        forEachShortcut(s -> {
            if (s.getType() == ShortcutType.ITEM) {
                final Item item = owner.getInventory().getItemByObjectId(s.getShortcutId());
                if (isNull(item)) {
                    deleteShortcut(s.getClientId());
                } else if (item.isEtcItem()) {
                    s.setSharedReuseGroup(item.getSharedReuseGroup());
                }
            }
            if(s.isActive()) {
                activeShortcuts.set(s.getClientId());
            }
        });
    }

    public void storeMe() {
        var dao = getDAO(ShortcutDAO.class);
        shortcuts.values().forEach(dao::save);
    }

    /**
     * Updates the shortcut bars with the new skill.
     *
     * @param skillId       the skill Id to search and update.
     * @param skillLevel    the skill level to update.
     * @param skillSubLevel the skill sub level to update.
     */
    public void updateShortCuts(int skillId, int skillLevel, int skillSubLevel) {
        // Update all the shortcuts for this skill
        for (Shortcut sc : shortcuts.values()) {
            if ((sc.getShortcutId() == skillId) && (sc.getType() == ShortcutType.SKILL)) {
                final Shortcut newsc = new Shortcut(sc.getClientId(), sc.getType(), sc.getShortcutId(), skillLevel, skillSubLevel, 1);
                owner.sendPacket(new ShortCutRegister(newsc));
                owner.registerShortCut(newsc);
            }
        }
    }
}
