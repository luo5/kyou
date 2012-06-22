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
 * 使用SchemaItem的forEach()方法遍历报文结构树时进行的操作
 * 
 * @author nuclearg
 */
public interface ISchemaVisitor {
    /**
     * 提供开始对SchemaDocument的遍历时需要执行的操作
     * 
     * @param doc
     *            当前正被遍历的SchemaDocument对象
     * @throws Exception
     */
    public void docStart(SchemaDocument doc) throws Exception;

    /**
     * 提供结束对SchemaDocument的遍历时需要执行的操作
     * 
     * @param doc
     *            当前正被遍历的SchemaDocument对象
     * @throws Exception
     */
    public void docEnd(SchemaDocument doc) throws Exception;

    /**
     * 提供开始对SchemaStruct的遍历时需要执行的操作
     * 
     * @param stru
     *            当前正被遍历的SchemaStruct对象
     * @throws Exception
     */
    public void struStart(SchemaStruct stru) throws Exception;

    /**
     * 提供结束对SchemaStruct的遍历时需要执行的操作
     * 
     * @param stru
     *            当前正被遍历的MsgStruct对象
     * @throws Exception
     */
    public void struEnd(SchemaStruct stru) throws Exception;

    /**
     * 提供遍历SchemaField时要执行的操作
     * 
     * @param field
     *            当前正被遍历的SchemaField对象
     * @throws Exception
     */
    public void field(SchemaField field) throws Exception;
}
