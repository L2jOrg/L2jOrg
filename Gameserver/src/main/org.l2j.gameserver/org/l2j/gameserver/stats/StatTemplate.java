package org.l2j.gameserver.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.stats.funcs.Func;
import org.l2j.gameserver.stats.funcs.FuncTemplate;
import org.l2j.gameserver.stats.triggers.TriggerInfo;

/**
 * @author VISTALL
 * @date 23:05/22.01.2011
 */
public class StatTemplate
{
	protected FuncTemplate[] _funcTemplates = FuncTemplate.EMPTY_ARRAY;
	protected List<TriggerInfo> _triggerList = Collections.emptyList();

	public List<TriggerInfo> getTriggerList()
	{
		return _triggerList;
	}

	public void addTrigger(TriggerInfo f)
	{
		if(_triggerList.isEmpty())
			_triggerList = new ArrayList<TriggerInfo>(4);
		_triggerList.add(f);
	}

	public void attachFunc(FuncTemplate f)
	{
		_funcTemplates = ArrayUtils.add(_funcTemplates, f);
	}

	public FuncTemplate[] getAttachedFuncs()
	{
		return _funcTemplates;
	}

	public Func[] getStatFuncs(Object owner)
	{
		if(_funcTemplates.length == 0)
			return Func.EMPTY_FUNC_ARRAY;

		Func[] funcs = new Func[_funcTemplates.length];
		for(int i = 0; i < funcs.length; i++)
		{
			funcs[i] = _funcTemplates[i].getFunc(owner);
		}
		return funcs;
	}
}