package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date  23:18/12.12.2010
 */
public class BlockInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private boolean _isRed;

	public BlockInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	public boolean isRed()
	{
		return _isRed;
	}

	public void setRed(boolean red)
	{
		_isRed = red;
		broadcastCharInfo();
	}

	public void changeColor()
	{
		setRed(!_isRed);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{}

	@Override
	public boolean isNameAbove()
	{
		return false;
	}

	@Override
	public int getFormId()
	{
		return _isRed ? 0x53 : 0;
	}
}