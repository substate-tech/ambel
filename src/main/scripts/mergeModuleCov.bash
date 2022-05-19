#!/bin/bash
# Chisel Module name must be passed on the command line
if [ ! -f "src/main/scala/$1.scala" ]; then
  echo "Error: No scala file found for Chisel Module $1!"
  exit
fi

# First check for coverage data
datfiles=$(find test_run_dir/$1* -name "coverage.dat")

if [ -z "$datfiles" ]; then
  echo "Error: No coverage.dat files found for Chisel Module $1!"
  exit
fi

# Find all the generated *.sv files
svfiles=$(find test_run_dir -name "$1*.sv")

# Copy them to top level, overwrite duplicates (they will be identical)
for file in ${svfiles[@]}; do
  cp -f $file .
done

# Merge coverage 
mkdir -p coverage
verilator_coverage -write coverage/merged.dat test_run_dir/$1*/coverage.dat
