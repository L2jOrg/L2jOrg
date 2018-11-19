package l2s.gameserver.templates;

import l2s.gameserver.network.l2.components.SystemMsg;

public class BotPunishment
{
	private final int _needReportPoints;
	private final int _skillId;
	private final int _skillLevel;
	private final SystemMsg _message;

	public BotPunishment(int needReportPoints, int skillId, int skillLevel, SystemMsg message)
	{
		_needReportPoints = needReportPoints;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_message = message;
	}

	public int getNeedReportPoints()
	{
		return _needReportPoints;
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLevel()
	{
		return _skillLevel;
	}

	public SystemMsg getMessage()
	{
		return _message;
	}
}