with Ada.Text_IO; use Ada.Text_IO;

package body Test is

    function Call (C : ST) return Integer is
    begin
        return C.all (4);
    end;

    function C (I : Integer) return Integer is
    begin
        return I * 2;
    end C;

    function Get_Callback return ST
    is (C'Access);

end Test;
