use ex1::example;

fn main() {
    let mut c = example::Counter::new_default();

    c.increment();
    println!("count = {}", c.get_count());
    c.increment_1(3);
    println!("count = {}", c.get_count());
    c.reset();
    println!("count = {}", c.get_count());
    *c.get_count_mut() = 5;
    println!("count = {}", c.get_count());
    c.set_count(6);
    println!("count = {}", c.get_count());
}

