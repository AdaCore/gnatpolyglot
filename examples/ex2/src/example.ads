package Example is

    type Counter is record
        Count : Natural := 0;
    end record;

    procedure Increment (C : in out Counter);

    type C_Arr is array (Positive range <>) of Counter;

    procedure Increment (Arr : in out C_Arr);

    type Int_Arr is array (Positive range <>) of Natural;

    function To_Int_Arr (Arr : in out C_Arr) return Int_Arr;

end Example;
