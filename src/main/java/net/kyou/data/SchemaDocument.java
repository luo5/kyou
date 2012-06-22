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

import java.io.InputStream;
import java.io.OutputStream;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 表示整篇报文的结构
 * <p>
 * 表示整棵报文结构树的树根
 * </p>
 * <p>
 * 在kyou中，一篇报文的结构和数据是分开存放的。结构由SchemaDocument存储，而数据则由DataDocument存储。<br/>
 * 一个SchemaDocument本质上是一棵树，其节点由SchemaItem的实现类组成。<br/>
 * SchemaDocument本身不存储数据，只存储报文的结构定义，例如一篇报文中有哪些字段，这些字段是什么类型，长度是多少等等。至于每个字段中的值是由DataDocument存储的。
 * </p>
 * <p>
 * 已知限制：
 * <li>每个array为true的元素，只能拥有最多65536个数组元素。</li>
 * </p>
 * 
 * @author nuclearg
 * @see SchemaItem
 * @see SchemaField
 * @see SchemaStruct
 * @see DataDocument
 */
public class SchemaDocument extends SchemaStruct {
    @Override
    public void foreach(ISchemaVisitor visitor) {
        try {
            visitor.docStart(this);
            
            for (SchemaItem child : this)
                child.foreach(visitor);
            
            visitor.docEnd(this);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Schema.ForEachFail, ex);
        }
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
        buffer.append("<schema");
        buffer.append(this.attrs);
        buffer.append(">\r\n");
        for (SchemaItem child : this)
            buffer.append(child.toString());
        buffer.append("</schema>");
        
        return buffer.toString();
    }
    
    /**
     * 报文结构（{@link SchemaDocument}）的序列化/反序列化接口
     * 
     * @author nuclearg
     */
    public interface ISchemaSerializer {
        /**
         * 将提供的报文结构进行序列化并写入到指定的输出流中
         * 
         * @param doc
         *            要被序列化的报文结构对象
         * @param out
         *            要将序列化的数据写入到的流<br/>
         *            序列化完毕时该流仍将保持打开状态
         */
        public void serializeSchema(SchemaDocument doc, OutputStream out);
        
        /**
         * 从指定的输入流中读取并尝试反序列化出一个报文结构对象
         * 
         * @param in
         *            要从中读取数据的输入流<br/>
         *            反序列化完毕时该流仍将保持打开状态
         * @return 从输入流反序列化得到的SchemaDocument对象
         */
        public SchemaDocument deserializeSchema(InputStream in);
    }
}
