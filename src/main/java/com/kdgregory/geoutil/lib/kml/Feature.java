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
 *  Abstract class for containers and placemarks, managing common elements.
 *  See https://developers.google.com/kml/documentation/kmlreference#feature.
 */
public abstract class Feature<T extends Feature<T>>
extends KmlObject<T>
{
    private String name;
    private Boolean visibility;
    private String description;
    private TimeStamp timestamp;
    private TimeSpan timespan;
    private String styleRef;
    private Style styleSelector;

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns this feature's name, if any.
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Sets the name for this feature.
     */
    public T setName(String value)
    {
        name = value;
        return (T)this;
    }


    /**
     *  Returns this feature's visibility. May be null, which is equivalent to false.
     */
    public Boolean getVisibility()
    {
        return visibility;
    }


    /**
     *  Sets this feature's visibility.
     */
    public T setVisibility(Boolean value)
    {
        visibility = value;
        return (T)this;
    }


    /**
     *  Returns the description of this feature, if any.
     */
    public String getDescription()
    {
        return description;
    }


    /**
     *  Sets the description of this feature.
     */
    public T setDescription(String value)
    {
        description = value;
        return (T)this;
    }


    /**
     *  Returns this feature's timestamp, if any.
     */
    public TimeStamp getTimestamp()
    {
        return timestamp;
    }


    /**
     *  Sets this feature's timestamp. This will also clear any existing timespan.
     */
    public T setTimestamp(TimeStamp value)
    {
        timestamp = value;
        timespan = null;
        return (T)this;
    }


    /**
     *  Returns this feature's timespan, if any.
     */
    public TimeSpan getTimespan()
    {
        return timespan;
    }


    /**
     *  Sets this feature's timestamp. This will also clear any existing timestamp.
     */
    public T setTimespan(TimeSpan value)
    {
        timespan = value;
        timestamp = null;
        return (T)this;
    }


    /**
     *  Returns the style reference for this feature, if any.
     */
    public String getStyleRef()
    {
        return styleRef;
    }


    /**
     *  Sets the style reference for this feature. This method takes a URI; see
     *  {@link #setLocalStyleRef} if you want to set a reference to a style in
     *  the same document.
     */
    public T setStyleRef(String value)
    {
        styleRef = value;
        return (T)this;
    }


    /**
     *  Sets the style reference for this feature. This method takes an ID for a
     *  style, and prepends "#" to turn it into a local document URI.
     */
    public T setLocalStyleRef(String value)
    {
        styleRef = "#" + value;
        return (T)this;
    }


    /**
     *  Returns the style selector for this feature, if any.
     */
    public Style getStyleSelector()
    {
        return styleSelector;
    }


    /**
     *  Sets the style reference for this feature.
     */
    public T setStyleSelector(Style value)
    {
        styleSelector = value;
        return (T)this;
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Identifies whether this feature is a Container (and can therefore hold
     *  other features).
     */
    public boolean isContainer()
    {
        return this instanceof Container;
    }

//----------------------------------------------------------------------------
//  XML conversion helpers
//----------------------------------------------------------------------------

    @Override
    protected void toXmlHelper(Element elem)
    {
        super.toXmlHelper(elem);

        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_NAME,        getName());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_VISIBILITY,  getVisibility());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_DESCRIPTION, getDescription());
        ObjectUtils.optSet(getTimestamp(), t -> t.appendAsXml(elem));
        ObjectUtils.optSet(getTimespan(),  t -> t.appendAsXml(elem));
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_STYLEREF,    getStyleRef());
        ObjectUtils.optSet(getStyleSelector(), s -> s.appendAsXml(elem));
    }


    @Override
    protected void fromXmlHelper(Element elem)
    {
        super.fromXmlHelper(elem);
        for (Element child : DomUtil.getChildren(elem))
        {
            String childName = DomUtil.getLocalName(child);
            String childText = DomUtil.getText(child);
            switch (childName)
            {
                case KmlConstants.E_FEATURE_NAME:
                    setName(childText);
                    break;
                case KmlConstants.E_FEATURE_VISIBILITY:
                    setVisibility(ObjectUtils.parseAsBoolean(childText));
                    break;
                case KmlConstants.E_FEATURE_DESCRIPTION:
                    setDescription(childText);
                    break;
                case KmlConstants.E_TIMESTAMP:
                    setTimestamp(TimeStamp.fromXml(child));
                    break;
                case KmlConstants.E_TIMESPAN:
                    setTimespan(TimeSpan.fromXml(child));
                    break;
                case KmlConstants.E_FEATURE_STYLEREF:
                    setStyleRef(childText);
                    break;
                case KmlConstants.E_STYLE:
                    setStyleSelector(Style.fromXml(child));
                    break;
                // no default; there may be other children
            }
        }
    }
}