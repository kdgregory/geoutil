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

import net.sf.kdgcommons.lang.StringUtil;


/**
 *  An abstract base class for KML entities that can have an ID. See
 *  https://developers.google.com/kml/documentation/kmlreference#object.
 */
public abstract class KmlObject<T extends KmlObject<T>>
{
    private String id;

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

    /**
     *  Returns this folder's unique ID. May be null.
     */
    public String getId()
    {
        return id;
    }


    /**
     *  Sets this folder's ID. Note that there is no attempt made to verify
     *  that the ID is unique.
     */
    public T setId(String value)
    {
        id = value;
        return (T)this;
    }

//----------------------------------------------------------------------------
//  XML conversion helpers
//----------------------------------------------------------------------------

    /**
     *  Sets the fields controlled by this class from children/attributes of
     *  the passed element.
     */
    protected void fromXmlHelper(Element elem)
    {
        setId(StringUtil.trimToNull(elem.getAttribute(KmlConstants.A_OBJECT_ID)));
    }


    /**
     *  Adds the fields controlled by this class as children/attributes of the
     *  passed element.
     */
    protected void appendAsXmlHelper(Element elem)
    {
        elem.setAttribute(KmlConstants.A_OBJECT_ID, getId());
    }
}