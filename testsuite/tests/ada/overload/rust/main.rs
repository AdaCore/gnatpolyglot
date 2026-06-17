use test::test as pkg;

fn main() {
    let t = pkg::T1::new(0);

    // Overloaded_Proc / Overloaded_Proc (A: Integer): same (void) return type,
    // disambiguated only by parameters.
    pkg::overloaded_proc();
    pkg::overloaded_proc_1(1);

    // Overloaded_Fun (Integer, Integer, Integer) / Overloaded_Fun (Integer, T1, T1):
    // same Integer return type, disambiguated only by parameters.
    let r1 = pkg::overloaded_fun(1, 2, 3);
    println!("Got from ada : {}", r1);
    let r2 = pkg::overloaded_fun_1(4, &t, &t);
    println!("Got from ada : {}", r2);

    // Overloaded_Ret family: void / Integer / T1 are disambiguated by return type,
    // and the second Integer overload (with a parameter) by the counter suffix.
    pkg::void_overloaded_ret();
    let r3 = pkg::standard_integer_overloaded_ret();
    println!("Got from ada : {}", r3);
    let r4 = pkg::test_t1_overloaded_ret();
    println!("Got from ada : {}", r4.get_v());
    let r5 = pkg::standard_integer_overloaded_ret_1(4);
    println!("Got from ada : {}", r5);

    // No_Rename: different return types and parameters, but both must still get a
    // distinct Rust name since Rust has no overloading.
    println!("10 * 2 = {}", pkg::no_rename(10));
    println!("10 * 4 = {}", pkg::no_rename_1(10, 4));

    // Overloaded_Int (Integer) / Overloaded_Int (My_Int).
    pkg::overloaded_int(1);
    pkg::overloaded_int_1(2);
}
