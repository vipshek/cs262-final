#! /bin/bash

# compile tests
find ../tests/ -name "*.java" > tests.txt
javac -cp '../tests:../src:../resources/*' -g @tests.txt

# run tests
sed 's/^.*\/\(.*\).java$/\1/' tests.txt | xargs java -cp '../src:../tests:../resources/*' org.junit.runner.JUnitCore

# cleanup
rm tests.txt
