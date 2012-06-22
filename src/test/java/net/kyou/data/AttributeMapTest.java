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

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class AttributeMapTest {
    @Test
    public void testSize() {
        AttributeMap map = new AttributeMap();

        Assert.assertEquals(0, map.size());
        map.put("ASDF", "asdf");
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testIsEmpty() {
        AttributeMap map = new AttributeMap();

        Assert.assertFalse(map.isEmpty());
        map.put("ASDF", "asdf");
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testContainsKey() {
        AttributeMap map = new AttributeMap();

        Assert.assertFalse(map.containsKey("asdf"));
        map.put("ASDF", "asdf");
        Assert.assertTrue(map.containsKey("asdf"));

        Assert.assertFalse(map.containsKey(null));
        Assert.assertFalse(map.containsKey(""));
        Assert.assertFalse(map.containsKey("KKK"));
        Assert.assertFalse(map.containsKey(new Date()));
    }

    @Test
    public void testContainsValue() {
        AttributeMap map = new AttributeMap();

        Assert.assertFalse(map.containsValue("asdf"));
        map.put("ASDF", "asdf");
        Assert.assertTrue(map.containsValue("asdf"));
    }

    @Test
    public void testGet() {
        AttributeMap map = new AttributeMap();

        Assert.assertNull(map.get("asdf"));
        map.put("ASDF", "asdf");
        Assert.assertEquals("asdf", map.get("Asdf"));

        Assert.assertEquals(null, map.get(null));
        Assert.assertEquals(null, map.get(""));
        Assert.assertEquals(null, map.get("KKK"));
        Assert.assertEquals(null, map.get(new Date()));
    }

    @Test
    public void testPut() {
        AttributeMap map = new AttributeMap();

        Assert.assertNull(map.get("asdf"));
        map.put("ASDF", "asdf");
        Assert.assertEquals("asdf", map.get("Asdf"));
    }

    @Test
    public void testPutEx() {
        AttributeMap map = new AttributeMap();

        try {
            map.put(null, "asdf");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.EmptyAttributeName, ex.err);
        }
        try {
            map.put("", "asdf");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.EmptyAttributeName, ex.err);
        }
    }

    @Test
    public void testRemove() {
        AttributeMap map = new AttributeMap();

        Assert.assertNull(map.get("asdf"));
        map.put("ASDF", "asdf");
        Assert.assertEquals("asdf", map.get("Asdf"));

        map.remove("ASDF");
        Assert.assertNull(map.get("Asdf"));
    }

    @Test
    public void testPutAll() {
        Map<String, String> _map = new HashMap<String, String>();

        _map.put("name", "name");
        _map.put("array", "array");

        AttributeMap map = new AttributeMap();

        map.putAll(_map);
        Assert.assertEquals(map.get("name"), "name");
        Assert.assertEquals(map.get("array"), "array");
    }

    @Test
    public void testPutAllEx() {
        Map<String, String> _map = new HashMap<String, String>();

        _map.put("name", "name");
        _map.put(null, "array");

        AttributeMap map = new AttributeMap();

        try {
            map.putAll(_map);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.EmptyAttributeName, ex.err);
        }
    }

    @Test
    public void testClear() {
        AttributeMap map = new AttributeMap();

        map.put("ASDF", "asdf");
        Assert.assertEquals(1, map.size());
        map.clear();
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testKeySet() {
        Assert.assertNotNull(new AttributeMap().keySet());
    }

    @Test
    public void testValues() {
        Assert.assertNotNull(new AttributeMap().values());
    }

    @Test
    public void testEntrySet() {
        Assert.assertNotNull(new AttributeMap().entrySet());
    }

    @Test
    public void testToString() {
        Map<String, String> _map = new LinkedHashMap<String, String>();

        _map.put("name", "name");
        _map.put("array", "array");

        AttributeMap map = new AttributeMap();

        map.putAll(_map);

        Assert.assertEquals(" name=\"name\" array=\"array\"", map.toString());
    }
}
