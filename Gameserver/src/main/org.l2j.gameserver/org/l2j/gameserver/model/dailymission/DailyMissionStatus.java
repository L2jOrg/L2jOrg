package org.l2j.gameserver.model.dailymission;

/**
 * @author UnAfraid
 */
public enum DailyMissionStatus {
    AVAILABLE(1),
    NOT_AVAILABLE(2),
    COMPLETED(3);

    private int _clientId;

    DailyMissionStatus(int clientId) {
        _clientId = clientId;
    }

    public static DailyMissionStatus valueOf(int clientId) {
        for (DailyMissionStatus type : values()) {
            if (type.getClientId() == clientId) {
                return type;
            }
        }
        return null;
    }

    public int getClientId() {
        return _clientId;
    }
}
