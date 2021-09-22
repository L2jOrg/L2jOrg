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
package org.l2j.authserver.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @author JoeAlisson
 */
public record Endpoint(byte[] host, short port, byte[] subnet, byte[] mask, boolean isIPv4) {

    static final Endpoint LOCALHOST = new Endpoint(new byte[]{ 127,0,0,1 }, (short) 7777,  new byte[0], new byte[0], true);

    public boolean isInSameSubnet(String hostAddress) {
        try {
            return applyMask(InetAddress.getByName(hostAddress).getAddress());
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean applyMask(byte[] address) {
        boolean applied;
        if(isIPv4 == (address.length == 4)) {
            applied = applyIPv4(address);
        } else if(isIPv4) {
            applied = applyIPv4OverIPv6(address);
        } else {
            applied = applyIPv6OverIPv4(address);
        }
        return applied;
    }

    private boolean applyIPv6OverIPv4(byte[] address) {
        for (int i = 0; i < subnet.length; i++) {
            if ((address[i] & mask[i + 12]) != subnet[i + 12]) {
                return true;
            }
        }
        return false;
    }

    private boolean applyIPv4OverIPv6(byte[] address) {
        for (int i = 0; i < subnet.length; i++) {
            if ((address[i + 12] & mask[i]) != subnet[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean applyIPv4(byte[] address) {
        for (int i = 0; i < subnet.length; i++) {
            if ((address[i] & mask[i]) != subnet[i]) {
                return false;
            }
        }
        return true;
    }


    public static Endpoint of(String host, short port, String subnet) throws UnknownHostException {
        var hostAddress = InetAddress.getByName(host).getAddress();

        var subnetAddressEnd = subnet.indexOf("/");
        int bitLength = 0;
        if(subnetAddressEnd > 0) {
            bitLength = Integer.parseInt(subnet.substring(subnetAddressEnd + 1));
            subnet = subnet.substring(0, subnetAddressEnd);
        }

        var subnetAddress = InetAddress.getByName(subnet).getAddress();
        var mask = calcMask(subnetAddressEnd > 0 ? bitLength : subnetAddress.length << 3, subnetAddress.length);
        var isIPv4 = subnetAddress.length == 4;

        var endPoint = new Endpoint(hostAddress, port, subnetAddress, mask, isIPv4);

        if(bitLength > 0 && !endPoint.applyMask(subnetAddress)) {
            throw new UnknownHostException("Invalid endpoint for " + host + " and subnet: " + subnet);
        }

        return endPoint;
    }

    private static byte[] calcMask(int bitLength, int maxLength) throws UnknownHostException {
        if(bitLength > (maxLength << 3) || bitLength < 0) {
            throw new UnknownHostException("invalid subnet mask: " + bitLength);
        }

        var mask = new byte[maxLength];
        Arrays.fill(mask, (byte) 0xFF);

        for (int i = (maxLength << 3) - 1; i >= bitLength ; i--) {
            var index = i >> 3;
            mask[index] = (byte) (mask[index] << 1);
        }
        return mask;
    }
}
