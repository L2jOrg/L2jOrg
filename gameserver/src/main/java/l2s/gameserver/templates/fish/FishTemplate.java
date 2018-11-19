package l2s.gameserver.templates.fish;

/**
 * @author Bonux
 **/
public final class FishTemplate
{
    private final int _id;
    private final double _chance;
    private final int _duration;
    private final int _rewardType;

    public FishTemplate(int id, double chance, int duration, int rewardType)
    {
        _id = id;
        _chance = chance;
        _duration = duration;
        _rewardType = rewardType;
    }

    public int getId()
    {
        return _id;
    }

    public double getChance()
    {
        return _chance;
    }

    public int getDuration()
    {
        return _duration;
    }

    public int getRewardType()
    {
        return _rewardType;
    }
}