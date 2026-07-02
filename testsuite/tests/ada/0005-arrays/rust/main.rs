use test::test as pkg;
use test::ada::arrays::PolyglotArray;

fn test_integer_arrays() {
    println!("-- integer arrays --");

    // A function returning an unconstrained array, owned on the Rust side.
    let mut arr = pkg::f_u_1();
    println!("bounds: {} {}", arr.begin(), arr.end());
    // The Debug impl reports the descriptor (bounds and length), not the elements.
    println!("debug: {arr:?}");
    println!("content: [{} {} {}]", arr.get(1), arr.get(2), arr.get(3));
    println!("sum: {}", pkg::f_u_2(&arr));

    // Write elements back, then read them through a parameter.
    arr.set(1, 4);
    arr.set(2, 5);
    arr.set(3, 6);
    println!("content: [{} {} {}]", arr.get(1), arr.get(2), arr.get(3));
    println!("sum: {}", pkg::f_u_2(&arr));

    // An `in out` parameter: Ada mutates the shared buffer in place.
    pkg::out_proc(&mut arr);
    print!("doubled:");
    for i in arr.begin()..=arr.end() {
        print!(" {}", arr.get(i));
    }
    println!();

    // An array allocated and filled on the Rust side.
    let mut owned = PolyglotArray::<i32>::new(1, 5);
    let mut v = 0;
    for i in owned.begin()..=owned.end() {
        v += 2;
        owned.set(i, v);
    }
    print!("owned:");
    for i in owned.begin()..=owned.end() {
        print!(" {}", owned.get(i));
    }
    println!();

    // IntoIterator (`for x in &array`) and the iter() adapter walk the same elements; iter() is a
    // DoubleEndedIterator so `.rev()` works.
    let sum: i32 = owned.iter().sum();
    print!("iter:");
    for v in &owned {
        print!(" {v}");
    }
    println!(" (sum {sum})");
    print!("rev:");
    for v in owned.iter().rev() {
        print!(" {v}");
    }
    println!();
    // ExactSizeIterator: the element count is known without walking.
    println!("len: {}", owned.iter().len());
}

fn test_integer_widths() {
    println!("-- integer widths --");

    let bytes = pkg::make_bytes();
    print!("bytes:");
    for i in bytes.begin()..=bytes.end() {
        print!(" {}", bytes.get(i));
    }
    println!();

    let shorts = pkg::make_shorts();
    print!("shorts:");
    for i in shorts.begin()..=shorts.end() {
        print!(" {}", shorts.get(i));
    }
    println!();

    // The 64-bit values exceed 32 bits, proving the elements are not truncated.
    let longs = pkg::make_longs();
    print!("longs:");
    for i in longs.begin()..=longs.end() {
        print!(" {}", longs.get(i));
    }
    println!();
}

fn test_floating_point_arrays() {
    println!("-- floating-point arrays --");

    let mut floats = pkg::make_floats();
    print!("floats:");
    for i in floats.begin()..=floats.end() {
        print!(" {:.1}", floats.get(i));
    }
    println!();
    println!("sum: {:.1}", pkg::sum_floats(&floats));

    // An `in out` parameter mutates the shared buffer in place.
    pkg::scale_floats(&mut floats);
    print!("scaled:");
    for i in floats.begin()..=floats.end() {
        print!(" {:.1}", floats.get(i));
    }
    println!();
    println!("sum: {:.1}", pkg::sum_floats(&floats));

    // A float array allocated on the Rust side.
    let mut owned = PolyglotArray::<f32>::new(1, 3);
    owned.set(1, 0.25);
    owned.set(2, 0.5);
    owned.set(3, 0.75);
    print!("owned:");
    for i in owned.begin()..=owned.end() {
        print!(" {:.2}", owned.get(i));
    }
    println!();

    let doubles = pkg::make_doubles();
    print!("doubles:");
    for i in doubles.begin()..=doubles.end() {
        print!(" {:.2}", doubles.get(i));
    }
    println!();
    println!("sum: {:.2}", pkg::sum_doubles(&doubles));
}

fn test_boolean_arrays() {
    println!("-- boolean arrays --");

    let mut bools = pkg::make_bools();
    print!("bools:");
    for i in bools.begin()..=bools.end() {
        print!(" {}", bools.get(i));
    }
    println!();
    println!("any: {}", pkg::any_true(&bools));

    // An `in out` parameter flips each element in place.
    pkg::negate_bools(&mut bools);
    print!("negated:");
    for i in bools.begin()..=bools.end() {
        print!(" {}", bools.get(i));
    }
    println!();
    println!("any: {}", pkg::any_true(&bools));

    // A boolean array allocated on the Rust side.
    let mut owned = PolyglotArray::<bool>::new(1, 2);
    owned.set(1, true);
    owned.set(2, false);
    print!("owned:");
    for i in owned.begin()..=owned.end() {
        print!(" {}", owned.get(i));
    }
    println!();
}

fn test_record_arrays() {
    println!("-- record arrays --");

    // A getter on a record element yields a non-owning view, so `get(i).get_i()`
    // reads a field.
    let mut structs = pkg::my_int_arr_func();
    println!("bounds: {} {}", structs.begin(), structs.end());
    print!("content:");
    for i in structs.begin()..=structs.end() {
        print!(" {}", structs.get(i).get_i());
    }
    println!();

    // An `in out` parameter mutates the records in place.
    pkg::my_int_arr_proc(&mut structs);
    print!("tripled:");
    for i in structs.begin()..=structs.end() {
        print!(" {}", structs.get(i).get_i());
    }
    println!();
    pkg::print_image(&structs);

    // A record array allocated and filled on the Rust side.
    let mut owned = PolyglotArray::<pkg::MyInt>::new(1, 3);
    for i in owned.begin()..=owned.end() {
        owned.set(i, pkg::MyInt::new(i * 10));
    }
    print!("owned:");
    for i in owned.begin()..=owned.end() {
        print!(" {}", owned.get(i).get_i());
    }
    println!();

    // Iteration yields the same non-owning views as get(), for record elements too.
    print!("iter:");
    for e in &owned {
        print!(" {}", e.get_i());
    }
    println!();
}

fn main() {
    test_integer_arrays();
    test_integer_widths();
    test_floating_point_arrays();
    test_boolean_arrays();
    test_record_arrays();
}
