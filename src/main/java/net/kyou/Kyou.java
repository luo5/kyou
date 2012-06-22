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
package net.kyou;

import java.io.InputStream;
import java.io.OutputStream;

import net.kyou.data.DataDocument;
import net.kyou.data.SchemaDocument;
import net.kyou.data.DataDocument.IDataSerializer;
import net.kyou.data.SchemaDocument.ISchemaSerializer;
import net.kyou.data.XmlSerializer;
import net.kyou.pack.PackService;
import net.kyou.pack.StyleSpecification;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.util.KyouByteOutputStream;

/**
 * kyou的入口类，提供kyou的基本功能
 * <p>
 * 本类是Kyou的入口，可以实现kyou的主要功能：
 * <li>装载/保存SchemaDocument和DataDocument</li>
 * <li>装载StyleSpecification</li>
 * <li>kyou最根本的存在价值：执行组包过程</li>
 * </p>
 * <p>
 * 在load/save系列方法中默认使用XmlSerializer作为序列化/反序列化实现。<br/>
 * 如果期望使用一个不同的序列化策略，请手工调用其它的序列化/反序列化实现。
 * </p>
 * 
 * @author nuclearg
 */
public class Kyou {
    /**
     * 提供一个默认的Kyou实例
     * <p>
     * 该实例的各项配置如下：
     * <li>使用{@link XmlSerializer}作为报文结构和报文数据的序列化/反序列化实现</li>
     * <li>支持net.kyou.pack.param中提供的各种参数</li>
     * <li>支持net.kyou.pack.param.expr中提供的各种表达式</li>
     * </p>
     * <p>
     * 如果需要一些额外的定制，例如添加一些参数或表达式，或使用另外的序列化/反序列化形式，可以使用{@link KyouBuilder}构造一个满足自身需要的Kyou实例
     * </p>
     * 
     */
    public static final Kyou instance = new KyouBuilder().export();
    
    /**
     * Schema的序列化/反序列化器
     */
    private final ISchemaSerializer schemaSerializer;
    /**
     * 报文数据的序列化/反序列化器
     */
    private final IDataSerializer dataSerializer;
    /**
     * 参数工厂
     */
    private final ParamFactory paramFactory;
    /**
     * 表达式工厂
     */
    private final ExprFactory exprFactory;
    
    /**
     * 初始化一个Kyou实例
     * 
     * @param schemaSerializer
     *            报文结构序列化/反序列化实现
     * @param dataSerializer
     *            报文数据序列化/反序列化实现
     * @param paramFactory
     *            参数工厂
     * @param exprFactory
     *            表达式工厂
     */
    Kyou(ISchemaSerializer schemaSerializer, IDataSerializer dataSerializer, ParamFactory paramFactory, ExprFactory exprFactory) {
        this.schemaSerializer = schemaSerializer;
        this.dataSerializer = dataSerializer;
        this.paramFactory = paramFactory;
        this.exprFactory = exprFactory;
    }
    
    /**
     * 从输入流中读取一个SchemaDocument对象
     * 
     * @param in
     *            要从中读取数据的输入流，该流中的数据将被送给XmlSerializer进行解析以得到一个SchemaDocument对象。<br/>
     *            读取完毕时该流仍将保持打开状态
     * @return 从流中读取出的SchemaDocument对象
     */
    public SchemaDocument loadSchema(InputStream in) {
        return this.schemaSerializer.deserializeSchema(in);
    }
    
    /**
     * 将指定的SchemaDocument保存到指定的输出流中
     * 
     * @param schema
     *            要被保存的SchemaDocument对象。将使用一个XmlSerializer进行序列化过程。
     * @param out
     *            用于保存数据的流<br/>
     *            保存完毕时该流仍将保持打开状态
     */
    public void saveSchema(SchemaDocument schema, OutputStream out) {
        this.schemaSerializer.serializeSchema(schema, out);
    }
    
    /**
     * 从输入流中读取一个DataDocument对象
     * 
     * @param in
     *            要从中读取数据的输入流，该流中的数据将被送给XmlSerializer进行解析以得到一个DataDocument对象。<br/>
     *            读取完毕时该流仍将保持打开状态
     * @return 从流中读取出的DataDocument对象
     */
    public DataDocument loadData(InputStream in) {
        return this.dataSerializer.deserializeData(in);
    }
    
    /**
     * 将指定的DataDocument保存到指定的输出流中
     * 
     * @param data
     *            要被保存的DataDocument对象。将使用一个XmlSerializer进行序列化过程。
     * @param out
     *            用于保存数据的流<br/>
     *            保存完毕时该流仍将保持打开状态
     */
    public void saveData(DataDocument data, OutputStream out) {
        this.dataSerializer.serializeData(data, out);
    }
    
    /**
     * 从输入流中装载一篇StyleSpecification
     * 
     * @param in
     *            要从中读取数据的输入流<br/>
     *            读取完毕时该流仍将保持打开状态
     * @return 从流中读取出的StyleSpecification对象
     */
    public StyleSpecification loadStyle(InputStream in) {
        return new StyleSpecification(in, this.paramFactory, this.exprFactory);
    }
    
    /**
     * 执行组包过程
     * 
     * @param data
     *            被组包的数据
     * @param schema
     *            被组包的数据依赖的结构
     * @param style
     *            组包样式定义
     * @return 组包出来的报文
     */
    public byte[] pack(DataDocument data, SchemaDocument schema, StyleSpecification style) {
        KyouByteOutputStream s = new KyouByteOutputStream();
        PackService.__document(schema, data, style, s);
        return s.export();
    }
}
