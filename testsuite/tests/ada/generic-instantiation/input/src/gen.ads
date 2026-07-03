generic
    I: Integer;
    type TI is private;
    with procedure Subp (I: TI);
package Gen is

    type T is record
        I : TI;
    end record;

    package Nested is
        procedure N;
    end Nested;

    generic
        type T2 is range <>;
    package Subgen is

        type Rec is record
            I : T2;
        end record;

        function F (I: T2; O: TI) return Rec;
    end Subgen;

    procedure P;

    procedure Prim (O: T);

end Gen;
