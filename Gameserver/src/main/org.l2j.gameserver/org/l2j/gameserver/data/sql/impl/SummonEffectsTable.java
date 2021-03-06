/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Collection;

/**
 * @author Nyaran
 * @author JoeAlisson
 */
public class SummonEffectsTable {

    // Map tree
    // -> key: charObjectId, value: classIndex Map
    // ---> key: servitorSkillId, value: Effects list
    private final IntMap<IntMap<Collection<SummonEffect>>> _servitorEffects = new HashIntMap<>();

    private final IntMap<Collection<SummonEffect>> _petEffects = new HashIntMap<>(); // key: petItemObjectId, value: Effects list

    private SummonEffectsTable() {
    }

    public IntMap<IntMap<Collection<SummonEffect>>> getServitorEffectsOwner() {
        return _servitorEffects;
    }

    public IntMap<Collection<SummonEffect>> getServitorEffects(Player owner) {
        return _servitorEffects.getOrDefault(owner.getObjectId(), Containers.emptyIntMap());
    }

    public IntMap<Collection<SummonEffect>> getPetEffects() {
        return _petEffects;
    }

    public static class SummonEffect {

        private final Skill _skill;
        private final int _effectCurTime;
        public SummonEffect(Skill skill, int effectCurTime) {
            _skill = skill;
            _effectCurTime = effectCurTime;
        }

        public Skill getSkill() {
            return _skill;
        }

        public int getEffectCurTime() {
            return _effectCurTime;
        }

    }

    public static SummonEffectsTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SummonEffectsTable INSTANCE = new SummonEffectsTable();
    }
}
