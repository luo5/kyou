package net.kyou.pack;

import java.io.ByteArrayInputStream;

import net.kyou.data.DPath;
import net.kyou.data.DataDocument;
import net.kyou.data.SchemaItem;
import net.kyou.util.KyouXmlUtils;

import org.w3c.dom.Element;

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

public class UTILS {
    public static Element prepareElement(String xml) {
        try {
            xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + xml;
            ByteArrayInputStream in;

            in = new ByteArrayInputStream(xml.getBytes("utf-8"));

            return KyouXmlUtils.load(in).getDocumentElement();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static PackContext PackContext(DPath path, SchemaItem schema, DataDocument data, StyleSpecification spec) {
        return new PackContext(path, schema, data, spec);
    }
}
