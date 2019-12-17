package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ShortCuts implements IRestorable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortCuts.class);
    private final Player owner;
    private final IntMap<Shortcut> shortCuts = new CHashIntMap<>();

    public ShortCuts(Player owner) {
        this.owner = owner;
    }

    public Shortcut[] getAllShortCuts() {
        return shortCuts.values().toArray(Shortcut[]::new);
    }

    public Shortcut getShortCut(int slot, int page) {
        return getShortCut(Shortcut.pageAndSlotToClientId(page, slot));
    }

    public Shortcut getShortCut(int shortcutId) {
        Shortcut sc = shortCuts.get(shortcutId);
        if (nonNull(sc) && sc.getType() == ShortcutType.ITEM && isNull(owner.getInventory().getItemByObjectId(sc.getId())) ) {
            deleteShortCut(sc.getSlot(), sc.getPage());
            sc = null;
        }
        return sc;
    }

    public void registerShortCut(Shortcut shortcut) {
        if (shortcut.getType() == ShortcutType.ITEM) {
            final Item item = owner.getInventory().getItemByObjectId(shortcut.getId());
            if (item == null) {
                return;
            }
            shortcut.setSharedReuseGroup(item.getSharedReuseGroup());
        }
        registerShortCutInDb(shortcut, shortCuts.put(shortcut.getClientId(), shortcut));
    }

    private void registerShortCutInDb(Shortcut shortcut, Shortcut oldShortCut) {
        if (oldShortCut != null) {
            deleteShortCutFromDb(oldShortCut);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO character_shortcuts (charId,slot,page,type,shortcut_id,level,sub_level,class_index) values(?,?,?,?,?,?,?,?)")) {
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, shortcut.getSlot());
            statement.setInt(3, shortcut.getPage());
            statement.setInt(4, shortcut.getType().ordinal());
            statement.setInt(5, shortcut.getId());
            statement.setInt(6, shortcut.getLevel());
            statement.setInt(7, shortcut.getSubLevel());
            statement.setInt(8, owner.getClassIndex());
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("Could not store character shortcut: " + e.getMessage(), e);
        }
    }

    public synchronized void deleteShortCut(int slot, int page) {
        final Shortcut old = shortCuts.remove(Shortcut.pageAndSlotToClientId(page, slot));
        if ((old == null) || (owner == null)) {
            return;
        }
        deleteShortCutFromDb(old);
    }

    public synchronized void deleteShortCutByObjectId(int objectId) {
        for (Shortcut shortcut : shortCuts.values()) {
            if ((shortcut.getType() == ShortcutType.ITEM) && (shortcut.getId() == objectId)) {
                deleteShortCut(shortcut.getSlot(), shortcut.getPage());
                break;
            }
        }
    }

    private void deleteShortCutFromDb(Shortcut shortcut) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=? AND slot=? AND page=? AND class_index=?")) {
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, shortcut.getSlot());
            statement.setInt(3, shortcut.getPage());
            statement.setInt(4, owner.getClassIndex());
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("Could not delete character shortcut: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean restoreMe() {
        shortCuts.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT charId, slot, page, type, shortcut_id, level, sub_level FROM character_shortcuts WHERE charId=? AND class_index=?")) {
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, owner.getClassIndex());

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final int slot = rset.getInt("slot");
                    final int page = rset.getInt("page");
                    final int type = rset.getInt("type");
                    final int id = rset.getInt("shortcut_id");
                    final int level = rset.getInt("level");
                    final int subLevel = rset.getInt("sub_level");
                    var shortcut = new Shortcut(slot, page, ShortcutType.values()[type], id, level, subLevel, 1);
                    shortCuts.put(shortcut.getClientId(), shortcut);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore character shortcuts: " + e.getMessage(), e);
            return false;
        }

        // Verify shortcuts
        for (Shortcut sc : getAllShortCuts()) {
            if (sc.getType() == ShortcutType.ITEM) {
                final Item item = owner.getInventory().getItemByObjectId(sc.getId());
                if (item == null) {
                    deleteShortCut(sc.getSlot(), sc.getPage());
                } else if (item.isEtcItem()) {
                    sc.setSharedReuseGroup(item.getEtcItem().getSharedReuseGroup());
                }
            }
        }

        return true;
    }

    /**
     * Updates the shortcut bars with the new skill.
     *
     * @param skillId       the skill Id to search and update.
     * @param skillLevel    the skill level to update.
     * @param skillSubLevel the skill sub level to update.
     */
    public synchronized void updateShortCuts(int skillId, int skillLevel, int skillSubLevel) {
        // Update all the shortcuts for this skill
        for (Shortcut sc : shortCuts.values()) {
            if ((sc.getId() == skillId) && (sc.getType() == ShortcutType.SKILL)) {
                final Shortcut newsc = new Shortcut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, skillSubLevel, 1);
                owner.sendPacket(new ShortCutRegister(newsc));
                owner.registerShortCut(newsc);
            }
        }
    }
}
