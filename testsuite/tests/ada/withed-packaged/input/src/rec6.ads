package Rec6 is

    generic
        type T is private;
    package G is

        type Typ is record
            N : T;
        end record;

    end G;

    package Inst is new G (Integer);

end Rec6;
