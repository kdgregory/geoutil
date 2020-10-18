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
 *  Represents a point on the map.
 */
public class KmlPoint
extends com.kdgregory.geoutil.lib.shared.Point
implements Geometry
{
    private Double altitude;
    private AltitudeMode altitudeMode;
    private Boolean extrude;


    /**
     *  Creates an instance with latitude, longitude, and altitude.
     */
    public KmlPoint(double lat, double lon, double altitude)
    {
        super(lat,lon);
        this.altitude = Double.valueOf(altitude);
    }


    /**
     *  Creates an instance with latitude and longitude but no altitude.
     */
    public KmlPoint(double lat, double lon)
    {
        super(lat,lon);
    }


//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns this point's coordinates, as a comma-delimited string.
     */
    public String getCoordinates()
    {
        StringBuilder sb = new StringBuilder()
                           .append(getLat())
                           .append(",")
                           .append(getLon());

        if (altitude != null)
        {
            sb.append(",").append(altitude);
        }

        return sb.toString();
    }


    /**
     *  Returns this point's altitude, if any.
     */
    public Double getAltitude()
    {
        return altitude;
    }


    /**
     *  Sets this point's altitude.
     */
    public KmlPoint setAltitude(Double value)
    {
        altitude = value;
        return this;
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
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Creates a new instance from the comma-separated string tuple lat,lon,alt
     *  used to represent the point's coordinates in a KML file.
     */
    public static KmlPoint fromCoordinates(String coord)
    {
        try
        {
            String[] parts = coord.split(",");
            if (parts.length == 2)
            {
                return new KmlPoint(Double.parseDouble(parts[0]),
                                    Double.parseDouble(parts[1]));
            }
            if (parts.length == 3)
            {
                return new KmlPoint(Double.parseDouble(parts[0]),
                                    Double.parseDouble(parts[1]),
                                    Double.parseDouble(parts[2]));
            }
            // drop through to shared throw
        }
        catch (NullPointerException|NumberFormatException ex)
        {
            // drop through to shared throw
        }
        throw new IllegalArgumentException("invalid coordinates: " + coord);
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

        KmlPoint p = fromCoordinates(XmlUtils.getChildText(elem, namespace, KmlConstants.E_POINT_COORD));
        p.setAltitudeMode(ObjectUtils.optInvoke(
            XmlUtils.getChildText(elem, namespace, KmlConstants.E_POINT_ALTMODE),
            AltitudeMode::fromString));
        p.setExtrude(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_POINT_EXTRUDE));

        return p;
    }


    /**
     *  Appends this point's XML representation to the provided element.
     */
    @Override
    public void appendAsXml(Element parent)
    {
        Element ep = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_POINT);

        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_POINT_EXTRUDE, extrude);
        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_POINT_ALTMODE,
            ObjectUtils.optInvoke(altitudeMode, AltitudeMode::name));
        XmlUtils.optAppendDataElement(ep, KmlConstants.NAMESPACE, KmlConstants.E_POINT_COORD, getCoordinates());
    }
}
