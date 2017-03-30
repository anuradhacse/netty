/*
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.handler.codec.quic;

/**
 * Supported QUIC versions and utility methods to convert version tags to version numbers
 * and vice versa.
 */
public class QuicVersion {

    public static final int QUIC_VERSION_35 = 35;
    public static final int QUIC_VERSION_36 = 36;
    public static final int QUIC_VERSION_37 = 37;
    public static final int QUIC_VERSION_38 = 38;
    public static final int QUIC_VERSION_UNSUPPORTED = -1;


    public static int[] suppotedVersions = {
            QUIC_VERSION_35, QUIC_VERSION_36, QUIC_VERSION_37, QUIC_VERSION_38
    };

    /**
     * Converting version Number to Version Tag (38 --> 'Q038')
     * note: This method will create version number like '830Q'. so write it as it is to the buffer.
     *
     * @param versionNumber verion number to be converted
     *
     * @return version Tag
     */
    public static int versionNumberToVersionTag(int versionNumber) {
        int a = 'Q';
        int b = ((versionNumber / 100 % 10) + '0') << 8;
        int c = ((versionNumber / 10 % 10) + '0') << 16;
        int d = ((versionNumber % 10) + '0') << 24;

        return a + b + c + d;
    }

    /**
     * Coverting version tag to version Number.for example version Tag is the representaion
     * of version in wire. version number is just an integer value to represent version.
     * Version Tag 'Q038' converted to version Number 38
     * note:Verison Tag is in little endian byte order
     *
     * @param versionTag version Tag 32bit value read from wire
     *
     * @return version Number
     */
    public static int versionTagToVersionNumber(int versionTag) {
        int a = (((versionTag >> 8) & 0xff) - '0') * 100;
        int b = (((versionTag >> 16) & 0xff) - '0') * 10;
        int c = (((versionTag >> 24) & 0xff) - '0');

        return a + b + c;
    }

    /**
     * return {@code true} if server supports this version
     *
     * @param versionNumber version number to be checked
     * @return supported or not
     */
    public static boolean isSupportedVersion(int versionNumber) {
        for (int supportedVersion : suppotedVersions) {
            if (versionNumber == supportedVersion) {
                return true;
            }
        }
        return false;
    }

}
