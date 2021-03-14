#!/bin/sh
# An entrypoint script for running different implementations of the sampler
# @author aorzecho (2016)

DIR=$(cd "$(dirname "$0")" && pwd)

fail () {
  echo "$@" >&2
  exit 2
}

help () {
  me="$0"
  echo "" >&2
  echo "Stream sampler generating random representative sample of given size" >&2
  echo "The script reads/writes standard in/out" >&2
  echo "Supports single byte character sets" >&2
  echo "" >&2
  echo "Usage:" >&2
  echo "$me <sample size>" >&2
  echo "" >&2
  echo "Configuration (environment variables):" >&2
  echo "  ENGINE          - which engine to use (java|go|rust), default is java" >&2
  echo "  S_BUFFER_SIZE   - the size of stream reader buffer, used to improve throughput, default 1024, set 0 to disable" >&2
  echo "" >&2
  echo "Examples:" >&2
  echo "  create a sample of size 50 from /dev/urandom" >&2
  echo "  $> dd if=/dev/urandom count=100 bs=1MB | base64 | $me 50" >&2
  echo "" >&2
  echo "  sample a string using a 'go' engine" >&2
  echo "  $> echo 'abcdef' | ENGINE=go $me 3" >&2
  echo "" >&2
  echo "  sample a random string from www.random.org" >&2
  cat <<EXAMPLE >&2
  $> curl -s 'https://www.random.org/strings/?num=10&len=20&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new'|tr -d '\n'|$me 20
EXAMPLE
  echo "" >&2
  echo "  sample directly some input from the console" >&2
  echo "  $> $me 20" >&2
  echo "   enter the text to be sampled, finish with ctrl-d on a newline to get the sample (note that newlines _are_ part of the stream so can happen in the sample as well)" >&2
  echo "" >&2
  exit 1
}

ENGINE=${ENGINE:=java}
SAMPLE_SIZE=${1:-0}
[ "$SAMPLE_SIZE" -gt 0 ] || help

BIN="$DIR/$ENGINE/target/sampler"
VM=""

case $ENGINE in
  java)
    BIN="$DIR/$ENGINE/target/Sampler-1.0-SNAPSHOT.jar"
    VM="java -jar"
    ;;
  go|rust)
    ## just defaults
    ;;
  *)
    fail "Engine \"${ENGINE}\" not yet implemented..."
esac

[ -f "$BIN" ] || fail "Binary file ($BIN) not found - please build the $ENGINE implementation first!"

export TIME="Maximum allocated memory during runtime: %MkB\nCPU: real %e  user %U  sys %S"

#the VM should not be quoted not to act as the first arg to time if empty
#shellcheck disable=SC2086
time $VM "$BIN" "$SAMPLE_SIZE"
