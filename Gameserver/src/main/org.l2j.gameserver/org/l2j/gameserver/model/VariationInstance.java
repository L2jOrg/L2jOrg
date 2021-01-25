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

import org.l2j.gameserver.data.database.data.ItemVariationData;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Objects;

/**
 * Used to store an augmentation and its bonuses.
 *
 * @author durgus, UnAfraid, Pere
 * @author JoeAlisson
 */
public final class VariationInstance {

    private final ItemVariationData data;

    public VariationInstance(int itemObjectId, int mineralId, int option1Id, int option2Id) {
        Objects.requireNonNull(AugmentationEngine.getInstance().getOptions(option1Id), "Couldn't find option for id " + option1Id);
        Objects.requireNonNull(AugmentationEngine.getInstance().getOptions(option2Id), "Couldn't find option for id "+ option2Id);
        data = ItemVariationData.of(itemObjectId, mineralId, option1Id, option2Id);
    }

    public VariationInstance(ItemVariationData data) {
        Objects.requireNonNull(AugmentationEngine.getInstance().getOptions(data.getOption1()), "Couldn't find option for id " + data.getOption1());
        Objects.requireNonNull(AugmentationEngine.getInstance().getOptions(data.getOption2()), "Couldn't find option for id " + data.getOption2());
        this.data = data;
    }

    public int getMineralId() {
        return data.getMineralId();
    }

    public int getOption1Id() {
        return data.getOption1();
    }

    public int getOption2Id() {
        return data.getOption2();
    }

    public void applyBonus(Player player) {
        AugmentationEngine.getInstance().getOptions(data.getOption1()).apply(player);
        AugmentationEngine.getInstance().getOptions(data.getOption2()).apply(player);
    }

    public void removeBonus(Player player) {
        AugmentationEngine.getInstance().getOptions(data.getOption1()).remove(player);
        AugmentationEngine.getInstance().getOptions(data.getOption2()).remove(player);
    }

    public ItemVariationData getData() {
        return data;
    }
}