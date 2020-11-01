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


/**
 *  A container for style definitions.
 *  <p>
 *  See https://developers.google.com/kml/documentation/kmlreference#style.
 */
public class Style
extends KmlObject<Style>
{
    private IconStyle iconStyle;
    private LineStyle lineStyle;

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the IconStyle attached to this container. May be null.
     */
    public IconStyle getIconStyle()
    {
        return iconStyle;
    }


    /**
     *  Attaches an IconStyle to this container.
     */
    public Style setIconStyle(IconStyle value)
    {
        iconStyle = value;
        return this;
    }

    /**
     *  Returns the LineStyle attached to this container. May be null.
     */
    public LineStyle getLineStyle()
    {
        return lineStyle;
    }


    /**
     *  Attaches a LineStyle to this container.
     */
    public Style setLineStyle(LineStyle value)
    {
        lineStyle = value;
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
    public static Style fromXml(Element elem)
    {
        if (! KmlConstants.E_STYLE.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        Style s = new Style();
        s.fromXmlHelper(elem);

        for (Element child : DomUtil.getChildren(elem))
        {
            String childName = DomUtil.getLocalName(child);
            switch (childName)
            {
                case KmlConstants.E_ICONSTYLE:
                    s.setIconStyle(IconStyle.fromXml(child));
                    break;

                case KmlConstants.E_LINESTYLE:
                    s.setLineStyle(LineStyle.fromXml(child));
                    break;

                case KmlConstants.E_LABELSTYLE:
                case KmlConstants.E_POLYSTYLE:
                case KmlConstants.E_BALLOONSTYLE:
                case KmlConstants.E_LISTSTYLE:
                    // TODO: implement
                    break;

                default:
                    throw new IllegalArgumentException("invalid Style child element: " + childName);
            }
        }

        return s;
    }


    /**
     *  Appends this folder's XML representation to the provided element.
     */
    public Element appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_STYLE);
        super.appendObjectXml(elem);
        ObjectUtils.optSet(iconStyle, s -> s.appendAsXml(elem));
        ObjectUtils.optSet(lineStyle, s -> s.appendAsXml(elem));
        return elem;
    }
}
