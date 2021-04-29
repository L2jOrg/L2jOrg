/*
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public final class ShowTutorialMark extends ServerPacket {
    private final Mark mark;
    private final int _markType;

    private ShowTutorialMark(Mark mark, int markType) {
        this.mark = mark;
        _markType = markType;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SHOW_TUTORIAL_MARK, buffer );
        buffer.writeByte(_markType);
        buffer.writeInt(mark.id);
    }

    public static ShowTutorialMark question(Mark mark) {
        return new ShowTutorialMark(mark, 0);
    }

    public static ShowTutorialMark info(Mark mark) {
        return new ShowTutorialMark(mark, 1);
    }

    public enum Mark {
        CHAT(1),
        AUTO_USE_SUPPLIES(2),
        AUTO_HUNTING(3),
        AUTO_USE_POTION(4),
        ACTION_INFO(6),
        SHORTCUT_KEYS(7),
        L2_COIN(8),
        QUEST_PROGRESS(9),
        MAP_INFO(10),
        VIEW_MAP(12),
        BATTLE(13),
        SOULSHOTS_SPIRITSHOT(14),
        SKILLS(15),
        USING_ITEMS(16),
        SAYHA_GRACE(19),
        MAGIC_LAMP(20),
        MEET_NEWBIE_HELPER(21),
        PICKING_UP_ITEMS(23),
        MAIL(24),
        PARTY(28),
        TIMED_HUNTING_ZONE(30),
        ATTRIBUTE_SYSTEM(31),
        ATTRIBUTE_RELATIONS(32),
        BOSS_RAID_MONSTERS(33),
        TRADING_ITEMS(36),
        AUGMENTATION(39),
        SOUL_CRYSTAL(40),
        MONSTER_INFO(43),
        SERVITOR_INFO(44),
        LIST_OF_COMMANDS(45),
        TALISMAN(47),
        JEWEL(48),
        RANDOM_CRAFT(49),
        SHARE_LOCATION(52),
        PETS(53),
        TELEPORT(55),
        ;

        private final int id;

        Mark(int id) {
            this.id = id;
        }
    }

}