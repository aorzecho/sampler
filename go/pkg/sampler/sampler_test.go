package sampler_test

import (
	"bufio"
	"fmt"
	"strings"
	"testing"

	"github.com/aorzecho/sampler/go/pkg/sampler"
	"github.com/stretchr/testify/assert"
)

func TestSamplerBasic(t *testing.T) {
	fmt.Println("asdd")
	//t.Fatalf("not ok")
}

func Test_streamOfSameCharactersShouldProduceSampleOfSameCharacters(t *testing.T) {

	instance, _ := sampler.NewSampler(3)
	sample, _ := instance.Process(bufio.NewReader(strings.NewReader("aaaaaaaaaa")))

	assert.Equal(t, "aaa", string(sample))
}

func Test_streamShorterThanSampleShouldProduceFullSample(t *testing.T) {

	instance, _ := sampler.NewSampler(3)
	sample, _ := instance.Process(bufio.NewReader(strings.NewReader("a")))

	assert.Equal(t, "aaa", string(sample))
}

func Test_emptyStreamShouldProduceEmptySample(t *testing.T) {
	instance, _ := sampler.NewSampler(3)
	sample, _ := instance.Process(bufio.NewReader(strings.NewReader("")))

	assert.Equal(t, "", string(sample))
}

func Test_beforeProcessingShouldProduceEmptySample(t *testing.T) {
	instance, _ := sampler.NewSampler(3)
	sample := instance.GetSample()

	assert.Equal(t, "", string(sample))
}

func Test_samplingMultipleStreamsShouldWork(t *testing.T) {
	instance, _ := sampler.NewSampler(3)
	instance.Process(bufio.NewReader(strings.NewReader("")))
	instance.Process(bufio.NewReader(strings.NewReader("a")))
	instance.Process(bufio.NewReader(strings.NewReader("aaaa")))

	assert.Equal(t, "aaa", string(instance.GetSample()))
}

func Test_samplingNullStreamShouldThrowErrorWithMessage(t *testing.T) {
	instance, _ := sampler.NewSampler(3)
	_, err := instance.Process(nil)

	assert.EqualError(t, err, "reader is nil")
}

func Test_zeroSampleSizeShouldThrowException(t *testing.T) {

	_, err := sampler.NewSampler(0)

	assert.EqualError(t, err, "sampleSize must be >0")
}
