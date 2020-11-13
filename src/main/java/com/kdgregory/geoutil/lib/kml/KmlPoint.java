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
import com.kdgregory.geoutil.lib.kml.fieldtypes.AltitudeMode;
import com.kdgregory.geoutil.lib.kml.fieldtypes.Coordinates;


/**
 *  Represents a point on the map; used as the Geometry of a Placemark.
 *  <p>
 *  This object has identity equality semantics; for manipulation, extract
 *  the underlying <code>Point</code> object.
 */
public class KmlPoint
extends Geometry<KmlPoint>
{
    private Coordinates coordinates;
    private AltitudeMode altitudeMode;
    private Boolean extrude;


    /**
     *  Creates an instance with latitude, longitude, and altitude.
     */
    public KmlPoint(double lat, double lon, double alt)
    {
        this.coordinates = new Coordinates(lat, lon, alt);
    }


    /**
     *  Creates an instance with latitude and longitude but no altitude.
     */
    public KmlPoint(double lat, double lon)
    {
        this.coordinates = new Coordinates(lat, lon);
    }


    /**
     *  Creates an instance from a set of serialized coordinates.
     */
    public KmlPoint(String coords)
    {
        this.coordinates = Coordinates.fromString(coords);
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns this point's coordinates.
     */
    public Coordinates getCoordinates()
    {
        return coordinates;
    }


    /**
     *  Returns the point's altitude mode, if it is set; null otherwise.
     */
    public AltitudeMode getAltitudeMode()
    {
        return altitudeMode;
    }


    /**
     *  Sets the point's altitude mode. Value may be null, to clear mode.
     */
    public KmlPoint setAltitudeMode(AltitudeMode value)
    {
        this.altitudeMode = value;
        return this;
    }


    /**
     *  Returns the point's altitude mode, if set, as a string; null otherwise.
     */
    public String getAltitudeModeString()
    {
        return (altitudeMode == null) ? null : altitudeMode.name();
    }


    /**
     *  Sets the point's altitude mode, given a string value. Value may be null, to clear mode.
     */
    public KmlPoint setAltitudeModeString(String value)
    {
        this.altitudeMode = AltitudeMode.fromString(value);
        return this;
    }


    /**
     *  Gets the extrude flag, if it is set; null otherwise.
     */
    public Boolean getExtrude()
    {
        return extrude;
    }


    /**
     *  Sets the extrude flag; may be null.
     */
    public KmlPoint setExtrude(Boolean value)
    {
        extrude = value;
        return this;
    }

//----------------------------------------------------------------------------
//  XML Conversion
//----------------------------------------------------------------------------

    /**
     *  Appends this point's XML representation to the provided element.
     */
    @Override
    public Element appendAsXml(Element parent)
    {
        Element child = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_POINT);

        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_EXTRUDE, getExtrude());
        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_ALTMODE, getAltitudeModeString());
        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_COORD,   getCoordinates());

        return child;
    }


    /**
     *  Creates an instance from an element tree following the description in
     *  https://developers.google.com/kml/documentation/kmlreference#point.
     *  <p>
     *  Note: since KML documents may use multiple namespaces, this operation
     *  merely requires that the child elements have the same namespace as the
     *  passed element. It does not validate that the provided namespace is
     *  one of the expected ones.
     *
     *  @throws IllegalArgumentException if the provided element does not have
     *          the name "Point", or cannot be parsed according to the KML
     *          specification.
     */
    public static KmlPoint fromXml(Element elem)
    {
        if (! KmlConstants.E_POINT.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        String namespace = elem.getNamespaceURI();

        KmlPoint p = new KmlPoint(XmlUtils.getChildText(elem, namespace, KmlConstants.E_GEOMETRY_COORD));
        ObjectUtils.optSet(XmlUtils.getChildText(elem, namespace, KmlConstants.E_GEOMETRY_ALTMODE),          p::setAltitudeModeString);
        ObjectUtils.optSet(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_GEOMETRY_EXTRUDE), p::setExtrude);

        return p;
    }
}
