/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package l2s.commons.collections;

import java.util.Iterator;
import java.util.List;

/**
 * An JoinedIterator is an Iterator that wraps a number of Iterators.
 * <p/>
 * This class makes multiple iterators look like one to the caller.
 * When any method from the Iterator interface is called, the JoinedIterator
 * will delegate to a single underlying Iterator. The JoinedIterator will
 * invoke the Iterators in sequence until all Iterators are exhausted.
 *
 * @modify VISTALL
 */
public class JoinedIterator<E> implements Iterator<E>
{
	// wrapped iterators
	private Iterator<E>[] _iterators;

	// index of current iterator in the wrapped iterators array
	private int _currentIteratorIndex;

	// the current iterator
	private Iterator<E> _currentIterator;

	// the last used iterator
	private Iterator<E> _lastUsedIterator;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JoinedIterator(List<Iterator<E>> iterators)
	{
		this(iterators.toArray(new Iterator[iterators.size()]));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JoinedIterator(Iterator... iterators)
	{
		if(iterators == null)
			throw new NullPointerException("Unexpected NULL iterators argument");
		_iterators = iterators;
	}

	@Override
	public boolean hasNext()
	{
		updateCurrentIterator();
		return _currentIterator.hasNext();
	}

	@Override
	public E next()
	{
		updateCurrentIterator();
		return _currentIterator.next();
	}

	@Override
	public void remove()
	{
		updateCurrentIterator();
		_lastUsedIterator.remove();
	}

	// call this before any Iterator method to make sure that the current Iterator
	// is not exhausted
	protected void updateCurrentIterator()
	{
		if(_currentIterator == null)
		{
			if(_iterators.length == 0)
				_currentIterator = EmptyIterator.getInstance();
			else
				_currentIterator = _iterators[0];
			// set last used iterator here, in case the user calls remove
			// before calling hasNext() or next() (although they shouldn't)
			_lastUsedIterator = _currentIterator;
		}

		while(!_currentIterator.hasNext() && _currentIteratorIndex < _iterators.length - 1)
		{
			_currentIteratorIndex++;
			_currentIterator = _iterators[_currentIteratorIndex];
		}
	}
}
