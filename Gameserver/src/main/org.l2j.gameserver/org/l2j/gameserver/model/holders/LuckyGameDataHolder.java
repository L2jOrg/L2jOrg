/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sdw
 */
public class LuckyGameDataHolder {
    private final int _index;
    private final int _turningPoints;
    private final List<ItemChanceHolder> _commonRewards = new ArrayList<>();
    private final List<ItemPointHolder> _uniqueRewards = new ArrayList<>();
    private final List<ItemChanceHolder> _modifyRewards = new ArrayList<>();
    private int _minModifyRewardGame;
    private int _maxModifyRewardGame;

    public LuckyGameDataHolder(StatsSet params) {
        _index = params.getInt("index");
        _turningPoints = params.getInt("turning_point");
    }

    public void addCommonReward(ItemChanceHolder item) {
        _commonRewards.add(item);
    }

    public void addUniqueReward(ItemPointHolder item) {
        _uniqueRewards.add(item);
    }

    public void addModifyReward(ItemChanceHolder item) {
        _modifyRewards.add(item);
    }

    public List<ItemChanceHolder> getCommonReward() {
        return _commonRewards;
    }

    public List<ItemPointHolder> getUniqueReward() {
        return _uniqueRewards;
    }

    public List<ItemChanceHolder> getModifyReward() {
        return _modifyRewards;
    }

    public int getMinModifyRewardGame() {
        return _minModifyRewardGame;
    }

    public void setMinModifyRewardGame(int minModifyRewardGame) {
        _minModifyRewardGame = minModifyRewardGame;
    }

    public int getMaxModifyRewardGame() {
        return _maxModifyRewardGame;
    }

    public void setMaxModifyRewardGame(int maxModifyRewardGame) {
        _maxModifyRewardGame = maxModifyRewardGame;
    }

    public int getIndex() {
        return _index;
    }

    public int getTurningPoints() {
        return _turningPoints;
    }
}
