package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PledgeReceiveMemberInfo;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestPledgeSetAcademyMaster extends L2GameClientPacket
{
	private int _mode; // 1=set, 0=unset
	private String _sponsorName;
	private String _apprenticeName;

	@Override
	protected void readImpl()
	{
		_mode = readD();
		_sponsorName = readS(16);
		_apprenticeName = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		if((activeChar.getClanPrivileges() & Clan.CP_CL_APPRENTICE) == Clan.CP_CL_APPRENTICE)
		{
			UnitMember sponsor = activeChar.getClan().getAnyMember(_sponsorName);
			UnitMember apprentice = activeChar.getClan().getAnyMember(_apprenticeName);
			if(sponsor != null && apprentice != null)
			{
				if(apprentice.getPledgeType() != Clan.SUBUNIT_ACADEMY || sponsor.getPledgeType() == Clan.SUBUNIT_ACADEMY)
					return; // hack?

				if(_mode == 1)
				{
					if(sponsor.hasApprentice())
					{
						activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.RequestOustAlly.MemberAlreadyHasApprentice"));
						return;
					}
					if(apprentice.hasSponsor())
					{
						activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.RequestOustAlly.ApprenticeAlreadyHasSponsor"));
						return;
					}
					sponsor.setApprentice(apprentice.getObjectId());
					clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(apprentice));
					clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1).addString(sponsor.getName()).addString(apprentice.getName()));
				}
				else
				{
					if(!sponsor.hasApprentice())
					{
						activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.RequestOustAlly.MemberHasNoApprentice"));
						return;
					}
					sponsor.setApprentice(0);
					clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(apprentice));
					clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.S2_CLAN_MEMBER_C1S_APPRENTICE_HAS_BEEN_REMOVED).addString(sponsor.getName()).addString(apprentice.getName()));
				}
				if(apprentice.isOnline())
					apprentice.getPlayer().broadcastCharInfo();
				activeChar.sendPacket(new PledgeReceiveMemberInfo(sponsor));
			}
		}
		else
			activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.RequestOustAlly.NoMasterRights"));
	}
}