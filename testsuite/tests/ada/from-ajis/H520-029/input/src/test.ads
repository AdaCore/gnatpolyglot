with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type R1 is record
      V : Integer;
   end record;

   function F (V : R1) return Integer;

   type R2 is record
      V : Integer;
   end record;

   function F (V : R2) return Integer;

   procedure Print (Stream : File_Type; Str : String);

   function Get_Std_Output return File_Type;

end Test;