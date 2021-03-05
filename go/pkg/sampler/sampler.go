package sampler

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"math/rand"
	"time"
)

//Sampler generating random representative sample of given size.
type Sampler struct {
	sampleSize  uint16
	reservoir   []byte
	streamIndex int
	rnd         *rand.Rand
}

func (p *Sampler) init(sampleSize uint16) {
	p.sampleSize = sampleSize
	p.reservoir = make([]byte, p.sampleSize)
	p.rnd = rand.New(rand.NewSource(time.Now().UnixNano()))
}

//GetSample returns current random representative sample of processed streams
func (p *Sampler) GetSample() []byte {
	if p.streamIndex == 0 {
		return make([]byte, 0)
	}

	res := make([]byte, p.sampleSize)
	for i := 0; i < len(p.reservoir); i++ {
		if i < p.streamIndex {
			res[i] = p.reservoir[i]
		} else {
			res[i] = p.reservoir[rand.Intn(p.streamIndex)]
		}
	}

	return res
}

//Process reads the stream and updates the random sample in reservoir
func (p *Sampler) Process(reader *bufio.Reader) ([]byte, error) {

	if reader == nil {
		return nil, errors.New("reader is nil")
	}

	n, err := io.ReadFull(reader, p.reservoir)
	if err == io.EOF {
		return nil, fmt.Errorf("empty stream: %w", err)
	} else if err == io.ErrUnexpectedEOF {

	}

	p.streamIndex = n
	for chr, err := reader.ReadByte(); err != io.EOF; {
		if err != nil {
			return nil, fmt.Errorf("broken pipe: %w", err)
		}
		p.streamIndex++
		j := uint16(p.rnd.Intn(p.streamIndex))
		if j < p.sampleSize {
			p.reservoir[j] = chr
		}
		chr, err = reader.ReadByte()
	}
	return p.GetSample(), nil
}

//NewSampler creates new Sampler instance
func NewSampler(sampleSize uint16) (*Sampler, error) {
	if sampleSize == 0 {
		return nil, errors.New("sampleSize must be >0")
	}
	sampler := new(Sampler)
	sampler.init(sampleSize)
	return sampler, nil
}
