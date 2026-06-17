use test::test as pkg;

fn main() {
    let root = pkg::Root::new(1, 2);
    let recr = pkg::Rec::new(1, 2);
    root.p1();

    let child = pkg::Child::new(3, 4, 5);
    let recc = pkg::Rec::new(3, 4);
    child.p1();

    root.p2(&recr);
    child.p2(&recc);

    let other = pkg::OtherChild::new(3, 4, 5);
    other.p1();

    // Deref coercion: &Child / &OtherChild coerce to &Root for classwide dispatch
    pkg::p_root(&root);
    pkg::p_root(&child);
    pkg::p_root(&other);

    let priv_root = test::r#priv::PrivRoot::new_default();
    priv_root.foo();

    let publ = test::publ::Publ::new(1, 3);
    publ.foo();
}
