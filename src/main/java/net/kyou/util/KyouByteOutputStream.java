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

import java.io.OutputStream;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * Kyou内部的基于字节数组的输出流
 * <p>
 * 该流主要用于承载组包时组出来的字节
 * </p>
 * 
 * @author nuclearg
 */
public class KyouByteOutputStream extends OutputStream {
    /**
     * 缓存
     */
    private byte[] buffer;
    /**
     * 写入指针的当前位置
     */
    private int pos;

    /**
     * 初始化一个KyouByteOutputStream对象
     */
    public KyouByteOutputStream() {
        this.buffer = new byte[32];
    }

    @Override
    public void write(int b) {
        this.ensure(1);

        this.buffer[this.pos++] = (byte) b;
    }

    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.ensure(len);

        System.arraycopy(b, off, this.buffer, this.pos, len);

        this.pos += len;
    }

    @Override
    public void close() {
    }

    /**
     * 导出流中存储的字节
     * 
     * @return 流中存储的字节的一个副本
     */
    public byte[] export() {
        byte[] bytes = new byte[this.pos];
        System.arraycopy(this.buffer, 0, bytes, 0, this.pos);
        return bytes;
    }

    /**
     * 回退掉最后放到流中的指定数量的字节
     * 
     * @param backspace
     *            要回退的字节数量<br/>
     *            该值不能小于0，并且不能大于流中目前已有的字节数。
     */
    public void backspace(int backspace) {
        if (backspace < 0)
            throw new KyouException(KyouErr.Base.ByteStream.IllegalBackspaceNum, String.valueOf(backspace));
        if (backspace > this.pos)
            throw new KyouException(KyouErr.Base.ByteStream.IllegalBackspaceNum, "backspace: " + backspace + " total: " + this.pos);

        this.pos -= backspace;
    }

    /**
     * 确保buffer中还有期望的空间
     */
    private void ensure(int space) {
        if (this.buffer.length - this.pos < space) {
            byte[] buffer = new byte[(this.buffer.length + space) * 2];
            System.arraycopy(this.buffer, 0, buffer, 0, this.pos);
            this.buffer = buffer;
        }
    }
}
