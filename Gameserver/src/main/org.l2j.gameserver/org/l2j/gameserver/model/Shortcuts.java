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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;

import java.util.BitSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class Shortcuts {

    private final Player owner;
    private final IntMap<Shortcut> shortcuts = new CHashIntMap<>();
    private final BitSet activeShortcuts = new BitSet(Shortcut.MAX_ROOM);
    private final BitSet summonShortcuts = new BitSet(Shortcut.MAX_ROOM);
    private final Set<Shortcut> suppliesShortcuts = ConcurrentHashMap.newKeySet();
    private int nextAutoShortcut = 0;
    private int nextSummonShortcut = 0;

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
        owner.sendPacket(new ShortCutRegister(shortcut));
    }

    private void registerShortCutInDb(Shortcut shortcut, Shortcut oldShortCut) {
        if (nonNull(oldShortCut)) {
            deleteShortcutFromDb(oldShortCut);
        }

        shortcut.setPlayerId(owner.getObjectId());
        getDAO(ShortcutDAO.class).save(shortcut);
    }

    public void setActiveShortcut(int room, boolean active) {
        var shortcut = setActive(room, active);
        if(nonNull(shortcut)) {
            if(shortcut.isSummonShortcut()) {
                setActiveSummonShortcut(room, active);
            } else if (active){
                activeShortcuts.set(room);
            } else {
                activeShortcuts.clear(room);
            }
        } else {
            activeShortcuts.clear(room);
        }
    }

    private void setActiveSummonShortcut(int room, boolean active) {
        if(active) {
            summonShortcuts.set(room);
        } else {
            summonShortcuts.clear(room);
        }
    }

    public void setActiveSupplyShortcut(int room, boolean active) {
        var shortcut = setActive(room, active);
        if(isNull(shortcut)) {
            return;
        }

        if(active) {
            suppliesShortcuts.add(shortcut);
        } else {
            suppliesShortcuts.remove(shortcut);
        }
    }

    private Shortcut setActive(int room, boolean active) {
        var shortcut = shortcuts.get(room);
        if(nonNull(shortcut)) {
            shortcut.setActive(active);
            owner.sendPacket(new ExActivateAutoShortcut(room, active));
        }
        return shortcut;
    }

    public Set<Shortcut> getSuppliesShortcuts() {
        return suppliesShortcuts;
    }
    
    public Shortcut nextAutoShortcut() {
        var shortcut = nextAutoShortcut(activeShortcuts, nextAutoShortcut);
        nextAutoShortcut = nonNull(shortcut) ? shortcut.getClientId() + 1 : 0;
        return shortcut;
    }

    public Shortcut nextAutoSummonShortcut() {
        var shortcut =  nextAutoShortcut(summonShortcuts, nextSummonShortcut);
        nextSummonShortcut = nonNull(shortcut) ? shortcut.getClientId() + 1 : 0;
        return shortcut;
    }

    private Shortcut nextAutoShortcut(BitSet autoShortcuts, int nextAutoShortcut) {
        if(autoShortcuts.isEmpty()) {
            return null;
        }
        Shortcut shortcut = null;
        var next = autoShortcuts.nextSetBit(nextAutoShortcut);
        if(next == -1) {
            next = autoShortcuts.nextSetBit(0);
        }
        if(next >= 0) {
            shortcut = shortcuts.get(next);
            if(isNull(shortcut)) {
                deleteShortcut(next);
                autoShortcuts.clear(next);
            }
        }
        return shortcut;
    }

    public void resetNextAutoShortcut() {
        nextAutoShortcut = 0;
        nextSummonShortcut = 0;
    }
    
    private void deleteShortcutFromDb(Shortcut shortcut) {
        getDAO(ShortcutDAO.class).delete(owner.getObjectId(), shortcut.getClientId());
    }

    public void deleteShortcuts(Predicate<Shortcut> filter) {
        shortcuts.values().stream().filter(filter).forEach(s -> deleteShortcut(s.getClientId()));
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
        var autoPlayEngine = AutoPlayEngine.getInstance();
        for (Shortcut shortcut : getDAO(ShortcutDAO.class).findByPlayer(owner.getObjectId())) {
            if (!addShortcut(shortcut)) {
                continue;
            }

            processActiveShortcut(autoPlayEngine, shortcut);
        }
        if(!suppliesShortcuts.isEmpty()) {
            autoPlayEngine.setActiveAutoShortcut(owner, suppliesShortcuts.iterator().next().getClientId(), true);
        }
    }

    private void processActiveShortcut(AutoPlayEngine autoPlayEngine, Shortcut shortcut) {
        if(shortcut.isActive()) {
            if(autoPlayEngine.isAutoSupply(owner, shortcut)) {
                suppliesShortcuts.add(shortcut);
            } else {
                setActiveShortcut(shortcut.getClientId(), true);
            }
        }
    }

    private boolean addShortcut(Shortcut shortcut) {
        if (shortcut.getType() == ShortcutType.ITEM) {
            final Item item = owner.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if (isNull(item)) {
                deleteShortcutFromDb(shortcut);
                return false;
            }

            if (item.isEtcItem()) {
                shortcut.setSharedReuseGroup(item.getSharedReuseGroup());
            }
        }

        shortcuts.put(shortcut.getClientId(), shortcut);
        return true;
    }

    public void storeMe() {
        getDAO(ShortcutDAO.class).save(shortcuts.values());
    }

    /**
     * Updates the shortcut bars with the new skill.
     *
     * @param skillId       the skill Id to search and update.
     * @param skillLevel    the skill level to update.
     */
    public void updateShortCuts(int skillId, int skillLevel) {
        for (Shortcut sc : shortcuts.values()) {
            if ((sc.getShortcutId() == skillId) && (sc.getType() == ShortcutType.SKILL)) {
                sc.setLevel(skillLevel);
                owner.sendPacket(new ShortCutRegister(sc));
                getDAO(ShortcutDAO.class).save(sc);
            }
        }
    }
}
