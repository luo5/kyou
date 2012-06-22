package net.kyou.util;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 格式字符串
 * <p>
 * 格式字符串是kyou自定义的方式，用来表示组包格式。其中使用\作为转义符，支持字节的16进制表示。
 * <li>%表示参数</li>
 * <li>\%表示%字符</li>
 * <li>\r表示回车</li>
 * <li>\n表示换行</li>
 * <li>\xx表示值为xx的字节</li>
 * <li>\\表示\本身</li>
 * </p>
 * <p>
 * 例：
 * <li>asdf - a s d f</li>
 * <li>as\00df - a s \0 d f</li>
 * <li>as\\df - a s \ d f</li>
 * <li>as%db - a s 参数 d f</li>
 * <li>as\%df - a s % d f</li>
 * </p>
 * 
 * @param str
 *            Kyou字符串
 * @param encoding
 *            编码
 * @return 该kyou字符串表示的字节流
 */
public class KyouFormatString {
    private static final Logger logger = Logger.getLogger(KyouFormatString.class);
    
    /**
     * KyouString的各个段
     * <p>
     * 其中文本段存储为其字面量，参数段存储为null
     * </p>
     */
    private final List<byte[]> segments;
    
    /**
     * 字节的编码
     */
    private final Charset encoding;
    
    /**
     * 构造一个格式字符串
     * 
     * @param str
     *            格式字符串
     * @param encoding
     *            编码
     */
    public KyouFormatString(String str, Charset encoding) {
        logger.debug("parse kyou format string, str=" + str + ", encoding=" + encoding);
        
        if (str == null)
            throw new KyouException(KyouErr.StyleSpec.Format.EmptyFormatString, "<null>");
        if (str.length() == 0)
            throw new KyouException(KyouErr.StyleSpec.Format.EmptyFormatString, "<empty>");
        if (encoding == null)
            throw new KyouException(KyouErr.Base.UnsupportedCharset, "<null>");
        
        this.encoding = encoding;
        List<byte[]> segments = new LinkedList<byte[]>();
        KyouString s = new KyouString(str);
        
        // 解析FormatString
        try {
            while (s.hasRemaining())
                segments.add(this.parseSegment(s, encoding));
            
            logger.debug("parse kyou format string finished. size=" + segments.size() + ", contents=" + segments);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.StyleSpec.Format.FormatStringSyntaxError, str, ex);
        }
        
        this.segments = Collections.unmodifiableList(segments);
    }
    
    /**
     * 获取此KyouString实例中的段的数量
     * 
     * @return 此KyouString实例中的段的数量
     */
    public int size() {
        return this.segments.size();
    }
    
    /**
     * 获取指定段的文本
     * 
     * @param index
     *            index 段的下标
     * @return 该段的内容。如果该段是一个参数段则返回null
     * @throws ArrayIndexOutOfBoundsException
     */
    public byte[] segment(int index) throws ArrayIndexOutOfBoundsException {
        return this.segments.get(index);
    }
    
    /**
     * 判断该KyouFormatString实例是不是简单的
     * <p>
     * “简单”是指此实例只表示一个字节数组，具体来说是指
     * <li>长度为1</li>
     * <li>这个唯一的段不是参数段</li>
     * </p>
     * 
     * @return
     */
    public boolean isSimple() {
        return this.segments.size() == 1 && this.segments.get(0) != null;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (byte[] segment : this.segments)
            if (segment != null)
                builder.append(StringUtils.replace(new String(segment, this.encoding), "%", "\\%"));
            else
                builder.append("%");
        return builder.toString();
    }
    
    /**
     * 从KyouString的头部解析出一个段
     * 
     * @param str
     *            格式字符串
     * @param encoding
     *            编码
     * @return 解析出来的段，如果是文本段则返回该段的字面量，如果是参数段则返回null
     */
    private byte[] parseSegment(KyouString str, Charset encoding) {
        // 首先判断首字母是不是%，这表示一个参数段
        if (str.attempt('%') != null)
            // 如果是参数段，则按照约定返回一个null
            return null;
        
        KyouByteOutputStream s = new KyouByteOutputStream();
        StringBuilder buffer = new StringBuilder();
        int start = str.pos();
        
        try {
            while (str.hasRemaining()) {
                // 取下一个字符
                char c = str.get();
                
                // 判断是不是转义符
                if (c == '\\') {
                    // 如果是转义符则先把buffer中的东西写到流中
                    if (buffer.length() != 0) {
                        s.write(buffer.toString().getBytes(encoding));
                        buffer = new StringBuilder();
                    }
                    // 解析这个转义符
                    byte b = this.parseEscape(str, encoding);
                    // 写到流中
                    s.write(b);
                    
                    // 继续解析后面的字符
                    continue;
                }
                
                // 判断是不是参数
                if (c == '%') {
                    // 如果是参数，则先把已经取出来的这个'%'字符再退回去
                    str.pushback();
                    // 中断解析过程，把已经解析出来的部分作为一个文本段返回去
                    break;
                }
                
                // 不是这两种情况，就是普通的字符，则加到buffer中
                buffer.append(c);
            }
            
            // 解析完毕，如果buffer中有东西，则加到流中
            if (buffer.length() != 0)
                s.write(buffer.toString().getBytes(encoding));
            
            return s.export();
        } catch (Exception ex) {
            str.pos(start);
            throw new KyouException(KyouErr.StyleSpec.Format.FormatStringSyntaxError, str.toString(), ex);
        } finally {
            s.close();
        }
    }
    
    /**
     * 从KyouString的头部解析一个转义符
     * 
     * @param str
     *            格式字符串
     * @param encoding
     *            编码
     * @return 转义符表示的字节
     */
    private byte parseEscape(KyouString str, Charset encoding) {
        if (!str.hasRemaining())
            throw new KyouException(KyouErr.StyleSpec.Format.UnsupportedEscapeSequence, "<eof> at " + str.pos());
        
        // 取出下一个字符
        char c = str.get();
        switch (c) {
            case '\\': // '\'
                return '\\';
            case 'r': // '\r'
                return '\r';
            case 'n': // '\n'
                return '\n';
            case '%': // '%'
                return '%';
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F': // HEX
                // 取下一个字符
                if (!str.hasRemaining())
                    throw new KyouException(KyouErr.StyleSpec.Format.HexStringSyntaxError, "<eof> at " + str.pos());
                char next = str.get();
                
                // 算出16进制的值
                try {
                    int v = hex2dec(c) * 16 + hex2dec(next);
                    return (byte) v;
                } catch (Exception ex) {
                    throw new KyouException(KyouErr.StyleSpec.Format.HexStringSyntaxError, "\\" + c + next + " at " + (str.pos() - 2));
                }
            default: // ERROR
                throw new KyouException(KyouErr.StyleSpec.Format.UnsupportedEscapeSequence, "\\" + c + " at " + str.pos());
        }
    }
    
    /**
     * 尝试将一个字符按照16进制解释成数字
     */
    private int hex2dec(char c) {
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'a' && c <= 'f')
            return c - 'a' + 10;
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        
        throw new KyouException(KyouErr.StyleSpec.Format.HexStringSyntaxError, String.valueOf(c));
    }
}
