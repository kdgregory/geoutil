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

package com.kdgregory.geoutil.lib.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *  Apends a child element that contains the XML Schema Instance representation
     * of the passed boolean value, iff it's not null.
     */
    public static void optAppendDataElement(Element parent, String namespace, String localName, Boolean value)
    {
        if (value == null)
            return;

        Element c = DomUtil.appendChild(parent, namespace, localName);
        c.setTextContent(value.booleanValue() ? "1" : "0");
    }


    /**
     *  Returns the text content of the specified child element, null if the child
     *  doesn't exist.
     */
    public static String getChildText(Element parent, String lclName)
    {
        Element child = DomUtil.getChild(parent, lclName);
        return (child == null)
             ? null
             : DomUtil.getText(child);
    }


    /**
     *  Returns the text content of the specified child element, null if the child
     *  doesn't exist.
     */
    public static String getChildText(Element parent, String nsUri, String lclName)
    {
        Element child = DomUtil.getChild(parent, nsUri, lclName);
        return (child == null)
             ? null
             : DomUtil.getText(child);
    }


    /**
     *  Retrieves the text from a child element and parses it as a Double, null if
     *  the child doesn't exist.
     */
    public static Double getChildTextAsDouble(Element parent, String nsUri, String lclName)
    {
        String text = getChildText(parent, nsUri, lclName);
        if (text == null)
            return null;

        try
        {
            return Double.valueOf(text);
        }
        catch (NumberFormatException ex)
        {
            // trim stack trace and give a more useful message
            throw new IllegalArgumentException(nsUri + ":" + lclName + " expected double, was: " + text);
        }
    }


    /**
     *  Returns the text content of a child element as a Boolean, using the
     *  encoding supported by XML Schema. Returns null if the child doesn't
     *  exist, throws if the value is not a supported encoding.
     */
    public static Boolean getChildTextAsBoolean(Element parent, String nsUri, String childName)
    {
        String text = getChildText(parent, nsUri, childName);
        try
        {
            return ObjectUtils.parseAsBoolean(text);
        }
        catch (IllegalArgumentException ex)
        {
            throw new IllegalArgumentException("could not parse " + childName + ": " + text);
        }
    }


    /**
     *  Retrieves and parses an attribute containg a double value.
     */
    public static double getAttributeAsDouble(Element elem, String name)
    {
        String value = elem.getAttribute(name);
        if (StringUtil.isEmpty(value))
            throw new IllegalArgumentException(elem.getNodeName() + "missing attribute: " + name);
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException("could not parse " + name + ": " + value);
        }
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
