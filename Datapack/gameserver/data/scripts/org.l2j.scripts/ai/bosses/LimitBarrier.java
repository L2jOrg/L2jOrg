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
package ai.bosses;

import ai.AbstractNpcAI;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limit Barrier AI
 * @author RobikBobik<br>
 *         OK - Many Raid Bosses lvl 50 and higher from now on use "Limit Barrier" skill when their HP reaches 90%, 60% and 30%.<br>
 *         OK - 500 hits in 15 seconds are required to destroy the barrier. Amount of damage does not matter.<br>
 *         OK - If barrier destruction is failed, Boss restores full HP.<br>
 *         OK - Death Knight, who randomly appear after boss's death, also use Limit Barrier.<br>
 *         OK - Epic Bosses Orfen, Queen Ant and Core also use Limit Barrier.<br>
 *         OK - Epic Bosses Antharas, Zaken and Baium and their analogues in instance zones do not use Limit Barrier.<br>
 *         OK - Raid Bosses in Clan Arena do not use Limit Barrier.<br>
 *         OK - All Raid Bosses who use Limit Barrier are listed below:<br>
 */
public final class LimitBarrier extends AbstractNpcAI {
    // NPCs
    private final static int[] RAID_BOSSES =
            {
                    29001, // Queen Ant
                    29006, // Core
                    29014, // Orfen
                    25010, // Furious Thiles
                    25013, // Ghose of Peasant Captain
                    25050, // Verfa
                    25067, // Red Flag Captain Shaka
                    25070, // Enchanted Valley Lookout Ruell
                    25089, // Soulless Wild Boar
                    25099, // Rooting Tree Repira
                    25103, // Wizard Isirr
                    25119, // Faire Queens Messenger Berun
                    25159, // Paniel the Unicorn
                    25122, // Refugee Applicant Leo
                    25131, // Slaughter Lord Gata
                    25137, // Beleth Seer Sephira
                    25176, // Black Lily
                    25217, // Cursed Clara
                    25230, // Timak Priest Ragothi
                    25241, // Harit Hero Tamashi
                    25418, // Dread Avenger Kraven
                    25420, // Orfens Handmaiden
                    25434, // Bandit Leader Barda
                    25460, // Deaman Ereve
                    25463, // Harit Guardian Garangky
                    25473, // Grave Robber Kim
                    25475, // Ghost Knight Kabed
                    25744, // Zombie Lord Darkhon
                    25745, // Orc Timak Darphen
                    18049, // Shilens Messenger Cabrio
                    25051, // Rahha
                    25106, // Ghost of the Well Lidia
                    25125, // Fierce Tiger King Angel
                    25163, // Roaring Skylancer
                    25226, // Roaring Lord Kastor
                    25234, // Ancient Weird Drake
                    25252, // Palibati Queen Themis
                    25255, // Gargayle Lord Tiphon
                    25256, // Taik High Prefect Arak
                    25263, // Kernons Faithul Servant Kelone
                    25407, // Lord Ishka
                    25423, // Fairy Queen Timiniel
                    25453, // Meanas Anor
                    25478, // Shilens Priest Hisilrome
                    25738, // Queen Ant Drone Priest
                    25739, // Angel Priest of Baium
                    25742, // Priest of Core Decar
                    25743, // Priest of Lord Ipos
                    25746, // Evil Magikus
                    25747, // Rael Mahum Radium
                    25748, // Rael Mahum Supercium
                    25749, // Tayga Feron King
                    25750, // Tayga Marga Shaman
                    25751, // Tayga Septon Champion
                    25754, // Flamestone Giant
                    25755, // Gross Salamander
                    25756, // Gross Dre Vanul
                    25757, // Gross Ifrit
                    25758, // Fiend Goblier
                    25759, // Fiend Cherkia
                    25760, // Fiend Harthemon
                    25761, // Fiend Sarboth
                    25762, // Demon Bedukel
                    25763, // Bloody Witch Rumilla
                    25766, // Monster Minotaur
                    25767, // Monster Bulleroth
                    25768, // Dorcaus
                    25769, // Kerfaus
                    25770, // Milinaus
                    25772, // Evil Orc Zetahl
                    25773, // Evil Orc Tabris
                    25774, // Evil Orc Ravolas
                    25775, // Evil Orc Dephracor
                    25776, // Amden Orc Turahot
                    25777, // Amden Orc Turation
                    25779, // Gariott
                    25780, // Varbasion
                    25781, // Varmoni
                    25782, // Overlord Muscel
                    25783, // Bathsus Elbogen
                    25784, // Daumen Kshana
                    25787, // Death Knight 1
                    25788, // Death Knight 2
                    25789, // Death Knight 3
                    25790, // Death Knight 4
                    25791, // Death Knight 5
                    25792, // Death Knight 6
                    25792, // Giant Golden Pig
            };
    // Skill
    private static final SkillHolder LIMIT_BARRIER = new SkillHolder(32203, 1);
    private static final Map<Npc, Integer> RAIDBOSS_HITS = new ConcurrentHashMap<>();

    private LimitBarrier()
    {
        addAttackId(RAID_BOSSES);
        addKillId(RAID_BOSSES);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        switch (event)
        {
            case "RESTORE_FULL_HP":
            {
                final int hits = RAIDBOSS_HITS.getOrDefault(npc, 0);
                if(!npc.isDead()) {
                    if (hits < Config.RAIDBOSS_LIMIT_BARRIER) {
                        if (player != null)
                            npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED_TO_DESTROY_THE_LIMIT_BARRIER_NTHE_RAID_BOSS_FULLY_RECOVERS_ITS_HEALTH, 2, 5000, true));
                        npc.setCurrentHp(npc.getStats().getMaxHp(), true);
                    } else if (hits > Config.RAIDBOSS_LIMIT_BARRIER) {
                        if (player != null)
                            npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_DESTROYED_THE_LIMIT_BARRIER, 2, 5000, true));
                    }
                    npc.stopSkillEffects(true, LIMIT_BARRIER.getSkillId());
                }
                RAIDBOSS_HITS.put(npc, 0);
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
    {
        if (npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()))
        {
            final int hits = RAIDBOSS_HITS.getOrDefault(npc, 0);
            RAIDBOSS_HITS.put(npc, hits + 1);
        }
        if (!npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()) && (getQuestTimers().get("RESTORE_FULL_HP") == null || getQuestTimers().get("RESTORE_FULL_HP").size() == 0))
            if (canCastBarrier(npc)) {
                startQuestTimer("RESTORE_FULL_HP", 15000, npc, attacker);
                npc.setTarget(npc);
                npc.abortAttack();
                npc.abortCast();
                npc.doCast(LIMIT_BARRIER.getSkill(), SkillCastingType.SIMULTANEOUS);
                npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SEC, 2, 5000, true));
            }

        return super.onAttack(npc, attacker, damage, isSummon, skill);
    }

    private boolean canCastBarrier(Npc npc) {
        return ( (npc.getCurrentHp() < (npc.getMaxHp() * 0.9)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.87)) )
                || ( (npc.getCurrentHp() < (npc.getMaxHp() * 0.6)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.58)) )
                || ( (npc.getCurrentHp() < (npc.getMaxHp() * 0.3)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.28)) );
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon)
    {
        RAIDBOSS_HITS.remove(npc);
        return super.onKill(npc, killer, isSummon);
    }


    public static LimitBarrier provider()
    {
        return new LimitBarrier();
    }
}
