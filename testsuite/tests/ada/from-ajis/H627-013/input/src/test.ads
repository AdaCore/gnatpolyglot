package Test is

   type A_Type is abstract tagged null record;

   function Raise_Exception (Var : A_Type) return Integer is abstract;

   procedure Call_And_Raise (Var : A_Type'Class);

   procedure Call_And_Catch (Var : A_Type'Class);

end Test;
