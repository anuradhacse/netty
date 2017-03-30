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

import java.util.ArrayList;

/**
 * Public Header of QUIC Packet. This is not stable and will change soon.
 */
public class PublicPacketHeader {

    private long connectionId;
    private boolean versionFlag;
    private boolean resetFlag;
    private boolean truncatedConnectionId;
    private int packetNumberLength;
    /**
     * Vesion sent by client
     */
    private int versionNumber;
    /**
     * Versions sent by sever
     */
    private ArrayList<Integer> supportedVersions;
    private byte[] diversificationNonce;

    public PublicPacketHeader(long connectionId, boolean versionFlag, boolean resetFlag,
                              boolean truncateConnectionId, int packetNumberLength, int versionNumber
            , byte[] diversificationNonce) {
        this.connectionId = connectionId;
        this.versionFlag = versionFlag;
        this.resetFlag = resetFlag;
        truncatedConnectionId = truncateConnectionId;
        this.packetNumberLength = packetNumberLength;
        this.versionNumber = versionNumber;
        this.diversificationNonce = diversificationNonce;
    }

    PublicPacketHeader() {
        this.supportedVersions = new ArrayList<Integer>();
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public boolean isVersionFlagSet() {
        return versionFlag;
    }

    public void setVersionFlag(boolean versionFlag) {
        this.versionFlag = versionFlag;
    }

    public boolean isResetFlagSet() {
        return resetFlag;
    }

    public void setResetFlag(boolean resetFlag) {
        this.resetFlag = resetFlag;
    }

    public boolean isTruncatedConnectionId() {
        return truncatedConnectionId;
    }

    public void setTruncatedConnectionId(boolean truncatedConnectionId) {
        this.truncatedConnectionId = truncatedConnectionId;
    }

    public int getPacketNumberLength() {
        return packetNumberLength;
    }

    public void setPacketNumberLength(int packetNumberLength) {
        this.packetNumberLength = packetNumberLength;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public ArrayList<Integer> getSupportedVersions() {
        return supportedVersions;
    }

    public void setSupportedVersions(int supportedVersion) {
        this.supportedVersions.add(supportedVersion);
    }

    public byte[] getDiversificationNonce() {
        return diversificationNonce;
    }

    public void setDiversificationNonce(byte[] diversificationNonce) {
        this.diversificationNonce = diversificationNonce;
    }
}
