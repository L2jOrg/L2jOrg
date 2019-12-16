package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.ShortcutType;

/**
 * Shortcut DTO.
 *
 * @author Zoey76
 */
public class Shortcut {

    public static final int MAX_SLOTS_PER_PAGE = 12;
    public static final int AUTO_PLAY_PAGE = 23;
    public static final int AUTO_SUPPLY_PAGE = 22;
    public static final int AUTO_MACRO_SLOT = 0;
    public static final int AUTO_POTION_SLOT = 1;

    /**
     * Slot from 0 to 11.
     */
    private final int slot;
    /**
     * Page from 0 to 23.
     */
    private final int page;
    /**
     * Type: item, skill, action, macro, recipe, bookmark.
     */
    private final ShortcutType type;
    /**
     * Shortcut ID.
     */
    private final int id;
    /**
     * Shortcut level (skills).
     */
    private final int level;
    /**
     * Shortcut level (skills).
     */
    private final int subLevel;
    /**
     * Character type: 1 player, 2 summon.
     */
    private final int characterType;
    /**
     * Shared reuse group.
     */
    private int sharedReuseGroup = -1;

    public Shortcut(int slot, int page, ShortcutType type, int id, int level, int subLevel, int characterType) {
        this.slot = slot;
        this.page = page;
        this.type = type;
        this.id = id;
        this.level = level;
        this.subLevel = subLevel;
        this.characterType = characterType;
    }

    public int getClientId() {
        return pageAndSlotToClientId(page, slot);
    }

    /**
     * Gets the shortcut ID.
     *
     * @return the ID
     */
    public int getId() {
        return id;
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
}
