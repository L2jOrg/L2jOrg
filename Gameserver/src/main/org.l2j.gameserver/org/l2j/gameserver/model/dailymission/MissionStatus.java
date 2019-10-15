package org.l2j.gameserver.model.dailymission;

/**
 * @author UnAfraid
 */
public enum MissionStatus {
    AVAILABLE(1),
    NOT_AVAILABLE(2),
    COMPLETED(3);

    private int _clientId;

    MissionStatus(int clientId) {
        _clientId = clientId;
    }

    public static MissionStatus valueOf(int clientId) {
        for (MissionStatus type : values()) {
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
