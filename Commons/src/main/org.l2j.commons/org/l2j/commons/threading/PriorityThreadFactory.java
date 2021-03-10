/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author JoeAlisson
 */
public class PriorityThreadFactory implements ThreadFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(PriorityThreadFactory.class);

	private final int priority;
	private final String name;
	private final AtomicInteger threadNumber = new AtomicInteger(1);

	public PriorityThreadFactory(String name, int priority) {
		this.priority = priority;
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new PriorityThread(r, name + "-" + threadNumber.getAndDecrement(), priority);
	}

	private static final class PriorityThread extends Thread {

		private PriorityThread(Runnable target, String name, int priority) {
			super(null, target, name);
			setPriority(priority);
		}

		@Override
		public void run() {
			try {
				super.run();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
