package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.player.ClassData;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class ClassDataHolder extends AbstractHolder
{
	private static final ClassDataHolder _instance = new ClassDataHolder();

	private final HashIntObjectMap<ClassData> _classDataList = new HashIntObjectMap<ClassData>();

	public static ClassDataHolder getInstance()
	{
		return _instance;
	}

	public void addClassData(ClassData classData)
	{
		_classDataList.put(classData.getClassId(), classData);
	}

	public ClassData getClassData(int classId)
	{
		return _classDataList.get(classId);
	}

	@Override
	public int size()
	{
		return _classDataList.size();
	}

	@Override
	public void clear()
	{
		_classDataList.clear();
	}
}