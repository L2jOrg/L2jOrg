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
package org.l2j.gameserver.model.skills.targets;

/**
 * Affect scope enumerated.
 *
 * @author Zoey76
 */
public enum AffectScope {
    /**
     * Affects Valakas.
     */
    VALAKAS_SCOPE,
    /**
     * Affects dead clan mates.
     */
    DEAD_PLEDGE,
    /**
     * Affects dead union (Command Channel?) members.
     */
    DEAD_UNION,
    /**
     * Affects fan area.
     */
    FAN,
    /**
     * Affects fan area, using caster as point of origin..
     */
    FAN_PB,
    /**
     * Affects nothing.
     */
    NONE,
    /**
     * Affects party members.
     */
    PARTY,
    /**
     * Affects dead party members.
     */
    DEAD_PARTY,
    /**
     * Affects party and clan mates.
     */
    PARTY_PLEDGE,
    /**
     * Affects dead party and clan members.
     */
    DEAD_PARTY_PLEDGE,
    /**
     * Affects clan mates.
     */
    PLEDGE,
    /**
     * Affects point blank targets, using caster as point of origin.
     */
    POINT_BLANK,
    /**
     * Affects ranged targets, using selected target as point of origin.
     */
    RANGE,
    /**
     * Affects ranged targets, using selected target as point of origin sorted by lowest to highest HP.
     */
    RANGE_SORT_BY_HP,
    /**
     * Affects targets in donut shaped area, using caster as point of origin.
     */
    RING_RANGE,
    /**
     * Affects a single target.
     */
    SINGLE,
    /**
     * Affects targets inside an square area, using selected target as point of origin.
     */
    SQUARE,
    /**
     * Affects targets inside an square area, using caster as point of origin.
     */
    SQUARE_PB,
    /**
     * Affects static object targets.
     */
    STATIC_OBJECT_SCOPE,
    /**
     * Affects all summons except master.
     */
    SUMMON_EXCEPT_MASTER,
    /**
     * Affects wyverns.
     */
    WYVERN_SCOPE
}
