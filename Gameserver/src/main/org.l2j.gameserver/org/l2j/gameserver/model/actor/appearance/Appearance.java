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
package org.l2j.gameserver.model.actor.appearance;

import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
public class Appearance {
    public static final int DEFAULT_TITLE_COLOR = 0xECF9A2;

    private final Player owner;
    private final PlayerData data;

    private String _visibleName;
    private String _visibleTitle;
    private int _nameColor = 0xFFFFFF;

    public Appearance(Player owner, PlayerData data) {
        this.owner = owner;
        this.data = data;
    }

    public final String getVisibleName() {
        if (_visibleName == null) {
            return owner.getName();
        }
        return _visibleName;
    }

    public final void setVisibleName(String visibleName) {
        _visibleName = visibleName;
    }

    public final String getVisibleTitle() {
        if (_visibleTitle == null) {
            return owner.getTitle();
        }
        return _visibleTitle;
    }

    public final void setVisibleTitle(String visibleTitle) {
        _visibleTitle = visibleTitle;
    }

    public final byte getFace() {
        return data.getFace();
    }

    public final void setFace(int value) {
        data.setFace((byte) value);
    }

    public final byte getHairColor() {
        return data.getHairColor();
    }

    public final void setHairColor(int value) {
        data.setHairColor((byte) value);
    }

    public final byte getHairStyle() {
        return data.getHairStyle();
    }

    public final void setHairStyle(int value) {
        data.setHairStyle((byte) value);
    }

    public final boolean isFemale() {
        return data.isFemale();
    }

    public final void setFemale(boolean isFemale) {
        data.setFemale(isFemale);
    }

    public Sex getSexType() {
        return data.isFemale() ? Sex.FEMALE : Sex.MALE;
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

    public int getTitleColor() {
        return data.getTitleColor();
    }

    public void setTitleColor(int titleColor) {
        if (titleColor < 0) {
            return;
        }

        data.setTitleColor(titleColor);
    }

    public int getVisibleClanId() {
        return owner.getClanId();
    }

    public int getVisibleClanCrestId() {
        return owner.getClanCrestId();
    }

    public int getVisibleClanLargeCrestId() {
        return owner.getClanCrestLargeId();
    }

    public int getVisibleAllyId() {
        return owner.getAllyId();
    }

    public int getVisibleAllyCrestId() {
        return owner.getAllyCrestId();
    }
}
