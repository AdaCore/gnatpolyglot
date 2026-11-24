package Test is

   type T_1 is abstract tagged null record;

   procedure P (V : T_1) is abstract;

   procedure P_2 (V : T_1);

   type T_2 is abstract new T_1 with null record;

   --  This test checks that T_2 is generated with proper P & P_2 inherited
   --  primitives, which are correctly bound to java.

   procedure Call_P (V : T_1'Class);

   procedure Call_P_2 (V : T_1'Class);

end Test;
