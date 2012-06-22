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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.data.Attrs;
import net.kyou.data.DPath;
import net.kyou.data.DataDocument;
import net.kyou.data.SchemaDocument;
import net.kyou.data.SchemaItem;
import net.kyou.data.dquery.restriction.DQueryRestriction;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString;

import org.apache.commons.lang.StringUtils;

/**
 * DQuery是一种针对某个或某些报文元素的描述方式
 * <p>
 * 类似于XQuery，DQuery用于在给定的{@link SchemaDocument} 或{@link DataDocument}
 * 中选定一个或一批节点，用以完成组包过程。<br/>
 * DQuery是kyou组包的核心。用户用DQuery来精确描述出报文中的一个或一批节点应当应用什么样式，kyou在组包时递归找出这些节点并应用样式，
 * 以完成组包过程。
 * </p>
 * <p>
 * 为便于理解，以一篇类似于html的报文作为示例：<br/>
 * 
 * <pre>
 *   head
 *     title = "demo"
 *   body
 *     div [isarray=true]
 *       p = "1"
 *     div [isarray=true]
 *       p = "2"
 *     div [isarray=true]
 *       table
 *         tr [isarray=true]
 *           td [isarray=true] = "a1"
 *           td [isarray=true] = "a2"
 *         tr [isarray=true]
 *           td [isarray=true] = "b1"
 *           td [isarray=true] = "b2"
 *     table2
 *       tr [isarray=true]
 *         td [isarray=true] = "111"
 *         td [isarray=true] = "222"
 *       tr [isarray=true]
 *         td [isarray=true] = "333"
 *         td [isarray=true] = "444"
 * </pre>
 * 
 * <li># 表示报文根节点</li>
 * <li>. 表示报文的层次结构关系</li>
 * <li>#.head 表示名为head的第一级报文节点</li>
 * <li>#.head.title 表示head下的title节点</li>
 * <li>#.body.div 表示body下的div节点<b>数组</b>。注意：这个DQuery仅表示数组<b>本身</b>，而不是数组中的元素</li>
 * <li>* 表示所有子节点</li>
 * <li>#.body.div.* 表示body下面的所有div节点</li>
 * <li>[] 表示一些附加的限制条件</li>
 * <li>#.body.div.*[0] 表示body下面的第零个div节点。节点从0开始计数。</li>
 * <li>#.body.div.*[0|1] 表示body下面的第零个和第一个div节点</li>
 * <li>#.body.div.2.table.tr.*.td.* 表示body下面的第二个div节点下面的所有tr节点下面的所有td节点</li>
 * 可以不以#开头，此时表示浮动匹配。这是比较常见的用法。
 * <li>p 表示匹配所有p节点</li>
 * <li>div 表示匹配所有div节点</li>
 * <li>tr.*.td 表示匹配所有位于tr下面的td节点<b>数组</b></li>
 * <li>tr.*.td.* 表示匹配所有位于tr下面的td节点</li>
 * </p>
 * <p>
 * 为便于编写配置文件，预定义了如下几个DQuery：
 * <li>#FIELD 表示所有isarray不为true的域节点</li>
 * <li>#FIELD_IN_ARRAY 表示所有isarray为true的域节点</li>
 * <li>#FIELD_ARRAY 表示所有isarray为true的域节点对应的数组节点</li>
 * <li>#STRU 表示所有isarray不为true的结构节点</li>
 * <li>#STRU_IN_ARRAY 表示所有isarray为true的结构节点</li>
 * <li>#STRU_ARRAY 表示所有isarray为true的结构节点对应的数组节点</li>
 * </p>
 * 
 * @author nuclearg
 */
public class DQuery {
    /**
     * DQuery中的各个段
     */
    private final List<DQuerySegment> segments;
    
    /**
     * 初始化一个DQuery实例
     * 
     * @param query
     *            dquery字符串
     */
    public DQuery(String query) {
        if (query == null || query.length() == 0)
            throw new KyouException(KyouErr.DQuery.EmptyDQuery, query);
        
        this.segments = Collections.unmodifiableList(this.parse(query));
    }
    
    /**
     * 判断某个报文元素是否符合当前DQuery实例的描述
     * 
     * @param schema
     *            报文元素的Schema
     * @param path
     *            报文元素路径
     * @return 该报文元素是否符合DQuery的描述
     */
    public boolean matches(SchemaItem schema, DPath path) {
        // 从后往前进行匹配，把所有DQuery都视作浮动匹配
        for (int i = this.segments.size() - 1; i >= 0; i--) {
            DQuerySegment segment = this.segments.get(i);
            
            // 取出的父级
            SchemaItem parent = schema.parent();
            
            // 检查该段的类型
            switch (segment.type) {
                case Normal:
                    // Normal类型的段要判断一下path的name、schema的name和segment的body是否一致
                    if (!segment.body.equals(path.name()) || !segment.body.equals(schema.name()))
                        return false;
                    break;
                case Root:
                    // Root类型的段要判断一下schema和path是不是都是root级别的
                    if (!(schema instanceof SchemaDocument) || !path.isRoot())
                        return false;
                    break;
                case All:
                    // All类型的segment不进行任何判断
                    
                    // 如果当前schema的array为true，则表示当前这个All匹配的是数组元素
                    if (schema.attrb(Attrs.ARRAY))
                        // 此时将parent改成当前schema，以便在检查下一个segment时可以令下一次的“当前schema”为现在的当前schema
                        parent = schema;
                    break;
                default:
                    throw new KyouException(KyouErr.Unexpected, segment.type.toString());
            }
            
            // 检查该段的各个约束条件
            for (DQueryRestriction restriction : segment.restrictions)
                if (!restriction.matches(schema, path))
                    return false;
            
            // 将schema向父级推一级
            schema = parent;
            // 将path向父级推一级
            path = path.parent();
        }
        return true;
    }
    
    /**
     * 解析DQuery字符串，解析成一系列的DQuery段。
     */
    private List<DQuerySegment> parse(String query) {
        ArrayList<DQuerySegment> segments = new ArrayList<DQuerySegment>();
        
        // 尝试解析query中所有的段，直到query中没有剩余的字符
        KyouString str = new KyouString(query);
        while (str.hasRemaining())
            segments.add(new DQuerySegment(str));
        
        return segments;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(this.segments, ".");
    }
}
