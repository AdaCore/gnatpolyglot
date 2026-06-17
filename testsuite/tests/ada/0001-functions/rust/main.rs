use test::test as pkg;

fn main() {
    println!("Got {} from Ada.", pkg::f());
    println!("Got {} from Ada.", pkg::f_expr());
}
