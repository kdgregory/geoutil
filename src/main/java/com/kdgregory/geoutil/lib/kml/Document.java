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

import net.sf.practicalxml.DomUtil;


/**
 *  A top-level container for other features and shared styles.
 *
 *  See https://developers.google.com/kml/documentation/kmlreference#document.
 */
public class Document
extends Container<Document>
{
    private List<Style> sharedStyles = new ArrayList<>();

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the list of shared styles from this document. May be empty,
     *  never null.
     */
    public List<Style> getSharedStyles()
    {
        return sharedStyles;
    }


    /**
     *  Clears the list of shared styles and appends the contents of the
     *  passed list.
     *
     *  @throws IllegalArgumentException if the styles in the passed list do
     *          not have IDs. Note that partial replacement is possible.
     */
    public Document setSharedStyles(List<Style> value)
    {
        sharedStyles.clear();
        if (value != null)
        {
            for (Style style : value)
            {
                addSharedStyle(style);
            }
        }
        return this;
    }


    /**
     *  Adds a single shared style to the list managed by this document.
     *
     *  @throws IllegalArgumentException if the style does not have an ID.
     */
    public Document addSharedStyle(Style value)
    {
        if (value.getId() == null)
        {
            throw new IllegalArgumentException("shared styles must have an ID");
        }

        sharedStyles.add(value);
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
     *          the name "Document", or cannot be parsed according to the KML
     *          specification.
     */
    public static Document fromXml(Element elem)
    {
        if (! KmlConstants.E_DOCUMENT.equals(DomUtil.getLocalName(elem)))
        {
            throw new IllegalArgumentException("incorrect element name: " + DomUtil.getLocalName(elem));
        }

        Document f = new Document();
        f.fromXmlHelper(elem);
        return f;
    }


    /**
     *  Appends this folder's XML representation to the provided element.
     */
    @Override
    public Element appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, KmlConstants.NAMESPACE, KmlConstants.E_DOCUMENT);
        appendAsXmlHelper(elem);
        for (Style style : sharedStyles)
        {
            style.appendAsXml(elem);
        }
        appendFeaturesAsXml(elem);
        return elem;
    }
}
