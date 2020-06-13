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
package org.l2j.gameserver.model.actor.appearance;

import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.actor.instance.Player;

import static java.util.Objects.isNull;

public class PlayerAppearance {
    public static final int DEFAULT_TITLE_COLOR = 0xECF9A2;

    private Player owner;

    private byte face;

    private byte hairColor;

    private byte hairStyle;

    private boolean female; // Female true(1)

    /**
     * The current visible name of this player, not necessarily the real one
     */
    private String _visibleName;

    /**
     * The current visible title of this player, not necessarily the real one
     */
    private String _visibleTitle;

    /**
     * The default name color is 0xFFFFFF.
     */
    private int _nameColor = 0xFFFFFF;

    /**
     * The default title color is 0xECF9A2.
     */
    private int _titleColor = DEFAULT_TITLE_COLOR;

    private int _visibleClanId = -1;
    private int _visibleClanCrestId = -1;
    private int _visibleClanLargeCrestId = -1;
    private int _visibleAllyId = -1;
    private int _visibleAllyCrestId = -1;

    public PlayerAppearance(Player owner, byte face, byte hairColor, byte hairStyle, boolean female) {
        this.owner = owner;
        this.face = face;
        this.hairColor = hairColor;
        this.hairStyle = hairStyle;
        this.female = female;
    }


    /**
     * @return Returns the visibleName.
     */
    public final String getVisibleName() {
        if (_visibleName == null) {
            return owner.getName();
        }
        return _visibleName;
    }

    /**
     * @param visibleName The visibleName to set.
     */
    public final void setVisibleName(String visibleName) {
        _visibleName = visibleName;
    }

    /**
     * @return Returns the visibleTitle.
     */
    public final String getVisibleTitle() {
        if (_visibleTitle == null) {
            return owner.getTitle();
        }
        return _visibleTitle;
    }

    /**
     * @param visibleTitle The visibleTitle to set.
     */
    public final void setVisibleTitle(String visibleTitle) {
        _visibleTitle = visibleTitle;
    }

    public final byte getFace() {
        return face;
    }

    /**
     * @param value
     */
    public final void setFace(int value) {
        face = (byte) value;
    }

    public final byte getHairColor() {
        return hairColor;
    }

    /**
     * @param value
     */
    public final void setHairColor(int value) {
        hairColor = (byte) value;
    }

    public final byte getHairStyle() {
        return hairStyle;
    }

    /**
     * @param value
     */
    public final void setHairStyle(int value) {
        hairStyle = (byte) value;
    }

    /**
     * @return true if char is female
     */
    public final boolean isFemale() {
        return female;
    }

    /**
     * @param isfemale
     */
    public final void setFemale(boolean isfemale) {
        female = isfemale;
    }

    /**
     * @return Sex of the char
     */
    public Sex getSexType() {
        return female ? Sex.FEMALE : Sex.MALE;
    }

    public int getNameColor() {
        return _nameColor;
    }

    public void setNameColor(int nameColor) {
        if (nameColor < 0) {
            return;
        }

        _nameColor = nameColor;
    }

    public void setNameColor(int red, int green, int blue) {
        _nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
    }

    public int getTitleColor() {
        return _titleColor;
    }

    public void setTitleColor(int titleColor) {
        if (titleColor < 0) {
            return;
        }

        _titleColor = titleColor;
    }

    public void setTitleColor(int red, int green, int blue) {
        _titleColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
    }

    /**
     * @return Returns the owner.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @param owner The owner to set.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getVisibleClanId() {
        return _visibleClanId != -1 ? _visibleClanId : owner.getClanId();
    }

    public int getVisibleClanCrestId() {
        return _visibleClanCrestId != -1 ? _visibleClanCrestId : owner.getClanCrestId();
    }

    public int getVisibleClanLargeCrestId() {
        return _visibleClanLargeCrestId != -1 ? _visibleClanLargeCrestId : owner.getClanCrestLargeId();
    }

    public int getVisibleAllyId() {
        return _visibleAllyId != -1 ? _visibleAllyId : owner.getAllyId();
    }

    public int getVisibleAllyCrestId() {
        return _visibleAllyCrestId != -1 ? _visibleAllyCrestId : isNull(owner) ? 0 : owner.getAllyCrestId();
    }

    public void setVisibleClanData(int clanId, int clanCrestId, int clanLargeCrestId, int allyId, int allyCrestId) {
        _visibleClanId = clanId;
        _visibleClanCrestId = clanCrestId;
        _visibleClanLargeCrestId = clanLargeCrestId;
        _visibleAllyId = allyId;
        _visibleAllyCrestId = allyCrestId;
    }
}
