use test::test as pkg;
use test::ada::strings::PolyglotString;

fn main() {
    let ada_arr = pkg::string_func();
    pkg::string_proc(&ada_arr);
    println!("Ada.String from Rust: {}", ada_arr);

    let user_arr = pkg::user_string_func();
    pkg::user_string_proc(&user_arr);
    println!("Test.User_Str from Rust: {}", user_arr);

    // We should be able to use functions defined for similar types.
    pkg::user_string_proc(&ada_arr);
    pkg::string_proc(&user_arr);

    // A string built on the Rust side can be passed across the boundary too.
    let rust_arr = PolyglotString::from("from Rust");
    pkg::string_proc(&rust_arr);
    pkg::user_string_proc(&rust_arr);
}
