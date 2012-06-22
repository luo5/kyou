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
package net.kyou.data.dquery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.data.dquery.restriction.DQueryRestriction;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString;

/**
 * 表示DQuery中的各个段
 * <p>
 * 一个完整的DQuery看起来像是<code>#.body.element.*</code>
 * </p>
 * 
 * @author nuclearg
 */
class DQuerySegment {
    /**
     * 段的类型
     */
    final SegmentType type;
    /**
     * DQuery段的主体
     */
    final String body;
    /**
     * 约束条件列表
     */
    final List<DQueryRestriction> restrictions;
    
    /**
     * 初始化一个DQuery段
     */
    DQuerySegment(KyouString str) {
        if (str == null || str.length() == 0)
            throw new KyouException(KyouErr.DQuery.EmptyDQuerySegment);
        
        int start = str.pos();
        
        try {
            // 解析body
            this.body = this.parseBody(str);
            if (body == null)
                throw new KyouException(KyouErr.DQuery.EmptyDQuerySegmentBody, str.toString());
            
            // 如果body后面跟了'.'则匹配掉
            str.attempt('.');
            
            // 判断body是否是某些特殊的字符
            if (this.body.length() == 1)
                // 如果body的长度为1，则判断一下是否是特殊字符
                switch (this.body.charAt(0)) {
                    case '#': // #字符表示报文根结点
                        this.type = SegmentType.Root;
                        break;
                    case '*': // *字符表示所有报文子节点
                        this.type = SegmentType.All;
                        break;
                    default: // 其它字符
                        this.type = SegmentType.Normal;
                        break;
                }
            else
                // 如果body的长度不为1，则它不可能是特殊字符
                this.type = SegmentType.Normal;
            
            // 解析restrictions
            this.restrictions = Collections.unmodifiableList(this.parseRestrictions(str));
            
            // 如果restrictions后面跟了'.'则匹配掉
            str.attempt('.');
        } catch (Exception ex) {
            str.pos(start);
            throw new KyouException(KyouErr.DQuery.DQuerySyntaxError, str.toString(), ex);
        }
    }
    
    /**
     * 解析DQuery段中的主体部分
     */
    private String parseBody(KyouString str) {
        return str.attemptUntil('.', '[');
    }
    
    /**
     * 解析DQuery段中的约束部分
     */
    private List<DQueryRestriction> parseRestrictions(KyouString str) {
        ArrayList<DQueryRestriction> restrictions = new ArrayList<DQueryRestriction>();
        
        int start = str.pos();
        try {
            // 匹配左边界
            if (!this.parseLeftBracket(str))
                return restrictions;
            
            while (!this.parseRightBracket(str)) {
                // 把约束条件前面可能的分隔符和空白匹配掉
                this.parseDelimiter(str);
                
                // 尝试解析一个约束条件
                DQueryRestriction restriction = DQueryRestriction.__parse(str);
                if (restriction != null)
                    restrictions.add(restriction);
                
                // 把约束条件后面可能的分隔符和空白匹配掉
                this.parseDelimiter(str);
            }
            
            return restrictions;
        } catch (Exception ex) {
            str.pos(start);
            throw new KyouException(KyouErr.DQuery.Restriction.SyntaxError, str.toString(), ex);
        }
    }
    
    /**
     * 匹配约束条件的左括号
     */
    private boolean parseLeftBracket(KyouString str) {
        return str.attempt('[') != null;
    }
    
    /**
     * 匹配掉约束条件中间的分隔符
     */
    private void parseDelimiter(KyouString str) {
        // 取出所有','和' '
        str.attemptMore(',', ' ');
    }
    
    /**
     * 匹配约束条件的右括号
     */
    private boolean parseRightBracket(KyouString str) {
        // 判断第一个字符是不是']'
        return str.attempt(']') != null;
    }
    
    @Override
    public String toString() {
        return this.body + this.restrictions;
    }
    
    /**
     * 段类型
     * 
     * @author nuclearg
     */
    enum SegmentType {
        /**
         * 该段定位到指定的报文子节点
         */
        Normal,
        /**
         * 该段定位到所有报文子节点
         */
        All,
        /**
         * 该段定位到报文根结点
         */
        Root,
    }
}
