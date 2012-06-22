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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kyou.data.DataDocument.IDataSerializer;
import net.kyou.data.SchemaDocument.ISchemaSerializer;
import net.kyou.data.XmlSerializer;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.ParamTag;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.endpoint.EndpointExpr;
import net.kyou.pack.param.expr.pipe.PipeExpr;

/**
 * Kyou实例的构造工厂
 * <p>
 * 当默认的Kyou实例<code>Kyou.instance</code>不能满足需求时，可以使用本工厂构造一个符合需求的Kyou实例。<br/>
 * 可以进行如下设置：
 * <li>修改序列化/反序列化报文结构的方式。默认使用{@link XmlSerializer}</li>
 * <li>修改序列化/反序列化报文数据的方式。默认使用{@link XmlSerializer}</li>
 * <li>添加自定义的参数</li>
 * <li>用自定义的参数覆盖掉Kyou的默认行为（不建议）</li>
 * <li>添加自定义的表达式</li>
 * <li>也可以使用自定义的表达式覆盖掉Kyou的默认行为（不建议）</li>
 * </p>
 * <p>
 * 在设置好后，调用export()方法得到一个自定义的Kyou实例。
 * </p>
 * 
 * @author nuclearg
 * 
 */
public class KyouBuilder {
    /**
     * Schema的序列化/反序列化器
     */
    private ISchemaSerializer schemaSerializer;
    /**
     * 报文数据的序列化/反序列化器
     */
    private IDataSerializer dataSerializer;
    
    /**
     * 自定义参数类型列表
     */
    private List<Class<? extends Param>> paramClasses = new ArrayList<Class<? extends Param>>();
    /**
     * 自定义端点表达式类型列表
     */
    private List<Class<? extends EndpointExpr>> endpointExprClasses = new ArrayList<Class<? extends EndpointExpr>>();
    /**
     * 自定义管道表达式类型列表
     */
    private List<Class<? extends PipeExpr>> pipeExprClasses = new ArrayList<Class<? extends PipeExpr>>();
    
    /**
     * 设置报文结构序列化/反序列化实现
     * 
     * @param schemaSerializer
     *            报文结构序列化/反序列化实现
     */
    public void setSchemaSerializer(ISchemaSerializer schemaSerializer) {
        this.schemaSerializer = schemaSerializer;
    }
    
    /**
     * 设置报文结构序列化/反序列化实现
     * 
     * @param schemaSerializer
     *            报文结构序列化/反序列化实现
     */
    public void setDataSerializer(IDataSerializer dataSerializer) {
        this.dataSerializer = dataSerializer;
    }
    
    /**
     * 注册组包时可以使用的参数类型
     * <p>
     * <li>被注册的参数类型必须有{@link ParamTag}标记</li>
     * <li>如果新的参数类型的关键字与已有的冲突，则后注册的会覆盖掉之前注册的</li>
     * <li>默认已经包含了Kyou中提供的所有参数类型</li>
     * </p>
     * 
     * @param paramClasses
     *            用户自定义的组包参数类型列表
     */
    public void registerPackParams(Class<? extends Param>... paramClasses) {
        this.paramClasses.addAll(Arrays.asList(paramClasses));
    }
    
    /**
     * 注册组包时可以使用的端点表达式类型
     * <p>
     * <li>被注册的表达式类型必须有{@link ExprTag}标记</li>
     * <li>如果新的表达式类型的关键字与已有的冲突，则后注册的会覆盖掉之前注册的</li>
     * <li>默认已经包含了Kyou中提供的所有端点表达式类型</li>
     * </p>
     * 
     * @param paramClasses
     *            用户自定义的组包端点表达式类型列表
     */
    public void registerPackEndpointExprs(Class<? extends EndpointExpr>... endpointExprClasses) {
        this.endpointExprClasses.addAll(Arrays.asList(endpointExprClasses));
    }
    
    /**
     * 注册组包时可以使用的管道表达式类型
     * <p>
     * <li>被注册的表达式类型必须有{@link ExprTag}标记</li>
     * <li>如果新的表达式类型的关键字与已有的冲突，则后注册的会覆盖掉之前注册的</li>
     * <li>默认已经包含了Kyou中提供的所有管道表达式类型</li>
     * </p>
     * 
     * @param paramClasses
     *            用户自定义的组包管道表达式类型列表
     */
    public void registerPackPipeExprs(Class<? extends PipeExpr>... pipeExprClasses) {
        this.pipeExprClasses.addAll(Arrays.asList(pipeExprClasses));
    }
    
    /**
     * 构造一个Kyou实例
     * 
     * @return 返回构造好的Kyou实例
     */
    public Kyou export() {
        // schemaSerializer和dataSerializer默认使用XmlSerializer
        if (this.schemaSerializer == null && this.dataSerializer == null) {
            XmlSerializer xmlSerializer = new XmlSerializer();
            this.schemaSerializer = xmlSerializer;
            this.dataSerializer = xmlSerializer;
        }
        if (this.schemaSerializer == null)
            this.schemaSerializer = new XmlSerializer();
        if (this.dataSerializer == null)
            this.dataSerializer = new XmlSerializer();
        
        // 构造ParamFactory
        ParamFactory paramFactory = new ParamFactory(this.paramClasses);
        
        // 构造ExprFactory
        ExprFactory exprFactory = new ExprFactory(this.endpointExprClasses, this.pipeExprClasses);
        
        // 构造一个Kyou实例并返回给调用者
        return new Kyou(this.schemaSerializer, this.dataSerializer, paramFactory, exprFactory);
    }
}
