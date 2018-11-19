package ai.door;

import l2s.gameserver.ai.DoorAI;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ConfirmDlgPacket;

/**
 * @author VISTALL
 * @date 15:32/11.07.2011
 */
public class ResidenceDoor extends DoorAI
{
	public ResidenceDoor(DoorInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtTwiceClick(final Player player)
	{
		final DoorInstance door = getActor();

		Residence residence = ResidenceHolder.getInstance().getResidence(door.getTemplate().getAIParams().getInteger("residence_id"));
		if(residence.getOwner() != null && player.getClan() != null && player.getClan() == residence.getOwner() && (player.getClanPrivileges() & Clan.CP_CS_ENTRY_EXIT) == Clan.CP_CS_ENTRY_EXIT)
		{
			SystemMsg msg = door.isOpen() ? SystemMsg.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE : SystemMsg.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
			player.ask(new ConfirmDlgPacket(msg, 0), new OnAnswerListener()
			{
				@Override
				public void sayYes()
				{
					if(door.isOpen())
						door.closeMe(player, true);
					else
						door.openMe(player, true);
				}

				@Override
				public void sayNo()
				{
					//
				}
			});
		}
	}
}
