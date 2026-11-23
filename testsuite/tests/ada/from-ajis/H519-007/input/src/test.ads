package Test is

   type My_Type is private;

   procedure Set_Fields (Obj : in out My_Type; A : Integer; B : Float);

   procedure Display_My_Type (Obj : My_Type);

   procedure Set_Fields_2 (Obj : access My_Type; A : Integer; B : Float);

   procedure Display_My_Type_2 (Obj : access My_Type);

   type My_Type_Access is access all My_Type;

   procedure Set_Fields_3 (Obj : My_Type_Access; A : Integer; B : Float);

   procedure Display_My_Type_3 (Obj : My_Type_Access);

private

   type My_Type is record
      A : Integer;
      B : Float;
   end record;

end Test;