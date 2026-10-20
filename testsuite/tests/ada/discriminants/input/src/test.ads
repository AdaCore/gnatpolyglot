package Test is

    type R (A : Positive) is record
        S : String (1 .. A);
    end record;

    type Root (A, B : Integer) is tagged null record;

    procedure Print (V : Root);

    type Child_1 (C : Integer) is new Root (C, C) with null record;

    type Child_2 is new Root with null record;

    type R_Acc is access all R;

    type Wrapper (Acc : R_Acc := null) is null record;

    type Priv_ND (I : Integer) is private;
    type Priv (I : Integer := 2) is private;
    function Get_J_Component (P : Priv) return Integer;

    type DDTR (A : Integer := 3) is tagged limited private;

private

    type Priv_ND (I : Integer) is record
        J : Integer;
    end record;

    type Priv (I : Integer := 2) is record
        J : Integer := I + 4;
    end record;

    function Get_J_Component (P : Priv) return Integer
    is (P.J);

    type DDTR (A : Integer := 3) is tagged limited null record;

end Test;
