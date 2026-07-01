with Ada.Text_IO; use Ada.Text_IO;

package body Example is

    procedure Increment (C : in out Counter) is
    begin
        C.Count := C.Count + 1;
    end Increment;

    procedure Increment (Arr : in out C_Arr) is
    begin
        for C of Arr loop
            Increment (C);
        end loop;
    end Increment;

    function To_Int_Arr (Arr : in out C_Arr) return Int_Arr is
        Res : Int_Arr (Arr'Range);
    begin
        for I in Arr'Range loop
            Res (I) := Arr (I).Count;
        end loop;
        return Res;
    end To_Int_Arr;

end Example;
