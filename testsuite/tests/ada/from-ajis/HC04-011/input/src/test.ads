
package Test is

   type Enum_1 is (A);

   type Enum_2 is (A, B, C, E, F, G);

   procedure Proc (V : Enum_1);

   procedure Proc_2 (V : in out Enum_2);

   function Get return Enum_2;

   type A_Callback is access procedure
     (V1 : Enum_2; V2 : out Enum_2; V3 : in out Enum_2);

   procedure Call (V : A_Callback);

end Test;
