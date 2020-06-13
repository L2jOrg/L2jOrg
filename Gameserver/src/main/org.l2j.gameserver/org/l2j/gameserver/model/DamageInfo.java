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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.STRING_EMPTY;
import static org.l2j.commons.util.Util.emptyIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class DamageInfo {

    private final int attackerId;
    private final int skillId;
    private final double damage;
    private final DamageType damageType;

    private DamageInfo(int attackerId, Skill skill, double damage, DamageType damageType) {
        this.attackerId = attackerId;
        this.skillId = nonNull(skill) ? skill.getId() : 0;
        this.damage = damage;
        this.damageType = damageType;
    }

    public short attackerType() {
        return 0;
    }

    public int attackerId() {
        return attackerId;
    }

    public String attackerName() {
        return STRING_EMPTY;
    }

    public int skillId() {
        return skillId;
    }

    public double damage(){
        return damage;
    }

    public short damageType() {
        return (short) damageType.clientId;
    }

    public static DamageInfo of(Creature attacker, Skill skill, double damage, DamageType damageType) {
        if(attacker instanceof Player player) {
            return new PlayerDamage(player, skill, damage, damageType);
        } else if(attacker instanceof Npc npc){
            return new NpcDamage(npc, skill, damage, damageType);
        }
        return new DamageInfo(0, skill, damage, damageType);
    }
    
    public static final class NpcDamage extends DamageInfo {
        private NpcDamage(Npc npc, Skill skill, double damage, DamageType damageType) {
            super(npc.getId(), skill, damage, damageType);
        }

        @Override
        public short attackerType() {
            return 1;
        }
    }

    public static final class PlayerDamage extends DamageInfo {

        private final String playerName;
        private final String clanName;

        private PlayerDamage(Player player, Skill skill, double damage, DamageType damageType) {
            super(player.getObjectId(), skill, damage, damageType);
            this.playerName = player.getAppearance().getVisibleName();
            this.clanName = emptyIfNullOrElse(player.getClan(), Clan::getName);
        }

        @Override
        public short attackerType() {
            return 2;
        }

        @Override
        public String attackerName() {
            return playerName;
        }

        public String clanName() {
            return clanName;
        }
    }

    public enum DamageType{
        OTHER(0),
        ATTACK(1),
        FALL(2),
        DROWN(3),
        ZONE(6),
        POISON(8),
        TRANSFERED_DAMAGE(9),
        REFLECT(14);

        private final int clientId;

        DamageType(int clientId) {
            this.clientId = clientId;
        }
    }

}


