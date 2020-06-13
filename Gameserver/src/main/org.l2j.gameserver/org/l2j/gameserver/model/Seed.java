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
package org.l2j.gameserver.model;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.ItemTemplate;

public final class Seed {
    private final int _seedId;
    private final int _cropId; // crop type
    private final int _level; // seed level
    private final int _matureId; // mature crop type
    private final int _reward1;
    private final int _reward2;
    private final int _castleId; // id of manor (castle id) where seed can be farmed
    private final boolean _isAlternative;
    private final int _limitSeeds;
    private final int _limitCrops;
    private final long _seedReferencePrice;
    private final long _cropReferencePrice;

    public Seed(StatsSet set) {
        _cropId = set.getInt("id");
        _seedId = set.getInt("seedId");
        _level = set.getInt("level");
        _matureId = set.getInt("mature_Id");
        _reward1 = set.getInt("reward1");
        _reward2 = set.getInt("reward2");
        _castleId = set.getInt("castleId");
        _isAlternative = set.getBoolean("alternative");
        _limitCrops = set.getInt("limit_crops");
        _limitSeeds = set.getInt("limit_seed");
        // Set prices
        ItemTemplate item = ItemEngine.getInstance().getTemplate(_cropId);
        _cropReferencePrice = (item != null) ? item.getReferencePrice() : 1;
        item = ItemEngine.getInstance().getTemplate(_seedId);
        _seedReferencePrice = (item != null) ? item.getReferencePrice() : 1;
    }

    public final int getCastleId() {
        return _castleId;
    }

    public final int getSeedId() {
        return _seedId;
    }

    public final int getCropId() {
        return _cropId;
    }

    public final int getMatureId() {
        return _matureId;
    }

    public final int getReward(int type) {
        return (type == 1) ? _reward1 : _reward2;
    }

    public final int getLevel() {
        return _level;
    }

    public final boolean isAlternative() {
        return _isAlternative;
    }

    public final int getSeedLimit() {
        return _limitSeeds * Config.RATE_DROP_MANOR;
    }

    public final int getCropLimit() {
        return _limitCrops * Config.RATE_DROP_MANOR;
    }

    public final long getSeedReferencePrice() {
        return _seedReferencePrice;
    }

    public final long getSeedMaxPrice() {
        return _seedReferencePrice * 10;
    }

    public final int getSeedMinPrice() {
        return (int) (_seedReferencePrice * 0.6);
    }

    public final long getCropReferencePrice() {
        return _cropReferencePrice;
    }

    public final long getCropMaxPrice() {
        return _cropReferencePrice * 10;
    }

    public final int getCropMinPrice() {
        return (int) (_cropReferencePrice * 0.6);
    }

    @Override
    public final String toString() {
        return "SeedData [_id=" + _seedId + ", _level=" + _level + ", _crop=" + _cropId + ", _mature=" + _matureId + ", _type1=" + _reward1 + ", _type2=" + _reward2 + ", _manorId=" + _castleId + ", _isAlternative=" + _isAlternative + ", _limitSeeds=" + _limitSeeds + ", _limitCrops=" + _limitCrops + "]";
    }
}