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


/**
 *  A top-level container for other features and shared styles.
 *
 *  See https://developers.google.com/kml/documentation/kmlreference#document.
 */
public class Document
extends Container<Document>
{

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
        super.appendAsXmlHelper(elem);
        return elem;
    }
}
