PKG="github.com/aorzecho/sampler"
GOBIN=${CURDIR}/bin
GOPATH:=${CURDIR}:${GOPATH}

default: build
all: fetch build test

build: bin/sampler

bin/sampler:
	go install ./...

clean:
	go clean ./...
	-rm $(GOBIN)/sampler

test:
	go test ./...
