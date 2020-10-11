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
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.StringUtil;
import net.sf.practicalxml.DomUtil;


/**
 *  Represents a Folder, as defined by https://developers.google.com/kml/documentation/kmlreference#folder.
 *  <p>
 *  Note that a Folder has no required components; any and all accessors
 *  may return null.
 */
public class Folder
extends Feature<Folder>
{
    private String id;
    private List<Feature<?>> features = new ArrayList<>();

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
    public Folder setId(String value)
    {
        id = value;
        return this;
    }


    /**
     *  Returns an unmodifiable list of features in this folder. May be empty,
     *  never null. If necessary, caller must determine concrete type.
     */
    public List<Feature<?>> getFeatures()
    {
        return Collections.unmodifiableList(features);
    }


    /**
     *  Replaces the current list of features with the provided list. Subsequent
     *  modifications to the list will not be reflected in this object, but
     *  modifications to the contained features will. May pass null, to clear the
     *  current list of features.
     */
    public Folder setFeatures(List<? extends Feature<?>> value)
    {
        features.clear();
        if (value != null)
        {
            features.addAll(value);
        }
        return this;
    }


    /**
     *  Adds a feature to the end of the list managed by this object. Subsequent
     *  modifications to the feature will be reflected in this object.
     */
    public Folder addFeature(Feature<?> value)
    {
        features.add(value);
        return this;
    }

//----------------------------------------------------------------------------
//  Other Public Methods
//----------------------------------------------------------------------------

    /**
     *  Creates an instance from a DOM element tree.
     *  <p>
     *  Note: since KML documents may use multiple namespaces, this operation
     *  merely requires that the child elements have the same namespace as the
     *  passed element.
     *
     *  @throws IllegalArgumentException if the provided element does not have
     *          the name "Placemark", or cannot be parsed according to the KML
     *          specification.
     */
    public static Folder fromXml(Element elem)
    {
        if (! KmlConstants.E_FOLDER.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        Folder f = new Folder();
        f.fromXmlHelper(elem);

        f.setId(StringUtil.trimToNull(elem.getAttribute(KmlConstants.A_FOLDER_ID)));

        for (Element child : DomUtil.getChildren(elem))
        {
            // TODO - support other geometries
            if (DomUtil.getLocalName(child).equals(KmlConstants.E_PLACEMARK))
            {
                f.addFeature(Placemark.fromXml(child));
            }
        }

        return f;
    }


    /**
     *  Appends this folder's XML representation to the provided element.
     */
    @Override
    public void appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_FOLDER);

        elem.setAttribute(KmlConstants.A_FOLDER_ID, getId());
        appendAsXmlHelper(elem);

        for (Feature<?> feature : features)
        {
            feature.appendAsXml(elem);
        }
    }
}
