package org.l2j.gameserver.enums;

public enum LampType
{
    RED(1),
    PURPLE(2),
    BLUE(3),
    GREEN(4);

    private final int _grade;

    LampType(int grade)
    {
        _grade = grade;
    }

    public int getGrade()
    {
        return _grade;
    }
}