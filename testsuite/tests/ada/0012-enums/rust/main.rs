use test::test as pkg;

fn main() {
    let e1 = pkg::Enum1::A;
    pkg::p_enum_1(e1);
    let e1 = pkg::f_enum_1(e1);
    pkg::p_enum_1(e1);
    println!("Rust value:{}", e1 as i32);

    let e2 = pkg::Enum2::E;
    pkg::p_enum_2(e2);
    let e2 = pkg::f_enum_2(e2);
    pkg::p_enum_2(e2);
    println!("Rust value:{}", e2 as i32);

    let mut e2 = pkg::Enum2::F;
    pkg::p_in_out(&mut e2);
    println!("Rust value:{}", e2 as i32);

    // An enumeration whose representation does not fit in 32 bits uses i64: the
    // literal value is preserved, not truncated to the low 32 bits (which would
    // print 705032704).
    println!("Rust big:{}", pkg::EnumBig::I as i64);
    // In-range items of the same enum still round-trip through the binding.
    let eb = pkg::f_enum_big(pkg::EnumBig::G);
    pkg::p_enum_big(eb);

    let t = pkg::T::new_default();
    println!("{}", pkg::call_t_f(&t, pkg::Enum1::A) as i32);

    let mut t2 = pkg::T::new_default();
    println!("{}", t2.get_e() as i32);
    t2.set_e(pkg::Enum1::B);
    println!("{}", t2.get_e() as i32);
    // Enum getters return a copy too; the `_mut` accessor gives a real &mut reference
    // into the object for in-place mutation, just like a numeric scalar.
    *t2.get_e_mut() = pkg::Enum1::C;
    println!("{}", t2.get_e() as i32);
}
