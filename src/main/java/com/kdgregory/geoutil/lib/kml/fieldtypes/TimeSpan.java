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

package com.kdgregory.geoutil.lib.kml.fieldtypes;

import java.time.Instant;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.ObjectUtil;
import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.TimestampUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;
import com.kdgregory.geoutil.lib.kml.KmlConstants;

/**
 *  A child element of Feature that holds a point in time. It exists as a
 *  separate object to simplify conversion to/from XML.
 *  <p>
 *  This is an immutable object.
 */
public class TimeSpan
{
    private Instant begin;
    private Instant end;


    /**
     *  Constructs an instance that has no value.
     */
    public TimeSpan()
    {
        // nothing here
    }


    /**
     *  Constructs an instance from Java8 Instants. May pass nulls to create
     *  an empty instance.
     */
    public TimeSpan(Instant begin, Instant end)
    {
        this.begin = begin;
        this.end = end;
    }


    /**
     *  Constructs an instance based on milliseconds since epoch. Note that
     *  this constructor is passed primitives, while the related accessors
     *  return objects.
     */
    public TimeSpan(long begin, long end)
    {
        this(Instant.ofEpochMilli(begin), Instant.ofEpochMilli(end));
    }


    /**
     *  Constructs an instance from ISO-8601 formatted strings. May pass
     *  nulls to create an empty instance.
     */
    public TimeSpan(String begin, String end)
    {
        this(TimestampUtils.parse(begin), TimestampUtils.parse(end));
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the span's beginning as a Java8 Instant. May be null.
     */
    public Instant getBegin()
    {
        return begin;
    }


    /**
     *  Returns the span's ending as a Java8 Instant. May be null.
     */
    public Instant getEnd()
    {
        return end;
    }


    /**
     *  Returns the span's beginning as millis since epoch. May be null.
     */
    public Long getBeginMillis()
    {
        return (begin == null)
             ? null
             : Long.valueOf(begin.toEpochMilli());
    }


    /**
     *  Returns the span's ending as millis since epoch. May be null.
     */
    public Long getEndMillis()
    {
        return (end == null)
             ? null
             : Long.valueOf(end.toEpochMilli());
    }


    /**
     *  Returns the span's beginning as an ISO-8601 formatted string.. May be null.
     */
    public String getBeginString()
    {
        return (begin == null)
             ? null
             : begin.toString();
    }


    /**
     *  Returns the span's beginning as an ISO-8601 formatted string.. May be null.
     */
    public String getEndString()
    {
        return (end == null)
             ? null
             : end.toString();
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    @Override
    /**
     *  Equality is value based: two instances are equal if they represent the
     *  same span. Null instances are also considered equal.
     */
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        else if (obj instanceof TimeSpan)
        {
            TimeSpan that = (TimeSpan)obj;
            return ObjectUtil.equals(this.begin, that.begin)
                && ObjectUtil.equals(this.end, that.end);
        }
        return false;
    }


    @Override
    public final int hashCode()
    {
        // I don't have any reason to believe that adding the end would make
        // this a better hash function
        return (this.begin == null)
             ? 0
             : this.begin.hashCode();
    }

//----------------------------------------------------------------------------
//  XML conversion
//----------------------------------------------------------------------------

    /**
     *  Adds this object to the DOM as a child of the passed parent, IFF the
     *  timestamp is set; no-op for a null timestamp.
     */
    public Element appendAsXml(Element parent)
    {
        // a span must have a beginning and an ending to be valid
        if ((begin == null) || (end == null))
            return null;

        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_TIMESPAN);
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_TIMESPAN_BEGIN, begin);
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_TIMESPAN_END, end);
        return elem;
    }


    /**
     *  Creates a new instance from the XML representation. The passed element
     *  may be null, in which case this function returns a null instance. The
     *  name and namespace of the passed element are not verified, but the
     *  "when" element namespace must match the passed element's namespace.
     */
    public static TimeSpan fromXml(Element elem)
    {
        if (elem == null)
            return null;

        String begin = XmlUtils.getChildText(elem, elem.getNamespaceURI(), KmlConstants.E_TIMESPAN_BEGIN);
        if (begin == null)
            throw new IllegalArgumentException("TimeSpan missing \"begin\" child element");

        String end = XmlUtils.getChildText(elem, elem.getNamespaceURI(), KmlConstants.E_TIMESPAN_END);
        if (end == null)
            throw new IllegalArgumentException("TimeSpan missing \"end\" child element");

        return new TimeSpan(begin, end);
    }
}
