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
import java.util.List;

import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.ObjectUtil;
import net.sf.practicalxml.DomUtil;


/**
 *  An abstract superclass for {@link Folder} and {@link Document}. Containers
 *  may have child features.
 */
public abstract class Container<T extends Container<T>>
extends Feature<T>
{
    private List<Feature<?>> features = new ArrayList<>();

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the list of features held in this folder. May be empty, never
     *  null. If necessary, caller must determine concrete type.
     */
    public List<Feature<?>> getFeatures()
    {
        return features;
    }


    /**
     *  Replaces the current list of features with the provided list. Subsequent
     *  modifications to the list will not be reflected in this object, but
     *  modifications to the contained features will. May pass null, to clear the
     *  current list of features.
     */
    public T setFeatures(List<? extends Feature<?>> value)
    {
        features.clear();
        if (value != null)
        {
            features.addAll(value);
        }
        return (T)this;
    }


    /**
     *  Adds a feature to the end of the list managed by this object. Subsequent
     *  modifications to the feature will be reflected in this object.
     */
    public T addFeature(Feature<?> value)
    {
        features.add(value);
        return (T)this;
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Recursively searches this container and any descendents for features
     *  with the given name.
     */
    public List<Feature<?>> findByName(String name)
    {
        List<Feature<?>> result = new ArrayList<>();

        for (Feature<?> f : features)
        {
            if (ObjectUtil.equals(name, f.getName()))
            {
                result.add(f);
            }
            if (f.isContainer())
            {
                result.addAll(((Container<?>)f).findByName(name));
            }
        }

        return result;
    }


    /**
     *  Recursively searches this container and any descendents for features
     *  with the given type.
     */
    public List<Feature<?>> findByType(Class<? extends Feature<?>> type)
    {
       List<Feature<?>> result = new ArrayList<>();

        for (Feature<?> f : features)
        {
            if (f.getClass().equals(type))
            {
                result.add(f);
            }
            if (f.isContainer())
            {
                result.addAll(((Container<?>)f).findByType(type));
            }
        }

        return result;
    }

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
        super.fromXmlHelper(elem);
        for (Element child : DomUtil.getChildren(elem))
        {
            // note: this iteration includes children that represent attributes
            //       of the container; we have to ignore them, so will ignore
            //       any unrecognized child (even ones that are invalid)
            switch (DomUtil.getLocalName(child))
            {
                // TODO - support other geometries
                case KmlConstants.E_PLACEMARK:
                    addFeature(Placemark.fromXml(child));
                    break;
                case KmlConstants.E_FOLDER:
                    addFeature(Folder.fromXml(child));
                    break;
            }
        }
    }


    /**
     *  Appends all features managed by this container.
     */
    protected void appendFeaturesAsXml(Element elem)
    {
        for (Feature<?> feature : features)
        {
            feature.appendAsXml(elem);
        }
    }
}
