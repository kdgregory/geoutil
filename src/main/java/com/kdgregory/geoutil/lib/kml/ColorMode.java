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

/**
 *  The legal values for colorMode, used by various style elements.
 */
public enum ColorMode
{
    normal,
    random;


    /**
     *  Attempts to match the provided string to one of these values, throwing
     *  if unable.
     */
    public static ColorMode fromString(String value)
    {
        for (ColorMode mode : ColorMode.values())
        {
            if (mode.name().equals(value))
                return mode;
        }

        throw new IllegalArgumentException("invalid colorMode: " + value);
    }
}