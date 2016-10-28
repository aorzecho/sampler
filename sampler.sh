#!/bin/sh

ENGINE=${ENGINE:=java}
SAMPLE_SIZE=${1:-50}

case $ENGINE in
  java)
    CMD="java -jar java/target/Sampler-1.0-SNAPSHOT.jar"
    ;;
  go)
    CMD="./go/bin/sampler"
    ;;
  *)
    echo "Engine \"${ENGINE}\" not yet implemented..." >&2
	exit 2
esac

export TIME="Maximum allocated memory during runtime: %MkB\nCPU: real %e  user %U  sys %S"
time $CMD $SAMPLE_SIZE
