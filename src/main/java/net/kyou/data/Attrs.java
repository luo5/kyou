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

/**
 * 定义一些标准的属性名称
 * 
 * @author NuclearG (<a href="nuclearg@163.com">nuclearg@163.com</a>)
 */
public class Attrs {
    /**
     * 内部使用的属性的前缀
     */
    public static final String ATTR_INTERNAL_PREFIX = "__kyou_internal_";
    
    /**
     * 名称<br/>
     * <b>("name")</b>
     * <p>
     * name属性表示报文元素的名称，是报文元素的最基础的属性。<br/>
     * 对name属性的约定如下：
     * <li>不可为空 (SchemaDocument对象除外)</li>
     * <li>在ASCII表中列出的非 a-z A-Z 0-9的字符（包括空格，不包含下划线）由内部保留，不可使用</li>
     * <li>建议仅使用 a-z A-Z 0-9 和下划线</li>
     * <li>可以使用包括中文在内的复杂符号</li>
     * <li>鉴于性能考虑，不对上述约定进行验证，但不应违背，否则会在无法预料到的地方产生错误</li>
     * </p>
     */
    public static final String NAME = "name";
    
    /**
     * 是否是数组<br/>
     * <b>("array")</b>
     * <p>
     * array属性指示某个报文元素是否可重复。该属性是用来描述报文结构的非常重要的属性。<br/>
     * 对array属性的约定如下：
     * <li>取值为"true"或"false"</li>
     * <li>实际使用时，仅判断该属性是否与"true"相同，区分大小写。如果相同则认为是true，否则认为是false。</li>
     * </p>
     */
    public static final String ARRAY = "array";
}
