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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class KyouFormatStringTest {
    @Test
    public void testParse() throws UnsupportedEncodingException {
        assertKyouFormatString("aaa", "utf-8", "aaa".getBytes("utf-8"));
        
        assertKyouFormatString("[]{}", "gb2312", "[]{}".getBytes("gb2312"));
        
        assertKyouFormatString("\\\\", "utf-8", "\\".getBytes("utf-8"));
        
        assertKyouFormatString("\\r\\n\\%", "utf-8", "\r\n%".getBytes("utf-8"));
        
        assertKyouFormatString("aa\\\\bb", "utf-8", "aa\\bb".getBytes("utf-8"));
        
        assertKyouFormatString("\\01", "utf-8", new byte[] { 1 });
        
        assertKyouFormatString("\\0a\\0b\\0C\\0D", "utf-8", new byte[] { 0x0a, 0x0b, 0x0c, 0x0d });
        
        assertKyouFormatString("\\12\\34\\56\\78\\90\\ab\\cd\\ef", "utf-8", new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef });
        
        assertKyouFormatString("工要在地一上是中国同", "iso8859-1", "工要在地一上是中国同".getBytes("iso8859-1"));
    }
    
    @Test
    public void testParseEx() {
        try {
            new KyouFormatString(null, KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.EmptyFormatString, ex.err);
        }
        
        try {
            new KyouFormatString("", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.EmptyFormatString, ex.err);
        }
        
        try {
            new KyouFormatString("asdf", null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.UnsupportedCharset, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\0", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\0K", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\\\\\", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\a\\b\\c\\d\\e", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
        
        try {
            new KyouFormatString("asdf\\KK", KyouRuntimeUtils.utf8);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Format.FormatStringSyntaxError, ex.err);
        }
    }
    
    private void assertKyouFormatString(String str, String encoding, byte[]... segments) {
        KyouFormatString s = new KyouFormatString(str, Charset.forName(encoding));
        Assert.assertEquals(segments.length, s.size());
        
        for (int i = 0; i < segments.length; i++)
            Assert.assertArrayEquals(segments[i], s.segment(i));
    }
    
    @Test
    public void testToString() {
        KyouFormatString s = new KyouFormatString("asdf%jkl;%\\%\\%\\r\\\\t\\n", Charset.forName("utf-8"));
        Assert.assertEquals("asdf%jkl;%\\%\\%\r\\t\n", s.toString());
    }
    
    @Test
    public void testSimple() {
        Assert.assertTrue(new KyouFormatString("asdf", Charset.forName("utf-8")).isSimple());
        
        Assert.assertFalse(new KyouFormatString("as%df", Charset.forName("utf-8")).isSimple());
        
        Assert.assertTrue(new KyouFormatString("as\\%df", Charset.forName("utf-8")).isSimple());
    }
}
