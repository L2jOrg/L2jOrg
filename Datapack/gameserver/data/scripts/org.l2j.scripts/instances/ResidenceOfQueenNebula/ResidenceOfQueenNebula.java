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
package instances.ResidenceOfQueenNebula;

import instances.AbstractInstance;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author RobikBobik
 * @NOTE: Retail like working
 * @TODO: Rewrite code to modern style.
 * @TODO: The less Nebula's HP, the more damage she deals.
 */
public class ResidenceOfQueenNebula extends AbstractInstance
{
    // NPCs
    private static final int IRIS = 34046;
    private static final int NEBULA = 29106;
    private static final int WATER_SLIME = 29111;
    // Misc
    private static final int TEMPLATE_ID = 196;
    // Skills
    private static final int AQUA_RAGE = 50036;
    private static SkillHolder AQUA_RAGE_1 = new SkillHolder(AQUA_RAGE, 1);
    private static SkillHolder AQUA_RAGE_2 = new SkillHolder(AQUA_RAGE, 2);
    private static SkillHolder AQUA_RAGE_3 = new SkillHolder(AQUA_RAGE, 3);
    private static SkillHolder AQUA_RAGE_4 = new SkillHolder(AQUA_RAGE, 4);
    private static SkillHolder AQUA_RAGE_5 = new SkillHolder(AQUA_RAGE, 5);
    private static SkillHolder AQUA_SUMMON = new SkillHolder(50037, 1);

    private ResidenceOfQueenNebula()
    {
        super(TEMPLATE_ID);
        addStartNpc(IRIS);
        addKillId(NEBULA, WATER_SLIME);
        addAttackId(NEBULA);
        addInstanceLeaveId(TEMPLATE_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        switch (event)
        {
            case "ENTER":
            {
                enterInstance(player, npc, TEMPLATE_ID);
                break;
            }
            case "SPAWN_WATER_SLIME":
            {
                final Instance world = npc.getInstanceWorld();
                if (world != null)
                {
                    var plr = world.getPlayers().stream().findAny().orElse(null);
                    startQuestTimer("CAST_AQUA_RAGE", 60000 + getRandom(-15000, 15000), npc, plr);
                    if (npc.getId() == NEBULA)
                    {
                        npc.doCast(AQUA_SUMMON.getSkill());
                        for (int i = 0; i < getRandom(4, 6); i++)
                        {
                            addSpawn(npc, WATER_SLIME, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, -1, true, npc.getInstanceId());
                            startQuestTimer("SPAWN_WATER_SLIME", 300000, npc, null);
                        }
                    }
                }
                break;
            }
            case "PLAYER_PARA":
            {
                if (player.getAffectedSkillLevel(AQUA_RAGE) == 5)
                {
                    player.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.FROZEN_PILLAR);
                    player.setIsImmobilized(true);
                    startQuestTimer("PLAYER_UNPARA", 5000, npc, player);
                }
                break;
            }
            case "PLAYER_UNPARA":
            {
                player.getEffectList().stopSkillEffects(true, AQUA_RAGE_5.getSkill());
                player.setIsImmobilized(false);
                break;
            }
            case "CAST_AQUA_RAGE":
            {
                startQuestTimer("CAST_AQUA_RAGE", 5000, npc, player);
                if (MathUtil.isInsideRadius3D(player, npc, 1000))
                {
                    if (player.getAffectedSkillLevel(AQUA_RAGE) == 1)
                    {
                        if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_2.getSkill()))
                        {
                            npc.doCast(AQUA_RAGE_2.getSkill());
                        }
                    }
                    else if (player.getAffectedSkillLevel(AQUA_RAGE) == 2)
                    {
                        if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_3.getSkill()))
                        {
                            npc.doCast(AQUA_RAGE_3.getSkill());
                        }
                    }
                    else if (player.getAffectedSkillLevel(AQUA_RAGE) == 3)
                    {
                        if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_4.getSkill()))
                        {
                            npc.doCast(AQUA_RAGE_4.getSkill());
                        }
                    }
                    else if (player.getAffectedSkillLevel(AQUA_RAGE) == 4)
                    {
                        if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_5.getSkill()))
                        {
                            npc.doCast(AQUA_RAGE_5.getSkill());
                            startQuestTimer("PLAYER_PARA", 100, npc, player);
                        }
                    }
                    else if (player.getAffectedSkillLevel(AQUA_RAGE) == 5)
                    {
                        npc.abortCast();
                    }
                    else
                    {
                        if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_1.getSkill()))
                        {
                            npc.doCast(AQUA_RAGE_1.getSkill());
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    @Override
    public String onSpawn(Npc npc)
    {
        startQuestTimer("SPAWN_WATER_SLIME", 300000, npc, null);
        return super.onSpawn(npc);
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon)
    {
        switch (npc.getId())
        {
            case NEBULA:
            {
                cancelQuestTimer("CAST_AQUA_RAGE", npc, player);
                cancelQuestTimer("SPAWN_WATER_SLIME", npc, player);
                final Instance world = npc.getInstanceWorld();
                if (world != null)
                {
                    world.finishInstance();
                }
            }
            case WATER_SLIME:
            {
                if (player.getAffectedSkillLevel(AQUA_RAGE) == 1)
                {
                    if (getRandom(100) < 50)
                    {
                        player.stopSkillEffects(AQUA_RAGE_1.getSkill());
                    }
                }
                else if (player.getAffectedSkillLevel(AQUA_RAGE) == 2)
                {
                    if (getRandom(100) < 50)
                    {
                        player.stopSkillEffects(AQUA_RAGE_2.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(AQUA_RAGE, 1);
                        skill.applyEffects(player, player);
                    }
                }
                else if (player.getAffectedSkillLevel(AQUA_RAGE) == 3)
                {
                    if (getRandom(100) < 50)
                    {
                        player.stopSkillEffects(AQUA_RAGE_3.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(AQUA_RAGE, 2);
                        skill.applyEffects(player, player);
                    }
                }
                else if (player.getAffectedSkillLevel(AQUA_RAGE) == 4)
                {
                    if (getRandom(100) < 50)
                    {
                        player.stopSkillEffects(AQUA_RAGE_4.getSkill());
                        final Skill skill = SkillEngine.getInstance().getSkill(AQUA_RAGE, 3);
                        skill.applyEffects(player, player);
                    }
                }
            }
        }
        return super.onKill(npc, player, isSummon);
    }

    public static ResidenceOfQueenNebula provider()
    {
        return new ResidenceOfQueenNebula();
    }
}
