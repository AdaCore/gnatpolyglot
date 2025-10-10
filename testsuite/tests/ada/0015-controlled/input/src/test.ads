with Ada.Finalization;
with Ada.Unchecked_Deallocation;
with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Rec is record
      A, B : Integer;
   end record;

   type Cont is new Ada.Finalization.Controlled with private;

   type Cont_Wrapper is record
      C : Cont;
   end record;

   procedure P (Value: Cont);

   procedure P_Make_Copy (Value: Cont'Class);

   overriding
   procedure Initialize (Object : in out Cont);

   overriding
   procedure Adjust (Object : in out Cont);

   overriding
   procedure Finalize (Object : in out Cont);

   type Cont_Array is array (Positive range <>) of Cont;

   procedure Foo (Arr : in out Cont_Array);

   function Get_Arr return Cont_Array;

private

   type Rec_Access is access all Rec;

   type Cont is new Ada.Finalization.Controlled with record
      C : Rec_Access;
      Generation : Positive := 1;
   end record;

end Test;
