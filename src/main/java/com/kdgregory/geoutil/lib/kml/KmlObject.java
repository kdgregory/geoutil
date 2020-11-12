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
//  Abstract methods
//----------------------------------------------------------------------------

    /**
     *  Serializes this object to XML and appends it to an existing DOM tree.
     */
    public abstract Element appendAsXml(Element parent);

//----------------------------------------------------------------------------
//  XML conversion helpers
//----------------------------------------------------------------------------

    /**
     *  Appends fields from this class and its ancestors to the passed object.
     *  Subclasses should call before appending their own fields.
     */
    protected void toXmlHelper(Element elem)
    {
        if (!StringUtil.isEmpty(id))
        {
            elem.setAttribute(KmlConstants.A_OBJECT_ID, id);
        }
    }


    /**
     *  Extracts the fields managed by this class from the passed element.
     */
    protected void fromXmlHelper(Element elem)
    {
        setId(StringUtil.trimToNull(elem.getAttribute(KmlConstants.A_OBJECT_ID)));
    }
}
