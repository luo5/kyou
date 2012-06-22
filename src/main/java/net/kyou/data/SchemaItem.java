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

import java.util.Collections;
import java.util.Map;

/**
 * 定义报文的结构元素
 * <p>
 * 在kyou中，一篇报文由两部分组成：结构和数据。<br/>
 * 报文结构，在kyou中也称为"schema"，以级联的方式反映报文的层次结构，并且以属性的方式描述报文各项元素独一无二的细节。<br/>
 * SchemaItem中拥有一个children域，该域保存了所有该节点的子节点。<br/>
 * 一篇报文由许多个报文元素(SchemaItem)组成，总体上呈现一棵树的形态。这棵树的深度和广度没有限制。<br/>
 * </p>
 * 
 * @author nuclearg
 */
public abstract class SchemaItem {
    /**
     * 该报文元素的属性列表
     */
    final Map<String, String> attrs = new AttributeMap();
    
    /**
     * 该报文元素的父节点
     */
    SchemaStruct parent;
    
    /**
     * 获取该报文元素的某个指定的属性
     * <p>
     * "属性"用来描述一个报文元素的细节，例如名称、类型、长度等。<br/>
     * 在这个Map中不关心键的大小写，即属性名称不区分大小写。在内部一律按照小写来处理。<br/>
     * 属性的名称和含义可以自由定义(有一些保留字符不能使用)。<br/>
     * 除了一些预先规定的属性({@link Attrs})外kyou根本不关心这些属性的实际含义，这些完全由组包或拆包时使用的配置文件来定义。
     * </p>
     * 
     * @param name
     *            属性名称
     * @return 属性的值 如果不存在该属性则返回null
     */
    public String attr(String name) {
        return this.attrs.get(name);
    }
    
    /**
     * 获取指定元素的boolean类型的值
     * <p>
     * 判断条件是该属性的值是否严格等于"true"
     * </p>
     * 
     * @param name
     *            属性名称
     * @return 属性的值是否严格等于"true"
     */
    public boolean attrb(String name) {
        return "true".equals(this.attrs.get(name));
    }
    
    /**
     * 获取该报文元素的属性列表
     * <p>
     * 该列表是只读的
     * </p>
     * 
     * @return 返回该报文元素的属性列表
     */
    public Map<String, String> attrs() {
        return Collections.unmodifiableMap(this.attrs);
    }
    
    /**
     * 获取该报文元素的父元素
     * <p>
     * 注：SchemaDocument的父元素是其自身
     * </p>
     * 
     * @return 返回该节点的父节点
     */
    public SchemaStruct parent() {
        return this.parent;
    }
    
    /**
     * 获取该节点的名称
     * <p>
     * 实际上是取atts中的name属性的值，该方法仅仅是obj.atts.get(SpecialAttributes.NAME)的简写。
     * 提供此方法单纯是为了减轻编写程序时的负担。
     * </p>
     * 
     * @return 返回该节点的名称
     */
    public String name() {
        return this.attrs.get(Attrs.NAME);
    }
    
    /**
     * 从该节点出发遍历报文结构树的各子节点并执行指定的操作
     * <p>
     * 遍历时采用深度优先算法
     * </p>
     * 
     * @param visitor
     *            在遍历报文结构树时要执行的操作
     */
    public abstract void foreach(ISchemaVisitor visitor);
    
    /**
     * 获取当前元素的深度
     */
    protected int depth() {
        if (this.parent == null || this.parent == this)
            return 0;
        else
            return this.parent.depth() + 1;
    }
}
