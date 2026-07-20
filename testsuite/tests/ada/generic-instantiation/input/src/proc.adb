with Ada.Text_IO; use Ada.Text_IO;

package body Proc is

    procedure P1 (I : Integer) is
    begin
        Put_Line("P1 :" & I'Image);
    end P1;

end Proc;

