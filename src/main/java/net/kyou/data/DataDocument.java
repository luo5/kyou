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
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 报文数据
 * <p>
 * 在kyou中，一篇报文的结构和数据是分开存放的。结构由SchemaDocument存储，而数据则由DataDocument存储。<br/>
 * DataDocument本质上就是一个键值对的列表。其键是报文中各个域的路径，其值是报文中各个域的值。<br/>
 * DataDocument中并未显式包含任何报文结构的信息（虽然可以从各个域的路径中推断出层次结构），报文的层次结构是由SchemaDocument存储的。
 * <br/>
 * 一个DataDocument对象和其对应的SchemaDocument对象完整地描述了一篇报文。
 * </p>
 * <p>
 * 为了避免将内部状态弄乱，始终应当使用DataBuilder类对DataDocument的实例进行修改。
 * </p>
 * 
 * @author nuclearg
 * @see SchemaDocument
 */
public class DataDocument implements Serializable {
    private static final long serialVersionUID = -2294014881411235540L;
    
    /**
     * 数组的占位符
     * <p>
     * 当某项存在名称为该占位符的子元素时表示该元素是一个数组元素，该子元素中保存着数组元素的个数
     * </p>
     */
    public static final String ARRAY_PLACEHOLDER = "[]";
    
    /**
     * 用于保存报文数据的map
     */
    final LinkedHashMap<DPath, String> map = new LinkedHashMap<DPath, String>();
    
    /**
     * 取出与某个路径对应的值。如果不存在指定的路径则返回null。
     * 
     * @param path
     *            路径
     * @return 报文数据中与指定的路径对应的值
     */
    public String get(DPath path) {
        return this.map.get(path);
    }
    
    /**
     * 获取某个指定数组元素的子元素的路径列表
     * <p>
     * <li>如果该元素不存在或不是一个数组元素则返回null</li>
     * <li>如果该元素是一个数组，则返回该数组的所有元素。如果该数组长度为0则返回一个空数组。</li>
     * </p>
     * 
     * @param path
     *            要列出子元素的数组元素的路径
     * @return 返回期望的子元素路径列表
     */
    public DPath[] elements(DPath path) {
        if (!this.map.containsKey(path))
            return null;
        
        // 判断该path是否是一个数组
        DPath arrayPath = path.child(ARRAY_PLACEHOLDER);
        if (!this.map.containsKey(arrayPath))
            return null;
        
        // 取出数组元素的数量
        String value = this.map.get(arrayPath);
        int count;
        try {
            count = Integer.parseInt(value);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Data.ArrayElementCountParseFail, "path: " + path + ", value: " + value, ex);
        }
        
        // 拼出各个数组元素
        DPath[] children = new DPath[count];
        for (int i = 0; i < count; i++)
            children[i] = path.child(String.valueOf(i));
        
        return children;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<data>\r\n");
        
        for (Entry<DPath, String> entry : this.map.entrySet())
            builder.append("<").append(entry.getKey()).append(">").append(entry.getValue()).append("</").append(entry.getKey()).append(">").append("\r\n");
        
        return builder.append("</data>").toString();
    }
    
    /**
     * 报文数据（{@link DataDocument}）的序列化/反序列化接口
     * 
     * @author nuclearg
     */
    public interface IDataSerializer {
        /**
         * 将提供的报文数据进行序列化并写入到指定的输出流中
         * 
         * @param doc
         *            要被序列化的报文数据对象
         * @param out
         *            要将序列化的数据写入到的流<br/>
         *            序列化完毕时该流仍将保持打开状态
         */
        public void serializeData(DataDocument doc, OutputStream out);
        
        /**
         * 从指定的输入流中读取并尝试反序列化出一个报文数据对象
         * 
         * @param in
         *            要从中读取数据的输入流<br/>
         *            反序列化完毕时该流仍将保持打开状态
         */
        public DataDocument deserializeData(InputStream in);
    }
}
