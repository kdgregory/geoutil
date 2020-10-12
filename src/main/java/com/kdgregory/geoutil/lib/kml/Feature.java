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

//----------------------------------------------------------------------------
//  Methods to be implemented by children
//---------------------------------------------------------------------------

    public abstract Element appendAsXml(Element parent);

//----------------------------------------------------------------------------
//  XML conversion helpers
//----------------------------------------------------------------------------

    /**
     *  Sets the fields controlled by this class from children/attributes of
     *  the passed element.
     */
    @Override
    protected void fromXmlHelper(Element elem)
    {
        String namespace = elem.getNamespaceURI();

        super.fromXmlHelper(elem);
        setName(XmlUtils.getChildText(elem, namespace, KmlConstants.E_FEATURE_NAME));
        setVisibility(XmlUtils.getChildTextAsBoolean(elem, namespace, KmlConstants.E_FEATURE_VISIBILITY));
        setDescription(XmlUtils.getChildText(elem, namespace, KmlConstants.E_FEATURE_DESCRIPTION));
    }


    /**
     *  Adds the fields controlled by this class as children/attributes of the
     *  passed element.
     */
    @Override
    protected void appendAsXmlHelper(Element elem)
    {
        super.appendAsXmlHelper(elem);
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_NAME, getName());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_VISIBILITY,  getVisibility());
        XmlUtils.optAppendDataElement(elem, KmlConstants.NAMESPACE, KmlConstants.E_FEATURE_DESCRIPTION, getDescription());
    }
}
