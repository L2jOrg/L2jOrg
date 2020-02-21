package org.l2j.gameserver.model;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.enums.ShortcutType;

/**
 * Shortcut DTO.
 *
 * @author Zoey76
 * @author JoeAlisson
 */
@Table("character_shortcuts")
public class Shortcut {

    public static final int MAX_SLOTS_PER_PAGE = 12;
    public static final int MAX_ROOM = 20 * MAX_SLOTS_PER_PAGE;
    public static final int AUTO_POTION_ROOM = pageAndSlotToClientId(23, 1);
    public static final int AUTO_PLAY_PAGE = 23;
    public static final int AUTO_SUPPLY_PAGE = 22;
    public static final int AUTO_MACRO_SLOT = 0;
    public static final int AUTO_POTION_SLOT = 1;

    @Column("char_id")
    private int playerId;

    @Column("client_id")
    private final int clientId;

    @Column("class_index")
    private int classIndex;

    private final ShortcutType type;

    @Column("shortcut_id")
    private final int shortcutId;

    /**
     * Shortcut level (used for skills).
     */
    private final int level;
    /**
     * Shortcut sub level (used for skills).
     */
    private final int subLevel;

    /**
     * Slot from 0 to 11.
     */
    @Transient
    private final int slot;

    /**
     * Page from 0 to 23.
     */
    @Transient
    private final int page;

    @Transient
    private int sharedReuseGroup = -1;

    /**
     * Character type: 1 player, 2 summon.
     */
    @Transient
    private final int characterType;

    public Shortcut(int slot, int page, ShortcutType type, int shortcutId, int level, int subLevel, int characterType) {
        this.slot = slot;
        this.page = page;
        this.type = type;
        this.clientId = pageAndSlotToClientId(page, slot);
        this.shortcutId = shortcutId;
        this.level = level;
        this.subLevel = subLevel;
        this.characterType = characterType;
    }

    public int getClientId() {
        return clientId;
    }

    /**
     * Gets the shortcut ID.
     *
     * @return the ID
     */
    public int getShortcutId() {
        return shortcutId;
    }

    /**
     * Gets the shortcut level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the shortcut level.
     *
     * @return the level
     */
    public int getSubLevel() {
        return subLevel;
    }

    /**
     * Gets the shortcut page.
     *
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets the shortcut slot.
     *
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets the shortcut type.
     *
     * @return the type
     */
    public ShortcutType getType() {
        return type;
    }

    /**
     * Gets the shortcut character type.
     *
     * @return the character type
     */
    public int getCharacterType() {
        return characterType;
    }

    /**
     * Gets the shared reuse group.
     *
     * @return the shared reuse group
     */
    public int getSharedReuseGroup() {
        return sharedReuseGroup;
    }

    /**
     * Sets the shared reuse group.
     *
     * @param sharedReuseGroup the shared reuse group to set
     */
    public void setSharedReuseGroup(int sharedReuseGroup) {
        this.sharedReuseGroup = sharedReuseGroup;
    }

    public static int pageAndSlotToClientId(int page, int slot) {
        return  slot + (page * MAX_SLOTS_PER_PAGE);
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }
}
