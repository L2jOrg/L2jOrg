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