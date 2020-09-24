// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.geoutil.lib.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

/**
 *  A temporary class containing XML utility methods. These should be
 *  moved to the PracticalXML library.
 */
public class XmlUtils
{
    /**
     *  Apends a child element that contains the string value of the passed
     *  object, iff it's not null.
     */
    public static void optAppendDataElement(Element parent, String namespace, String localName, Object value)
    {
        if (value == null)
            return;

        Element c = DomUtil.appendChild(parent, namespace, localName);
        c.setTextContent(value.toString());
    }


    /**
     *  Given a list of elements, converts them to a map keyed by localname. If the
     *  source contains elements with duplicate name, only the last is retained.
     */
    public static Map<String,Element> listToMap(List<Element> children)
    {
        Map<String,Element> result = new HashMap<>();
        for (Element child : children)
        {
            result.put(DomUtil.getLocalName(child), child);
        }
        return result;
    }
}
