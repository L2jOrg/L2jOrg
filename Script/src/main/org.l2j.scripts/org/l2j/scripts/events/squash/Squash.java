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
package org.l2j.scripts.events.squash;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.LongTimeEvent;
import org.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * @author JoeAlisson
 */
public abstract class Squash extends LongTimeEvent {

    private static final IntSet CHRONO_LIST = IntSet.of(4202, 5133, 5817, 7058, 8350);

    private static final String[] NOCHRONO_TEXT = {
        "You cannot kill me without Chrono",
        "Hehe...keep trying...",
        "Nice try...",
        "Tired ?",
        "Go go ! haha..."
    };

    private static final String[] CHRONO_TEXT = {
        "Arghh... Chrono weapon...",
        "My end is coming...",
        "Please leave me!",
        "Heeellpppp...",
        "Somebody help me please..."
    };

    private static final String[] NECTAR_TEXT = {
        "Yummie... Nectar...",
        "Plase give me more...",
        "Hmmm.. More.. I need more...",
        "I would like you more, if you give me more...",
        "Hmmmmmmm...",
        "My favourite..."
    };

    public static final int YOUNG_SQUASH = 13399;
    public static final int LARGE_YOUNG_SQUASH = 13403;

    private final IntSet squashes;
    private final IntSet largeSquashes;

    protected Squash(int manager, IntSet squashes, IntSet largeSquashes) {
        addAttackId(squashes);
        addSpawnId(squashes);
        addSpawnId(largeSquashes);
        addSkillSeeId(squashes);

        addStartNpc(manager);
        addFirstTalkId(manager);
        addTalkId(manager);

        this.squashes = squashes;
        this.largeSquashes = largeSquashes;
    }

    @Override
    public String onSpawn(Npc npc) {
        npc.setIsImmobilized(true);
        npc.disableCoreAI(true);

        if (largeSquashes.contains(npc.getId())) {
            npc.setIsInvul(true);
        }
        return null;
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isPet) {
        if (largeSquashes.contains(npc.getId())) {
            var weapon = attacker.getActiveWeaponInstance();
            if (weapon != null && CHRONO_LIST.contains(weapon.getId())) {
                sayText(npc, 20, CHRONO_TEXT);
                npc.setIsInvul(false);
                npc.getStatus().reduceHp(10, attacker);
            } else {
                sayText(npc, 20, NOCHRONO_TEXT);
                npc.setIsInvul(true);
            }
        }

        return super.onAttack(npc, attacker, damage, isPet);
    }

    @Override
    public String onSkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isPet) {
        if (squashes.contains(npc.getId()) && skill.getId() == getNectaSkill()) {
            int id = npc.getId();
            if (id == getYoungSquash()) {
                randomSpawn(squashSpawnChances(), npc);
            } else if (id == getLargeYoungSquash()) {
                randomSpawn(largeSquashSpawnChances(), npc);
            }
        }
        return super.onSkillSee(npc, caster, skill, targets, isPet);
    }


    @Override
    public String onFirstTalk(Npc npc, Player player)
    {
        return npc.getId() + ".htm";
    }

    private void randomSpawn(int[][] spawnChances, Npc npc) {
        for (int[] spawnChance : spawnChances) {
            if (Rnd.chance(spawnChance[1])) {
                spawnSquash(spawnChance[0], npc);
                return;
            }
        }
        sayText(npc, 30, NECTAR_TEXT);
    }

    private void sayText(Npc npc, int chance, String[] texts) {
        if(Rnd.chance(chance)) {
            npc.broadcastPacket(new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), Rnd.get(texts)));
        }
    }

    private void spawnSquash(int npcId, Npc npc) {
        addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 60000);
        npc.deleteMe();
    }

    protected abstract int getNectaSkill();

    protected abstract int getYoungSquash();

    protected abstract int getLargeYoungSquash();

    protected abstract int[][] squashSpawnChances();

    protected abstract int[][] largeSquashSpawnChances();
}