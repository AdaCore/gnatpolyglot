with Ada.Unchecked_Deallocation;

package Test is

   type Root;
   type Root_A is access all Root'Class;

   type Root is tagged record
      I : Integer;
   end record;

   function F_Root (R: Root; R2 : Root'Class) return Root'Class
   is (Root'(I => 0));
   procedure P_Root_A (R: Root; R2 : Root_A) is null;
   procedure P_Out_Root_A (R: Root; R2 : in out Root_A) is null;

   type Child is new Root with record
      J : Integer;
   end record;

   type Child_A is access all Child'Class;

   function Get_Root return Root'Class;
   procedure Get_Root (R: in out Root_A);
   function Get_Child return Root'Class;
   procedure Get_Child (R: in out Root_A);

   function Get_Root_A return Root_A;
   function Get_Child_A return Root_A;

   function Call_F_Root (R: Root'Class; R2 : Root'Class) return Root'Class;
   procedure Call_P_Root_A (R: Root'Class; R2 : Root_A);
   procedure Call_P_Out_Root_A (R: Root'Class; R2 : in out Root_A);

   function Clone (V : Root'Class) return Root'Class is (V);

   procedure Free (Acc : in out Root_A);

   type Callback is access procedure (R : Root'Class);
   procedure Call_Callback(C : Callback; R : Root'Class);

end Test;
