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

package com.kdgregory.geoutil.lib.gpx;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.ObjectUtil;
import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.ObjectUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;
import com.kdgregory.geoutil.lib.shared.TimestampedPoint;


/**
 *  Represents a location -- waypoint or trackpoint -- stored in a GPX
 *  file. See https://www.topografix.com/GPX/1/1/#type_wptType for more
 *  information.
 *  <p>
 *  This is implemented using bean-style getters and setters, although
 *  it's based on the shared <code>Point</code> class, which is immutable.
 *  The setters return the object itself, so may be chained.
 *  <p>
 *  In normal usage, the various conversion functions are the primary API.
 */
public class GpxPoint
extends com.kdgregory.geoutil.lib.shared.Point
{
    // note: order of variables follows required order of elements

    private Double elevation;
    private Instant timestamp;
    private Double variance;
    private Double geoidHeight;
    private String name;
    private String comment;
    private String description;


    /**
     *  Base constructor.
     */
    public GpxPoint(double lat, double lon)
    {
        super(lat,lon);
    }


    /**
     *  Constructs an instance from an XML node tree structured as a wptType
     *  per https://www.topografix.com/GPX/1/1/.
     *
     *  Does not validate the provided element's name or namespace, or whether
     *  it contains unexpected content.
     */
    public GpxPoint(Element elem)
    {
        this(XmlUtils.getAttributeAsDouble(elem, GpxConstants.A_WPT_LAT),
             XmlUtils.getAttributeAsDouble(elem, GpxConstants.A_WPT_LON));

        for (Element child : DomUtil.getChildren(elem))
        {
            String childNamespace = child.getNamespaceURI();
            String childName = DomUtil.getLocalName(child);
            String childText = child.getTextContent();

            if (! GpxConstants.NAMESPACE.equals(childNamespace))
                throw new IllegalArgumentException("invalid namespace: " + childNamespace);

            switch (childName)
            {
                case GpxConstants.E_WPT_ELEVATION:
                    ObjectUtils.optSetDouble(childText, this::setElevation);
                    break;
                case GpxConstants.E_WPT_TIMESTAMP:
                    ObjectUtils.optSetString(childText, this::setTimestampString);
                    break;
                case GpxConstants.E_WPT_VARIANCE:
                    ObjectUtils.optSetDouble(childText, this::setMagneticVariance);
                    break;
                case GpxConstants.E_WPT_GEOID_HEIGHT:
                    ObjectUtils.optSetDouble(childText, this::setGeoidHeight);
                    break;
                case GpxConstants.E_WPT_NAME:
                    ObjectUtils.optSetString(childText, this::setName);
                    break;
                case GpxConstants.E_WPT_COMMENT:
                    ObjectUtils.optSetString(childText, this::setComment);
                    break;
                case GpxConstants.E_WPT_DESCRIPTION:
                    ObjectUtils.optSetString(childText, this::setDescription);
                    break;
                default:
                    throw new IllegalArgumentException("unsupported element: " + childName);
            }
        }
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the point's timestamp, as a Java 8 Instant. May be null.
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or IS-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public Instant getTimestamp()
    {
        return timestamp;
    }


    /**
     *  Sets the point's timestamp, as a Java 8 Instant.
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or IS-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public GpxPoint setTimestamp(Instant timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }


    /**
     *  Returns the point's timestamp, as an ISO-8601 string with "Zulu" offset.
     *  May be null.
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or IS-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public String getTimestampString()
    {
        return (timestamp == null)
             ? null
             : timestamp.toString();
    }


    /**
     *  Sets the point's timestamp, as an ISO-8601 formatted string (accepts
     *  either YYYY-MM-DDTHH:MM:SSZ or YYYY-MM-DDTHH:MM:SS+OFFSET formats).
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or ISO-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public GpxPoint setTimestampString(String timestamp)
    {
        this.timestamp = (timestamp.endsWith("Z"))
                       ? Instant.parse(timestamp)
                       : Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(timestamp));
        return this;
    }


    /**
     *  Returns the point's timestamp, as milliseconds since epoch. Returns 0
     *  if the object's timestamp is missing.
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or IS-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public long getTimestampMillis()
    {
        return (timestamp == null)
             ? 0
             : timestamp.toEpochMilli();
    }


    /**
     *  Sets the point's timestamp, as milliseconds since epoch
     *  <p>
     *  Note: there are three ways to set/retrieve a point's timestamp: as an
     *  Instant (internal format), milliseconds since epoch, or ISO-8601 string.
     *  Each of these mechanisms has separate getters and setters, to avoid
     *  confusing bean introspectors.
     */
    public GpxPoint setTimestampMillis(long timestamp)
    {
        this.timestamp = Instant.ofEpochMilli(timestamp);
        return this;
    }


    /**
     *  Returns the point's elevation, in meters. May be null.
     */
    public Double getElevation()
    {
        return elevation;
    }


    /**
     *  Sets the point's elevation, in meters.
     */
    public GpxPoint setElevation(Double value)
    {
        this.elevation = value;
        return this;
    }


    /**
     *  Returns the point's magnetic variance, in degrees (0 ... 360). May be null.
     */
    public Double getMagneticVariance()
    {
        return this.variance;
    }


    /**
     *  Sets the point's magnetic variance, in degrees (0 ... 360).
     */
    public GpxPoint setMagneticVariance(Double value)
    {
        if ((value < 0) || (value > 360))
            throw new IllegalArgumentException("magnetic variance must be 0..360; was " + value);

        this.variance = value;
        return this;
    }


    /**
     *  Returns the point's geoid height, in meters. May be null.
     */
    public Double getGeoidHeight()
    {
        return geoidHeight;
    }


    /**
     *  Sets the point's geoid height, in meters.
     */
    public GpxPoint setGeoidHeight(Double value)
    {
        this.geoidHeight = value;
        return this;
    }


    /**
     *  Returns the point's name. May be null (and will be, for trackpoints).
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Sets the point's name.
     */
    public GpxPoint setName(String value)
    {
        this.name = value;
        return this;
    }


    /**
     *  Returns the comment attached to the point. May be null.
     */
    public String getComment()
    {
        return comment;
    }


    /**
     *  Sets a comment on the point.
     */
    public GpxPoint setComment(String value)
    {
        this.comment = value;
        return this;
    }


    /**
     *  Returns the point's description. May be null.
     */
    public String getDescription()
    {
        return description;
    }


    /**
     *  Sets the point's description.
     */
    public GpxPoint setDescription(String value)
    {
        this.description = value;
        return this;
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    /**
     *  Two instances are equal if all fields are equal.
     */
    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        else if (obj instanceof GpxPoint)
        {
            GpxPoint that = (GpxPoint)obj;
            return super.equals(that)
                && ObjectUtil.equals(this.elevation, that.elevation)
                && ObjectUtil.equals(this.timestamp, that.timestamp)
                && ObjectUtil.equals(this.variance,  that.variance);
        }
        return false;
    }


    /**
     *  Hashcode is based on the latitude and longitude of the point.
     */
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }


    /**
     *  Comparison is based first on timestamp, then on latitude and longitude
     *  per {@link com.kdgregory.geoutil.lib.shared.Point}. If the timestamp is
     *  not set, it is treated as 0 for the purposes of comparison.
     */
    @Override
    public int compareTo(com.kdgregory.geoutil.lib.shared.Point that)
    {
        if (that instanceof GpxPoint)
        {
            long thisTimestmap = getTimestampMillis();
            long thatTimestamp = ((GpxPoint)that).getTimestampMillis();
            return (thisTimestmap > thatTimestamp) ? 1
                 : (thisTimestmap < thatTimestamp) ? -1
                 : super.compareTo(that);
        }
        else
        {
            return super.compareTo(that);
        }
    }

//----------------------------------------------------------------------------
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Converts to the {@link com.kdgregory.geoutil.lib.shared.TimestampedPoint}
     *  representation. Missing timestamps are converted to 0.
     */
    public TimestampedPoint toTimestampedPoint()
    {
        return new TimestampedPoint(getTimestampMillis(), getLat(), getLon());
    }


    /**
     *  Converts to the <code>wptType</code> XML representation as specified by
     *  https://www.topografix.com/GPX/1/1/. Since this type can appear in
     *  multiple container types, the caller is responsible for providing the
     *  name that the element should use.
     *
     *  @param  parent      The DOM Element representing the container type.
     *  @para   nodeName    The local name to use for this element. To avoid typos,
     *                      use one of the names from {@link GpxConstants}.
     */
    public Element appendAsXml(Element parent, String nodeName)
    {
        Element elem = DomUtil.appendChild(parent, GpxConstants.NAMESPACE, nodeName);
        elem.setAttribute(GpxConstants.A_WPT_LAT, String.valueOf(getLat()));
        elem.setAttribute(GpxConstants.A_WPT_LON, String.valueOf(getLon()));

        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_ELEVATION,     getElevation());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_TIMESTAMP,     getTimestamp());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_VARIANCE,      getMagneticVariance());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_GEOID_HEIGHT,  getGeoidHeight());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_NAME,          getName());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_COMMENT,       getComment());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_WPT_DESCRIPTION,   getDescription());

        return elem;
    }


    /**
     *  Determines whether this point is in an inclusive range of timestamps.
     *  If the point does not have a timestamp, this is always false.
     */
    public boolean isBetween(Instant start, Instant finish)
    {
        if (timestamp == null)
            return false;

        return timestamp.compareTo(start) >= 0
            && timestamp.compareTo(finish) <= 0;
    }
}