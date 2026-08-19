use ex0::example;
use ex0::ada::strings::PolyglotString;

fn main() {
    example::hello();
    example::hello_1(&PolyglotString::from("Rust"));
}

