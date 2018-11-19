package l2s.gameserver.templates;

public final class ExperienceData
{
    private final int _level;
    private final long _exp;
    private final double _trainingRate;

    public ExperienceData(int level, long exp, double trainingRate)
    {
        _level = level;
        _exp = exp;
        _trainingRate = trainingRate;
    }

    public int getLevel()
    {
        return _level;
    }

    public long getExp()
    {
        return _exp;
    }

    public double getTrainingRate()
    {
        return _trainingRate;
    }
}