package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.model.BlockList;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class RequestBlock extends IClientIncomingPacket
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestBlock.class);
	private static final int BLOCK = 0;
	private static final int UNBLOCK = 1;
	private static final int BLOCKLIST = 2;
	private static final int ALLBLOCK = 3;
	private static final int ALLUNBLOCK = 4;
	
	private String _name;
	private Integer _type;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_type = packet.getInt(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock
		if ((_type == BLOCK) || (_type == UNBLOCK))
		{
			_name = readString(packet);
		}
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		final int targetId = CharNameTable.getInstance().getIdByName(_name);
		final int targetAL = CharNameTable.getInstance().getAccessLevelById(targetId);
		
		if (activeChar == null)
		{
			return;
		}
		
		switch (_type)
		{
			case BLOCK:
			case UNBLOCK:
			{
				// TODO: Save in database? :P
				if (FakePlayerData.getInstance().isTalkable(_name))
				{
					if (_type == BLOCK)
					{
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST);
						sm.addString(FakePlayerData.getInstance().getProperName(_name));
						activeChar.sendPacket(sm);
					}
					else
					{
						final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST);
						sm.addString(FakePlayerData.getInstance().getProperName(_name));
						activeChar.sendPacket(sm);
					}
					return;
				}
				
				// can't use block/unblock for locating invisible characters
				if (targetId <= 0)
				{
					// Incorrect player name.
					activeChar.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
					return;
				}
				
				if (targetAL > 0)
				{
					// Cannot block a GM character.
					activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
					return;
				}
				
				if (activeChar.getObjectId() == targetId)
				{
					return;
				}
				
				if (_type == BLOCK)
				{
					BlockList.addToBlockList(activeChar, targetId);
				}
				else
				{
					BlockList.removeFromBlockList(activeChar, targetId);
				}
				break;
			}
			case BLOCKLIST:
			{
				BlockList.sendListToOwner(activeChar);
				break;
			}
			case ALLBLOCK:
			{
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
				BlockList.setBlockAll(activeChar, true);
				break;
			}
			case ALLUNBLOCK:
			{
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
				BlockList.setBlockAll(activeChar, false);
				break;
			}
			default:
			{
				LOGGER.info("Unknown 0xA9 block type: " + _type);
			}
		}
	}
}
