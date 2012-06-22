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

public class SchemaField extends SchemaItem {

    @Override
    public void foreach(ISchemaVisitor visitor) {
        try {
            visitor.field(this);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Schema.ForEachFail, ex);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        int depth = this.depth();
        for (int i = 0; i < depth; i++)
            buffer.append("  ");

        buffer.append("<field");
        buffer.append(this.attrs);
        buffer.append("/>\r\n");

        return buffer.toString();
    }
}
