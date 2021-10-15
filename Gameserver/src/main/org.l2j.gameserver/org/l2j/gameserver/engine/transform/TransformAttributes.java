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
package org.l2j.gameserver.engine.transform;

import org.l2j.gameserver.model.item.type.WeaponType;

/**
 * @author JoeAlisson
 */
public record TransformAttributes (
        int range,
        int attackSpeed,
        WeaponType attackType,
        int criticalRate,
        int magicAttack,
        int physicAttack,
        int randomDamage,
        float radius,
        float femaleRadius,
        float height,
        float femaleHeight,
        int walk,
        int run,
        int waterWalk,
        int waterRun,
        int flyWalk,
        int flyRun) { }