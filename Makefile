ENGINES := java go
clean_ENGINES=$(addprefix clean_,$(ENGINES))
test_ENGINES=$(addprefix test_,$(ENGINES))
.PHONY: default $(ENGINES) $(clean_ENGINES) $(test_ENGINES)

build: $(ENGINES)
clean: $(clean_ENGINES)
test: $(test_ENGINES)
all: build test

$(ENGINES):
	$(MAKE) -C $@

$(clean_ENGINES):
	make -C $(patsubst clean_%,%,$@) clean

$(test_ENGINES):
	make -C $(patsubst test_%,%,$@) test

