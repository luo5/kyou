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
package net.kyou.pack;

import net.kyou.data.DPath;
import net.kyou.data.DataDocument;
import net.kyou.data.SchemaItem;

/**
 * 组包上下文 封闭了组包过程中会用到的一些上下文信息
 * 
 * @author nuclearg
 */
public class PackContext {
    /**
     * 当前正被组包的元素的路径
     */
    public DPath path;
    
    /**
     * 当前正被组包的元素的Schema信息
     */
    public SchemaItem schema;
    /**
     * 报文数据
     */
    public final DataDocument data;
    /**
     * 组包样式定义
     */
    public final StyleSpecification spec;
    
    /**
     * 初始化一个组包上下文实例
     * 
     * @param path
     *            当前正被组包的元素的路径
     * @param schema
     *            当前正被组包的元素的Schema信息
     * @param data
     *            报文数据
     * @param spec
     *            组包样式定义
     */
    PackContext(DPath path, SchemaItem schema, DataDocument data, StyleSpecification spec) {
        this.path = path;
        this.schema = schema;
        this.data = data;
        this.spec = spec;
    }
}
