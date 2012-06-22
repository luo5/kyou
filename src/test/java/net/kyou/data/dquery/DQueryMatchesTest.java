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

import net.kyou.data.Attrs;
import net.kyou.data.DPath;
import net.kyou.data.DPathUtils;
import net.kyou.data.SchemaBuilder;
import net.kyou.data.SchemaDocument;
import net.kyou.data.SchemaField;
import net.kyou.data.SchemaStruct;

import org.junit.Assert;
import org.junit.Test;

public class DQueryMatchesTest {
    
    @Test
    public void testMatchesSuccess() {
        SchemaBuilder builder = new SchemaBuilder();
        /**
         * <pre>
         *   #
         *     a
         *       b [isarray]
         *         c [isarray]
         *       d
         * </pre>
         */
        builder.beginStruct(Attrs.NAME, "a");
        builder.beginStruct(Attrs.NAME, "b", Attrs.ARRAY, "true");
        builder.field(Attrs.NAME, "c", Attrs.ARRAY, "true");
        builder.endStruct();
        builder.field(Attrs.NAME, "d");
        builder.endStruct();
        
        SchemaDocument schema = builder.result();
        SchemaStruct a = schema.get("a");
        SchemaStruct b = a.get("b");
        SchemaField c = b.get("c");
        SchemaField d = a.get("d");
        
        DQuery query;
        DPath path;
        
        query = new DQuery("#");
        path = DPathUtils.dpath("#");
        Assert.assertTrue(query.matches(schema, path));
        
        query = new DQuery("#.a.d");
        path = DPathUtils.dpath("#.a.d");
        Assert.assertTrue(query.matches(d, path));
        
        query = new DQuery("#.a.b");
        path = DPathUtils.dpath("#.a.b");
        Assert.assertTrue(query.matches(b, path));
        
        query = new DQuery("#.a.b.*");
        path = DPathUtils.dpath("#.a.b.0");
        Assert.assertTrue(query.matches(b, path));
        
        query = new DQuery("b.*.c.*");
        path = DPathUtils.dpath("#.a.b.0.c.1");
        Assert.assertTrue(query.matches(c, path));
        
        query = new DQuery("b.*.c.*[1]");
        path = DPathUtils.dpath("#.a.b.0.c.1");
        Assert.assertTrue(query.matches(c, path));
    }
}
