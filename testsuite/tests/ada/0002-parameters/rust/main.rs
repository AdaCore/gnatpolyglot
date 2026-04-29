use test::test as pkg;

fn main() {
    pkg::p_param(42);
    println!("Got {} from Ada.", pkg::f_param(2, 3, 4));
}
