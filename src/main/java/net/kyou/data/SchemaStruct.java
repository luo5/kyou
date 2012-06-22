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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

public class SchemaStruct extends SchemaItem implements Iterable<SchemaItem> {
    private final Map<String, SchemaItem> map = new LinkedHashMap<String, SchemaItem>();
    
    @Override
    public void foreach(ISchemaVisitor visitor) {
        try {
            visitor.struStart(this);
            
            for (SchemaItem child : this.map.values())
                child.foreach(visitor);
            
            visitor.struEnd(this);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Schema.ForEachFail, ex);
        }
    }
    
    @Override
    public Iterator<SchemaItem> iterator() {
        return this.map.values().iterator();
    }
    
    /**
     * 获取当前SchemaStruct对象的子节点数
     * 
     * @return 子节点数
     */
    public int size() {
        return this.map.size();
    }
    
    /**
     * 向结构中添加元素
     * <p>
     * <li>被添加的元素不得为null</li>
     * <li>被添加的元素不得为当前对象本身、当前对象的父节点或祖先节点</li>
     * <li>被添加的元素不得为SchemaDocument类型</li>
     * <li>被添加的元素的名称不能为空</li>
     * </p>
     * 
     * @param item
     *            被添加的元素
     */
    public void add(SchemaItem item) {
        if (item == null)
            throw new KyouException(KyouErr.Schema.AddEmptyChild, "parent: " + this);
        if (item == this)
            throw new KyouException(KyouErr.Schema.AddSelfAsChild);
        
        // 判断是否循环引用
        SchemaItem _item = this;
        while (_item != null && _item != _item.parent)
            if (_item == item)
                throw new KyouException(KyouErr.Schema.AddParentAsChild);
            else
                _item = _item.parent;
        
        if (item instanceof SchemaDocument)
            throw new KyouException(KyouErr.Schema.AddDocumentAsChild, "parent: " + this + " child: " + item);
        if (item.name() == null)
            throw new KyouException(KyouErr.Schema.EmptySchemaItemName, "parent: " + this + " child: " + item);
        
        // 判断完毕，执行实际的add操作
        this.unsafeAdd(item);
    }
    
    /**
     * 向结构中添加元素
     * <p>
     * 不进行任何校验以提高性能
     * </p>
     * 
     * @param item
     *            被添加的元素
     */
    void unsafeAdd(SchemaItem item) {
        item.parent = this;
        
        this.map.put(item.name(), item);
    }
    
    /**
     * 移除指定名称的子元素
     * <p>
     * 如果当前结构中不存在指定名称的子元素则不进行任何操作
     * </p>
     * 
     * @param name
     *            将被移除的子元素的名称
     */
    public void remove(String name) {
        this.map.remove(name);
    }
    
    /**
     * 查询指定名称的子元素
     * 
     * @param name
     *            子元素的名称
     * @return 指定名称的子元素<br/>
     *         如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public <T extends SchemaItem> T get(String name) {
        return (T) this.map.get(name);
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        int depth = this.depth();
        for (int i = 0; i < depth; i++)
            buffer.append("  ");
        
        buffer.append("<struct");
        buffer.append(this.attrs);
        buffer.append(">\r\n");
        
        for (SchemaItem child : this.map.values())
            buffer.append(child.toString());
        
        for (int i = 0; i < depth; i++)
            buffer.append("  ");
        buffer.append("</struct>\r\n");
        
        return buffer.toString();
    }
}
