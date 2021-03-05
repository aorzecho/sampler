BUILD_DIR ?= target

build:
	mkdir -p "$(BUILD_DIR)"
	go build -o "$(BUILD_DIR)" ./...

clean:
	go clean ./...
	-rm -r "$(BUILD_DIR)"

test:
	go test -v ./... 
