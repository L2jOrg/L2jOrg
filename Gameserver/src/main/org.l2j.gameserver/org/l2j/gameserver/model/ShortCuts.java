package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class ShortCuts implements IRestorable {

    private final Player owner;
    private final IntMap<Shortcut> shortcuts = new CHashIntMap<>();

    public ShortCuts(Player owner) {
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

    @Override
    public boolean restoreMe() {
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
        });
        return true;
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
                final Shortcut newsc = new Shortcut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getShortcutId(), skillLevel, skillSubLevel, 1);
                owner.sendPacket(new ShortCutRegister(newsc));
                owner.registerShortCut(newsc);
            }
        }
    }
}
