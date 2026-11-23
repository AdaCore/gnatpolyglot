package Test is

   type A_Tagged_Type is tagged null record;

   type A_Tagged_Access is access all A_Tagged_Type'Class;

   type A_Non_Tagged_Type is null record;

   procedure Set_Tagged_Object (V : A_Tagged_Access);

   function Get_Tagged_Object return A_Tagged_Type'Class;

   function Get_Non_Tagged_Object return A_Non_Tagged_Type;

   type A_Tagged_Callback is access procedure (V : A_Tagged_Type'Class);

   type A_Non_Tagged_Callback is access procedure (V : A_Non_Tagged_Type);

   procedure Call_Tagged_Callback (X : A_Tagged_Callback; Y : A_Tagged_Type'Class);

   procedure Call_Non_Tagged_Type_Callback (X : A_Non_Tagged_Callback; Y : A_Non_Tagged_Type);

end Test;
