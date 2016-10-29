package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"math/rand"
	"os"
	"strconv"
)

func main() {

	sampleSize, err := strconv.Atoi(os.Args[1])
	if err != nil {
		log.Fatalf("Invalid sample size '%s'  error: %s\n", os.Args[1], err)
	}

	reservoir := make([]byte, sampleSize)
	if _, err := io.ReadFull(os.Stdin, reservoir); err != nil {
		log.Fatal(err)
	}

	r := rand.New(rand.NewSource(99))
	reader := bufio.NewReader(os.Stdin)
	idx := sampleSize
	for chr, err := reader.ReadByte(); err != io.EOF; {
		idx++
		j := r.Intn(idx)
		if j < sampleSize {
			reservoir[j] = chr
		}
		chr, err = reader.ReadByte()
	}
	fmt.Printf("Random Sample: %s\n", reservoir)
}
