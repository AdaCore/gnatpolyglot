package Test is

   type A_Procedure is access procedure (I : Integer);

   function Get_Procedure return A_Procedure;

   procedure Proc (I : Integer);

   procedure Call_A_Procedure (I : Integer; P : A_Procedure);

end Test;