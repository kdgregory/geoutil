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

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.ObjectUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;


/**
 *  Style definitions that apply to lines, including the borders of polygons.
 *  <p>
 *  See https://developers.google.com/kml/documentation/kmlreference#style.
 */
public class LineStyle
extends KmlObject<LineStyle>
{
    private String color = "00000000";
    private ColorMode colorMode;
    private Double width;


//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the color assigned by this style. See {#link #setColor} for
     *  format. The default color is transparent black (00000000).
     */
    public String getColor()
    {
        return color;
    }


    /**
     *  Updates the color assigned by this style. Format is "aabbggrr", where
     *  "aa" is alpha (higher numbers = more opaque), "bb" is blue, "gg" is
     *  green, and "rr" is red. Note that this is <em>not</em> the same order
     *  as used by HTML.
     */
    public LineStyle setColor(String value)
    {
        color = value;
        return this;
    }


    /**
     *  Returns the color mode assigned by this style.
     */
    public ColorMode getColorMode()
    {
        return colorMode;
    }


    /**
     *  Updates the color mode assigned by this style.
     */
    public LineStyle setColorMode(ColorMode value)
    {
        colorMode = value;
        return this;
    }


    /**
     *  Returns the color mode assigned by this style, as a string.
     */
    public String getColorModeString()
    {
        return (colorMode == null) ? null : colorMode.name();
    }


    /**
     *  Updates the color mode assigned by this style, using a string.
     */
    public LineStyle setColorModeString(String value)
    {
        colorMode = ColorMode.fromString(value);
        return this;
    }


    /**
     *  Returns the width of this line, in pixels. May be null.
     */
    public Double getWidth()
    {
        return width;
    }


    /**
     *  Sets the width of this line, in pixels. Note that fractional widths
     *  are supported.
     */
    public LineStyle setWidth(Double value)
    {
        width = value;
        return this;
    }

//----------------------------------------------------------------------------
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Creates an instance from a DOM element tree.
     *  <p>
     *  Note: since KML documents may use multiple namespaces, this operation
     *  merely requires that the child elements have the same namespace as the
     *  passed element.
     *
     *  @throws IllegalArgumentException if the provided element does not have
     *          the name "Style", or cannot be parsed according to the KML
     *          specification.
     */
    public static LineStyle fromXml(Element elem)
    {
        if (! KmlConstants.E_LINESTYLE.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        String namespace = elem.getNamespaceURI();

        LineStyle s = new LineStyle();
        s.fromXmlHelper(elem);

        ObjectUtils.optSetString(XmlUtils.getChildText(elem, namespace, KmlConstants.E_COLORSTYLE_COLOR),   s::setColor);
        ObjectUtils.optSetString(XmlUtils.getChildText(elem, namespace, KmlConstants.E_COLORSTYLE_MODE),    s::setColorModeString);
        ObjectUtils.optSet(XmlUtils.getChildTextAsDouble(elem, namespace, KmlConstants.E_LINESTYLE_WIDTH),  s::setWidth);

        return s;
    }


    /**
     *  Appends this folder's XML representation to the provided element.
     */
    @Override
    public Element appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_LINESTYLE);
        super.appendObjectXml(elem);

        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_COLORSTYLE_COLOR,    getColor());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_COLORSTYLE_MODE,     getColorModeString());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_LINESTYLE_WIDTH,     getWidth());

        return elem;
    }
}
