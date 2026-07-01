use ex2::example;
use ex2::ada::arrays::PolyglotArray;

fn main() {
    let mut arr = PolyglotArray::<example::Counter>::new(1, 4);
    for mut e in &arr {
        e.increment();
    }
    for i in arr.begin()..=arr.end() {
        print!("{}, ", arr.get(i).unwrap().get_count());
    }
    println!();

    example::increment(&mut arr);
    for e in &arr {
        print!("{}, ", e.get_count());
    }
    println!();

    let int_arr = example::to_int_arr(&mut arr);
    for e in &int_arr {
        print!("{}, ", e);
    }
    println!();
}

