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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.ObjectUtil;
import net.sf.kdgcommons.lang.StringUtil;
import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.ObjectUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;
import com.kdgregory.geoutil.lib.shared.Point;


/**
 *  Represents a series of points on the map.
 *  <p>
 *  This object has identity equality semantics; for manipulation, extract
 *  the underlying <code>Coordinates</code>.
 */
public class LineString
extends Geometry<LineString>
{
    private List<Coordinates> coordinates;
    private AltitudeMode altitudeMode;
    private Boolean extrude;
    private Boolean tessellate;


    /**
     *  Creates an instance from a list of <code>Point</code> (may also be used
     *  for a list of <code>Coordinates</code>).
     */
    public LineString(List<? extends Point> points)
    {
        points = ObjectUtil.defaultValue(points, Collections.<Point>emptyList());
        coordinates = new ArrayList<>(points.size());
        for (Point p : points)
        {
            coordinates.add(new Coordinates(p));
        }
    }


    /**
     *  Creates an instance from individual points.
     */
    public LineString(Point... points)
    {
        this(Arrays.asList(points));
    }


    /**
     *  Creates an instance from serialized coordinates.
     */
    public LineString(String coords)
    {
        coordinates = Coordinates.fromStringList(coords);
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns this line's coordinates.
     */
    public List<Coordinates> getCoordinates()
    {
        return coordinates;
    }


    /**
     *  Returns this line's altitude mode, if it is set; null otherwise.
     */
    public AltitudeMode getAltitudeMode()
    {
        return altitudeMode;
    }


    /**
     *  Sets this line's altitude mode. Value may be null, to clear mode.
     */
    public LineString setAltitudeMode(AltitudeMode value)
    {
        this.altitudeMode = value;
        return this;
    }


    /**
     *  Returns this line's altitude mode, if set, as a string; null otherwise.
     */
    public String getAltitudeModeString()
    {
        return (altitudeMode == null) ? null : altitudeMode.name();
    }


    /**
     *  Sets this line's altitude mode, given a string value. Value may be null, to clear mode.
     */
    public LineString setAltitudeModeString(String value)
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
    public LineString setExtrude(Boolean value)
    {
        extrude = value;
        return this;
    }


    /**
     *  Gets the tessellate flag, if it is set; null otherwise.
     */
    public Boolean getTessellate()
    {
        return tessellate;
    }


    /**
     *  Sets the tessellate flag; may be null.
     */
    public LineString setTessellate(Boolean value)
    {
        tessellate = value;
        return this;
    }

//----------------------------------------------------------------------------
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Appends this line's XML representation to the provided element.
     */
    @Override
    public Element appendAsXml(Element parent)
    {
        Element child = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_LINESTRING);

        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_EXTRUDE, extrude);
        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_TESSELLATE, tessellate);
        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_ALTMODE,
                                          ObjectUtils.optInvoke(altitudeMode, AltitudeMode::name));
        XmlUtils.optAppendDataElement(child, KmlConstants.NAMESPACE, KmlConstants.E_GEOMETRY_COORD, Coordinates.stringify(coordinates));

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
    public static LineString fromXml(Element elem)
    {
        if (! KmlConstants.E_LINESTRING.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        String namespace = elem.getNamespaceURI();

        String coords = XmlUtils.getChildText(elem, namespace, KmlConstants.E_GEOMETRY_COORD);
        if (StringUtil.isEmpty(coords))
            throw new IllegalArgumentException("LineString must have coordinates");

        LineString p = new LineString(coords);
        p.setAltitudeMode(ObjectUtils.optInvoke(
            XmlUtils.getChildText(elem, namespace, KmlConstants.E_GEOMETRY_ALTMODE),
            AltitudeMode::fromString));
        p.setExtrude(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_GEOMETRY_EXTRUDE));
        p.setTessellate(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_GEOMETRY_TESSELLATE));

        return p;
    }
}
