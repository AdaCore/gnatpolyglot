with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Append (S : in out Unbounded_String; T : Time) is
      Hour : Integer := Integer (Seconds (T)) / 3600;
      Min  : Integer := Integer (Seconds (T)) mod 3600 / 60;
      Sec  : Integer := Integer (Seconds (T)) mod 60;

      procedure Append_Int (S : in out Unbounded_String; I : Integer) is
         Str : String := I'Image;
      begin
         Append (S, Str (2 .. Str'Last));
      end Append_Int;

   begin
      Append_Int (S, Year (T));
      Append (S, "-");
      Append_Int (S, Month (T));
      Append (S, "-");
      Append_Int (S, Day (T));
      Append (S, "-");
      Append_Int (S, Hour);
      Append (S, ":");
      Append_Int (S, Min);
      Append (S, ":");
      Append_Int (S, Sec);
   end Append;

end Test;
