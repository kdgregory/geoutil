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

package com.kdgregory.geoutil.lib.kml.model;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.ObjectUtils;
import com.kdgregory.geoutil.lib.kml.KmlConstants;


/**
 *  Represents an object on the map. This is a container for a {@link Geometry}
 *  object with metadata.
 *  <p>
 *  Note that a Placemark has no required components; any and all accessors
 *  may return null.
 */
public class Placemark
extends Feature<Placemark>
{
    private Geometry<?> geometry;

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the geometry associated with this placemark. There are multiple
     *  classes that implement geometry; if it is important to process them
     *  differently you will need to dispatch based on concrete class.
     */
    public Geometry<?> getGeometry()
    {
        return geometry;
    }


    /**
     *  Updates the geometry represented by this placemark.
     */
    public Placemark setGeometry(Geometry<?> value)
    {
        geometry = value;
        return this;
    }

//----------------------------------------------------------------------------
//  XML conversion
//----------------------------------------------------------------------------

    /**
     *  Appends this placemark's XML representation to the provided element.
     */
    @Override
    public Element appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_PLACEMARK);
        toXmlHelper(elem);
        ObjectUtils.optSet(geometry, g -> g.appendAsXml(elem));
        return elem;
    }


    /**
     *  Creates an instance from an element tree following the description in
     *  https://developers.google.com/kml/documentation/kmlreference#placemark.
     *  <p>
     *  Note: since KML documents may use multiple namespaces, this operation
     *  ignores child namespace.
     *
     *  @throws IllegalArgumentException if the provided element does not have
     *          the name "Placemark", or cannot be parsed according to the KML
     *          specification.
     */
    public static Placemark fromXml(Element elem)
    {
        if (! KmlConstants.E_PLACEMARK.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("expected element named Placemark, was: " + DomUtil.getLocalName(elem));
        }

        Placemark pm = new Placemark();
        pm.fromXmlHelper(elem);

        for (Element child : DomUtil.getChildren(elem))
        {
            String childName = DomUtil.getLocalName(child);
            switch (childName)
            {
                case KmlConstants.E_POINT:
                    pm.setGeometry(KmlPoint.fromXml(child));
                    break;
                case KmlConstants.E_LINESTRING:
                    pm.setGeometry(LineString.fromXml(child));
                    break;
                // note: no default case, Placemark can have non-geometry children
            }
        }

        return pm;
    }
}
