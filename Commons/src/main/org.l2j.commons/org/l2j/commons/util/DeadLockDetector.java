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
package org.l2j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.*;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Thread to check for deadlocked threads.
 * @author -Nemesiss- L2M
 * @author JoeAlisson
 */
public class DeadLockDetector implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeadLockDetector.class);

	private final ThreadMXBean tmx;
	private final Runnable callback;

	public DeadLockDetector(Runnable callback) {
		tmx = ManagementFactory.getThreadMXBean();
		this.callback = Objects.requireNonNull(callback);
	}

	@Override
	public final void run() {
		try {
			final long[] ids = tmx.findDeadlockedThreads();
			if(nonNull(ids)) {

				final ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
				final StringBuilder info = new StringBuilder("DeadLock Found!\n");

				appendThreadsInfo(tis, info);

				LOGGER.warn(info.toString());
				callback.run();
			}
		}
		catch (Exception e) {
			LOGGER.warn("DeadLockDetector: {}", e.getMessage(), e);
		}
	}

	private void appendThreadsInfo(ThreadInfo[] tis, StringBuilder info) {
		for (ThreadInfo ti : tis) {
			info.append(ti.toString()).append("\n");

			final LockInfo[] locks = ti.getLockedSynchronizers();
			final MonitorInfo[] monitors = ti.getLockedMonitors();
			if ((locks.length == 0) && (monitors.length == 0)) {
				continue;
			}

			info.append("Java-level deadlock:\n\t");
			info.append(ti.getThreadName());
			info.append(" is waiting to lock ");
			info.append(ti.getLockInfo().toString());
			info.append(" which is held by ");
			info.append(ti.getLockOwnerName());
			info.append("\n");
			appendThreadBlockingInfo(info, ti, ti);
		}
	}

	private void appendThreadBlockingInfo(StringBuilder info, ThreadInfo ti, ThreadInfo dl) {
		while ((dl = tmx.getThreadInfo(new long[] { dl.getLockOwnerId() }, true, true)[0]).getThreadId() != ti.getThreadId())
		{
			info.append('\t');
			info.append(dl.getThreadName());
			info.append(" is waiting to lock ");
			info.append(dl.getLockInfo().toString());
			info.append(" which is held by ");
			info.append(dl.getLockOwnerName());
			info.append("\n");
		}
	}
}
