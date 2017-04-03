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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import static io.netty.handler.codec.quic.QuicUtils.*;

/**
 * Decodes a {@link DatagramPacket}  to {@link QuicPacket}.
 * This Decoder can be used in client side or server side pipeline implementation.
 */
public class QuicPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    /**
     * {@code isServer true if this decoder is used in server pipeline
     */
    private final boolean isServer;

    public QuicPacketDecoder(boolean isServer) {
        this.isServer = isServer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out)
            throws Exception {
        final ByteBuf quicPacketData = packet.content();
        QuicPacket quicPacket = new QuicPacket();
        boolean success = false;
        try {
            //todo
        } finally {
            if (success) {
                //release response
                out.add(quicPacket);
            }

        }

    }

    /**
     * Decodes the public header of QUIC packet sent by server.
     *
     * @param quicPacket QUIC response created after decoding
     * @param quicPacketData Payload contain inside QUIC packet inlcuding headers
     */
    private void decodePublicHeaders(QuicPacket quicPacket, ByteBuf quicPacketData) throws QuicException {
        //read the 1st byte
        final byte publicFlagByte = quicPacketData.readByte();

        PublicPacketHeader publicPacketHeader = new PublicPacketHeader();

        //verionFlag indicated by 0x01
        boolean versionFlag = (publicFlagByte & 0x01) > 0;
        publicPacketHeader.setVersionFlag(versionFlag);

        //resetFlag indicated by 0x02
        boolean resetFlag = (publicFlagByte & 0x02) > 0;
        publicPacketHeader.setResetFlag(resetFlag);

        //indicate whether connectionId is 0 bytes
        boolean truncatedConnectionId = (publicFlagByte & 0x08) == 0;
        publicPacketHeader.setTruncatedConnectionId(truncatedConnectionId);
        //// TODO: 3/23/17 diversification nonce 0x04
        //// TODO: 3/23/17 support/not support connectionId truction

        //low-order-bytes of the packet number
        int packetNumberLength = 0;
        if (hasPacketNumber(publicPacketHeader)) {
            switch (publicFlagByte & 0x30) {
            case 0x30:
                packetNumberLength = PACKET_NUMBER_LENGTH_6BYTES;
                break;
            case 0x20:
                packetNumberLength = PACKET_NUMBER_LENGTH_4BYTES;
                break;
            case 0x10:
                packetNumberLength = PACKET_NUMBER_LENGTH_2BYTES;
                break;
            case 0x00:
                packetNumberLength = PACKET_NUMBER_LENGTH_1BYTE;
                break;
            default:
                throw new QuicException(QuicError.QUIC_INVALID_PACKET_HEADER,
                                        "Packet Number is not available");
            }
        }
        publicPacketHeader.setPacketNumberLength(packetNumberLength);

        //decoding connectionId
        if (!truncatedConnectionId) {
            //64bit long connection id. read in little endian byte order
            long connectionId = quicPacketData.readLongLE();
            //todo should call discardReadBytes() method
            if (connectionId == 0) {
                throw new QuicException
                        (QuicError.QUIC_INVALID_PACKET_HEADER, "ConnectionId cannot be 0");
                //todo what happen to connection after throwing error

            }
            publicPacketHeader.setConnectionId(connectionId);
        }
        //version decoding
        //Public Reset packets donot have a version
        //if version flag set by server it is a version negotiation packet
        //else it is a regular packet sent by client
        if (!resetFlag) {
            if (versionFlag) {
                if (!isServer) {
                    int versionTag = (int) quicPacketData.readUnsignedIntLE();
                    int versionNumber = QuicVersion.versionTagToVersionNumber(versionTag);
                    publicPacketHeader.setVersionNumber(versionNumber);
                } else {
                    // decode version negotiation packet
                    // contain 4 byte versions server support
                    if (quicPacketData.capacity() % 4 == 0) {

                        for (int i = 0; i < quicPacketData.capacity() / 4; ++i) {
                            int versionTag = (int) quicPacketData.readUnsignedIntLE();
                            int versionNumber = QuicVersion.versionTagToVersionNumber(versionTag);
                            //if the version is not supported. simply add it as
                            //unsupported version
                            if (!QuicVersion.isSupportedVersion(versionNumber)) {
                                versionNumber = QuicVersion.QUIC_VERSION_UNSUPPORTED;
                                publicPacketHeader.setSupportedVersions(versionNumber);
                            }
                            publicPacketHeader.setSupportedVersions(versionNumber);

                        }
                    } else {
                        throw new QuicException(QuicError.QUIC_INVALID_VERSION_NEGOTIATION_PACKET,
                                                "Versions should be indicated 4 bytes each");
                    }


                }
            }
        }

        //diversification nonce
        if ((publicFlagByte & 0x04) > 0) {

        }

        //parse packet number
        if (hasPacketNumber(publicPacketHeader)) {
            if (packetNumberLength == PACKET_NUMBER_LENGTH_1BYTE) {
                int packetNumber = quicPacketData.readByte();
            } else if (packetNumberLength == PACKET_NUMBER_LENGTH_2BYTES) {
                int packetNumber = quicPacketData.readShortLE();
            } else if (packetNumberLength == PACKET_NUMBER_LENGTH_4BYTES) {
                int packetNumber = quicPacketData.readMediumLE();
            } else if (packetNumberLength == PACKET_NUMBER_LENGTH_6BYTES) {
                //utility method for reading 6 bytes as a long
                ByteBuf packetNumberBytes = quicPacketData.readBytes(packetNumberLength);
                long packetNumber = QuicUtils.byteBufToInt(packetNumberBytes);
            } else {
                throw new QuicException(QuicError.QUIC_INVALID_PACKET_HEADER,
                                        "Invalid packet number size");
            }
        }

    }

    /**
     * Check whether packetNumber is available in public header.
     * Note that serverNegotiationPackets(sent by server with versionFlag Set) and PublicReset packets
     * doesn't have a packet number in it.
     *
     * @param publicPacketHeader public header of QUIC packet
     *
     * @return {@code true} if packet number is available
     */
    private boolean hasPacketNumber(PublicPacketHeader publicPacketHeader) {
        if (publicPacketHeader.isResetFlagSet()) {
            return false;
        }
        if (isServer || publicPacketHeader.isVersionFlagSet()) {
            return false;
        }
        return true;
    }

//// TODO: 4/3/17 test cases for the above methods
}