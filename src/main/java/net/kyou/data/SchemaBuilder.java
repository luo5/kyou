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

import java.util.LinkedHashMap;
import java.util.Map;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 工具类 用于根据需求建造出一棵DOM树
 * 
 * @author nuclearg
 */
public class SchemaBuilder {
    /**
     * 当前正被构造的MsgDocument对象
     */
    private SchemaDocument doc;
    /**
     * 当前构建点
     */
    private SchemaStruct parent;

    /**
     * 初始化一个SchemaBuilder
     */
    public SchemaBuilder() {
        this.clear();
    }

    /**
     * 得到当前结果
     * <p>
     * 多次调用此方法得到的将是同一个对象<br/>
     * 在任何时候调用此方法都将会得到一棵完好的报文结构树，不会存在任何内部状态不一致的现象
     * </p>
     * 
     * @return 当前结果
     */
    public SchemaDocument result() {
        return this.doc;
    }

    /**
     * 清空建造器的内部状态
     * <p>
     * 执行该操作后result()将返回一个空的SchemaDocument对象
     * </p>
     */
    public void clear() {
        this.parent = this.doc = new SchemaDocument();
    }

    /**
     * 构建一个SchemaStruct
     * 
     * @param atts
     *            该结构元素的属性
     */
    public void beginStruct(Map<String, String> atts) {
        SchemaStruct stru = new SchemaStruct();
        stru.attrs.putAll(atts);
        this.parent.unsafeAdd(stru);
        this.parent = stru;
    }

    /**
     * 构建一个SchemaStruct
     * 
     * @param atts
     *            该结构元素的属性
     *            <p>
     *            使用以下格式：属性名称，属性值，属性名称，属性值……
     *            </p>
     */
    public void beginStruct(String... atts) {
        this.beginStruct(this.parseAtts(atts));
    }

    /**
     * 关闭当前最外面的SchemaStruct的构建过程
     */
    public void endStruct() {
        this.parent = this.parent.parent();
    }

    /**
     * 构建一个域
     * 
     * @param atts
     *            该域元素的属性
     */
    public void field(Map<String, String> atts) {
        SchemaField field = new SchemaField();
        field.attrs.putAll(atts);
        this.parent.unsafeAdd(field);
    }

    /**
     * 构建一个域
     * 
     * @param atts
     *            该域元素的属性
     *            <p>
     *            使用以下格式：属性名称，属性值，属性名称，属性值……
     *            </p>
     */
    public void field(String... atts) {
        this.field(this.parseAtts(atts));
    }

    /**
     * 用于将用户以String...格式传进来的参数解析为Map
     */
    private Map<String, String> parseAtts(String[] atts) {
        if (atts.length % 2 != 0)
            throw new KyouException(KyouErr.Schema.InvalidSchemaBuilderArguments);

        Map<String, String> map = new LinkedHashMap<String, String>();

        String name = null;
        for (int i = 0; i < atts.length; i++)
            if (i % 2 == 0)
                name = atts[i];
            else
                map.put(name, atts[i]);

        return map;
    }
}
