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
package org.l2j.authserver.network.crypt;

import io.github.joealisson.mmocore.Buffer;
import org.l2j.commons.crypt.NewCrypt;

import java.io.IOException;

import static java.util.Objects.nonNull;

public class AuthServerCrypt {

    private static final String STATIC_BLOWFISH_KEY = "_;v.]05-31!|+-%xT!^[$\00";
    private static final int CHECKSUM_SIZE = 4;
    private static final int PADDING_SIZE = 8;

    private static final NewCrypt staticCrypt = new NewCrypt(STATIC_BLOWFISH_KEY);

    private NewCrypt crypt;

    public void setKey(byte[] key) {
        crypt = new NewCrypt(key);
    }

    public boolean decrypt(Buffer data, final int offset, final int size) throws IOException {
        if(nonNull(crypt)) {
            crypt.decrypt(data, offset, size);
        } else {
            staticCrypt.decrypt(data, offset, size);
        }
        return NewCrypt.verifyChecksum(data, offset, size);
    }

    public int encrypt(Buffer data, final int offset, int size) throws IOException {
        size += CHECKSUM_SIZE;
        size += PADDING_SIZE - (size % PADDING_SIZE);
        size += PADDING_SIZE;
        data.limit(offset + size);
        NewCrypt.appendChecksum(data, offset, size);
        if(nonNull(crypt)) {
            crypt.crypt(data, offset, size);
        } else {
            staticCrypt.crypt(data, offset, size);
        }
        return size;
    }
}
