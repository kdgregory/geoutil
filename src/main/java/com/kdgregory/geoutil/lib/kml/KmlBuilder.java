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

package com.kdgregory.geoutil.lib.kml;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.OutputUtil;

import com.kdgregory.geoutil.lib.shared.Point;


/**
 *  Helper to build a KML document without needing to deal with all of the XML DOM
 *  manipulation.
 *  <p>
 *  Like all builders, this class is based on the idea of adding components to some
 *  base object. However, it does not provide the normal <code>addXXX()</code> /
 *  <code>withXXX()</code> methods. Instead, there's a single {@link #add} method
 *  that takes one or more component objects. These component objects are actually
 *  functions, which are invoked to modify the document. You create components via
 *  a set of static methods (which can be static-imported if you don't like to
 *  prefix everything with <code>KmlBuilder</code>. These static methods themselves
 *  accept additional component functions, which will be invoked with the parent (and
 *  so on as far as you want to go).
 */
public class KmlBuilder
{
    private Element root;


    public KmlBuilder()
    {
        Element domRoot = DomUtil.newDocument("http://www.opengis.net/kml/2.2", "kml");
        root = DomUtil.appendChildInheritNamespace(domRoot, "Document");
    }


    /**
     *  Adds one or more components to this builder. Returns the builder itself, so
     *  that calls may be chained.
     */
    public KmlBuilder add(Consumer<Element>... children)
    {
        applyChildren(root, children);
        return this;
    }


    /**
     *  Writes the KML document to an output file.
     */
    public void save(File file)
    throws IOException
    {
        OutputUtil.indented(new DOMSource(root.getOwnerDocument()), new StreamResult(file), 4);
    }

//----------------------------------------------------------------------------
//  Components
//----------------------------------------------------------------------------

    /**
     *  Adds a description to some other component.
     */
    public static Consumer<Element> description(String content)
    {
        return parent -> {
            addNameValue(parent, "description", content);
        };
    }


    /**
     *  Adds a style reference to some other component.
     */
    public static Consumer<Element> styleRef(String id)
    {
        return parent -> {
            addNameValue(parent, "styleUrl", "#" + id);
        };
    }


    /**
     *  Adds a Style container.
     */
    public static Consumer<Element> style(String id, Consumer<Element>... children)
    {
        return parent -> {
            // TODO - verify that parent is Document
            Element child = DomUtil.appendChildInheritNamespace(parent, "Style");
            child.setAttribute("id", id);
            applyChildren(child, children);
        };
    }


    /**
     *  Adds a LineStyle component to a Style container.
     */
    public static Consumer<Element> lineStyle(int width, String color)
    {
        return parent -> {
            // TODO - verify that parent is Style
            Element child = DomUtil.appendChildInheritNamespace(parent, "LineStyle");
            addNameValue(child, "width", String.valueOf(width));
            addNameValue(child, "color", color);
        };
    }


    /**
     *  Adds a Placemark container, that can be customized with multiple children.
     */
    public static Consumer<Element>  placemark(Consumer<Element>... children)
    {
        return parent -> {
            // TODO - verify that parent is Document
            Element child = DomUtil.appendChildInheritNamespace(parent, "Placemark");
            applyChildren(child, children);        };
    }


    /**
     *  Adds a LineString component to a Placemark container.
     */
    public static Consumer<Element> lineSegment(Point start, Point finish, Consumer<Element>... children)
    {
        return parent -> {
            // TODO - verify that parent is Placemark
            Element ls = DomUtil.appendChildInheritNamespace(parent, "LineString");
            addNameValue(ls, "coordinates",
                         start.getLon() + "," + start.getLat() + " " + finish.getLon() + "," + finish.getLat());
        };
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static void applyChildren(Element parent, Consumer<Element>... children)
    {
        for (Consumer<Element> child : children)
        {
            child.accept(parent);
        }
    }


    private static void addNameValue(Element parent, String name, String value)
    {
        Element child = DomUtil.appendChildInheritNamespace(parent, name);
        child.setTextContent(value);
    }
}
