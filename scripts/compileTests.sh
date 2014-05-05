#! /bin/bash

# Won't work for complex hierarchies, but should work for us
find ../tests/ -name "*.java" > sources.txt
javac -cp '../tests:../src:../resources/*' -g @sources.txt
rm -f sources.txt
