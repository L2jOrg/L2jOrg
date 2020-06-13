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
package instances.ResidenceOfKingIgnis;

import instances.AbstractInstance;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RobikBobik
 * @NOTE: Retail like working - I get informations from wiki and youtube video.
 * @TODO: Maybe rewrite code to modern style.
 * @TODO: Check skill 50050 - working, but I dont know if is correct.
 * @TODO: Ignis other skills - skills are implemented, but I dont know if is correct.
 */
public class ResidenceOfKingIgnis extends AbstractInstance
{
    // NPCs
    private static final int TARA = 34047;
    private static final int FREYA = 29109;

    // RAID
    private static final int IGNIS = 29105;

    // SKILLS
    private static SkillHolder FIRE_RAG_1 = new SkillHolder(50050, 1);
    private static SkillHolder FIRE_RAG_2 = new SkillHolder(50050, 2);
    private static SkillHolder FIRE_RAG_3 = new SkillHolder(50050, 3);
    private static SkillHolder FIRE_RAG_4 = new SkillHolder(50050, 4);
    private static SkillHolder FIRE_RAG_5 = new SkillHolder(50050, 5);
    private static SkillHolder FIRE_RAG_6 = new SkillHolder(50050, 6);
    private static SkillHolder FIRE_RAG_7 = new SkillHolder(50050, 7);
    private static SkillHolder FIRE_RAG_8 = new SkillHolder(50050, 8);
    private static SkillHolder FIRE_RAG_9 = new SkillHolder(50050, 9);
    private static SkillHolder FIRE_RAG_10 = new SkillHolder(50050, 10);
    private static SkillHolder FREYA_SAFETY_ZONE = new SkillHolder(50052, 1); // Just for an effect
    // Misc
    private static final Map<Player, Integer> _playerFireRage = new ConcurrentHashMap<>();
    private static final int TEMPLATE_ID = 195;

    private ResidenceOfKingIgnis()
    {
        super(TEMPLATE_ID);
        addStartNpc(TARA);
        addTalkId(FREYA);
        addKillId(IGNIS);
        addAttackId(IGNIS);
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
            case "REMOVE_FIRE_RAGE":
            {
                if (player.isAffectedBySkill(FIRE_RAG_1))
                {
                    final int playerFireRage = _playerFireRage.getOrDefault(player, 0);
                    if (playerFireRage < 5)
                    {
                        _playerFireRage.put(player, playerFireRage + 1);
                        player.stopSkillEffects(true, FIRE_RAG_1.getSkillId());
                        player.doCast(FREYA_SAFETY_ZONE.getSkill());
                        npc.broadcastSay(ChatType.NPC_SHOUT, "Bless with you. Lets finish fight!");
                        break;
                    }
                    npc.broadcastSay(ChatType.NPC_SHOUT, "You cannot use my power again.");
                    player.sendMessage("Freya: You cannot use my power again.");
                    break;
                }
                npc.broadcastSay(ChatType.NPC_SHOUT, "I help you only when you affected by Fire Rage skill.");
                break;
            }
            case "CAST_FIRE_RAGE_1":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_1.getSkill()))
                {
                    npc.doCast(FIRE_RAG_1.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_2":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_2.getSkill()))
                {
                    npc.doCast(FIRE_RAG_2.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_3":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_3.getSkill()))
                {
                    npc.doCast(FIRE_RAG_3.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_4":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_4.getSkill()))
                {
                    npc.doCast(FIRE_RAG_4.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_5":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_5.getSkill()))
                {
                    npc.doCast(FIRE_RAG_5.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_6":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_6.getSkill()))
                {
                    npc.doCast(FIRE_RAG_6.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_7":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_7.getSkill()))
                {
                    npc.doCast(FIRE_RAG_7.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_8":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_8.getSkill()))
                {
                    npc.doCast(FIRE_RAG_8.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_9":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_9.getSkill()))
                {
                    npc.doCast(FIRE_RAG_9.getSkill());
                }
                break;
            }
            case "CAST_FIRE_RAGE_10":
            {
                if (SkillCaster.checkUseConditions(npc, FIRE_RAG_10.getSkill()))
                {
                    npc.doCast(FIRE_RAG_10.getSkill());
                }
                break;
            }

        }
        return null;
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
    {
        if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.99)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.70)))
        {
            startQuestTimer("CAST_FIRE_RAGE_1", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.70)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.50)))
        {
            startQuestTimer("CAST_FIRE_RAGE_2", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.50)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.40)))
        {
            startQuestTimer("CAST_FIRE_RAGE_3", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.40)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.25)))
        {
            startQuestTimer("CAST_FIRE_RAGE_4", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.25)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.15)))
        {
            startQuestTimer("CAST_FIRE_RAGE_5", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.15)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.10)))
        {
            startQuestTimer("CAST_FIRE_RAGE_6", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.10)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.7)))
        {
            startQuestTimer("CAST_FIRE_RAGE_7", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.7)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.5)))
        {
            startQuestTimer("CAST_FIRE_RAGE_8", 1000, npc, null);
        }
        else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.5)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.3)))
        {
            startQuestTimer("CAST_FIRE_RAGE_9", 1000, npc, null);
        }
        else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.3))
        {
            startQuestTimer("CAST_FIRE_RAGE_10", 1000, npc, null);
        }
        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon)
    {
        final Instance world = npc.getInstanceWorld();
        if (world != null)
        {
            world.finishInstance();
        }
        return super.onKill(npc, player, isSummon);
    }

    public static AbstractInstance provider() {
        return new ResidenceOfKingIgnis();
    }
}
