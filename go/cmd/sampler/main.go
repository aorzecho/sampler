package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"

	"github.com/aorzecho/sampler/go/pkg/sampler"
)

func main() {

	sampleSize, err := strconv.ParseUint(os.Args[1], 10, 16)
	if err != nil || sampleSize <= 2 {
		log.Fatalf("Invalid sample size '%s'  error: %s\n", os.Args[1], err)
	}

	s, samplingErr := sampler.NewSampler(uint16(sampleSize))
	if samplingErr == nil {
		s.Process(bufio.NewReader(os.Stdin))
	}
	fmt.Println(string(s.GetSample()))
}
