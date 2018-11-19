package l2s.gameserver.network.l2.s2c;

public class ExCleftState extends L2GameServerPacket
{
	public static final int CleftState_Total = 0;
	public static final int CleftState_TowerDestroy = 1;
	public static final int CleftState_CatUpdate = 2;
	public static final int CleftState_Result = 3;
	public static final int CleftState_PvPKill = 4;

	private int CleftState = 0; //TODO

	@Override
	protected void writeImpl()
	{
		writeD(CleftState);
		switch(CleftState)
		{
			case CleftState_Total:
				//dddddSS - BTeam Point:%d CatID:%d CatName:%s RemainSec:%d RTeam Point:%d CatID:%d CatName:%s RemainSec:%d
				//BlueTeam: d[dddd] - Total List TeamID:%d PlayerID:%d Kill:%d Death:%d Tower:%d
				//RedTeam: d[dddd] - Total List TeamID:%d PlayerID:%d Kill:%d Death:%d Tower:%d
				break;
			case CleftState_TowerDestroy:
				//ddddddddd - RemainSec:%d BlueTeamPt:%d RedTeamPt:%d TeamID:%d TowerType:%d PlayerID:%d CleftTowerCount:%d KillCount:%d DeathCount:%d
				break;
			case CleftState_CatUpdate:
				//dddS - RemainSec:%d TeamID:%d CatID:%d CatName:%s
				break;
			case CleftState_Result:
				//dd - WinTeamID:%d LoseTeamID:%d
				break;
			case CleftState_PvPKill:
				//ddd - BTeamPoint:%d RTeamPoint:%d
				//ddddd - PvPKill01 TeamID:%d PlayerID:%d CleftTowerCount:%d Kill:%d Death:%d RemainSec:%d
				//ddddd - PvPKill02 TeamID:%d PlayerID:%d CleftTowerCount:%d Kill:%d Death:%d
				break;
		}
	}
}