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
package org.l2j.gameserver.enums;

import org.l2j.gameserver.network.serverpackets.PlaySound;

/**
 * This enum contains known sound effects used by quests.<br>
 * The idea is to have only a single object of each quest sound instead of constructing a new one every time a script calls the playSound method.<br>
 * This is pretty much just a memory and CPU cycle optimization; avoids constructing/deconstructing objects all the time if they're all the same.<br>
 * For datapack scripts written in Java and extending the Quest class, this does not need an extra import.
 *
 * @author jurchiks
 */
public enum QuestSound {
    ITEMSOUND_QUEST_ACCEPT("ItemSound.quest_accept"),
    ITEMSOUND_QUEST_MIDDLE("ItemSound.quest_middle"),
    ITEMSOUND_QUEST_FINISH("ItemSound.quest_finish"),
    ITEMSOUND_QUEST_ITEMGET("ItemSound.quest_itemget"),
    // Newbie Guide tutorial (incl. some quests), Mutated Kaneus quests, Quest 192
    ITEMSOUND_QUEST_TUTORIAL("ItemSound.quest_tutorial"),
    // Quests 107, 363, 364
    ITEMSOUND_QUEST_GIVEUP("ItemSound.quest_giveup"),
    // Quests 212, 217, 224, 226, 416
    ITEMSOUND_QUEST_BEFORE_BATTLE("ItemSound.quest_before_battle"),
    // Quests 211, 258, 266, 330
    ITEMSOUND_QUEST_JACKPOT("ItemSound.quest_jackpot"),
    // Quests 508, 509 and 510
    ITEMSOUND_QUEST_FANFARE_1("ItemSound.quest_fanfare_1"),
    // Played only after class transfer via Test Server Helpers (ID 31756 and 31757)
    ITEMSOUND_QUEST_FANFARE_2("ItemSound.quest_fanfare_2"),
    // Quest 336
    ITEMSOUND_QUEST_FANFARE_MIDDLE("ItemSound.quest_fanfare_middle"),
    // Quest 114
    ITEMSOUND_ARMOR_WOOD("ItemSound.armor_wood_3"),
    // Quest 21
    ITEMSOUND_ARMOR_CLOTH("ItemSound.item_drop_equip_armor_cloth"),
    AMDSOUND_ED_CHIMES("AmdSound.ed_chimes_05"),
    HORROR_01("horror_01"), // played when spawned monster sees player
    // Quest 22
    AMBSOUND_HORROR_01("AmbSound.dd_horror_01"),
    AMBSOUND_HORROR_03("AmbSound.d_horror_03"),
    AMBSOUND_HORROR_15("AmbSound.d_horror_15"),
    // Quest 23
    ITEMSOUND_ARMOR_LEATHER("ItemSound.itemdrop_armor_leather"),
    ITEMSOUND_WEAPON_SPEAR("ItemSound.itemdrop_weapon_spear"),
    AMBSOUND_MT_CREAK("AmbSound.mt_creak01"),
    AMBSOUND_EG_DRON("AmbSound.eg_dron_02"),
    SKILLSOUND_HORROR_02("SkillSound5.horror_02"),
    CHRSOUND_MHFIGHTER_CRY("ChrSound.MHFighter_cry"),
    // Quest 24
    AMDSOUND_WIND_LOOT("AmdSound.d_wind_loot_02"),
    INTERFACESOUND_CHARSTAT_OPEN("InterfaceSound.charstat_open_01"),
    // Quest 25
    AMDSOUND_HORROR_02("AmdSound.dd_horror_02"),
    CHRSOUND_FDELF_CRY("ChrSound.FDElf_Cry"),
    // Quest 115
    AMBSOUND_WINGFLAP("AmbSound.t_wingflap_04"),
    AMBSOUND_THUNDER("AmbSound.thunder_02"),
    // Quest 120
    AMBSOUND_DRONE("AmbSound.ed_drone_02"),
    AMBSOUND_CRYSTAL_LOOP("AmbSound.cd_crystal_loop"),
    AMBSOUND_PERCUSSION_01("AmbSound.dt_percussion_01"),
    AMBSOUND_PERCUSSION_02("AmbSound.ac_percussion_02"),
    // Quest 648 and treasure chests
    ITEMSOUND_BROKEN_KEY("ItemSound2.broken_key"),
    // Quest 184
    ITEMSOUND_SIREN("ItemSound3.sys_siren"),
    // Quest 648
    ITEMSOUND_ENCHANT_SUCCESS("ItemSound3.sys_enchant_success"),
    ITEMSOUND_ENCHANT_FAILED("ItemSound3.sys_enchant_failed"),
    // Best farm mobs
    ITEMSOUND_SOW_SUCCESS("ItemSound3.sys_sow_success"),
    // Quest 25
    SKILLSOUND_HORROR_1("SkillSound5.horror_01"),
    // Quests 21 and 23
    SKILLSOUND_HORROR_2("SkillSound5.horror_02"),
    // Quest 22
    SKILLSOUND_ANTARAS_FEAR("SkillSound3.antaras_fear"),
    // Quest 505
    SKILLSOUND_JEWEL_CELEBRATE("SkillSound2.jewel.celebrate"),
    // Quest 373
    SKILLSOUND_LIQUID_MIX("SkillSound5.liquid_mix_01"),
    SKILLSOUND_LIQUID_SUCCESS("SkillSound5.liquid_success_01"),
    SKILLSOUND_LIQUID_FAIL("SkillSound5.liquid_fail_01"),
    // Quest 111
    ETCSOUND_ELROKI_SONG_FULL("EtcSound.elcroki_song_full"),
    ETCSOUND_ELROKI_SONG_1ST("EtcSound.elcroki_song_1st"),
    ETCSOUND_ELROKI_SONG_2ND("EtcSound.elcroki_song_2nd"),
    ETCSOUND_ELROKI_SONG_3RD("EtcSound.elcroki_song_3rd"),
    // Long duration AI sounds
    BS01_A("BS01_A"),
    BS02_A("BS02_A"),
    BS03_A("BS03_A"),
    BS04_A("BS04_A"),
    BS06_A("BS06_A"),
    BS07_A("BS07_A"),
    BS08_A("BS08_A"),
    BS01_D("BS01_D"),
    BS02_D("BS02_D"),
    BS05_D("BS05_D"),
    BS07_D("BS07_D");
    
    private final String sound;

    QuestSound(String playSound) {
        sound = playSound;
    }


    /**
     * @return the {@link PlaySound} packet of this QuestSound object
     */
    public PlaySound getPacket() {
        return PlaySound.sound(sound);
    }
}
