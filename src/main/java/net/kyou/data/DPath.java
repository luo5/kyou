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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.apache.commons.lang.StringUtils;

/**
 * 描述报文数据的路径
 * 
 * @author nuclearg
 */
public class DPath {
    /**
     * 预定义的DPath根节点，表示报文根元素
     */
    public static final DPath root = new DPath("#");
    
    /**
     * 该DPath的全路径形式
     */
    private final String dpath;
    /**
     * 该DPath的各个段
     */
    private final String[] segments;
    
    /**
     * 初始化一个DPath实例
     * 
     * @param dpath
     *            DPath的字符串形式
     */
    DPath(String dpath) {
        if (StringUtils.isEmpty(dpath))
            throw new KyouException(KyouErr.DPath.EmptyDPath);
        
        this.dpath = dpath;
        this.segments = StringUtils.split(dpath, '.');
    }
    
    /**
     * 初始化一个DPath实例
     * 
     * @param segments
     *            DPath的分段形式
     */
    private DPath(String[] segments) {
        this.segments = segments;
        this.dpath = StringUtils.join(segments, '.');
    }
    
    /**
     * 获取该路径的最后一段，即报文元素的名称
     * 
     * @return 报文元素的名称
     */
    public String name() {
        return this.segments[this.segments.length - 1];
    }
    
    /**
     * 获取一个描述当前路径的某个子节点的DPath实例
     * 
     * @param name
     *            子节点的名称
     * @return 描述该子节点的DPath实例
     */
    public DPath child(String name) {
        String[] segments = new String[this.segments.length + 1];
        System.arraycopy(this.segments, 0, segments, 0, this.segments.length);
        segments[this.segments.length] = name;
        return new DPath(segments);
    }
    
    /**
     * 获取一个描述该路径的父元素路径的DPath实例
     * 
     * @return 描述该路径的父元素路径的DPath实例。如果已经是根元素则返回自身
     */
    public DPath parent() {
        if (this.isRoot())
            return this;
        
        String[] segments = new String[this.segments.length - 1];
        System.arraycopy(this.segments, 0, segments, 0, segments.length);
        return new DPath(segments);
    }
    
    /**
     * 判断该路径是否指向报文根节点
     * 
     * @return 该路径是否指向报文根节点
     */
    public boolean isRoot() {
        return this.dpath.equals("#");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.dpath.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DPath other = (DPath) obj;
        if (!this.dpath.equals(other.dpath))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return this.dpath;
    }
}
