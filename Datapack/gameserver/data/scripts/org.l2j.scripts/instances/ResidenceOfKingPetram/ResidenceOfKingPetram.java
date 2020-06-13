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
package instances.ResidenceOfKingPetram;

import instances.AbstractInstance;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;

/**
 * @author RobikBobik
 * @NOTE: Retail like working
 * @TODO: Rewrite code to modern style.
 * @TODO: Petram Skills and minion skills
 */
public class ResidenceOfKingPetram extends AbstractInstance
{
    // NPCs
    private static final int TRITAN = 34049;
    private static final int PETRAM = 29108;
    private static final int PETRAM_PIECE = 29116;
    private static final int PETRAM_FRAGMENT = 29117;
    // Skills
    private static SkillHolder EARTh_ENERGY = new SkillHolder(50066, 1); // When spawn Minion.
    private static SkillHolder EARTh_FURY = new SkillHolder(50059, 1); // When change invul state.
    private static SkillHolder TEST = new SkillHolder(5712, 1); // TODO: This test skill is only for visual effect, but need to find correct skill ID.
    // Misc
    private static final int TEMPLATE_ID = 198;
    private RaidBoss _petram = null;
    private Monster _minion_1 = null;
    private Monster _minion_2 = null;
    private Monster _minion_3 = null;
    private Monster _minion_4 = null;
    private boolean _spawned_minions;

    public ResidenceOfKingPetram()
    {
        super(TEMPLATE_ID);
        addStartNpc(TRITAN);
        addKillId(PETRAM, PETRAM_PIECE, PETRAM_FRAGMENT);
        addAttackId(PETRAM);
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
                if (player.getInstanceWorld() != null)
                {
                    _petram = (RaidBoss) player.getInstanceWorld().getNpc(PETRAM);
                }
                break;
            }
            case "SPAWN_MINION":
            {
                _petram.useMagic(EARTh_ENERGY.getSkill());

                // Prevent to double or higher spawn when HP is between 68-70% + etc...
                if (!_spawned_minions)
                {
                    _minion_1 = (Monster) addSpawn(npc, PETRAM_PIECE, 221543, 191530, -15486, 1131, false, -1, true, npc.getInstanceId());
                    _minion_2 = (Monster) addSpawn(npc, PETRAM_FRAGMENT, 222069, 192019, -15486, 49364, false, -1, true, npc.getInstanceId());
                    _minion_3 = (Monster) addSpawn(npc, PETRAM_PIECE, 222595, 191479, -15486, 34013, false, -1, true, npc.getInstanceId());
                    _minion_4 = (Monster) addSpawn(npc, PETRAM_FRAGMENT, 222077, 191017, -15486, 16383, false, -1, true, npc.getInstanceId());
                    _spawned_minions = true;
                }

                startQuestTimer("SUPPORT_PETRAM", 3000, npc, null);
                break;
            }
            case "SUPPORT_PETRAM":
            {
                _minion_1.setTarget(_petram);
                _minion_1.useMagic(TEST.getSkill());
                _minion_2.setTarget(_petram);
                _minion_2.useMagic(TEST.getSkill());
                _minion_3.setTarget(_petram);
                _minion_3.useMagic(TEST.getSkill());
                _minion_4.setTarget(_petram);
                _minion_4.useMagic(TEST.getSkill());
                startQuestTimer("SUPPORT_PETRAM", 10100, npc, null); // NOTE: When find correct skill this number is reuse skill + 100
                break;
            }
            case "INVUL_MODE":
            {
                _petram.useMagic(EARTh_FURY.getSkill());
                if (_petram.isInvul())
                {
                    _petram.setIsInvul(false);
                    _petram.broadcastSay(ChatType.NPC_SHOUT, "Nooooo... Nooooo...");
                }
                else
                {
                    _petram.setIsInvul(true);
                    _petram.broadcastSay(ChatType.NPC_SHOUT, "HaHa, fighters lets kill them. Now Im invul!!!");
                }
                break;
            }
        }
        return null;
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
    {
        if (npc.getId() == PETRAM)
        {
            if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.70)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.68)))
            {
                startQuestTimer("INVUL_MODE", 1000, npc, null);
                startQuestTimer("SPAWN_MINION", 1000, npc, null);
            }
            else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.40)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.38)))
            {
                startQuestTimer("INVUL_MODE", 1000, npc, null);
                startQuestTimer("SPAWN_MINION", 1000, npc, null);
            }
            else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.20)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.18)))
            {
                startQuestTimer("INVUL_MODE", 1000, npc, null);
                startQuestTimer("SPAWN_MINION", 1000, npc, null);
            }
            else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.10)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.08)))
            {
                startQuestTimer("INVUL_MODE", 1000, npc, null);
                startQuestTimer("SPAWN_MINION", 1000, npc, null);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon)
    {
        if (npc.getId() == PETRAM)
        {
            final Instance world = npc.getInstanceWorld();
            if (world != null)
            {
                world.finishInstance();
            }
        }
        else if ((_minion_1.isDead()) && (_minion_2.isDead()) && (_minion_3.isDead()) && (_minion_4.isDead()))
        {
            startQuestTimer("INVUL_MODE", 3000, _petram, null);
            _spawned_minions = false;
        }
        return super.onKill(npc, player, isSummon);
    }

    public static ResidenceOfKingPetram provider() {
        return new ResidenceOfKingPetram();
    }
}