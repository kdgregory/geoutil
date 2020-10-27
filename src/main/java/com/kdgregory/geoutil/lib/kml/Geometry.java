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

/**
 *  This is a marker interface that corresponds to the Geometry abstract element
 *  defined in https://developers.google.com/kml/documentation/kmlreference#geometry.
 */
public interface Geometry
{
    /**
     *  Appends this object to a parent, using an implementation-defined element name.
     *  Returns the appended element (to support testing).
     */
    Element appendAsXml(Element parent);
}
