package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class ExVoteSystemInfoPacket extends L2GameServerPacket
{
	private int _receivedRec, _givingRec, _time, _bonusPercent;
	private boolean _showTimer;

	public ExVoteSystemInfoPacket(Player player)
	{
		_receivedRec = player.getRecomLeft();
		_givingRec = player.getRecomHave();
		_time = 0;
		_bonusPercent = 0;
		_showTimer = false;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_receivedRec); //полученые реки
		writeD(_givingRec); //отданые реки
		writeD(_time); //таймер скок секунд осталось
		writeD(_bonusPercent); // процент бонуса
		writeD(_showTimer ? 0x01 : 0x00); //если ноль то таймера нету 1 - пишет чтоли "Работает"
	}
}