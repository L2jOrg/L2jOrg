package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.ShortcutType;

/**
 * Shortcut DTO.
 *
 * @author Zoey76
 */
public class Shortcut {

    public static final int AUTO_PLAY_PAGE = 23;
    public static final int AUTO_MACRO_SLOT = 0;
    public static final int AUTO_POTION_SLOT = 1;

    /**
     * Slot from 0 to 11.
     */
    private final int _slot;
    /**
     * Page from 0 to 9.
     */
    private final int _page;
    /**
     * Type: item, skill, action, macro, recipe, bookmark.
     */
    private final ShortcutType _type;
    /**
     * Shortcut ID.
     */
    private final int _id;
    /**
     * Shortcut level (skills).
     */
    private final int _level;
    /**
     * Shortcut level (skills).
     */
    private final int _subLevel;
    /**
     * Character type: 1 player, 2 summon.
     */
    private final int _characterType;
    /**
     * Shared reuse group.
     */
    private int _sharedReuseGroup = -1;

    public Shortcut(int slot, int page, ShortcutType type, int id, int level, int subLevel, int characterType) {
        _slot = slot;
        _page = page;
        _type = type;
        _id = id;
        _level = level;
        _subLevel = subLevel;
        _characterType = characterType;
    }

    /**
     * Gets the shortcut ID.
     *
     * @return the ID
     */
    public int getId() {
        return _id;
    }

    /**
     * Gets the shortcut level.
     *
     * @return the level
     */
    public int getLevel() {
        return _level;
    }

    /**
     * Gets the shortcut level.
     *
     * @return the level
     */
    public int getSubLevel() {
        return _subLevel;
    }

    /**
     * Gets the shortcut page.
     *
     * @return the page
     */
    public int getPage() {
        return _page;
    }

    /**
     * Gets the shortcut slot.
     *
     * @return the slot
     */
    public int getSlot() {
        return _slot;
    }

    /**
     * Gets the shortcut type.
     *
     * @return the type
     */
    public ShortcutType getType() {
        return _type;
    }

    /**
     * Gets the shortcut character type.
     *
     * @return the character type
     */
    public int getCharacterType() {
        return _characterType;
    }

    /**
     * Gets the shared reuse group.
     *
     * @return the shared reuse group
     */
    public int getSharedReuseGroup() {
        return _sharedReuseGroup;
    }

    /**
     * Sets the shared reuse group.
     *
     * @param sharedReuseGroup the shared reuse group to set
     */
    public void setSharedReuseGroup(int sharedReuseGroup) {
        _sharedReuseGroup = sharedReuseGroup;
    }
}
