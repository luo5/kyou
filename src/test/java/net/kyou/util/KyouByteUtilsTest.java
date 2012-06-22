/* Copyright - Apache License 2.0
 * 
 * The project "kyou" is
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kyou.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KyouByteUtilsTest {
    @Before
    public void before() {
        new KyouByteUtils();
    }

    @Test
    public void testWriteInt8() {
        Assert.assertEquals((byte) 0x84, KyouByteUtils.writeInteger8(0x84));
    }

    @Test
    public void testWriteInt16() {
        Assert.assertArrayEquals(new byte[] { 0x12, 0x34 }, KyouByteUtils.writeInteger16(0x1234, true));
        Assert.assertArrayEquals(new byte[] { 0x34, 0x12 }, KyouByteUtils.writeInteger16(0x1234, false));
    }

    @Test
    public void testWriteInt32() {
        Assert.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78 }, KyouByteUtils.writeInteger32(0x12345678, true));
        Assert.assertArrayEquals(new byte[] { 0x78, 0x56, 0x34, 0x12 }, KyouByteUtils.writeInteger32(0x12345678, false));
    }

    @Test
    public void testWriteInt64() {
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 }, KyouByteUtils.writeInteger64(0x0102030405060708L, true));
        Assert.assertArrayEquals(new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01 }, KyouByteUtils.writeInteger64(0x0102030405060708L, false));
    }

    @Test
    public void testWriteFloat() {
        Assert.assertArrayEquals(new byte[] { 0x43, 0x6C, (byte) 0xB6, (byte) 0xF2 }, KyouByteUtils.writeFloat(236.71463f, true));
        Assert.assertArrayEquals(new byte[] { (byte) 0xF2, (byte) 0xB6, 0x6C, 0x43 }, KyouByteUtils.writeFloat(236.71463f, false));
    }

    @Test
    public void testWriteDouble() {
        Assert.assertArrayEquals(double2octet(123.4567), KyouByteUtils.writeDouble(123.4567, true));
        byte[] bytes = double2octet(123.4567);
        for (int i = 0; i < bytes.length / 2; i++) {
            byte b = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = b;
        }
        Assert.assertArrayEquals(bytes, KyouByteUtils.writeDouble(123.4567, false));
    }

    private static final long LAST_OCTET_BYTE = 0x00000000000000FFl;

    private static final byte[] double2octet(double d) {
        return long2octet(Double.doubleToLongBits(d));
    }

    private static final byte[] long2octet(long l) {
        byte[] num = new byte[8];
        num[0] = (byte) ((l >>> 56) & LAST_OCTET_BYTE);
        num[1] = (byte) ((l >>> 48) & LAST_OCTET_BYTE);
        num[2] = (byte) ((l >>> 40) & LAST_OCTET_BYTE);
        num[3] = (byte) ((l >>> 32) & LAST_OCTET_BYTE);
        num[4] = (byte) ((l >>> 24) & LAST_OCTET_BYTE);
        num[5] = (byte) ((l >>> 16) & LAST_OCTET_BYTE);
        num[6] = (byte) ((l >>> 8) & LAST_OCTET_BYTE);
        num[7] = (byte) ((l >>> 0) & LAST_OCTET_BYTE);
        return num;
    }
}
