/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.data.sql.impl;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nyaran
 */
public class SummonEffectsTable {
    /**
     * Servitors
     **/
    // Map tree
    // -> key: charObjectId, value: classIndex Map
    // --> key: classIndex, value: servitors Map
    // ---> key: servitorSkillId, value: Effects list
    private final Map<Integer, Map<Integer, Map<Integer, List<SummonEffect>>>> _servitorEffects = new HashMap<>();
    /**
     * Pets
     **/
    private final Map<Integer, List<SummonEffect>> _petEffects = new HashMap<>(); // key: petItemObjectId, value: Effects list

    public static SummonEffectsTable getInstance() {
        return SingletonHolder._instance;
    }

    public Map<Integer, Map<Integer, Map<Integer, List<SummonEffect>>>> getServitorEffectsOwner() {
        return _servitorEffects;
    }

    public Map<Integer, List<SummonEffect>> getServitorEffects(L2PcInstance owner) {
        final Map<Integer, Map<Integer, List<SummonEffect>>> servitorMap = _servitorEffects.get(owner.getObjectId());
        if (servitorMap == null) {
            return null;
        }
        return servitorMap.get(owner.getClassIndex());
    }

    public Map<Integer, List<SummonEffect>> getPetEffects() {
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

    private static class SingletonHolder {
        protected static final SummonEffectsTable _instance = new SummonEffectsTable();
    }
}
