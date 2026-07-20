with Ada.Text_IO; use Ada.Text_IO;

package body Gen is

    package body Nested is
        procedure N is
        begin
            Put_Line ("Nested :" & I'Image);
        end N;
    end Nested;

    package body Subgen is

        function F (I: T2; O: TI) return Rec is
        begin
            Subp (O);
            return (I => I * 2);
        end F;

    end Subgen;

    procedure P is
    begin
        Put_Line(I'Image);
    end P;

    procedure Prim (O : T) is
    begin
        Put ("Call from instantiation " & I'Image & ": ");
        Subp (O.I);
    end;

end Gen;
