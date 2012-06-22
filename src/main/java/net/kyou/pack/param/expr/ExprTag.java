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
package net.kyou.pack.param.expr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Param的实现类上，为Param的实现类提供一些静态信息。
 * 
 * @author nuclearg
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExprTag {
    /**
     * 参数的主体
     */
    String name();
    
    /**
     * 参数需要的后缀类型
     */
    ExprPostfix postfix();
    
    /**
     * 参数的返回值类型
     */
    ExprType type();
    
    /**
     * 二元参数接收的参数类型
     * <p>
     * 对于一元参数来说该值应当为Irrelevant
     * </p>
     */
    ExprType require() default ExprType.Irrelevant;
    
    /**
     * 定义了参数的后缀类型
     * 
     * @author nuclearg
     */
    public enum ExprPostfix {
        /**
         * 表示参数不应有后缀
         */
        Empty,
        /**
         * 表示参数应当有后缀
         */
        Required,
        /**
         * 表示参数应当有一个后缀，并且这个后缀必须可以被解释成一个非负整数
         */
        RequiresInt,
        /**
         * 表示参数可以没有后缀也可以有后缀
         */
        Dual,
        /**
         * 表示参数可以没有后缀也可以有后缀，但如果存在后缀则这个后缀必须可以被解释成一个非负整数
         */
        DualInt,
    }
    
    /**
     * 定义Param在执行组包过程时得出结果的什么类型
     * 
     * @author nuclearg
     */
    public enum ExprType {
        /**
         * 不关心
         */
        Irrelevant,
        
        /**
         * 整数
         */
        Integer,
        /**
         * 字符串
         */
        String,
        /**
         * 字节数组
         */
        Bytes
    }
}
