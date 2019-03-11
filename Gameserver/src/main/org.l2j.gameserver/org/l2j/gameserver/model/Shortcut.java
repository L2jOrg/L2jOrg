/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.ShortcutType;

/**
 * Shortcut DTO.
 *
 * @author Zoey76
 */
public class Shortcut {
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
