package main_test

import (
	"fmt"
	"os"
	"testing"
)

func TestCli(t *testing.T) {
	oldArgs := os.Args
	defer func() { os.Args = oldArgs }()

	os.Args = []string{"3"}

	fmt.Println("asdd")
	//t.Fatalf("not ok")
}
