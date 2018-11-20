package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.actor.instances.player.Macro;

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
	protected final void writeImpl()
	{
		writeByte(_action.ordinal()); //unknown
		writeInt(_macroId); //Macro ID
		writeByte(_count); //count of Macros

		if(_macro != null)
		{
			writeByte(1); //checked
			writeInt(_macro.id); //Macro ID
			writeString(_macro.name); //Macro Name
			writeString(_macro.descr); //Desc
			writeString(_macro.acronym); //acronym
			writeInt(_macro.icon); //icon

			writeByte(_macro.commands.length); //count

			for(int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				writeByte(i + 1); //i of count
				writeByte(cmd.type); //type  1 = skill, 3 = action, 4 = shortcut
				writeInt(cmd.d1); // skill id
				writeByte(cmd.d2); // shortcut id
				writeString(cmd.cmd); // command name
			}
		}
		else
			writeByte(0); //checked
	}
}