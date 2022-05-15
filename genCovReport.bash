#!/bin/bash
# Expecting merged coverage in coverage/merged.dat
if [ ! -f "coverage/merged.dat" ]; then
  echo "Error: No merged coverage database (coverage/merged.dat) found!"
  exit
fi

# Generate reports
mkdir -p coverage/html
verilator_coverage --write-info coverage/coverage.info coverage/merged.dat
lcov -a coverage/coverage.info -o coverage/lcov.info
genhtml -o coverage/html coverage/lcov.info 
