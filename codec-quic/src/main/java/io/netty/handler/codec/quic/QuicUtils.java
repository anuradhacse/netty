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

/**
 * constants and utility methods used for encoding/decoding QUIC packets
 */
public final class QuicUtils {

    //6 bytes of packet number is present
    public static final int PACKET_NUMBER_LENGTH_6BYTES = 6;
    //4 bytes of packet number is present
    public static final int PACKET_NUMBER_LENGTH_4BYTES = 4;
    //2 bytes of packet number is present
    public static final int PACKET_NUMBER_LENGTH_2BYTES = 2;
    //1 bytes of packet number is present
    public static final int PACKET_NUMBER_LENGTH_1BYTE = 1;

    /**
     * Utility method to convert 6 byte packet number to an int
     * note : receiving bytes are in little endian order
     * @param byteBuf byte
     * @return
     */
    public static long byteBufToInt(ByteBuf byteBuf){
        byte byte1 = byteBuf.readByte();
        byte byte2 = byteBuf.readByte();
        byte byte3 = byteBuf.readByte();
        byte byte4 = byteBuf.readByte();
        byte byte5 = byteBuf.readByte();
        byte byte6 = byteBuf.readByte();

        // TODO: 3/29/17 correct this 
        return byte1 >> 48 | (byte2 & 0xff) >> 40 | (byte3 & 0xff) >> 32 |
               (byte4 & 0xff) >> 24 | (byte6 & 0xff) >> 8 ;
    }





}
