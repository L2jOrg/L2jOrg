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
package org.l2j.gameserver.ai;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author DS
 */
public abstract class VehicleAI extends CreatureAI {
    /**
     * Simple AI for vehicles
     *
     * @param vehicle
     */
    public VehicleAI(Vehicle vehicle) {
        super(vehicle);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
    }

    @Override
    protected void onIntentionFollow(Creature target) {
    }

    @Override
    protected void onIntentionPickUp(WorldObject item) {
    }

    @Override
    protected void onIntentionInteract(WorldObject object) {
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    protected void onEvtActionBlocked(Creature attacker) {
    }

    @Override
    protected void onEvtRooted(Creature attacker) {
    }

    @Override
    protected void onEvtForgetObject(WorldObject object) {
    }

    @Override
    protected void onEvtCancel() {
    }

    @Override
    protected void onEvtDead() {
    }

    @Override
    protected void onEvtFakeDeath() {
    }

    @Override
    protected void onEvtFinishCasting() {
    }

    @Override
    protected void clientActionFailed() {
    }

    @Override
    public void moveToPawn(WorldObject pawn, int offset) {
    }

    @Override
    protected void clientStoppedMoving() {
    }
}