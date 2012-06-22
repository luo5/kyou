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
package net.kyou.pack.param.expr.endpoint;

import java.nio.charset.Charset;

import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.Expr;

/**
 * 端点参数 这种类型的参数不接受其它参数的计算结果
 * 
 * @author nuclearg
 */
public abstract class EndpointExpr extends Expr {
    /**
     * 初始化一个端点参数实例
     * <p>
     * 派生类在构造函数中必须调用此构造函数，以初始化一些基础信息
     * </p>
     * 
     * @param postfix
     *            后缀，可以为null
     * @param param
     *            该表达式隶属于的参数
     * @param encoding
     *            报文整体编码
     */
    protected EndpointExpr(String postfix, Param param, Charset encoding) {
        super(postfix, param, encoding);
    }
    
    @Override
    public String toString() {
        return this.postfix == null ? this.body : this.body + "." + this.postfix;
    }
}
