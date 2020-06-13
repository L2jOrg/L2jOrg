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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
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

    @Column("player_id")
    private int playerId;

    @Column("client_id")
    private int clientId;

    @Column("class_index")
    private int classIndex;

    private ShortcutType type;

    @Column("shortcut_id")
    private int shortcutId;

    /**
     * Shortcut level (used for skills).
     */
    private int level;
    /**
     * Shortcut sub level (used for skills).
     */
    @Column("sub_level")
    private int subLevel;

    /**
     * Slot from 0 to 11.
     */
    @NonUpdatable
    private int slot;

    /**
     * Page from 0 to 23.
     */
    @NonUpdatable
    private int page;

    @NonUpdatable
    private int sharedReuseGroup = -1;

    /**
     * Character type: 1 player, 2 summon.
     */
    @NonUpdatable
    private int characterType;

    /**
     * auto use shortcut is active
     */
    private boolean active;

    public Shortcut() {

    }

    public Shortcut(int clientId, ShortcutType type, int id, int level, int subLevel, int characterType) {
        this.clientId = clientId;
        this.type = type;
        this.shortcutId = id;
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

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static int pageAndSlotToClientId(int page, int slot) {
        return  slot + (page * MAX_SLOTS_PER_PAGE);
    }

    public boolean isActive() {
        return active;
    }
}
