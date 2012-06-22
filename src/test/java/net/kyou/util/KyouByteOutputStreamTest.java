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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class KyouByteOutputStreamTest {

    @Test
    public void testWriteInt() {
        KyouByteOutputStream s = new KyouByteOutputStream();
        s.write(3);

        byte[] bytes = s.export();

        Assert.assertEquals(1, bytes.length);
        Assert.assertEquals(3, bytes[0]);
    }

    @Test
    public void testWriteByteArray() {
        KyouByteOutputStream s = new KyouByteOutputStream();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            s.write("asdfjkl;".getBytes());
            buffer.append("asdfjkl;");
        }

        byte[] bytes = s.export();

        Assert.assertEquals(16000, bytes.length);

        Assert.assertEquals(buffer.toString(), new String(bytes));
    }

    @Test
    public void testBackspace() {
        KyouByteOutputStream s = new KyouByteOutputStream();
        s.write("asdfjkl;".getBytes());
        s.backspace(3);

        byte[] bytes = s.export();

        Assert.assertEquals(5, bytes.length);
        Assert.assertEquals("asdfj", new String(bytes));
    }

    @Test
    public void testBackspaceEx() {
        try {
            KyouByteOutputStream s = new KyouByteOutputStream();
            s.write("asdf".getBytes());

            s.backspace(-2);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.ByteStream.IllegalBackspaceNum, ex.err);
        }

        try {
            KyouByteOutputStream s = new KyouByteOutputStream();
            s.write("asdf".getBytes());

            s.backspace(8);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.ByteStream.IllegalBackspaceNum, ex.err);
        }
    }

    @Test
    public void testClose() {
        KyouByteOutputStream s = new KyouByteOutputStream();

        s.write("helloworld".getBytes());

        s.close();
    }
}
