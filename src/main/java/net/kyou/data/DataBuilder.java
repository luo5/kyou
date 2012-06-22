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

import java.util.Stack;

/**
 * 工具类 提供安全地编辑DataDocument中的内容的方法
 * 
 * @author nuclearg
 */
public class DataBuilder {
    /**
     * 
     */
    private final SchemaDocument schema;
    /**
     * 
     */
    private final DataDocument data;
    
    /**
     * 
     * @param schema
     */
    public DataBuilder(SchemaDocument schema) {
        this.schema = schema;
        this.data = this.create(schema);
    }
    
    /**
     * 
     * @param schema
     * @param data
     */
    public DataBuilder(SchemaDocument schema, DataDocument data) {
        this.schema = schema;
        this.data = this.load(data, schema);
    }
    
    /**
     * 获取某个指定路径的报文元素的值
     * 
     * @param path
     *            报文元素的路径
     * @return 报文元素的值
     */
    public String value(DPath path) {
        this.checkExists(path);
        
        return this.data.get(path);
    }
    
    /**
     * 设置某个指定路径的报文元素的值
     * 
     * @param path
     *            报文元素的路径
     * @param value
     *            报文元素的值
     */
    public void value(DPath path, String value) {
        this.checkExists(path);
        
        this.data.map.put(path, value);
    }
    
    /**
     * 
     * @param path
     * @return
     */
    public String add(DPath path) {
        return null;
    }
    
    /**
     * 
     * @param path
     * @return
     */
    public String dup(DPath path) {
        return null;
    }
    
    /**
     * 
     * @param path
     */
    public void remove(DPath path) {
        
    }
    
    /**
     * 
     * @return
     */
    public DataDocument export() {
        return this.data;
    }
    
    private void checkExists(DPath path) {
        
    }
    
    /**
     * 
     * @param schema
     * @return
     */
    private DataDocument create(SchemaDocument schema) {
        final Stack<DPath> stack = new Stack<DPath>();
        
        final DataDocument data = new DataDocument();
        schema.foreach(new ISchemaVisitor() {
            @Override
            public void docStart(SchemaDocument doc) {
                data.map.put(DPath.root, null);
                stack.push(DPath.root);
            }
            
            @Override
            public void docEnd(SchemaDocument doc) {
                stack.pop();
            }
            
            @Override
            public void struStart(SchemaStruct stru) {
                DPath path = stack.peek().child(stru.name());
                
                data.map.put(path, null);
                stack.push(path);
            }
            
            @Override
            public void struEnd(SchemaStruct stru) {
                stack.pop();
                
                if (stru.attrb(Attrs.ARRAY))
                    stack.pop();
            }
            
            @Override
            public void field(SchemaField field) {
                data.map.put(stack.peek().child(field.name()), "");
            }
        });
        return data;
    }
    
    /**
     * 
     * @param data
     * @param schema
     * @return
     */
    private DataDocument load(DataDocument data, SchemaDocument schema) {
        return data;
    }
}
