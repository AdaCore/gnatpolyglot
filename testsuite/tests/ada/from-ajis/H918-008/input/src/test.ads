package Test is

   type An_Array is array (Positive range <>) of Integer;

   type An_Array_Access is access all An_Array;

   procedure Set_To_New (V : in out An_Array_Access);

   procedure Set_To_Null (V : in out An_Array_Access);

   procedure Print_Contents (V : An_Array_Access);

   type Root is abstract tagged null record;

   procedure Set_To_Null (R : Root; V : in out An_Array_Access) is abstract;

   procedure Call_Set_To_Null_And_Print
     (R : Root'Class; V : in out An_Array_Access);

end Test;
