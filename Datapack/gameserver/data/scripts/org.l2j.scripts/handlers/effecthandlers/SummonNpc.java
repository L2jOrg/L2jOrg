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
package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.EffectPoint;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.targets.TargetType;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Summon Npc effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class SummonNpc extends AbstractEffect {
    private int despawnDelay;
    private final int npcId;

    private SummonNpc(StatsSet params) {
        despawnDelay = params.getInt("despawn-delay", 20000);
        npcId = params.getInt("npc", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.SUMMON_NPC;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected) || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode()) {
            return;
        }

        if (npcId <= 0) {
            LOGGER.warn(SummonNpc.class.getSimpleName() + ": Invalid NPC ID or count skill ID: " + skill.getId());
            return;
        }

        final Player player = effected.getActingPlayer();
        if (player.isMounted()) {
            return;
        }

        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
        if (isNull(npcTemplate)) {
            LOGGER.warn("Spawn of the nonexisting NPC ID: {}, skill ID: {}",  npcId, skill.getId());
            return;
        }

        int x = player.getX();
        int y = player.getY();
        int z = player.getZ();

        if (skill.getTargetType() == TargetType.GROUND) {
            final Location wordPosition = player.getActingPlayer().getCurrentSkillWorldPosition();
            if (nonNull(wordPosition)) {
                x = wordPosition.getX();
                y = wordPosition.getY();
                z = wordPosition.getZ();
            }
        } else {
            x = effected.getX();
            y = effected.getY();
            z = effected.getZ();
        }

        switch (npcTemplate.getType()) {
            // TODO: Implement proper signet skills.
            case "EffectPoint" -> {
                final EffectPoint effectPoint = new EffectPoint(npcTemplate, player);
                effectPoint.setCurrentHp(effectPoint.getMaxHp());
                effectPoint.setCurrentMp(effectPoint.getMaxMp());
                effectPoint.setIsInvul(true);
                effectPoint.setSummoner(player);
                effectPoint.setTitle(player.getName());
                effectPoint.spawnMe(x, y, z);
                despawnDelay = effectPoint.getParameters().getInt("despawn_time", 0) * 1000;
                if (despawnDelay > 0) {
                    effectPoint.scheduleDespawn(despawnDelay);
                }
            }
            default -> {
                Spawn spawn;
                try {
                    spawn = new Spawn(npcTemplate);
                } catch (Exception e) {
                    LOGGER.warn("Unable to create spawn. " + e.getMessage(), e);
                    return;
                }

                spawn.setXYZ(x, y, z);
                spawn.setHeading(player.getHeading());
                spawn.stopRespawn();


                final Npc npc = spawn.doSpawn(false);
                player.addSummonedNpc(npc); // npc.setSummoner(player);
                npc.setName(npcTemplate.getName());
                npc.setTitle(npcTemplate.getName());
                if (despawnDelay > 0) {
                    npc.scheduleDespawn(despawnDelay);
                }
                npc.broadcastInfo();
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SummonNpc(data);
        }

        @Override
        public String effectName() {
            return "summon-npc";
        }
    }
}
