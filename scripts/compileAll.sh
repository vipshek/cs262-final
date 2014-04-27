#! /bin/bash

# Won't work for complex hierarchies, but should work for us
find ../src/ -name "*.java" > sources.txt
javac -cp '../src:../resources/*' -g @sources.txt
rm -f sources.txt
