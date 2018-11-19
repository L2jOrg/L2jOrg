package l2s.commons.listener;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Класс реализующий список слушателей для каждого типа интерфейса.
 * 
 * @author G1ta0
 *
 * @param <T> базовый интерфейс слушателя
 */
public class ListenerList<T>
{
	protected Set<Listener<T>> listeners = new CopyOnWriteArraySet<Listener<T>>();

	public Collection<Listener<T>> getListeners()
	{
		return listeners;
	}
	
	/**
	 * Добавить слушатель в список
	 * @param listener
	 * @return возвращает true, если слушатель был добавлен
	 */
	public boolean add(Listener<T> listener)
	{
		return listeners.add(listener);
	}

	/**
	 * Удалить слушатель из списока
	 * @param listener
	 * @return возвращает true, если слушатель был удален
	 */
	public boolean remove(Listener<T> listener)
	{
		return listeners.remove(listener);
	}

}
