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
package net.kyou.data.dquery;

import net.kyou.util.KyouString;

import org.junit.Assert;
import org.junit.Test;

public class DQuerySegmentTest {
    
    @Test
    public void testDQuerySegment() {
        DQuerySegment segment;
        
        segment = new DQuerySegment(new KyouString("#"));
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segment.type);
        Assert.assertEquals("#", segment.body);
        Assert.assertEquals(0, segment.restrictions.size());
        
        segment = new DQuerySegment(new KyouString("*[@a=1]"));
        Assert.assertEquals(DQuerySegment.SegmentType.All, segment.type);
        Assert.assertEquals("*", segment.body);
        Assert.assertEquals(1, segment.restrictions.size());
    }
    
    @Test
    public void testMatches() {
    }
    
    @Test
    public void testToString() {
        Assert.assertEquals("a[]", new DQuerySegment(new KyouString("a[]")).toString());
        Assert.assertEquals("a[0]", new DQuerySegment(new KyouString("a[0]")).toString());
        Assert.assertEquals("a[0|1]", new DQuerySegment(new KyouString("a[0|1]")).toString());
        Assert.assertEquals("a[FIELD]", new DQuerySegment(new KyouString("a[FIELD]")).toString());
        Assert.assertEquals("a[FIELD, STRU]", new DQuerySegment(new KyouString("a[FIELD,STRU]")).toString());
        Assert.assertEquals("a[0|1, FIELD]", new DQuerySegment(new KyouString("a[0|1, FIELD]")).toString());
        Assert.assertEquals("a[0|1|2|3, @a=2, @b=3]", new DQuerySegment(new KyouString("a[0|1|2|3,@a=2,@b=3]")).toString());
        Assert.assertEquals("a[0|1|2|3, @a=2, @b=3|4]", new DQuerySegment(new KyouString("a[0|1|2|3,@a=2, @b=3|4]")).toString());
    }
}
