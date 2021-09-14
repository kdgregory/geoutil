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

package com.kdgregory.geoutil.lib.fit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;

import net.sf.kdgcommons.buffer.BufferUtil;


/**
 *  A read-only view of a file containing FIT data.
 */
public class FITFile
{
    ByteBuffer fileBuf;
    FileHeader fileHeader;
    
    
    /**
     *  Constructs an instance from a file on the filesystem.
     */
    public FITFile(File file)
    throws IOException
    {
        fileBuf = BufferUtil.map(file, 0, file.length(), MapMode.READ_ONLY);
        
        fileBuf.position(0);
        ByteBuffer headerBuf = fileBuf.slice();
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        fileHeader = new FileHeader(headerBuf);
        if (!fileHeader.isFIT())
            throw new IllegalArgumentException("not a FIT file");
    }
        
//----------------------------------------------------------------------------
//  File-level methods
//----------------------------------------------------------------------------
    
    /**
     *  Returns the file header object.
     */
    public FileHeader getFileHeader()
    {
        return fileHeader;
    }
    
//----------------------------------------------------------------------------
//  File Header
//----------------------------------------------------------------------------
    
    public static class FileHeader
    {
        private ByteBuffer buf;
        
        protected FileHeader(ByteBuffer buf)
        {
            this.buf = buf;
        }
        
        
        public int getHeaderSize()
        {
            return buf.get(0);
        }
        
        
        public int getProtocolVersion()
        {
            return buf.get(1);
        }
        
        
        public int getProfileVersion()
        {
            return buf.getShort(2);
        }
        
        
        public int getDataSize()
        {
            return buf.getInt(4);
        }
        
        
        public boolean isFIT()
        {
            return buf.get(8)  == '.'
                && buf.get(9)  == 'F'
                && buf.get(10) == 'I'
                && buf.get(11) == 'T';
        }
    }
}
