package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.actor.instances.player.Macro;

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
		writeC(_action.ordinal()); //unknown
		writeD(_macroId); //Macro ID
		writeC(_count); //count of Macros

		if(_macro != null)
		{
			writeC(1); //checked
			writeD(_macro.id); //Macro ID
			writeS(_macro.name); //Macro Name
			writeS(_macro.descr); //Desc
			writeS(_macro.acronym); //acronym
			writeD(_macro.icon); //icon

			writeC(_macro.commands.length); //count

			for(int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i + 1); //i of count
				writeC(cmd.type); //type  1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.d1); // skill id
				writeC(cmd.d2); // shortcut id
				writeS(cmd.cmd); // command name
			}
		}
		else
			writeC(0); //checked
	}
}