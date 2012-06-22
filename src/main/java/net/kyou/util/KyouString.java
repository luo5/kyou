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
package net.kyou.util;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 向普通的字符串上加一个pos，主要用于各种解析过程。支持基于各种条件的“读取下一个字符”操作
 * 
 * @author nuclearg
 */
public class KyouString {
    /**
     * 字符数组
     */
    private final char[] chars;
    /**
     * 当前位置
     */
    private int pos;
    
    /**
     * 初始化一个LL1String
     * 
     * @param str
     *            源字符串
     */
    public KyouString(String str) {
        if (str == null)
            throw new KyouException(KyouErr.Base.KyouString.EmptyKyouString, "<null>");
        
        this.chars = str.toCharArray();
    }
    
    /**
     * 从LL1String中获取当前位置的字符，并将pos向前推进一格
     * 
     * @return 当前位置的字符
     */
    public char get() {
        return this.chars[pos++];
    }
    
    /**
     * 获取LL1String中指定范围的字符
     * 
     * @param start
     *            起始位置
     * @param length
     *            长度
     * @return 指定范围内的字符组成的字符串
     */
    public String get(int start, int length) {
        return new String(this.chars, start, length);
    }
    
    /**
     * 向LL1String中推回一个位置的字符
     */
    public void pushback() {
        this.pos--;
        if (this.pos < 0)
            throw new IndexOutOfBoundsException(String.valueOf(this.pos));
    }
    
    /**
     * 获取当前位置
     * 
     * @return 当前位置
     */
    public int pos() {
        return this.pos;
    }
    
    /**
     * 设置当前位置
     * 
     * @param pos
     *            新位置
     */
    public void pos(int pos) {
        this.pos = pos;
    }
    
    /**
     * 获取总长度
     * 
     * @return 总长度
     */
    public int length() {
        return this.chars.length;
    }
    
    /**
     * 判断该LL1String的pos是否小于length
     * <p>
     * 这通常表示是否还可以进行get()操作
     * </p>
     * 
     * @return 该LL1String的pos是否小于length
     */
    public boolean hasRemaining() {
        return this.pos < this.chars.length;
    }
    
    /**
     * 判断LL1String的pos位置处的字符是不是给定字符中的一个，如果是则取出该字符
     * 
     * @param options
     *            所有可能的字符列表
     * @return 找到的字符，或为null，表示没有找到给定的字符
     */
    public Character attempt(char... options) {
        if (!this.hasRemaining())
            return null;
        
        // 看当前位置的字符是不是在给定的字符列表中
        if (ArrayUtils.contains(options, this.chars[this.pos]))
            return this.get();
        else
            return null;
    }
    
    /**
     * 判断LL1String的当前位置处开始是不是给定字符串中的一个，如果是则取出该字符串
     * <p>
     * 如果找到多个匹配，将返回长度最长的那个
     * </p>
     * 
     * @param options
     *            所有可能的字符串列表
     * @return 取出来的字符串，或为null，表示没有取到给定的字符串
     */
    public String attempt(String... options) {
        if (!this.hasRemaining())
            return null;
        
        String match = null;
        
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            
            // 先判断剩余的字符数是否满足要求的字符串的长度
            if (option.length() > this.chars.length - this.pos)
                continue;
            
            // 判断从pos开始的字符是否与要求的字符串匹配
            boolean matches = true;
            for (int j = 0; j < option.length(); j++)
                if (option.charAt(j) != this.chars[this.pos + j]) {
                    matches = false;
                    break;
                }
            
            if (matches)
                // 得到了一个匹配，看这个匹配是不是最长的
                if (match == null || option.length() > match.length())
                    match = option;
        }
        
        // 如果找到了匹配，则把pos往前移相应的长度
        if (match != null)
            this.pos += match.length();
        
        return match;
    }
    
    /**
     * 使用一个自定义的matcher，尽可能多地进行匹配
     * 
     * @param matcher
     *            自定义的matcher
     * @return 取出来的字符串。如果第一个字符就匹配失败或遇到字符串结尾，则返回null
     */
    public String attempt(AttemptMatcher matcher) {
        if (!this.hasRemaining())
            return null;
        
        int start = this.pos;
        
        // 从当前位置开始遍历各个字符，送给matcher判断是否匹配
        for (int i = this.pos; i < this.chars.length; i++) {
            char c = this.chars[i];
            if (matcher.matches(c, i))
                this.pos++;
            else
                break;
        }
        
        // 如果当前位置与起始位置相同则表示第一个字符就匹配失败了
        if (this.pos == start)
            // 此时返回null
            return null;
        else
            // 返回成功匹配的字符串
            return new String(this.chars, start, this.pos - start);
    }
    
    /**
     * 尽可能多地进行匹配，只要字符在给定的范围之内或遇到字符串结尾
     * 
     * @param chars
     *            允许的字符列表
     * @return 取出来的字符串。如果第一个字符就匹配失败或遇到字符串结尾，则返回null
     */
    public String attemptMore(char... chars) {
        return this.attempt(new AvailableCharMatcher(chars));
    }
    
    /**
     * 尽可能多地进行匹配，直到字符在给定的范围之内或遇到字符串结尾
     * 
     * @param chars
     *            不可以被匹配的字符列表
     * @return 取出来的字符串。如果第一个字符就匹配失败或遇到字符串结尾，则返回null
     */
    public String attemptUntil(char... chars) {
        return this.attempt(new UnavailableCharMatcher(chars));
    }
    
    @Override
    public String toString() {
        return new String(this.chars, this.pos, this.chars.length - this.pos);
    }
    
    /**
     * 自定义的匹配器，用于从LL1String的头部获取一些字符
     * 
     * @author nuclearg
     */
    public static interface AttemptMatcher {
        /**
         * 判断给定的字符是否满足匹配条件
         * 
         * @param c
         *            当前字符
         * @param pos
         *            当前匹配的位置
         * @return 该字符是否满足匹配条件
         */
        public boolean matches(char c, int pos);
    }
    
    /**
     * 允许某些字符的matcher
     * <p>
     * 如果某个字符在给定的字符集合内，则这个字符可以被匹配
     * </p>
     * 
     * @author nuclearg
     */
    private static class AvailableCharMatcher implements AttemptMatcher {
        /**
         * 允许的字符列表
         */
        private final char[] chars;
        
        /**
         * 初始化一个AvailableCharMatcher
         * 
         * @param chars
         *            可以被匹配的字符列表
         */
        public AvailableCharMatcher(char... chars) {
            this.chars = chars;
        }
        
        @Override
        public boolean matches(char c, int pos) {
            return ArrayUtils.contains(this.chars, c);
        }
    }
    
    /**
     * 不允许某些字符的matcher
     * <p>
     * 如果某个字符在给定的字符集合内，则这个字符不可以被匹配<br/>
     * 即，匹配到指定的字符为止的所有字符
     * </p>
     * 
     * @author nuclearg
     */
    private static class UnavailableCharMatcher implements AttemptMatcher {
        /**
         * 允许的字符列表
         */
        private final char[] chars;
        
        /**
         * 初始化一个UnavailableCharMatcher
         * 
         * @param chars
         *            不可以被匹配的字符列表
         */
        public UnavailableCharMatcher(char... chars) {
            this.chars = chars;
        }
        
        @Override
        public boolean matches(char c, int pos) {
            return !ArrayUtils.contains(this.chars, c);
        }
    }
}
