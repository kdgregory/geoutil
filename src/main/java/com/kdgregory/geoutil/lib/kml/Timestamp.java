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

import java.time.Instant;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.ObjectUtil;
import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.internal.TimestampUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;

/**
 *  A child element of Feature that holds a point in time. It exists as a
 *  separate object to simplify conversion to/from XML.
 *  <p>
 *  This is an immutable object; its value is set on construction. Null
 *  values are supported.
 */
public class Timestamp
{
    private Instant timestamp;


    /**
     *  Constructs an instance that has no value.
     */
    public Timestamp()
    {
        // nothing here
    }


    /**
     *  Constructs an instance from a Java8 Instant. May pass null to create
     *  an empty instance.
     */
    public Timestamp(Instant value)
    {
        this.timestamp = value;
    }


    /**
     *  Constructs an instance based on milliseconds since epoch. Note that
     *  this constructor is passed a primitive, while the related accessor
     *  returns an object.
     */
    public Timestamp(long value)
    {
        this.timestamp = Instant.ofEpochMilli(value);
    }


    /**
     *  Constructs an instance from an ISO-8601 formatted string. May pass
     *  null to create an empty instance.
     */
    public Timestamp(String value)
    {
        this.timestamp = TimestampUtils.parse(value);
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the timestamp's value as a Java8 Instant. May be null.
     */
    public Instant asInstant()
    {
        return timestamp;
    }


    /**
     *  Returns the timestamp's value as millis since epoch. May be null.
     */
    public Long asMillis()
    {
        return (timestamp == null)
             ? null
             : Long.valueOf(timestamp.toEpochMilli());
    }


    /**
     *  Returns the timestamp's value as an ISO-8601 formatted string.. May be null.
     */
    public String asString()
    {
        return (timestamp == null)
             ? null
             : timestamp.toString();
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    @Override
    /**
     *  Equality is value based: two instances are equal if they represent the
     *  same instant in time. Null instances are also considered equal.
     */
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        else if (obj instanceof Timestamp)
        {
            Timestamp that = (Timestamp)obj;
            return ObjectUtil.equals(this.timestamp, that.timestamp);
        }
        return false;
    }


    @Override
    public final int hashCode()
    {
        return (this.timestamp == null)
             ? 0
             : this.timestamp.hashCode();
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
        if (timestamp == null)
            return null;

        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_TIMESTAMP);
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_TIMESTAMP_WHEN, timestamp);
        return elem;
    }


    /**
     *  Creates a new instance from the XML representation. The passed element
     *  may be null, in which case this function returns a null instance. The
     *  name and namespace of the passed element are not verified, but the
     *  "when" element namespace must match the passed element's namespace.
     */
    public static Timestamp fromXml(Element elem)
    {
        if (elem == null)
            return null;

        String when = XmlUtils.getChildText(elem, elem.getNamespaceURI(), KmlConstants.E_TIMESTAMP_WHEN);
        if (when == null)
            throw new IllegalArgumentException("TimeStamp missing \"when\" child element");

        return new Timestamp(when);
    }
}
