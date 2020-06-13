/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.enums;

import org.l2j.gameserver.network.serverpackets.PlaySound;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contains known sound effects used by quests.<br>
 * The idea is to have only a single object of each quest sound instead of constructing a new one every time a script calls the playSound method.<br>
 * This is pretty much just a memory and CPU cycle optimization; avoids constructing/deconstructing objects all the time if they're all the same.<br>
 * For datapack scripts written in Java and extending the Quest class, this does not need an extra import.
 *
 * @author jurchiks
 */
public enum QuestSound {
    ITEMSOUND_QUEST_ACCEPT(new PlaySound("ItemSound.quest_accept")),
    ITEMSOUND_QUEST_MIDDLE(new PlaySound("ItemSound.quest_middle")),
    ITEMSOUND_QUEST_FINISH(new PlaySound("ItemSound.quest_finish")),
    ITEMSOUND_QUEST_ITEMGET(new PlaySound("ItemSound.quest_itemget")),
    // Newbie Guide tutorial (incl. some quests), Mutated Kaneus quests, Quest 192
    ITEMSOUND_QUEST_TUTORIAL(new PlaySound("ItemSound.quest_tutorial")),
    // Quests 107, 363, 364
    ITEMSOUND_QUEST_GIVEUP(new PlaySound("ItemSound.quest_giveup")),
    // Quests 212, 217, 224, 226, 416
    ITEMSOUND_QUEST_BEFORE_BATTLE(new PlaySound("ItemSound.quest_before_battle")),
    // Quests 211, 258, 266, 330
    ITEMSOUND_QUEST_JACKPOT(new PlaySound("ItemSound.quest_jackpot")),
    // Quests 508, 509 and 510
    ITEMSOUND_QUEST_FANFARE_1(new PlaySound("ItemSound.quest_fanfare_1")),
    // Played only after class transfer via Test Server Helpers (ID 31756 and 31757)
    ITEMSOUND_QUEST_FANFARE_2(new PlaySound("ItemSound.quest_fanfare_2")),
    // Quest 336
    ITEMSOUND_QUEST_FANFARE_MIDDLE(new PlaySound("ItemSound.quest_fanfare_middle")),
    // Quest 114
    ITEMSOUND_ARMOR_WOOD(new PlaySound("ItemSound.armor_wood_3")),
    // Quest 21
    ITEMSOUND_ARMOR_CLOTH(new PlaySound("ItemSound.item_drop_equip_armor_cloth")),
    AMDSOUND_ED_CHIMES(new PlaySound("AmdSound.ed_chimes_05")),
    HORROR_01(new PlaySound("horror_01")), // played when spawned monster sees player
    // Quest 22
    AMBSOUND_HORROR_01(new PlaySound("AmbSound.dd_horror_01")),
    AMBSOUND_HORROR_03(new PlaySound("AmbSound.d_horror_03")),
    AMBSOUND_HORROR_15(new PlaySound("AmbSound.d_horror_15")),
    // Quest 23
    ITEMSOUND_ARMOR_LEATHER(new PlaySound("ItemSound.itemdrop_armor_leather")),
    ITEMSOUND_WEAPON_SPEAR(new PlaySound("ItemSound.itemdrop_weapon_spear")),
    AMBSOUND_MT_CREAK(new PlaySound("AmbSound.mt_creak01")),
    AMBSOUND_EG_DRON(new PlaySound("AmbSound.eg_dron_02")),
    SKILLSOUND_HORROR_02(new PlaySound("SkillSound5.horror_02")),
    CHRSOUND_MHFIGHTER_CRY(new PlaySound("ChrSound.MHFighter_cry")),
    // Quest 24
    AMDSOUND_WIND_LOOT(new PlaySound("AmdSound.d_wind_loot_02")),
    INTERFACESOUND_CHARSTAT_OPEN(new PlaySound("InterfaceSound.charstat_open_01")),
    // Quest 25
    AMDSOUND_HORROR_02(new PlaySound("AmdSound.dd_horror_02")),
    CHRSOUND_FDELF_CRY(new PlaySound("ChrSound.FDElf_Cry")),
    // Quest 115
    AMBSOUND_WINGFLAP(new PlaySound("AmbSound.t_wingflap_04")),
    AMBSOUND_THUNDER(new PlaySound("AmbSound.thunder_02")),
    // Quest 120
    AMBSOUND_DRONE(new PlaySound("AmbSound.ed_drone_02")),
    AMBSOUND_CRYSTAL_LOOP(new PlaySound("AmbSound.cd_crystal_loop")),
    AMBSOUND_PERCUSSION_01(new PlaySound("AmbSound.dt_percussion_01")),
    AMBSOUND_PERCUSSION_02(new PlaySound("AmbSound.ac_percussion_02")),
    // Quest 648 and treasure chests
    ITEMSOUND_BROKEN_KEY(new PlaySound("ItemSound2.broken_key")),
    // Quest 184
    ITEMSOUND_SIREN(new PlaySound("ItemSound3.sys_siren")),
    // Quest 648
    ITEMSOUND_ENCHANT_SUCCESS(new PlaySound("ItemSound3.sys_enchant_success")),
    ITEMSOUND_ENCHANT_FAILED(new PlaySound("ItemSound3.sys_enchant_failed")),
    // Best farm mobs
    ITEMSOUND_SOW_SUCCESS(new PlaySound("ItemSound3.sys_sow_success")),
    // Quest 25
    SKILLSOUND_HORROR_1(new PlaySound("SkillSound5.horror_01")),
    // Quests 21 and 23
    SKILLSOUND_HORROR_2(new PlaySound("SkillSound5.horror_02")),
    // Quest 22
    SKILLSOUND_ANTARAS_FEAR(new PlaySound("SkillSound3.antaras_fear")),
    // Quest 505
    SKILLSOUND_JEWEL_CELEBRATE(new PlaySound("SkillSound2.jewel.celebrate")),
    // Quest 373
    SKILLSOUND_LIQUID_MIX(new PlaySound("SkillSound5.liquid_mix_01")),
    SKILLSOUND_LIQUID_SUCCESS(new PlaySound("SkillSound5.liquid_success_01")),
    SKILLSOUND_LIQUID_FAIL(new PlaySound("SkillSound5.liquid_fail_01")),
    // Quest 111
    ETCSOUND_ELROKI_SONG_FULL(new PlaySound("EtcSound.elcroki_song_full")),
    ETCSOUND_ELROKI_SONG_1ST(new PlaySound("EtcSound.elcroki_song_1st")),
    ETCSOUND_ELROKI_SONG_2ND(new PlaySound("EtcSound.elcroki_song_2nd")),
    ETCSOUND_ELROKI_SONG_3RD(new PlaySound("EtcSound.elcroki_song_3rd")),
    // Long duration AI sounds
    BS01_A(new PlaySound("BS01_A")),
    BS02_A(new PlaySound("BS02_A")),
    BS03_A(new PlaySound("BS03_A")),
    BS04_A(new PlaySound("BS04_A")),
    BS06_A(new PlaySound("BS06_A")),
    BS07_A(new PlaySound("BS07_A")),
    BS08_A(new PlaySound("BS08_A")),
    BS01_D(new PlaySound("BS01_D")),
    BS02_D(new PlaySound("BS02_D")),
    BS05_D(new PlaySound("BS05_D")),
    BS07_D(new PlaySound("BS07_D"));

    private static Map<String, PlaySound> soundPackets = new HashMap<>();
    private final PlaySound _playSound;

    QuestSound(PlaySound playSound) {
        _playSound = playSound;
    }

    /**
     * Get a {@link PlaySound} packet by its name.
     *
     * @param soundName the name of the sound to look for
     * @return the {@link PlaySound} packet with the specified sound or {@code null} if one was not found
     */
    public static PlaySound getSound(String soundName) {
        if (soundPackets.containsKey(soundName)) {
            return soundPackets.get(soundName);
        }

        for (QuestSound qs : QuestSound.values()) {
            if (qs._playSound.getSoundName().equals(soundName)) {
                soundPackets.put(soundName, qs._playSound); // cache in map to avoid looping repeatedly
                return qs._playSound;
            }
        }

        soundPackets.put(soundName, new PlaySound(soundName));
        return soundPackets.get(soundName);
    }

    /**
     * @return the name of the sound of this QuestSound object
     */
    public String getSoundName() {
        return _playSound.getSoundName();
    }

    /**
     * @return the {@link PlaySound} packet of this QuestSound object
     */
    public PlaySound getPacket() {
        return _playSound;
    }
}
