with Ada.Text_IO; use Ada.Text_IO;

package body Test is

    function Call (C : Callback_1) return Integer is
    begin
        return C.all (4, 5);
    end;

    function Call (C : Callback_2) return Rec is
    begin
        return C.all (Rec'(4, 5));
    end;

    function Call (C : Callback_3) return Arr is
    begin
        return C.all (Arr'(Rec'(1, 2), Rec'(3, 4)));
    end;

    function C (I, J : Integer) return Integer is
    begin
        return I * 2 + J;
    end C;

    function C (R : Rec) return Rec is
    begin
        return Rec'(R.I + R.j, R.I * R.J);
    end C;

    function C (A : Arr) return Arr is
        Res : Arr := A;
    begin
        for E of Res loop
            E := Rec'(E.I + 1, E.J + 2);
        end loop;
        return Res;
    end C;

    function Get_Callback_1 return Callback_1
    is (C'Access);

    function Get_Callback_2 return Callback_2
    is (C'Access);

    function Get_Callback_3 return Callback_3
    is (C'Access);

    procedure Get_Callback (C : in out Callback_1) is
    begin
      C := Test.C'Access;
    end Get_Callback;

    procedure Get_Callback (C : in out Callback_2) is
    begin
      C := Test.C'Access;
    end Get_Callback;

    procedure Get_Callback (C : in out Callback_3) is
    begin
      C := Test.C'Access;
    end Get_Callback;

end Test;
