package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.actor.instances.player.Macro;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * packet type id 0xe7
 *
 * sample
 *
 * e7
 * d // unknown change of Macro edit,add,delete
 * c // unknown
 * c //count of Macros
 * c // unknown
 *
 * d // id
 * S // macro name
 * S // desc
 * S // acronym
 * c // icon
 * c // count
 *
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 *
 * format:		cdccdSSScc (ccdcS)
 */
public class MacroListPacket extends L2GameServerPacket
{
	public static enum Action
	{
		DELETE,
		ADD,
		UPDATE
	}

	private final int _macroId, _count;
	private final Action _action;
	private final Macro _macro;

	public MacroListPacket(int macroId, Action action, int count, Macro macro)
	{
		_macroId = macroId;
		_action = action;
		_count = count;
		_macro = macro;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_action.ordinal()); //unknown
		buffer.putInt(_macroId); //Macro ID
		buffer.put((byte)_count); //count of Macros

		if(_macro != null)
		{
			buffer.put((byte)1); //checked
			buffer.putInt(_macro.id); //Macro ID
			writeString(_macro.name, buffer); //Macro Name
			writeString(_macro.descr, buffer); //Desc
			writeString(_macro.acronym, buffer); //acronym
			buffer.putInt(_macro.icon); //icon

			buffer.put((byte)_macro.commands.length); //count

			for(int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				buffer.put((byte) (i + 1)); //i of count
				buffer.put((byte)cmd.type); //type  1 = skill, 3 = action, 4 = shortcut
				buffer.putInt(cmd.d1); // skill id
				buffer.put((byte)cmd.d2); // shortcut id
				writeString(cmd.cmd, buffer); // command name
			}
		}
		else
			buffer.put((byte)0); //checked
	}
}