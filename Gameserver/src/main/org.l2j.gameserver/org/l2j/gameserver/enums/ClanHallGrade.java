package org.l2j.gameserver.enums;

/**
 * @author St3eT
 */
public enum ClanHallGrade {
    S(50),
    A(40),
    B(30),
    C(20),
    D(10),
    NONE(0);

    private final int _gradeValue;

    ClanHallGrade(int gradeValue) {
        _gradeValue = gradeValue;
    }

    public int getGradeValue() {
        return _gradeValue;
    }
}