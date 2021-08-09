package org.l2j.scripts.ai.bosses;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.scripts.ai.AbstractNpcAI;


public class RaidZone extends AbstractNpcAI {
    // NPCs
   // private static final Logger LOGGER = LoggerFactory.getLogger(RaidZone.class);
    private final static int[] RAID_BOSSES = {
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

    private RaidZone (){
        if(Config.ACTIVATE_PVP_BOSS_FLAG) addAttackId(RAID_BOSSES);
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon) {
        //LOGGER.info("{} is attacking {}", attacker, npc);
        if(GameUtils.isPlayer(attacker)) attacker.updatePvPStatus();
        return super.onAttack(npc, attacker, damage, isSummon);
    }

    public static RaidZone provider() {
        return new RaidZone();
    }
}
