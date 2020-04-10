package org.l2j.gameserver.model.skills;

/**
 * @author Nik
 */
public enum SkillCastingType {
    SIMULTANEOUS(-1),
    NORMAL(0),
    NORMAL_SECOND(1),
    BLUE(2),
    GREEN(3),
    RED(4);

    private final int _clientBarId;

    SkillCastingType(int clientBarId) {
        _clientBarId = clientBarId;
    }

    public int getClientBarId() {
        return _clientBarId;
    }
}
