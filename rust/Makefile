BUILD_DIR ?= target
BUILD_FLAGS ?= --release
BIN_NAME = sampler

build:
	cargo build $(BUILD_FLAGS)
	-test -f "$(BUILD_DIR)/release/$(BIN_NAME)" && ln -s "release/$(BIN_NAME)" "$(BUILD_DIR)/$(BIN_NAME)" || ln -s "debug/$(BIN_NAME)" "$(BUILD_DIR)/$(BIN_NAME)" 

clean:
	cargo clean

test:
	cargo test
