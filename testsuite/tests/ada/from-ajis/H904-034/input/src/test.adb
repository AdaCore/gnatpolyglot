with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;

package body Test is

   procedure Call (V : Callback) is
   begin
      V.all;
   exception
      when E : others =>
         declare
            Message : String := Exception_Information (E);
	    First, Last : Integer;
         begin
            for J in Message'Range loop
               if Message (J) = '[' then
                  First := J;
               elsif Message (J) = ']' then
		  Last := J;
		  exit;
               end if;
            end loop;

            Put_Line
              (Message (Message'First .. First)
                & Message (Last .. Message'Last));
         end;
   end Call;

end Test;
