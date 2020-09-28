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
import java.util.function.Consumer;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.StringUtil;
import net.sf.practicalxml.DomUtil;


/**
 *  Helper methods for dealing with the XML representation of an object. Most
 *  if not all belong in Practical XML.
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


    /**
     *  Retrieves and parses an attribute containg a double value.
     */
    public static double getAttributeAsDouble(Element elem, String name)
    {
        String value = elem.getAttribute(name);
        if (StringUtil.isEmpty(value))
            throw new IllegalArgumentException("missing attribute: " + name);
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException("attribute " + name + " has unparseable value: " + value);
        }
    }

//----------------------------------------------------------------------------
//  These can't be moved to PracticalXML until it supports Java 8
//----------------------------------------------------------------------------

    /**
     *  If passed a not-null Element, validates its namespace, attempts to parse
     *  its text as a Double, and invokes the passed setter object.
     *  <p>
     *  If passed a null Element, does nothing.
     */
    public static void optSetDouble(Element elem, String namespace, Consumer<Double> setter)
    {
        if (elem == null)
            return;

        String elemNS = elem.getNamespaceURI();
        if (! StringUtil.equalOrEmpty(namespace, elemNS))
            throw new IllegalArgumentException(elem.getNodeName() + " has an invalid namespace: " + elemNS);

        String text = DomUtil.getText(elem);
        if (StringUtil.isBlank(text))
            throw new IllegalArgumentException(elem.getNodeName() + " has blank content");

        double value = 0.0;
        try
        {
            value = Double.parseDouble(text);
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException(elem.getNodeName() + " has invalid content: " + text);
        }

        setter.accept(Double.valueOf(value));
    }


    /**
     *  If passed a not-null Element, validates its namespace and invokes the passed setter
     *  object on its text content.
     *  <p>
     *  If passed a null Element, does nothing.
     */
    public static void optSetString(Element elem, String namespace, Consumer<String> setter)
    {
        if (elem == null)
            return;

        String elemNS = elem.getNamespaceURI();
        if (! StringUtil.equalOrEmpty(namespace, elemNS))
            throw new IllegalArgumentException(elem.getNodeName() + " has an invalid namespace: " + elemNS);

        String text = DomUtil.getText(elem);
        if (StringUtil.isBlank(text))
            throw new IllegalArgumentException(elem.getNodeName() + " has blank content");

        setter.accept(text);
    }
}
