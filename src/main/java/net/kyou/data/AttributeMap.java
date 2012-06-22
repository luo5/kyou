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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 报文元素的属性值列表
 * <p>
 * 该类型用于SchemaItem的atts属性，用于保存报文元素的各项属性值。 <br/>
 * 该列表中存储的所有键不可为空(null或长度为0)，不区分大小写，在内部以小写形式保存。
 * </p>
 * 
 * @author NuclearG (<a href="nuclearg@163.com">nuclearg@163.com</a>)
 */
class AttributeMap implements Map<String, String> {
    /**
     * 实际用来存储数据的map实例
     */
    private final Map<String, String> map = new LinkedHashMap<String, String>(8);
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.size() != 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (key == null)
            return false;
        if (!(key instanceof String))
            return false;
        
        String name = (String) key;
        
        if (name.length() == 0)
            return false;
        
        return this.map.containsKey(name.toLowerCase(Locale.getDefault()));
    }
    
    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }
    
    @Override
    public String get(Object key) {
        if (key == null)
            return null;
        if (!(key instanceof String))
            return null;
        
        String name = (String) key;
        
        if (name.length() == 0)
            return null;
        
        return this.map.get(name.toLowerCase(Locale.getDefault()));
    }
    
    @Override
    public String put(String key, String value) {
        if (key == null || key.length() == 0)
            throw new KyouException(KyouErr.Schema.EmptyAttributeName);
        
        return this.map.put(key.toLowerCase(Locale.getDefault()), value);
    }
    
    @Override
    public String remove(Object key) {
        if (key != null && key instanceof String)
            key = ((String) key).toLowerCase(Locale.getDefault());
        return this.map.remove(key);
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Entry<? extends String, ? extends String> entry : m.entrySet())
            this.put(entry.getKey(), entry.getValue());
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public Collection<String> values() {
        return this.map.values();
    }
    
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.map.entrySet();
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Entry<String, String> entry : this.map.entrySet())
            buffer.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        return buffer.toString();
    }
}
