package body Test is

   function F (V : R1) return Integer is
   begin
      return V.V;
   end F;

   function F (V : R2) return Integer is
   begin
      return V.V;
   end F;

   procedure Print (Stream : File_Type; Str : String) is
   begin
      Put_Line (Stream, Str);
   end Print;

   function Get_Std_Output return File_Type is
   begin
      return Current_Output;
   end Get_Std_Output;

end Test;