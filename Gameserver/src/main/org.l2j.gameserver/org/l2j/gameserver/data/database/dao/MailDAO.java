/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.data.database.dao;

import io.github.joealisson.primitive.ConcurrentIntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.MailData;

/**
 * @author JoeAlisson
 */
public interface MailDAO extends DAO<MailData> {

    @Query("SELECT * FROM mail")
    ConcurrentIntMap<MailData> findAll();

    @Query("UPDATE mail SET unread=FALSE WHERE id=:mailId:")
    void markAsRead(int mailId);

    @Query("UPDATE mail SET sender_deleted=TRUE WHERE id=:mailId:")
    void markAsDeletedBySender(int mailId);

    @Query("UPDATE mail SET receiver_deleted=TRUE WHERE id=:mailId:")
    void markAsDeletedByReceiver(int mailId);

    @Query("UPDATE mail SET has_attachment=FALSE WHERE id=:mailId:")
    void deleteAttachment(int mailId);

    @Query("DELETE FROM mail WHERE id=:mailId:")
    void deleteById(int mailId);
}
