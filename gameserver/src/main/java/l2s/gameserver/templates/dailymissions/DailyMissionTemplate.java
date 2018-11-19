package l2s.gameserver.templates.dailymissions;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.handler.dailymissions.DailyMissionHandlerHolder;
import l2s.gameserver.handler.dailymissions.IDailyMissionHandler;

/**
 * @author Bonux
 **/
public class DailyMissionTemplate
{
    private final int _id;
    private final IDailyMissionHandler _handler;
    private final int _value;
    private final int _minLevel;
    private final int _maxLevel;
    private final List<DailyRewardTemplate> _rewards = new ArrayList<DailyRewardTemplate>();

    public DailyMissionTemplate(int id, String handler, int value, int minLevel, int maxLevel)
    {
        _id = id;
        _handler = DailyMissionHandlerHolder.getInstance().getHandler(handler);
        _value = value;
        _minLevel = minLevel;
        _maxLevel = maxLevel;
    }

    public int getId()
    {
        return _id;
    }

    public IDailyMissionHandler getHandler()
    {
        return _handler;
    }

    public int getValue()
    {
        return _value;
    }

    public int getMinLevel()
    {
        return _minLevel;
    }

    public int getMaxLevel()
    {
        return _maxLevel;
    }

    public void addReward(DailyRewardTemplate reward)
    {
        _rewards.add(reward);
    }

    public DailyRewardTemplate[] getRewards()
    {
        return _rewards.toArray(new DailyRewardTemplate[_rewards.size()]);
    }
}