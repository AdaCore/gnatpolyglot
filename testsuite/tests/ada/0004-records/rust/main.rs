use test::test as pkg;

fn main() {
    let mut p = pkg::init_pair(3, 4);
    p.print();

    println!("p.v_1.v = {}", p.get_v_1().get_v());

    // get_v_1/get_v_2 return a view into the Ada object (static ownership), not a copy;
    // mutating through the view changes the embedded field in-place.
    let mut view1 = p.get_v_1();
    view1.set_v(5);
    let mut view2 = p.get_v_2();
    view2.set_v(6);
    p.print();

    // Direct field replacement via set_v_1 / set_v_2
    p.set_v_1(&pkg::Value::new(7));
    p.set_v_2(&pkg::Value::new(8));
    p.print();

    let v4 = pkg::Value::new(4);
    let v2 = pkg::Value::new(2);
    let ctor = pkg::Pair::new(&v4, &v2);
    ctor.print();

    let mut bis = pkg::init_pair_1(5, 6);
    bis.print();
    bis.print_bis();
    let mut bv1 = bis.get_v_1();
    bv1.set_v(2);
    let mut bv2 = bis.get_v_2();
    bv2.set_v(4);
    bis.print_bis();
    bis.print();

    // A scalar field getter returns a copy; the `_mut` accessor hands out a real &mut
    // reference into the object, so writing through it mutates the field in place.
    let mut v = pkg::Value::new(10);
    println!("v = {}", v.get_v());
    *v.get_v_mut() = 20;
    println!("v = {}", v.get_v());
}
