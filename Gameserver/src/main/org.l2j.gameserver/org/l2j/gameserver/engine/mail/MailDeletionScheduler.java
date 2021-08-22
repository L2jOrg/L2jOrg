/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.engine.mail;

import io.github.joealisson.primitive.CHashLongMap;
import io.github.joealisson.primitive.LongMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.data.MailData;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/**
 * @author JoeAlisson
 */
class MailDeletionScheduler implements Runnable {

    private final LongMap<MailData> mails = new CHashLongMap<>();
    private final long rescheduleMinIntervalTime = Duration.ofMinutes(5).toMillis();
    private long nextCheck;
    private ScheduledFuture<?> task;

    @Override
    public void run() {
        nextCheck = 0;
        for (var entry : mails.entrySet()) {
            var timestamp = entry.getKey();
            if(System.currentTimeMillis() + rescheduleMinIntervalTime > timestamp) {
                MailEngine.getInstance().deleteExpiredMail(entry.getValue());
            } else {
                if(timestamp - nextCheck > rescheduleMinIntervalTime) {
                    nextCheck = timestamp;
                }
            }
        }
        if(nextCheck > 0) {
            task = ThreadPool.schedule(this, nextCheck);
        }
    }

    public void add(MailData mail) {
        mails.put(mail.getExpiration(), mail);

        if(mail.getExpiration() - nextCheck > rescheduleMinIntervalTime) {
            nextCheck = mail.getExpiration();
            if(task != null) {
                task.cancel(false);
            }
            task = ThreadPool.schedule(this, nextCheck);
        }
    }
}
