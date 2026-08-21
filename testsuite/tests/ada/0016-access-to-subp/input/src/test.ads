package Test is

    type Rec is record
        I : Integer;
        J : Integer;
    end record;

    type Arr is array (Positive range <>) of Rec;

    type Callback_1 is access function (I, J : Integer) return Integer;

    type Callback_2 is access function (R : Rec) return Rec;

    type Callback_3 is access function (A : Arr) return Arr;

    function Call (C : Callback_1) return Integer;
    function Call (C : Callback_2) return Rec;
    function Call (C : Callback_3) return Arr;

    function Get_Callback_1 return Callback_1;
    function Get_Callback_2 return Callback_2;
    function Get_Callback_3 return Callback_3;

    procedure Get_Callback (C : in out Callback_1);
    procedure Get_Callback (C : in out Callback_2);
    procedure Get_Callback (C : in out Callback_3);

    type T is tagged null record;

    function T_Call (O : T; C : Callback_1) return Integer is (C.all (1, 2));

    function Call_T_Call (O : T'Class; C : Callback_1) return Integer is (O.T_Call (C));

end Test;
