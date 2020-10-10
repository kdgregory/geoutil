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

import com.kdgregory.geoutil.lib.internal.XmlUtils;


/**
 *  Represents a Placemark as defined in https://developers.google.com/kml/documentation/kmlreference.
 *  <p>
 *  Note that a Placemark has no required components; any and all accessors
 *  may return null.
 */
public class Placemark
{
    public String name;
    public Boolean visibility;
    public String description;
    public Geometry geometry;

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns this placemark's name, if any.
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Sets the name for this placemark.
     */
    public Placemark setName(String value)
    {
        name = value;
        return this;
    }


    /**
     *  Returns this placemark's visibility. May be null, which is equivalent to false.
     */
    public Boolean getVisibility()
    {
        return visibility;
    }


    /**
     *  Sets this placemark's visibility.
     */
    public Placemark setVisibility(Boolean value)
    {
        visibility = value;
        return this;
    }


    /**
     *  Returns the description of this placemark, if any.
     */
    public String getDescription()
    {
        return description;
    }


    /**
     *  Sets the description of this placemark.
     */
    public Placemark setDescription(String value)
    {
        description = value;
        return this;
    }


    /**
     *  Returns the geometry associated with this placemark. There are multiple
     *  classes that implement geometry; if it is important to process them
     *  differently you will need to dispatch based on concrete class.
     */
    public Geometry getGeometry()
    {
        return geometry;
    }


    /**
     *  Updates the geometry represented by this placemark.
     */
    public Placemark setGeometry(Geometry value)
    {
        geometry = value;
        return this;
    }

//----------------------------------------------------------------------------
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Creates an instance from an element tree following the description in
     *  https://developers.google.com/kml/documentation/kmlreference#placemark.
     *  <p>
     *  Note: since KML documents may use multiple namespaces, this operation
     *  merely requires that the child elements have the same namespace as the
     *  passed element.
     *
     *  @throws IllegalArgumentException if the provided element does not have
     *          the name "Placemark", or cannot be parsed according to the KML
     *          specification.
     */
    public static Placemark fromXml(Element elem)
    {
        if (! KmlConstants.E_PLACEMARK.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        String namespace = elem.getNamespaceURI();

        Placemark pm = new Placemark();

        pm.setName(XmlUtils.getChildText(elem, namespace, KmlConstants.E_PLACEMARK_NAME));
        pm.setVisibility(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_PLACEMARK_VIS));
        pm.setDescription(XmlUtils.getChildText(elem, namespace, KmlConstants.E_PLACEMARK_DESC));

        // TODO - slightly higher performance if we convert to map, because not constantly iterating

        Element ePoint = DomUtil.getChild(elem, namespace, KmlConstants.E_POINT);
        if (ePoint != null)
        {
            pm.setGeometry(KmlPoint.fromXml(ePoint));
        }

        // TODO - support other geometries

        return pm;
    }


    /**
     *  Appends this placemark's XML representation to the provided element.
     */
    public void appendAsXml(Element parent)
    {
        Element ep = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_PLACEMARK);

        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_PLACEMARK_NAME, name);
        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_PLACEMARK_VIS,  visibility);
        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_PLACEMARK_DESC, description);

        if (geometry != null)
        {
            geometry.appendAsXml(ep);
        }
    }

}
