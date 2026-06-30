import com.adacore.libex1.animals.AnimalsPackage;
import com.adacore.libex1.animals.Animal;
import com.adacore.libex1.animals.Parrot;
import com.adacore.libex1.animals.Color;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {
    static class Dog extends Animal {
        @Override
        public void shout() {
            System.out.println("Woof!");
        }
    }
    public static void main(String[] args) {
        Parrot p = new Parrot(Color.BLUE);
        p.shout();
        p.repeat(new PolyglotString("Ada"));
        p.repeat(new PolyglotString("C++"));

        System.out.format("p is %s\n", AnimalsPackage.image(p.getC()).toString());

        Dog d = new Dog();
        d.shout();

        AnimalsPackage.callShout(d);
        Parrot.PtrArray flock = new Parrot.PtrArray(1, 2);
        p._setOwner(Owner.LIBRARY);
        flock.set(0, p);
        Parrot otherParrot = new Parrot(Color.BLUE);
        otherParrot._setOwner(Owner.LIBRARY);
        // 2 versions for indexing:
        // - java.util.List#set: slides indexes to 0.
        // - PolyglotArray#setUnslided: uses Ada indexes.
        flock.set(0, p);
        flock.setUnslided(2, p);
        AnimalsPackage.shout(flock);

        p._setOwner(Owner.USER);
        otherParrot._setOwner(Owner.USER);
    }
}
