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
package instances.ResidenceOfKingProcella;

import instances.AbstractInstance;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author RobikBobik
 * @NOTE: Retail like working
 * @TODO: Rewrite code to modern style.
 */
public class ResidenceOfKingProcella extends AbstractInstance {
    // NPCs
    private static final int WIRI = 34048;
    private static final int PROCELLA = 29107;
    private static final int PROCELLA_GUARDIAN_1 = 29112;
    private static final int PROCELLA_GUARDIAN_2 = 29113;
    private static final int PROCELLA_GUARDIAN_3 = 29114;
    private static final int PROCELLA_STORM = 29115;
    // Skills
    private static final SkillHolder HURRICANE_SUMMON = new SkillHolder(50042, 1); // When spawn Minion
    private static final int HURRICANE_BOLT = 50043;
    private static final SkillHolder HURRICANE_BOLT_LV_1 = new SkillHolder(50043, 1); // When player in Radius + para
    // Misc
    private static final int TEMPLATE_ID = 197;
    private static int STORM_MAX_COUNT = 16; // TODO: Max is limit ?
    private int _procellaStormCount;
    private RaidBoss _procella;
    private Monster _minion1;
    private Monster _minion2;
    private Monster _minion3;

    private ResidenceOfKingProcella()
    {
        super(TEMPLATE_ID);
        addStartNpc(WIRI);
        addKillId(PROCELLA, PROCELLA_GUARDIAN_1, PROCELLA_GUARDIAN_2, PROCELLA_GUARDIAN_3);
        addInstanceEnterId(TEMPLATE_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        switch (event)
        {
            case "ENTER":
            {
                enterInstance(player, npc, TEMPLATE_ID);
                _procella = (RaidBoss) addSpawn(PROCELLA, 212862, 179828, -15489, 49151, false, 0, true, player.getInstanceId());
                startQuestTimer("SPAWN_MINION", 300000 + getRandom(-15000, 15000), _procella, player);
                startQuestTimer("SPAWN_STORM", 5000, _procella, player);
                _procellaStormCount = 0;
                break;
            }
            case "SPAWN_MINION":
            {
                if (npc.getId() == PROCELLA)
                {
                    _minion1 = (Monster) addSpawn(PROCELLA_GUARDIAN_1, 212663, 179421, -15486, 31011, true, 0, true, npc.getInstanceId());
                    _minion2 = (Monster) addSpawn(PROCELLA_GUARDIAN_2, 213258, 179822, -15486, 12001, true, 0, true, npc.getInstanceId());
                    _minion3 = (Monster) addSpawn(PROCELLA_GUARDIAN_3, 212558, 179974, -15486, 12311, true, 0, true, npc.getInstanceId());
                    startQuestTimer("HIDE_PROCELLA", 1000, _procella, null);
                }
                break;
            }
            case "SPAWN_STORM":
            {
                if (_procellaStormCount < STORM_MAX_COUNT)
                {
                    _procella.useMagic(HURRICANE_SUMMON.getSkill());

                    final Npc procellaStorm = addSpawn(PROCELLA_STORM, _procella.getX() + getRandom(-500, 500), _procella.getY() + getRandom(-500, 500), _procella.getZ(), 31011, true, 0, true, npc.getInstanceId());
                    procellaStorm.setRandomWalking(true);
                    _procellaStormCount++;
                    startQuestTimer("SPAWN_STORM", 60000, _procella, null);
                    startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, procellaStorm, player);// All time checking
                }
                break;
            }
            case "HIDE_PROCELLA":
            {
                if (_procella.isInvisible())
                {
                    _procella.setInvisible(false);
                }
                else
                {
                    _procella.setInvisible(true);
                    startQuestTimer("SPAWN_MINION", 300000 + getRandom(-15000, 15000), _procella, player);
                }
                break;
            }
            case "CHECK_CHAR_INSIDE_RADIUS_NPC":
            {
                final Instance world = npc.getInstanceWorld();
                if (world != null)
                {
                    final Player plr = world.getPlayers().stream().findAny().orElse(null);
                    if ((plr != null) && (MathUtil.isInsideRadius3D(plr, npc, 100)))
                    {
                        npc.abortAttack();
                        npc.abortCast();
                        npc.setTarget(plr);
                        if (plr.getAffectedSkillLevel(HURRICANE_BOLT) == 1)
                        {
                            npc.abortCast();
                            startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);// All time checking
                        }
                        else
                        {
                            if (SkillCaster.checkUseConditions(npc, HURRICANE_BOLT_LV_1.getSkill()))
                            {
                                npc.doCast(HURRICANE_BOLT_LV_1.getSkill());
                            }
                        }
                        startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);// All time checking
                    }
                    else
                    {
                        startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", 100, npc, player);// All time checking
                    }
                }
                break;
            }
        }
        return null;
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon)
    {
        if (npc.getId() == PROCELLA)
        {
            final Instance world = npc.getInstanceWorld();
            if (world != null)
            {
                cancelQuestTimer("SPAWN_MINION", npc, player);
                cancelQuestTimer("SPAWN_STORM", npc, player);
                cancelQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC", npc, player);
                world.finishInstance();
            }
        }
        else if ((_minion1.isDead()) && (_minion2.isDead()) && (_minion3.isDead()))
        {
            startQuestTimer("HIDE_PROCELLA", 1000, _procella, null);
        }
        return super.onKill(npc, player, isSummon);
    }

    public static ResidenceOfKingProcella provider() {
        return new ResidenceOfKingProcella();
    }
}
