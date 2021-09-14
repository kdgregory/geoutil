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

package com.kdgregory.geoutil.util.fit;

import java.io.File;

import com.kdgregory.geoutil.lib.fit.FITFile;
import com.kdgregory.geoutil.lib.fit.FITFile.FileHeader;


/**
 *  Dumps the content of its input file. This is intended to validate the library
 *  classes as they are added.
 */
public class FileDump
{
    public static void main(String[] argv)
    throws Exception
    {
        FITFile file = new FITFile(new File(argv[0]));
        
        FileHeader header = file.getFileHeader();
        System.out.println("File Header");
        System.out.format("    header size      = %d\n", header.getHeaderSize());
        System.out.format("    protocol version = %d\n", header.getProtocolVersion());
        System.out.format("    profile version  = %d\n", header.getProfileVersion());
        System.out.format("    data size        = %d\n", header.getDataSize());
    }
}
