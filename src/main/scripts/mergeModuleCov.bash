#!/bin/bash

# First check for coverage data
datfiles=$(find test_run_dir/* -name "coverage.dat")

if [ -z "$datfiles" ]; then
  echo "Error: No coverage.dat files found!"
  exit
fi

# Find all the generated *.sv files
svfiles=$(find test_run_dir/* -name "*.sv")

# Copy them to top level, overwrite duplicates (they will be identical)
for file in ${svfiles[@]}; do
  cp -f $file .
done

# Merge coverage 
mkdir -p coverage
verilator_coverage -write coverage/merged.dat $datfiles
