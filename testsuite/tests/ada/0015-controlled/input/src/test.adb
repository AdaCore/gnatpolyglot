with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;
with System;

package body Test is

   procedure P (Value : Cont) is
   begin
      Put_Line
        ("Gen"
         & Value.Generation'Image
         & " = ("
         & Value.C.A'Image
         & ","
         & Value.C.B'Image
         & ")");
   end P;

   procedure P_Make_Copy (Value: Cont'Class) is
   begin
      Put_Line ("Making copy");
      declare
         Copy : Cont'Class := Value;
      begin
         Value.P;
         Copy.P;
      end;
      Put_Line ("Copy deleted");
   end P_Make_Copy;

   procedure Initialize (Object : in out Cont) is
   begin
      Put_Line ("Initialize");
      Object.C := new Rec'(1, 2, others => <>);
   end Initialize;

   procedure Adjust (Object : in out Cont) is
   begin
      Put_Line ("Adjust");
      Object.Generation := Object.Generation + 1;
      Object.C := new Rec'(Object.C.all);
   end Adjust;

   procedure Finalize (Object : in out Cont) is
      procedure Free is new Ada.Unchecked_Deallocation (Rec, Rec_Access);
   begin
      Put_Line ("Finalize");
      Free (Object.C);
   end Finalize;

   procedure Foo (Arr : in out Cont_Array) is
   begin
      for C of Arr loop
         C.C.A := C.C.A + 10;
         C.C.B := C.C.B + 10;
         C.P;
      end loop;
   end Foo;

   function Get_Arr return Cont_Array is
      Arr : Cont_Array (1 .. 3);
   begin
      return Arr;
   end Get_Arr;

end Test;
