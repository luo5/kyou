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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.kyou.data.dquery.DQuerySegment.SegmentType;
import net.kyou.data.dquery.restriction.DQueryRestriction;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class DQueryTest {
    @Test
    public void testDQuery() {
        try {
            new DQuery(null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.DQuery.EmptyDQuery, ex.err);
        }
        
        try {
            new DQuery("");
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.DQuery.EmptyDQuery, ex.err);
        }
    }
    
    @Test
    public void testRoot() throws Exception {
        DQuery query = new DQuery("#");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(1, segments.length);
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
    }
    
    @Test
    public void testRootHead() throws Exception {
        DQuery query = new DQuery("#.head");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(2, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("head", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
    }
    
    @Test
    public void testRootHeadTitle() throws Exception {
        DQuery query = new DQuery("#.head.title");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(3, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("head", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("title", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
    }
    
    @Test
    public void testBodyDiv() throws Exception {
        DQuery query = new DQuery("#.body.div");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(3, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("body", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("div", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
    }
    
    @Test
    public void testBodyDivAll() throws Exception {
        DQuery query = new DQuery("#.body.div.*");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(4, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("body", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("div", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
        
        Assert.assertEquals("*", segments[3].body);
        Assert.assertEquals(0, segments[3].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.All, segments[3].type);
    }
    
    @Test
    public void testBodyDivAll0() throws Exception {
        DQuery query = new DQuery("#.body.div.*[0]");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(4, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("body", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("div", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
        
        Assert.assertEquals("*", segments[3].body);
        Assert.assertEquals(1, segments[3].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.All, segments[3].type);
    }
    
    @Test
    public void testBodyDivAll01() throws Exception {
        DQuery query = new DQuery("#.body.div.*[0|1]");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(4, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("body", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("div", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
        
        Assert.assertEquals("*", segments[3].body);
        Assert.assertEquals(1, segments[3].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.All, segments[3].type);
    }
    
    @Test
    public void testBodyDivAllComplex() throws Exception {
        DQuery query = new DQuery("#.body.div.*[0|1, FIELD, @a=2, !children_count=3]");
        Segment[] segments = getSegments(query);
        
        Assert.assertEquals(4, segments.length);
        
        Assert.assertEquals("#", segments[0].body);
        Assert.assertEquals(0, segments[0].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Root, segments[0].type);
        
        Assert.assertEquals("body", segments[1].body);
        Assert.assertEquals(0, segments[1].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[1].type);
        
        Assert.assertEquals("div", segments[2].body);
        Assert.assertEquals(0, segments[2].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.Normal, segments[2].type);
        
        Assert.assertEquals("*", segments[3].body);
        Assert.assertEquals(4, segments[3].restrictions.size());
        Assert.assertEquals(DQuerySegment.SegmentType.All, segments[3].type);
    }
    
    @Test
    public void testToString() {
        DQuery query = new DQuery("#.a[STRU].*[0|1, FIELD].b");
        
        Assert.assertEquals("#[].a[STRU].*[0|1, FIELD].b[]", query.toString());
    }
    
    private Segment[] getSegments(DQuery query) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        Field f = DQuery.class.getDeclaredField("segments");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<DQuerySegment> segments = (List<DQuerySegment>) f.get(query);
        List<Segment> _segments = new ArrayList<DQueryTest.Segment>();
        for (DQuerySegment segment : segments)
            _segments.add(new Segment(segment));
        return _segments.toArray(new Segment[0]);
    }
    
    private class Segment {
        String body;
        DQuerySegment.SegmentType type;
        List<DQueryRestriction> restrictions;
        
        @SuppressWarnings("unchecked")
        Segment(DQuerySegment segment) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
            Field f;
            
            f = DQuerySegment.class.getDeclaredField("body");
            f.setAccessible(true);
            this.body = (String) f.get(segment);
            
            f = DQuerySegment.class.getDeclaredField("type");
            f.setAccessible(true);
            this.type = (SegmentType) f.get(segment);
            
            f = DQuerySegment.class.getDeclaredField("restrictions");
            f.setAccessible(true);
            this.restrictions = (List<DQueryRestriction>) f.get(segment);
        }
    }
}
