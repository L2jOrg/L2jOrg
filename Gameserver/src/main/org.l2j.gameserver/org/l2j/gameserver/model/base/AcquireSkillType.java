package org.l2j.gameserver.model.base;

/**
 * Learning skill types.
 *
 * @author Zoey76
 */
public enum AcquireSkillType {
    CLASS(0),
    DUMMY(1),
    PLEDGE(2),
    SUBPLEDGE(3),
    TRANSFORM(4),
    DUMMY2(8),
    DUMMY3(9),
    FISHING(10);

    private final int _id;

    AcquireSkillType(int id) {
        _id = id;
    }

    public static AcquireSkillType getAcquireSkillType(int id) {
        for (AcquireSkillType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return _id;
    }
}
