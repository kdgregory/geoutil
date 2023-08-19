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

package com.kdgregory.geoutil.lib.fit.messages;

import java.nio.ByteBuffer;


/**
 *  Common superclass for FIT records, providing generic header and field functions.
 */
public abstract class AbstractFITMessage
{
    private ByteBuffer buf;
    
    
    /**
     *  Creates a record instance given a slice of a parent buffer.
     */
    public AbstractFITMessage(ByteBuffer slice)
    {
        buf = slice;
    }
    
    
//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------
    
    
//----------------------------------------------------------------------------
//  Record Header
//----------------------------------------------------------------------------
    
    /**
     *  Determines whether this is a definition message or a data message.
     */
    public boolean isDefinition
}
