.PHONY: test clean
default: build

build: target

target:
	mvn package -DskipTests

clean:
	mvn clean

test: target
	mvn test
