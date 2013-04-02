# extract_cda_codes

Command line utility to extract clinical codes from a set of CDA files

## Installation

Requires [Leiningen](http://leiningen.org), once [installed](http://leiningen.org/#install)
run the following to build the code into a standalone JAR file.

    $ lein uberjar

## Usage

    $ java -jar extract_cda_codes-0.1.0-standalone.jar [options] dir [dir2...]
    
Where `dir` is the path to a file system directory containing CDA files.

## Options

    Switches               Default  Desc                                                        
    --------               -------  ----                                                        
    -h, --no-help, --help  false    Show help
    -o, --output                    A file into which the output will optionally be written, default is stdout  
    
## License

(c) The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
either express or implied. See the License for the specific language governing permissions 
and limitations under the License.