with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   Exc3 : exception;

   procedure Get_Exception (I: Integer) is
   begin
      case I is
         when 0 => raise ExcR with I'Img;
         when 1 => raise Exc1 with I'Img;
         when 2 => raise Exc2 with I'Img;
         when 3 => raise Exc3 with I'Img;
         when others => null;
      end case;
   end;

end Test;

