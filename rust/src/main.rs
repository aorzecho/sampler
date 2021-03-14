use std::env;
// use rand::Rng;/
use std::io;
// use std::io::{BufReader, Read};
use std::string::String;
// use std::ffi::OsString;

// use bytes::Buf;

// use byte_array::ByteArray;

mod sampler {
    // use std::borrow::Borrow;
    use std::io::{BufReader, Read};

// use rand::Rng;
    // use random_fast_rng::{local_rng, Random};

    pub struct Sampler {
        pub sample_size: usize,
        reservoir: Vec<u8>,
        stream_index: usize,
    }

    impl Sampler {
        pub fn new(sample_size: usize) -> Sampler {
            println!("New Sampler with reservoir of size {}", sample_size);
            Sampler {
                sample_size,
                reservoir: vec![0; sample_size],
                stream_index: 0,
            }
        }

        pub fn process(&mut self, read: &mut dyn Read) -> Vec<u8> {
            self.process_v2(read)
        }

        pub fn process_v3(&mut self, reader: &mut dyn Read) -> Vec<u8> {
            self.stream_index = reader.read(&mut *self.reservoir).unwrap();

            let mut buf: Vec<u8> = vec![0; 8 * 1024 * 1024];
            loop {
                match reader.read(&mut *buf) {
                    Ok(cnt) if cnt > 0 => {
                        let mut i = 0;
                        while i < cnt {
                            match fastrand::usize(..self.stream_index) {
                                upd if upd < self.sample_size => { self.reservoir[upd] = buf[i] }
                                _ => ()
                            }
                            i += 1;
                            self.stream_index = self.stream_index + 1;
                        }
                    },
                    _ => break
                }
            }
            println!("Stream index: {}", self.stream_index);
            self.reservoir.to_vec()
        }

        pub fn process_v2(&mut self, read: &mut dyn Read) -> Vec<u8> {
            let mut br = BufReader::new(read);
            self.stream_index = br.read(&mut *self.reservoir).unwrap();

            let mut buf: Vec<u8> = vec![0; 8096];

            loop {
                let cnt = br.read(&mut *buf);
                if cnt.is_ok() {
                    let end = cnt.unwrap();
                    if end <= 0 {
                        break;
                    }
                    let mut i = 0;
                    while i < end {
                        let rand = fastrand::usize(..self.stream_index);
                        if rand < self.sample_size {
                            self.reservoir[rand] = buf[i];
                        }
                        i += 1;
                        self.stream_index = self.stream_index + 1;
                    }
                } else {
                    break;
                }
            }
            println!("Stream index: {}", self.stream_index);
            self.reservoir.to_vec()
        }

        pub fn process_v1(&mut self, reader: &mut dyn Read) -> Vec<u8> {
            //initial implementation, few times slower than later one
            let buf_reader = BufReader::new(reader);
            for b in buf_reader.bytes() {
                    match b {
                        Ok(byte) => {
                            if self.stream_index < self.sample_size {
                                self.reservoir[self.stream_index] = byte;
                            } else {
                                match fastrand::usize(..self.stream_index) {
                                    upd if upd < self.sample_size => { self.reservoir[upd] = byte }
                                    _ => ()
                                }
                            }

                            self.stream_index = self.stream_index + 1;
                        }
                        _ => {}
                    }
            }
            self.reservoir.to_vec()
        }
    }
}

fn help() {
    println!("usage:
sampler <sample_size>
    Start sampling from stdin, with given sample size");
}


fn main() {
    let args: Vec<String> = env::args().collect();

    match args.len() {
        // one argument passed
        2 => {
            let sample_size = args[1].parse::<usize>();
            match sample_size {
                Ok(size) => {
                    let mut sampler = sampler::Sampler::new(size);
                    match env::var_os("IMPL") {
                        Some(val) if val == "v1" => println!("{}", String::from_utf8(sampler.process_v1(&mut io::stdin())).unwrap()),
                        Some(val) if val == "v2" => println!("{}", String::from_utf8(sampler.process_v2(&mut io::stdin())).unwrap()),
                        Some(val) if val == "v3" => println!("{}", String::from_utf8(sampler.process_v3(&mut io::stdin())).unwrap()),
                        None => println!("{}", String::from_utf8(sampler.process(&mut io::stdin())).unwrap()),
                        _ => println!("Unknown implementation version, supported v1|v2"),
                    }
                }
                _ => println!("Invalid sample_size, expected unsigned integer."),
            }
        }
        // all the other cases
        _ => {
            // show a help message
            help();
        }
    }
}
