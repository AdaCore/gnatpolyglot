package Test is

   type Root is tagged null record;

   type Str_Cst_Access is access constant String;

   function Get_Str (V : Root) return Str_Cst_Access;

   type Str_Access is access all String;

   procedure Print1 (Str : Str_Cst_Access);

   procedure Print2 (Str : Str_Access);

   procedure Print3 (Str : String);

   procedure Print4 (Str : in out String);

end Test;
