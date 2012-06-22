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
package net.kyou.data;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class DataDocumentTest {
    
    @Test
    public void testGetSet() {
        DataDocument doc = new DataDocument();
        
        Assert.assertNull(doc.get(new DPath("a")));
        doc.map.put(new DPath("a"), "a");
        Assert.assertEquals("a", doc.get(new DPath("a")));
    }
    
    @Test
    public void testElements() {
        DataDocument doc = new DataDocument();
        doc.map.put(new DPath("a"), "a");
        doc.map.put(new DPath("array"), "");
        doc.map.put(new DPath("array.[]"), "2");
        doc.map.put(new DPath("array.0"), "aaa");
        doc.map.put(new DPath("array.1"), "bbb");
        doc.map.put(new DPath("stru.a"), "aaa");
        doc.map.put(new DPath("stru.b"), "bbb");
        doc.map.put(new DPath("stru.c"), "ccc");
        
        Assert.assertNull(doc.elements(new DPath("asdf")));
        Assert.assertArrayEquals(new DPath[] {
                new DPath("array.0"), new DPath("array.1") }, doc.elements(new DPath("array")));
        Assert.assertNull(null, doc.elements(new DPath("stru.a")));
        
        doc.map.put(new DPath("array.[]"), "asdf");
        try {
            doc.elements(new DPath("array"));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.ArrayElementCountParseFail, ex.err);
        }
    }
    
    @Test
    public void testEntries() {
        DataDocument doc = new DataDocument();
        doc.map.put(new DPath("a"), "a");
        doc.map.put(new DPath("array"), "");
        doc.map.put(new DPath("array.[]"), "2");
        doc.map.put(new DPath("array.0"), "aaa");
        doc.map.put(new DPath("array.1"), "bbb");
        
        Assert.assertArrayEquals(new DPath[] {
                new DPath("a"), new DPath("array"), new DPath("array.[]"), new DPath("array.0"), new DPath("array.1") }, doc.map.keySet().toArray());
    }
    
    @Test
    public void testToString() {
        DataDocument doc = new DataDocument();
        doc.map.put(new DPath("a"), "a");
        doc.map.put(new DPath("array"), "2");
        doc.map.put(new DPath("array.0"), "aaa");
        doc.map.put(new DPath("array.1"), "bbb");
        
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<data>\r\n<a>a</a>\r\n<array>2</array>\r\n<array.0>aaa</array.0>\r\n<array.1>bbb</array.1>\r\n</data>";
        
        Assert.assertEquals(xml, doc.toString());
    }
    
}
