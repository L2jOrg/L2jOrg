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
package org.l2j.gameserver.enums;

/**
 * @author St3eT
 * @author JoeAlisson
 */
public enum ChatType {
    GENERAL,
    SHOUT,
    WHISPER,
    PARTY,
    CLAN,
    GM,
    PETITION_PLAYER,
    PETITION_GM,
    TRADE,
    ALLIANCE,
    ANNOUNCEMENT,
    BOAT,
    FRIEND,
    MSNCHAT,
    PARTYMATCH_ROOM,
    PARTYROOM_COMMANDER,
    PARTYROOM_ALL,
    HERO_VOICE,
    CRITICAL_ANNOUNCE,
    SCREEN_ANNOUNCE,
    BATTLEFIELD,
    MPCC_ROOM,
    NPC_GENERAL,
    NPC_SHOUT,
    NPC_WHISPER,
    WORLD;

    private static final ChatType[] CACHED = values();

    /**
     * Finds the {@code ChatType} by its clientId
     *
     * @param clientId the clientId
     * @return the {@code ChatType} if its found, {@code null} otherwise.
     */
    public static ChatType findByClientId(int clientId) {
        for (ChatType ChatType : CACHED) {
            if (ChatType.getClientId() == clientId) {
                return ChatType;
            }
        }
        return null;
    }

    public int getClientId() {
        return ordinal();
    }
}