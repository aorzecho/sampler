## Stream sampler generating random representative sample

The goal of this project is experimenting with programming languages - implementing
the same sample task in multiple languages using appropriate standards/conventions etc.
The first implementation is [java](java/) and an initial skeleton for [go](go/).

### building
The whole project:
```
$> make clean
$> make
```
run tests on all sub-projects and check shell scripts with [shellcheck](https://www.shellcheck.net/)
```
$> make test
```
That should build everything, but requires dev environments for all supported languages. To build individually:
```
$> make -C java
$> make -C go
```
or directly using language specific tools
```
$> cd java && mvn clean package
$> cg go && go install ./...
```
TODO: add Makefile targets to build using Docker containers so that dev environments do not have to be available locally


### running
use `sampler.sh` script:
```
$>  ./sampler.sh

Stream sampler generating random representative sample of given size
The script reads/writes standard in/out
Supports single byte character sets

Usage:
./sampler.sh <sample size>

Configuration (environment variables):
  ENGINE          - which engine to use (java|go), default is java
  S_BUFFER_SIZE   - the size of stream reader buffer, used to improve throughput, default 1024, set 0 to disable

Examples:
  create a sample of size 50 from /dev/urandom
  $> dd if=/dev/urandom count=100 bs=1MB | base64 | ./sampler.sh 50

  sample a string using a 'go' engine
  $> echo 'abcdef' | ENGINE=go ./sampler.sh 3

  sample a random string from www.random.org
  $> curl -s 'https://www.random.org/strings/?num=10&len=20&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new'|tr -d '\n'|./sampler.sh 20

  sample directly some input from the console
  $> ./sampler.sh 20
   enter the text to be sampled, finish with ctrl-d on a newline to get the sample  
   (note that newlines are part of the stream so can happen in the sample as well)

```
